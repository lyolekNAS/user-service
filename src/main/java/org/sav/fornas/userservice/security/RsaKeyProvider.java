package org.sav.fornas.userservice.security;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.sav.fornas.userservice.property.RsaKeyProperties;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Component
@Getter
public class RsaKeyProvider {

	private RSAPrivateKey privateKey;
	private RSAPublicKey publicKey;
	private final RsaKeyProperties rsaKeyProperties;

	public RsaKeyProvider(RsaKeyProperties rsaKeyProperties) {
		this.rsaKeyProperties = rsaKeyProperties;
	}

	@PostConstruct
	public void init() {
		this.privateKey = loadPrivateKey();
		this.publicKey = loadPublicKey();
	}
	private RSAPrivateKey loadPrivateKey() {
		try {
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(readKey(rsaKeyProperties.getPrivateKey()));
			return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new IllegalStateException("Can't load private key");
		}
	}

	private RSAPublicKey loadPublicKey() {
		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(readKey(rsaKeyProperties.getPublicKey()));
			return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new IllegalStateException("Can't load public key");
		}
	}

	private byte[] readKey(String key){
		key = key
				.replaceAll("-----BEGIN .+ KEY-----", "")
				.replaceAll("-----END .+ KEY-----", "")
				.replaceAll("\\s+", "");
		return Base64.getDecoder().decode(key);
	}
}
