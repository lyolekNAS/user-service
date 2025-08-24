package org.sav.fornas.userservice.controller.rest;

import lombok.RequiredArgsConstructor;
import org.sav.fornas.dto.sessions.SessionDto;
import org.sav.fornas.userservice.service.SessionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/session")
public class SessionRestController {


	private final SessionService sessionService;

	@GetMapping("/view")
	public SessionDto getSession(@RequestParam String sid) {
		return sessionService.getSession(sid);
	}
}
