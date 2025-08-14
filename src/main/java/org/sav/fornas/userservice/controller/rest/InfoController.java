package org.sav.fornas.userservice.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.sav.fornas.userservice.security.annotation.IsAdmin;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class InfoController {

	@RequestMapping("/")
	public Authentication me() {
		Authentication user = SecurityContextHolder.getContext().getAuthentication();
		log.info("User:{}", user);
		return user;
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
