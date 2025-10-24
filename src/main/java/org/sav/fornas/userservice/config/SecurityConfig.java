package org.sav.fornas.userservice.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sav.fornas.userservice.property.RsaKeyProperties;
import org.sav.fornas.userservice.security.CustomUserDetails;
import org.sav.fornas.userservice.security.RejectPasswordEncoder;
import org.sav.fornas.userservice.security.RsaKeyProvider;
import org.sav.fornas.userservice.service.CustomOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;

import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60*60*24*14, redisNamespace = "spring:session:user-service")
public class SecurityConfig {

	@Bean
	@Order(0)
	public SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher(
						"/error", "/css/**", "/js/**", "/api/pub/**"
				)
				.authorizeHttpRequests(authorize -> authorize
						.anyRequest().permitAll()
				)
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Відключення створення сесії
				)
				.csrf(AbstractHttpConfigurer::disable); // Також відключіть CSRF, оскільки сесій немає

		return http.build();
	}


	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
				new OAuth2AuthorizationServerConfigurer();

		http
				.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
				.with(authorizationServerConfigurer, authorizationServer ->
						authorizationServer
								.oidc(Customizer.withDefaults())
				)
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))  // <-- оце треба
				.csrf(csrf -> csrf.ignoringRequestMatchers(
						authorizationServerConfigurer.getEndpointsMatcher()
				))
				.authorizeHttpRequests(authorize ->
						authorize
								.anyRequest().authenticated()
				)
				.exceptionHandling(exceptions -> exceptions
						.defaultAuthenticationEntryPointFor(
								new LoginUrlAuthenticationEntryPoint("/login"),
								new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
						)
				);

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, CustomOidcUserService customOidcUserService)
			throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/.well-known/**").permitAll()
						.anyRequest().authenticated()
				)
				.formLogin(form -> form
						.loginPage("/login")
						.permitAll())
				.oauth2Login(oauth -> oauth
						.loginPage("/login-oauth2")
						.userInfoEndpoint(userInfo -> userInfo
								.oidcUserService(customOidcUserService)
						)
				)
				.sessionManagement(session -> session
						.maximumSessions(1)
						.maxSessionsPreventsLogin(false)
				);

		return http.build();
	}



	@Bean
	public PasswordEncoder passwordEncoder() {
		String encodingId = "argon2_v2";
		Map<String, PasswordEncoder> encoders = new HashMap<>();
		encoders.put("bcrypt", new BCryptPasswordEncoder());
		encoders.put("argon2_v2", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
		encoders.put("pbkdf2", new Pbkdf2PasswordEncoder(
				"",
				32,
				18500,
				Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256)
		);
		encoders.put("reject", new RejectPasswordEncoder());

		return new DelegatingPasswordEncoder(encodingId, encoders);
	}

	@Bean
	public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
		return new JdbcRegisteredClientRepository(jdbcTemplate);
	}

	@Bean
	public OAuth2AuthorizationConsentService authorizationConsentService(
			JdbcTemplate jdbcTemplate,
			RegisteredClientRepository registeredClientRepository) {

		return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
	}

	@Bean
	public JWKSource<SecurityContext> jwkSource(RsaKeyProvider rsaKeyProvider, RsaKeyProperties rsaKeyProperties) {
		RSAKey rsaKey = new RSAKey.Builder(rsaKeyProvider.getPublicKey())
				.privateKey(rsaKeyProvider.getPrivateKey())
				.keyID(rsaKeyProperties.getKeyId())
				.build();

		JWKSet jwkSet = new JWKSet(rsaKey);
		return new ImmutableJWKSet<>(jwkSet);
	}

	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
		return context -> {
			log.debug(">>>customizing token {}", context.getTokenType().getValue());

			if (context.getTokenType().getValue().equals("id_token") ||
					context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {

				var authUser = context.getPrincipal();
				log.debug(">>> customizing token for {}", authUser);

				if (authUser.getPrincipal() instanceof CustomUserDetails cud) {
					var claims = context.getClaims();
					log.debug(">>> adding claims for {}", cud.getUsername());

					claims.claim("userId", cud.getId());
					claims.claim("name", cud.getName());
					claims.claim("surname", cud.getSurname());
					claims.claim("email", cud.getEmail());
					claims.claim("roles", cud.getAuthorities().stream()
							.map(Object::toString).toList());
				}
			}
		};
	}

	@Bean
	public RedisTemplate<String, Object> redisCustomTemplate(RedisConnectionFactory redisConnectionFactory){

		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);

		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new JdkSerializationRedisSerializer());
		template.setHashValueSerializer(new JdkSerializationRedisSerializer());

		template.afterPropertiesSet();
		return template;
	}
}
