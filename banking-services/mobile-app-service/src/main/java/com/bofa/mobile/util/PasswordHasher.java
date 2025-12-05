package com.bofa.mobile.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class PasswordHasher {

    private PasswordHasher() {}

    public static String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] salt = "bofa-mobile-salt".getBytes(StandardCharsets.UTF_8);
            digest.update(salt);
            byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
