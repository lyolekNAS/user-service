package org.sav.fornas.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.sav.fornas.dto.users.UserDto;
import org.sav.fornas.userservice.mapper.UserMapper;
import org.sav.fornas.userservice.security.CustomUserDetails;
import org.sav.fornas.userservice.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ProfileController {

	private final UserService userService;

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@GetMapping("/profile")
	public String profile(Model model, @AuthenticationPrincipal CustomUserDetails cud) {
		model.addAttribute("user", userService.findByUsername(cud.getUsername()));
		return "profile";
	}

	@PostMapping("/profile")
	public String editProfile(@ModelAttribute UserDto userDto, @AuthenticationPrincipal CustomUserDetails cud) {
		userService.saveUserProfile(userDto, cud.getUsername());
		return "redirect:/profile";
	}
}
