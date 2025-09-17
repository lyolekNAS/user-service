package org.sav.fornas.userservice.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sav.fornas.userservice.property.RsaKeyProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.junit.jupiter.api.Assertions.*;

class RsaKeyProviderTest {

    private RsaKeyProperties rsaKeyProperties;
    private RsaKeyProvider rsaKeyProvider;

    private static final String TEST_PRIVATE_KEY = """
            -----BEGIN PRIVATE KEY-----
            MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDoLrEe1qj7knmY
            nvKMDi9H6kY4cElCJKBpaC6WFk3CST8hIDGjD4rMGklIRERecB1Hu35yhN0yiSTj
            9J2FBSw1flofknxKY58RHii2R4dm+Yf+X50KAjD2P2/F9j+OadLIS9J0lxSaPlrW
            NwyVLv9VgJ+g68MXWYja7BygOHXRtcNAVEwNfD2hcfeiRMckn5p7L7RRdHoU8A0B
            VDkxu3HBR1IZHiE/WsY6S7620IqQWqXr2QOjujzhRRwmawqUUvKnN/HNBqy/mlIY
            kk+/jYM3mArN/mH7nZSRGhbRhm8zS0/xPJcPlsodrebbaxyhbqJgaHsayDte+ehj
            maBnUbqdAgMBAAECggEAFNgIP5/+yKL0Np7iYG9vkDG4fx2Zfg4itoPLVt8GuQ/x
            Umx8+EoPaFZdyNaUGrRO9J8g8W/7GavVO3+82nHF78F5nsb8UINUBunhtseCINz6
            9/umcf0TXy//vVJnj0Xxzdh8usnLaDN73SpQ/12aP5dN/VjdCU4Y6qpywXGAJkTS
            w2GYOW5PRHIam3H85efLAWrH+r8Yd6D8VwhRwza1UTIDCaV/o4lTYjyZnDFDkW0D
            fp+8nlomdDRuX8krXwEhat3OqnbH0RCtRIKYjIfTNy0CUioRL8l5ngdBztmIfpxY
            I7F85ZbhMGPI7g2Qg4JDsAX2DpRlPoT1YgE25YT9wQKBgQD+hxKZc8xYRe6o4dM3
            lx7za5ra/rZEyne7mwLOg+ZNz2JoLpiK5zWjh1KbjXoU8NP/aigu6AQF4mAojByE
            s9xnGuojkfLRvIEFK7TXy/maDR6pF82bS8hYR1N7sAxEj46jBwae6feJXXH3MSY2
            eVxBRYxDEgsV1c+DjCiCDhF8PQKBgQDphodEmiR59b699wvhk5uL7Btpq+UTj0kW
            a0V7UhyQLxm/ko/M0bfARfpA6R7GcAICT1Z9k2iOIkCgfH/bshn6ToeQ0wAWzbt0
            E9Xqr5MjfjlaySqanbQP6ALKHlnuadSH12iGma1RGXo1ghYknTbmEEfMMGbT0yt8
            Z5delU094QKBgQCqnqkdLL7ivZ5Kn3cTYL5ittNL0kskugShQjwTa9/KcSaqZ/Hh
            yzPttxztIV2hA/9YzDS7nNR1jVagV6wWWOt6QlSKG6AGcLKcYLwNnLJpTjorZ8Vw
            QNDAPK/9zfmrTAB10JPjmztQXc+zwMK46YmqR5DKtSGxNW6x5BaTeBLIkQKBgB8g
            BiF3pgr3XR/2sIe1Y9MHejPgbFzqCSTK0Wz5acMx8+2bvDzhauteqFdEewyZ2zqx
            lmaQYiwnlpONZIFEwL+i/eCorudbLnPvPODV2/esGDroexw2jsxb8Rs9ZmEni+M7
            K7xc+UPI8If3ObpRlgKeAuPNQ7dR+wbOy9cYcT4hAoGBANthA+QTVwfKkQqyy+eL
            H2hqfrq1RAd3y6E0joIT5CTIWDgal6i9lp4FpPsWNVKb3x6wF5k/MX926HxkVoHj
            q4mB/H+Sjz5K9WxzI/0IeKLREW3Z6osMK6enG/qhkCkKsX5E+HhRBN2SRHQn2kP4
            gLWYaCFSElkVOj7M7BE62okg
            -----END PRIVATE KEY-----
            """;

    private static final String TEST_PUBLIC_KEY = """
            -----BEGIN PUBLIC KEY-----
            MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6C6xHtao+5J5mJ7yjA4v
            R+pGOHBJQiSgaWgulhZNwkk/ISAxow+KzBpJSEREXnAdR7t+coTdMokk4/SdhQUs
            NX5aH5J8SmOfER4otkeHZvmH/l+dCgIw9j9vxfY/jmnSyEvSdJcUmj5a1jcMlS7/
            VYCfoOvDF1mI2uwcoDh10bXDQFRMDXw9oXH3okTHJJ+aey+0UXR6FPANAVQ5Mbtx
            wUdSGR4hP1rGOku+ttCKkFql69kDo7o84UUcJmsKlFLypzfxzQasv5pSGJJPv42D
            N5gKzf5h+52UkRoW0YZvM0tP8TyXD5bKHa3m22scoW6iYGh7Gsg7XvnoY5mgZ1G6
            nQIDAQAB
            -----END PUBLIC KEY-----
            """;

    private static final String INVALID_KEY = "invalid-key";

    @BeforeEach
    void setUp() {
        rsaKeyProperties = new RsaKeyProperties();
        rsaKeyProperties.setPrivateKey(TEST_PRIVATE_KEY);
        rsaKeyProperties.setPublicKey(TEST_PUBLIC_KEY);

        rsaKeyProvider = new RsaKeyProvider(rsaKeyProperties);
        rsaKeyProvider.init();
    }

    @Test
    void init_ShouldLoadKeys() {
        RSAPrivateKey privateKey = rsaKeyProvider.getPrivateKey();
        RSAPublicKey publicKey = rsaKeyProvider.getPublicKey();

        assertNotNull(privateKey, "Private key should not be null");
        assertNotNull(publicKey, "Public key should not be null");
    }

    @Test
    void readKey_ShouldDecodeBase64Correctly() {
        byte[] decoded = rsaKeyProvider.readKey(TEST_PRIVATE_KEY);
        assertNotNull(decoded);
        assertTrue(decoded.length > 0);
    }

    @Test
    void loadPrivateKey_WithInvalidKey_ShouldThrow() {
        rsaKeyProperties.setPrivateKey(INVALID_KEY);
        RsaKeyProvider provider = new RsaKeyProvider(rsaKeyProperties);

        Exception ex = assertThrows(IllegalStateException.class, provider::init);
        assertEquals("Can't load private key", ex.getMessage());
    }

    @Test
    void loadPublicKey_WithInvalidKey_ShouldThrow() {
        rsaKeyProperties.setPublicKey(INVALID_KEY);
        RsaKeyProvider provider = new RsaKeyProvider(rsaKeyProperties);

        Exception ex = assertThrows(IllegalStateException.class, provider::init);
        assertEquals("Can't load public key", ex.getMessage());
    }
}