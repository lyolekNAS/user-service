package org.sav.fornas.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sav.fornas.dto.sessions.SessionDto;
import org.sav.fornas.userservice.security.CustomUserDetails;
import org.sav.fornas.userservice.security.annotation.IsAdmin;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@IsAdmin
public class SessionService {

	private final RedisTemplate<String, Object> redisCustomTemplate;

	private final String redisKeyPrefix = "spring:session:sessions:";

	public List<SessionDto> getAllSessions() {
		Set<String> keys = redisCustomTemplate.keys(redisKeyPrefix + "*");
		log.debug(">>> keys={}", keys);

		List<SessionDto> sessions = new ArrayList<>();

		for (String key : keys) {

			sessions.add(getSessionByKey(key));
		}
		return sessions;
	}


	public SessionDto getSession(String sid){
		return getSessionByKey(redisKeyPrefix + sid);
	}

	private SessionDto getSessionByKey(String key){
		Map<Object, Object> value = redisCustomTemplate.opsForHash().entries(key);
		Long ttl = redisCustomTemplate.getExpire(key);

		SessionDto sessionDto = new SessionDto(key.replace(redisKeyPrefix, ""));

		Object contextAttr = value.get("sessionAttr:SPRING_SECURITY_CONTEXT");

		sessionDto.setUsername("---");
		if (contextAttr instanceof SecurityContext ctx) {
			if (ctx.getAuthentication() != null && ctx.getAuthentication().getPrincipal() instanceof CustomUserDetails cud) {
				sessionDto.setUsername(cud.getUsername());
			}
		}

		sessionDto.setAttributes(value);
		sessionDto.setCreationTime(Instant.ofEpochMilli(
				Long.parseLong(value.getOrDefault("creationTime", "0").toString())
		));
		sessionDto.setLastAccessedTime(Instant.ofEpochMilli(
				Long.parseLong(value.getOrDefault("lastAccessedTime", "0").toString())
		));
		sessionDto.setTtl(ttl);

		return sessionDto;
	}

	public void deleteSession(String sessionId) {
		String key = redisKeyPrefix + sessionId;
		redisCustomTemplate.delete(key);
		log.info("Deleted session: {}", sessionId);
	}

}
