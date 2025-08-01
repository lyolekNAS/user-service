package org.sav.fornas.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sav.fornas.userservice.entity.UserEntity;
import org.sav.fornas.userservice.repository.UserRepository;
import org.sav.fornas.userservice.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

	private final OidcUserService delegate = new OidcUserService();
	private final UserRepository userRepository;

	@Override
	public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
		OidcUser oidcUser = delegate.loadUser(request);
		String userEmail = oidcUser.getEmail();

		log.debug("email={}", userEmail);
		UserEntity user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));
		log.debug("user={}", user);

		return CustomUserDetails.fromUserEntity(user);
	}
}