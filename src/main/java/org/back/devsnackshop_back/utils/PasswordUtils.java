package org.back.devsnackshop_back.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PasswordUtils {
    public static String sha256(String pw, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashed = md.digest(pw.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
