package org.sav.fornas.userservice.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

@Slf4j
@RestController
public class CustomErrorController implements ErrorController {

	@RequestMapping("/error")
	public Map<String, String> error(HttpServletRequest request){
		Map<String, String> m = new java.util.HashMap<>();
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
		Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

		m.put("status", status != null ? status.toString() : "No status");
		m.put("message", message != null ? message.toString() : "No message");

		if (exception instanceof Throwable ex) {
			StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
			m.put("trace", sw.toString());
			log.error("Error handled:", ex);
		} else {
			m.put("trace", "No trace");
		}
		return m;
	}
}
