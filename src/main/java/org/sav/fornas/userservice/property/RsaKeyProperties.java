package org.sav.fornas.userservice.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app-props.rsa-keys")
@Getter
@Setter
public class RsaKeyProperties {
	private String privateKey;
	private String publicKey;
	private String keyId;
}
