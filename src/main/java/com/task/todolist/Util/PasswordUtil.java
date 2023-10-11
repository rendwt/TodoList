package com.task.todolist.Util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class PasswordUtil {
    public static String generateRandomSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return bytesToHex(salt);
    }

    public static String hashPassword(String password, String salt) {
        try {
            String saltedPassword = password + salt;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexStringBuilder = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexStringBuilder.append(String.format("%02x", b));
        }
        return hexStringBuilder.toString();
    }
}
