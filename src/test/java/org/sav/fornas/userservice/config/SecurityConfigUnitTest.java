package org.sav.fornas.userservice.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sav.fornas.userservice.property.RsaKeyProperties;
import org.sav.fornas.userservice.security.RsaKeyProvider;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityConfigUnitTest {

	@Mock
	private JdbcTemplate jdbcTemplate;

	@Mock
	private RsaKeyProvider rsaKeyProvider;

	@Mock
	private RsaKeyProperties rsaKeyProperties;

	@Mock
	private RedisConnectionFactory redisConnectionFactory;

	private final SecurityConfig securityConfig = new SecurityConfig();

	@Test
	void passwordEncoder_ShouldReturnDelegatingPasswordEncoder() {
		PasswordEncoder encoder = securityConfig.passwordEncoder();

		assertNotNull(encoder);
		assertInstanceOf(DelegatingPasswordEncoder.class, encoder);
	}

	@Test
	void passwordEncoder_ShouldSupportArgon2AsDefault() {
		PasswordEncoder encoder = securityConfig.passwordEncoder();

		String encoded = encoder.encode("test");
		assertTrue(encoded.startsWith("{argon2_v2}"));
		assertTrue(encoder.matches("test", encoded));
	}

	@Test
	void registeredClientRepository_ShouldReturnJdbcRepository() {
		RegisteredClientRepository repository = securityConfig.registeredClientRepository(jdbcTemplate);

		assertNotNull(repository);
	}

	@Test
	void jwkSource_ShouldCreateJWKSource() throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		when(rsaKeyProvider.getPublicKey()).thenReturn((RSAPublicKey) keyPair.getPublic());
		when(rsaKeyProvider.getPrivateKey()).thenReturn((RSAPrivateKey) keyPair.getPrivate());
		when(rsaKeyProperties.getKeyId()).thenReturn("test-key-id");

		var jwkSource = securityConfig.jwkSource(rsaKeyProvider, rsaKeyProperties);

		assertNotNull(jwkSource);
	}

	@Test
	void tokenCustomizer_ShouldReturnCustomizer() {
		var customizer = securityConfig.tokenCustomizer();

		assertNotNull(customizer);
	}

	@Test
	void redisCustomTemplate_ShouldConfigureTemplate() {
		var template = securityConfig.redisCustomTemplate(redisConnectionFactory);

		assertNotNull(template);
		assertEquals(redisConnectionFactory, template.getConnectionFactory());
	}
}
