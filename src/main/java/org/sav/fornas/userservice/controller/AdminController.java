package org.sav.fornas.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.sav.fornas.userservice.security.annotation.IsAdmin;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@IsAdmin
@RequiredArgsConstructor
public class AdminController {


//	@GetMapping("/user")
//	public String userSessions(Model model, @RequestParam String name) {
//		model.addAttribute("username", name);
//		return "admin/user-sessions";
//	}

}
