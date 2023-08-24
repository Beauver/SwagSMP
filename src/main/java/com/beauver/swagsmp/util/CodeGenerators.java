package com.beauver.swagsmp.util;

import java.security.SecureRandom;

public class CodeGenerators {

    public static String codeEight() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int codeLength = 8;

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(codeLength);

        for (int i = 0; i < codeLength; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
    public static String codeTwelve() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int codeLength = 12;

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(codeLength);

        for (int i = 0; i < codeLength; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
