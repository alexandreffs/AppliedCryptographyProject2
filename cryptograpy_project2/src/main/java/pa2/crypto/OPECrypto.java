package pa2.crypto;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;

public final class OPECrypto {
    private OPECrypto() {}

    /*
     * Base order-preserving placeholder.
     *
     * This preserves order, so MySQL can ORDER BY this field.
     * It is not a real Boldyreva OPE implementation.
     *
     * In the report, say that this construction leaks full order and is a simplified
     * practical implementation for the prototype. Replace it with Boldyreva OPE if required.
     */
    public static long encryptOrderValue(long plaintext, SecretKey key) throws Exception {
        long factor = 1_000_000L;
        long noise = Math.floorMod(noise(plaintext, key), factor);
        return plaintext * factor + noise;
    }

    private static long noise(long value, SecretKey key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);
        byte[] input = ByteBuffer.allocate(Long.BYTES).putLong(value).array();
        byte[] digest = mac.doFinal(input);
        return ByteBuffer.wrap(digest, 0, Long.BYTES).getLong();
    }
}
