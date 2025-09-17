package org.sav.fornas.userservice.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RejectPasswordEncoderTest {

    private final RejectPasswordEncoder encoder = new RejectPasswordEncoder();

    @Test
    void encode_shouldReturnReject() {
        assertEquals("REJECT", encoder.encode("password"));
        assertEquals("REJECT", encoder.encode(""));
        assertEquals("REJECT", encoder.encode("any-password"));
    }

    @Test
    void matches_shouldAlwaysReturnFalse() {
        assertFalse(encoder.matches("password", "REJECT"));
        assertFalse(encoder.matches("password", "encoded"));
        assertFalse(encoder.matches("", ""));
        assertFalse(encoder.matches("any", "any"));
    }
}