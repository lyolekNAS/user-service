package org.sav.fornas.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.sav.fornas.dto.users.UserDto;
import org.sav.fornas.userservice.security.annotation.IsAdmin;
import org.sav.fornas.userservice.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@IsAdmin
@RequiredArgsConstructor
public class AdminController {

	private final UserService userService;

	@GetMapping("/roles")
	public String getUserRoles(Model model, @RequestParam String name) {
		model.addAttribute("user", userService.findByUsername(name));
		model.addAttribute("roles", userService.getAllRoles());
		return "/admin/roles";
	}

	@PostMapping("/roles")
	public String setUserRoles(@ModelAttribute UserDto userDto) {
		userService.saveUserRoles(userDto);
		return "redirect:/admin/roles?name=" + userDto.getUsername();
	}

}
