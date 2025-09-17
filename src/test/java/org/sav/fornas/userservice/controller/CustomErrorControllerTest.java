package org.sav.fornas.userservice.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomErrorControllerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CustomErrorController customErrorController;

    @Test
    void error_WithAllAttributes_ReturnsCompleteErrorMap() {
        RuntimeException exception = new RuntimeException("Test exception");
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(500);
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn("Internal Server Error");
        when(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)).thenReturn(exception);

        Map<String, String> result = customErrorController.error(request);

        assertEquals("500", result.get("status"));
        assertEquals("Internal Server Error", result.get("message"));
        assertEquals(true, result.get("trace").contains("RuntimeException: Test exception"));
    }

    @Test
    void error_WithNullAttributes_ReturnsDefaultValues() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(null);
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn(null);
        when(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)).thenReturn(null);

        Map<String, String> result = customErrorController.error(request);

        assertEquals("No status", result.get("status"));
        assertEquals("No message", result.get("message"));
        assertEquals("No trace", result.get("trace"));
    }

    @Test
    void error_WithNonThrowableException_ReturnsNoTrace() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(404);
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn("Not Found");
        when(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)).thenReturn("Not a throwable");

        Map<String, String> result = customErrorController.error(request);

        assertEquals("404", result.get("status"));
        assertEquals("Not Found", result.get("message"));
        assertEquals("No trace", result.get("trace"));
    }
}