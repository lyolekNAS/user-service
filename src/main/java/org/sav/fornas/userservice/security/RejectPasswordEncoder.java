package org.sav.fornas.userservice.security;

import org.springframework.security.crypto.password.PasswordEncoder;

public class RejectPasswordEncoder implements PasswordEncoder {
	@Override
	public String encode(CharSequence rawPassword) {
		return "REJECT";
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return false;
	}
}

