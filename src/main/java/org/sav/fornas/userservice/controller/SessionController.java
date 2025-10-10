package org.sav.fornas.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sav.fornas.userservice.service.SessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {

	private final SessionService sessionService;

	@GetMapping("/all/{appName}")
	public String getAllSessions(@PathVariable String appName, Model model) {
		model.addAttribute("appName", appName);
		model.addAttribute("sessions", sessionService.getAllSessions(appName));
		return "/session/all";
	}

	@GetMapping("/delete/{appName}")
	public String deleteSession(String sid, @PathVariable String appName) {
		sessionService.deleteSession(sid, appName);
		return "redirect:/session/all/" + appName;
	}
}

