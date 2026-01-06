package com.SecureChatApp.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

public final class PushTokenCrypto {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int NONCE_LEN = 12;
    private static final int TAG_LEN = 128; // bits

    public static String sha256Hex(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(value.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : dig) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static Encrypted encrypt(String plaintext, SecretKey key) {
        try {
            byte[] nonce = new byte[NONCE_LEN];
            RANDOM.nextBytes(nonce);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LEN, nonce));
            byte[] cipherTextWithTag = cipher.doFinal(plaintext.getBytes());

            int tagPos = cipherTextWithTag.length - 16;
            byte[] ciphertext = Arrays.copyOfRange(cipherTextWithTag, 0, tagPos);
            byte[] tag = Arrays.copyOfRange(cipherTextWithTag, tagPos, cipherTextWithTag.length);

            return new Encrypted(ciphertext, nonce, tag);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public record Encrypted(byte[] ciphertext, byte[] nonce, byte[] tag) {}
}
