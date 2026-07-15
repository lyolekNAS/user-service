package org.sav.fornas.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sav.fornas.userservice.dto.sessions.SessionDto;
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

	private static final String REDIS_KEY_PREFIX = "spring:session:%s:";

	public List<SessionDto> getAllSessions(String appName) {
		Set<String> keys = redisCustomTemplate.keys(REDIS_KEY_PREFIX.formatted(appName) + "*");
		log.debug(">>> keys={}", keys);

		List<SessionDto> sessions = new ArrayList<>();

		for (String key : keys) {

			sessions.add(getSessionByKey(key, appName));
		}
		return sessions;
	}


	public SessionDto getSession(String sid, String appName){
		return getSessionByKey(REDIS_KEY_PREFIX.formatted(appName) + sid, appName);
	}

	private SessionDto getSessionByKey(String key, String appName){
		Map<Object, Object> value = redisCustomTemplate.opsForHash().entries(key);
		Long ttl = redisCustomTemplate.getExpire(key);

		SessionDto sessionDto = new SessionDto(key.replace(REDIS_KEY_PREFIX.formatted(appName), ""));

		Object contextAttr = value.get("sessionAttr:SPRING_SECURITY_CONTEXT");

		sessionDto.setUsername("---");
		if (contextAttr instanceof SecurityContext ctx && ctx.getAuthentication() != null && ctx.getAuthentication().getPrincipal() instanceof CustomUserDetails cud) {
			sessionDto.setUsername(cud.getUsername());
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

	public void deleteSession(String sessionId, String appName) {
		String key = REDIS_KEY_PREFIX.formatted(appName) + sessionId;
		redisCustomTemplate.delete(key);
		log.info("Deleted session: {}", sessionId);
	}

}
