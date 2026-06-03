package pa2.crypto;

import java.util.TreeMap;

/**
 * Mutable Order-Preserving Encryption demo based on the teacher's example.
 *
 * plaintext -> ciphertext
 * ciphertext -> plaintext
 *
 * This preserves order:
 * if a < b, then Enc(a) < Enc(b)
 *
 * Important:
 * This is suitable for the class prototype and for supporting ORDER BY queries.
 * It leaks the relative order of the encrypted values.
 */
public class OPECrypto {

    /*
     * plaintext -> ciphertext
     */
    private final TreeMap<Integer, Long> plaintextIndex = new TreeMap<>();

    /*
     * ciphertext -> plaintext
     */
    private final TreeMap<Long, Integer> ciphertextIndex = new TreeMap<>();

    /*
     * Initial spacing between ciphertexts
     */
    private static final long GAP = 1_000_000L;

    /*
     * Encrypt integer while preserving order.
     */
    public long encrypt(int value) {
        if (plaintextIndex.containsKey(value)) {
            return plaintextIndex.get(value);
        }

        long ciphertext;

        if (plaintextIndex.isEmpty()) {
            ciphertext = GAP;
        } else {
            Integer lower = plaintextIndex.lowerKey(value);
            Integer higher = plaintextIndex.higherKey(value);

            if (lower == null) {
                ciphertext = plaintextIndex.get(higher) / 2;
            } else if (higher == null) {
                ciphertext = plaintextIndex.get(lower) + GAP;
            } else {
                long lowCipher = plaintextIndex.get(lower);
                long highCipher = plaintextIndex.get(higher);

                ciphertext = (lowCipher + highCipher) / 2;

                if (ciphertext == lowCipher || ciphertext == highCipher) {
                    throw new RuntimeException("Ciphertext space exhausted");
                }
            }
        }

        plaintextIndex.put(value, ciphertext);
        ciphertextIndex.put(ciphertext, value);

        return ciphertext;
    }

    /*
     * Decrypt OPE ciphertext.
     *
     * In this project, the client usually does not need this because the real
     * value is stored separately under AES. The server only uses OPE values
     * for ORDER BY operations.
     */
    public int decrypt(long ciphertext) {
        Integer value = ciphertextIndex.get(ciphertext);

        if (value == null) {
            throw new IllegalArgumentException("Unknown ciphertext");
        }

        return value;
    }
}

// package pa2.crypto;

// import javax.crypto.Mac;
// import javax.crypto.SecretKey;
// import java.nio.ByteBuffer;

// public final class OPECrypto {
// private OPECrypto() {}

// /*
// * Base order-preserving placeholder.
// *
// * This preserves order, so MySQL can ORDER BY this field.
// * It is not a real Boldyreva OPE implementation.
// *
// * In the report, say that this construction leaks full order and is a
// simplified
// * practical implementation for the prototype. Replace it with Boldyreva OPE
// if required.
// */
// public static long encryptOrderValue(long plaintext, SecretKey key) throws
// Exception {
// long factor = 1_000_000L;
// long noise = Math.floorMod(noise(plaintext, key), factor);
// return plaintext * factor + noise;
// }

// private static long noise(long value, SecretKey key) throws Exception {
// Mac mac = Mac.getInstance("HmacSHA256");
// mac.init(key);
// byte[] input = ByteBuffer.allocate(Long.BYTES).putLong(value).array();
// byte[] digest = mac.doFinal(input);
// return ByteBuffer.wrap(digest, 0, Long.BYTES).getLong();
// }
// }
