package org.sav.fornas.userservice.controller.rest;

import lombok.RequiredArgsConstructor;
import org.sav.fornas.userservice.dto.sessions.SessionDto;
import org.sav.fornas.userservice.service.SessionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/session")
public class SessionRestController {


	private final SessionService sessionService;

	@GetMapping("/view/{appName}")
	public SessionDto getSession(@RequestParam String sid, @PathVariable String appName) {
		return sessionService.getSession(sid, appName);
	}
}
