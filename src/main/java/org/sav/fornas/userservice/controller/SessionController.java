package org.sav.fornas.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sav.fornas.userservice.service.SessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {

	private final SessionService sessionService;

	@GetMapping("/all")
	public String getAllSessions(Model model) {
		model.addAttribute("sessions", sessionService.getAllSessions());
		return "/session/all";
	}

	@GetMapping("/delete")
	public String deleteSession(String sid) {
		sessionService.deleteSession(sid);
		return "redirect:/session/all";
	}
}

