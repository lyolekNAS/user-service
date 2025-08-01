package org.sav.fornas.userservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomAuthController {

	@GetMapping("/login")
	public String login() {
		return "login";
	}


	@GetMapping("/logout")
	public String logout() {
		return "logout";
	}
}
