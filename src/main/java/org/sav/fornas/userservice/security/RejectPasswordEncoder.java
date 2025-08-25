package org.sav.fornas.userservice.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

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

