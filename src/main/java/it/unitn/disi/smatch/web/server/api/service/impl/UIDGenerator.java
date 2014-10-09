package it.unitn.disi.smatch.web.server.api.service.impl;

import java.security.SecureRandom;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class UIDGenerator {

    private static final char[] DEFAULT_CODEC = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int UID_LENGTH = 8;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public UIDGenerator() {
    }

    public static String getUID() {
        //lifted from RandomValueVerifierServices.java
        byte[] verifierBytes = new byte[UID_LENGTH];
        SECURE_RANDOM.nextBytes(verifierBytes);

        char[] chars = new char[verifierBytes.length];
        for (int i = 0; i < verifierBytes.length; i++) {
            chars[i] = DEFAULT_CODEC[((verifierBytes[i] & 0xFF) % DEFAULT_CODEC.length)];
        }
        return new String(chars);
    }

    public static String getUID(int length) {
        //lifted from RandomValueVerifierServices.java
        byte[] verifierBytes = new byte[length];
        SECURE_RANDOM.nextBytes(verifierBytes);

        char[] chars = new char[verifierBytes.length];
        for (int i = 0; i < verifierBytes.length; i++) {
            chars[i] = DEFAULT_CODEC[((verifierBytes[i] & 0xFF) % DEFAULT_CODEC.length)];
        }
        return new String(chars);
    }
}