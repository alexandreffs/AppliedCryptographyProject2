package pa2.crypto;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Base64;

public final class HMACIndex {
    private HMACIndex() {}

    public static String token(String value, SecretKey key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);
        byte[] digest = mac.doFinal(normalize(value).getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(digest);
    }

    public static String normalize(String value) {
        if (value == null) return "";
        String normalized = Normalizer.normalize(value.trim().toLowerCase(), Normalizer.Form.NFKC);
        return normalized;
    }
}
