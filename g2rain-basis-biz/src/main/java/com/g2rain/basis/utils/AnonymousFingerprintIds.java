package com.g2rain.basis.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 根据客户端指纹稳定派生匿名会话的 passportId / userId。
 */
public final class AnonymousFingerprintIds {

    private static final String PASSPORT_NAMESPACE = "|passport";
    private static final String USER_NAMESPACE = "|user";

    private AnonymousFingerprintIds() {
    }

    public static long derivePassportId(String fingerprint) {
        return deriveId(fingerprint, PASSPORT_NAMESPACE);
    }

    public static long deriveUserId(String fingerprint) {
        return deriveId(fingerprint, USER_NAMESPACE);
    }

    private static long deriveId(String fingerprint, String namespace) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((fingerprint + namespace).getBytes(StandardCharsets.UTF_8));
            long id = 0L;
            for (int i = 0; i < 8; i++) {
                id = (id << 8) | (hash[i] & 0xffL);
            }
            id &= Long.MAX_VALUE;
            return id == 0L ? 1L : id;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
