package org.sav.fornas.userservice.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sav.fornas.userservice.security.CustomUserDetails;
import org.sav.fornas.userservice.security.annotation.IsAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class InfoRestController {

	private final PasswordEncoder passwordEncoder;
	@Value("${spring.data.redis.host}")
	private String redisHost;

	@RequestMapping("/me")
	public Authentication me() {
		Authentication user = SecurityContextHolder.getContext().getAuthentication();
		log.info("User:{}", user);
		return user;
	}
	@RequestMapping("/my-auth")
	public CustomUserDetails myAuth(@AuthenticationPrincipal CustomUserDetails cud) {
		log.info("CustomUserDetails:{}", cud);
		return cud;
	}

	@GetMapping("/pub/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok(redisHost);
	}

	@GetMapping("/password/{pass}")
	@IsAdmin
	public ResponseEntity<String> password(@PathVariable String pass) {
		return ResponseEntity.ok(passwordEncoder.encode(pass));
	}

	@IsAdmin
	@RequestMapping("/registerClient")
	public void registerClient(RegisteredClientRepository registeredClientRepository){
		RegisteredClient rc = RegisteredClient.withId("messaging-client")
				.clientSecret("{noop}secret")
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.redirectUri("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
				.scope("openid")
				.build();
		registeredClientRepository.save(rc);
	}
}
