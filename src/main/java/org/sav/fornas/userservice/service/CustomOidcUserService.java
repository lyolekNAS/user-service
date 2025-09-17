package org.sav.fornas.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.sav.fornas.userservice.entity.UserEntity;
import org.sav.fornas.userservice.repository.UserRepository;
import org.sav.fornas.userservice.security.CustomUserDetails;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

	@Setter
	private OidcUserService oidcUserService = new OidcUserService();
	private final UserRepository userRepository;

	@Override
	public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
		OidcUser oidcUser = oidcUserService.loadUser(request);
		String userEmail = oidcUser.getEmail();

		log.debug("oidcUser={}", oidcUser);
		UserEntity user = userRepository.findByEmail(userEmail).orElseGet(
				() -> {
					UserEntity newUser = new UserEntity();
					newUser.setEmail(userEmail);
					newUser.setUsername(userEmail);
					newUser.setName(oidcUser.getAttribute("given_name"));
					newUser.setSurname(oidcUser.getAttribute("family_name"));
					newUser.setPassword("{reject}reject");

					log.debug("newUser={}", newUser);
					return userRepository.save(newUser);
				}
		);
		log.debug("user={}", user);

		return CustomUserDetails.fromUserEntity(user);
	}
}