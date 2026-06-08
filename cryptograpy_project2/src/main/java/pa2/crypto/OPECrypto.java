package pa2.crypto;

import java.util.TreeMap;

public class OPECrypto {

    // plaintext -> ciphertext
    private final TreeMap<Integer, Long> plaintextIndex = new TreeMap<>();

    // ciphertext -> plaintext
    private final TreeMap<Long, Integer> ciphertextIndex = new TreeMap<>();

    // Initial spacing between ciphertexts
    private static final long GAP = 1_000_000L;

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

    // don't rly need because it is also stored under AES
    public int decrypt(long ciphertext) {
        Integer value = ciphertextIndex.get(ciphertext);

        if (value == null) {
            throw new IllegalArgumentException("Unknown ciphertext");
        }

        return value;
    }
}
