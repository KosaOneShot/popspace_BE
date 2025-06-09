package org.example.popspace.util.auth;

public class RandomStringUtil {

    public static String generateRandomCode() {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<6; i++) {
            builder.append((int)(Math.random()*9+1));
        }
        return builder.toString();
    }

    public static String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = (int)(Math.random() * chars.length());
            builder.append(chars.charAt(index));
        }
        return builder.toString();
    }
}
