package org.sav.fornas.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.sav.fornas.userservice.dto.users.UserDto;
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

	@GetMapping("/users")
	public String getUsers(Model model) {
		model.addAttribute("users", userService.getAllUsers());
		return "/admin/users";
	}

	@GetMapping("/roles")
	public String getUserRoles(Model model, @RequestParam Long id) {
		model.addAttribute("user", userService.findById(id));
		model.addAttribute("roles", userService.getAllRoles());
		return "/admin/roles";
	}

	@PostMapping("/roles")
	public String setUserRoles(@ModelAttribute UserDto userDto) {
		userService.saveUserRoles(userDto);
		return "redirect:/admin/roles?id=" + userDto.getId();
	}
}
