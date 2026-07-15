package org.sav.fornas.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sav.fornas.userservice.dto.sessions.SessionDto;
import org.sav.fornas.userservice.security.CustomUserDetails;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class SessionServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisCustomTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails customUserDetails;

    @InjectMocks
    private SessionService sessionService;

    private final String sessionId = "test-session-id";
    private final String appName = "user-service";
    private final String redisKey = "spring:session:" + appName + ":" + sessionId;


    @Test
    void getAllSessions_ReturnsSessionList() {
        Set<String> keys = Set.of(redisKey);
        Map<Object, Object> sessionData = createSessionData();

        when(redisCustomTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisCustomTemplate.keys("spring:session:user-service:*")).thenReturn(keys);
        when(hashOperations.entries(redisKey)).thenReturn(sessionData);
        when(redisCustomTemplate.getExpire(redisKey)).thenReturn(3600L);

        List<SessionDto> result = sessionService.getAllSessions(appName);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sessionId, result.get(0).getId());
    }

    @Test
    void getSession_ValidSessionId_ReturnsSessionDto() {
        Map<Object, Object> sessionData = createSessionData();

        when(redisCustomTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(redisKey)).thenReturn(sessionData);
        when(redisCustomTemplate.getExpire(redisKey)).thenReturn(3600L);

        SessionDto result = sessionService.getSession(sessionId, appName);

        assertNotNull(result);
        assertEquals(sessionId, result.getId());
        assertEquals("---", result.getUsername());
    }

    @Test
    void getSession_WithAuthenticatedUser_ReturnsSessionWithUsername() {
        Map<Object, Object> sessionData = createSessionData();
        sessionData.put("sessionAttr:SPRING_SECURITY_CONTEXT", securityContext);

        when(redisCustomTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(redisKey)).thenReturn(sessionData);
        when(redisCustomTemplate.getExpire(redisKey)).thenReturn(3600L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUsername()).thenReturn("testuser");

        SessionDto result = sessionService.getSession(sessionId, appName);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void deleteSession_ValidSessionId_DeletesSession() {
        sessionService.deleteSession(sessionId, appName);

        verify(redisCustomTemplate).delete(redisKey);
    }

    private Map<Object, Object> createSessionData() {
        Map<Object, Object> data = new HashMap<>();
        data.put("creationTime", String.valueOf(Instant.now().toEpochMilli()));
        data.put("lastAccessedTime", String.valueOf(Instant.now().toEpochMilli()));
        return data;
    }
}