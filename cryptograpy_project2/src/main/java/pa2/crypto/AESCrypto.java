package pa2.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public final class AESCrypto {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_SIZE_BITS = 256;
    private static final int IV_SIZE_BYTES = 12;
    private static final int TAG_SIZE_BITS = 128;

    private AESCrypto() {}

    public static SecretKey generateKey() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(KEY_SIZE_BITS);
        return generator.generateKey();
    }

    public static String encrypt(String plaintext, SecretKey key) throws Exception {
        if (plaintext == null) plaintext = "";

        byte[] iv = new byte[IV_SIZE_BYTES];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_SIZE_BITS, iv));

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] output = new byte[iv.length + ciphertext.length];

        System.arraycopy(iv, 0, output, 0, iv.length);
        System.arraycopy(ciphertext, 0, output, iv.length, ciphertext.length);

        return Base64.getEncoder().encodeToString(output);
    }

    public static String decrypt(String encryptedBase64, SecretKey key) throws Exception {
        byte[] input = Base64.getDecoder().decode(encryptedBase64);

        byte[] iv = Arrays.copyOfRange(input, 0, IV_SIZE_BYTES);
        byte[] ciphertext = Arrays.copyOfRange(input, IV_SIZE_BYTES, input.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_SIZE_BITS, iv));

        return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
    }
}
