package org.vsynytsyn.storagemanager.security;

import java.time.Duration;

public class JWTSecurityConstants {
    public static final String SECRET = System.getenv("JWTSecret");
    public static final String HEADER_NAME = "Authorization";
    public static final String JWT_TOKEN_PREFIX = "Barrier ";
    public static final Long EXPIRATION_TIME = Duration.ofDays(2).getSeconds();
}
