package org.sav.fornas.userservice.security;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.sav.fornas.userservice.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Setter
@Builder
@ToString
public class CustomUserDetails implements UserDetails, OidcUser {

	private String username;
	private String password;
	private String name;
	private String surname;
	private String email;
	private Collection<? extends GrantedAuthority> authorities;


	@Override
	public boolean isAccountNonExpired() { return true; }

	@Override
	public boolean isAccountNonLocked() { return true; }

	@Override
	public boolean isCredentialsNonExpired() { return true; }

	@Override
	public boolean isEnabled() { return true; }


	public static CustomUserDetails fromUserEntity(UserEntity userEntity){

		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(
				userEntity.getRoles().stream().map(re -> "ROLE_" + re.getRoleName()).toArray(String[]::new)
		);

		return CustomUserDetails.builder()
				.username(userEntity.getUsername())
				.password(userEntity.getPassword())
				.name(userEntity.getName())
				.surname(userEntity.getSurname())
				.authorities(authorities)
				.email(userEntity.getEmail())
				.build();
	}

	@Override
	public Map<String, Object> getClaims() {
		return Map.of();
	}

	@Override
	public OidcUserInfo getUserInfo() {
		return null;
	}

	@Override
	public OidcIdToken getIdToken() {
		return null;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return Map.of();
	}
}