package org.sav.fornas.userservice.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.sav.fornas.userservice.property.RsaKeyProperties;
import org.sav.fornas.userservice.security.CustomUserDetails;
import org.sav.fornas.userservice.security.RsaKeyProvider;
import org.sav.fornas.userservice.service.CustomOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;

import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
			throws Exception {
		OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
				OAuth2AuthorizationServerConfigurer.authorizationServer();

		http
				.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
				.with(authorizationServerConfigurer, authorizationServer ->
						authorizationServer
								.oidc(Customizer.withDefaults())	// Enable OpenID Connect 1.0
				)
				.authorizeHttpRequests(authorize ->
						authorize
								.anyRequest().authenticated()
				)
				// Redirect to the login page when not authenticated from the
				// authorization endpoint
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
						.requestMatchers("/error", "/css/**", "/js/**", "/.well-known/**").permitAll()
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

		return new DelegatingPasswordEncoder(encodingId, encoders);
	}

	@Bean
	public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
		return new JdbcRegisteredClientRepository(jdbcTemplate);
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

			if (!context.getTokenType().getValue().equals("id_token"))
				return;
			var authUser = context.getPrincipal();
			log.debug(">>>customizing token for {}", authUser);
			if(authUser.getPrincipal() instanceof CustomUserDetails cud){
				var claims = context.getClaims();
				log.debug(">>> adding claims for {}", cud.getUsername());
				claims.claim("userId", cud.getId());
				claims.claim("name", cud.getName());
				claims.claim("surname", cud.getSurname());
				claims.claim("email", cud.getEmail());
						claims.claim("roles", cud.getAuthorities().stream()
						.map(Object::toString).toList());
			}
		};
	}
}
