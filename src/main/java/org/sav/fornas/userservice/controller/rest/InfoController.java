package org.sav.fornas.userservice.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
}
