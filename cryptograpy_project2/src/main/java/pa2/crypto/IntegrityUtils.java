package pa2.crypto;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public final class IntegrityUtils {
    private IntegrityUtils() {}

    public static String hmac(String data, SecretKey key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);
        byte[] digest = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(digest);
    }

    public static boolean verify(String data, String expectedBase64, SecretKey key) throws Exception {
        String actual = hmac(data, key);
        return MessageDigest.isEqual(
                actual.getBytes(StandardCharsets.UTF_8),
                expectedBase64.getBytes(StandardCharsets.UTF_8)
        );
    }
}
