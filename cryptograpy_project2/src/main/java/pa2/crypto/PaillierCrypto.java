package pa2.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class PaillierCrypto {
    public static class PublicKey {
        public final BigInteger n;
        public final BigInteger nSquared;
        public final BigInteger g;

        public PublicKey(BigInteger n, BigInteger g) {
            this.n = n;
            this.nSquared = n.multiply(n);
            this.g = g;
        }
    }

    public static class PrivateKey {
        public final BigInteger lambda;
        public final BigInteger mu;

        public PrivateKey(BigInteger lambda, BigInteger mu) {
            this.lambda = lambda;
            this.mu = mu;
        }
    }

    public static class KeyPair {
        public final PublicKey publicKey;
        public final PrivateKey privateKey;

        public KeyPair(PublicKey publicKey, PrivateKey privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }
    }

    private final SecureRandom random = new SecureRandom();

    public KeyPair generateKeyPair(int bits) {
        BigInteger p = BigInteger.probablePrime(bits / 2, random);
        BigInteger q = BigInteger.probablePrime(bits / 2, random);

        BigInteger n = p.multiply(q);
        BigInteger g = n.add(BigInteger.ONE);

        BigInteger lambda = lcm(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE));
        BigInteger nSquared = n.multiply(n);

        BigInteger l = g.modPow(lambda, nSquared).subtract(BigInteger.ONE).divide(n);
        BigInteger mu = l.modInverse(n);

        return new KeyPair(new PublicKey(n, g), new PrivateKey(lambda, mu));
    }

    public BigInteger encrypt(BigInteger plaintext, PublicKey publicKey) {
        BigInteger r;
        do {
            r = new BigInteger(publicKey.n.bitLength(), random);
        } while (r.compareTo(BigInteger.ZERO) <= 0
                || r.compareTo(publicKey.n) >= 0
                || !r.gcd(publicKey.n).equals(BigInteger.ONE));

        BigInteger a = publicKey.g.modPow(plaintext, publicKey.nSquared);
        BigInteger b = r.modPow(publicKey.n, publicKey.nSquared);

        return a.multiply(b).mod(publicKey.nSquared);
    }

    public BigInteger decrypt(BigInteger ciphertext, PublicKey publicKey, PrivateKey privateKey) {
        BigInteger l = ciphertext.modPow(privateKey.lambda, publicKey.nSquared)
                .subtract(BigInteger.ONE)
                .divide(publicKey.n);
        return l.multiply(privateKey.mu).mod(publicKey.n);
    }

    public BigInteger add(BigInteger encryptedA, BigInteger encryptedB, PublicKey publicKey) {
        return encryptedA.multiply(encryptedB).mod(publicKey.nSquared);
    }

    public BigInteger multiplyByConstant(BigInteger encryptedValue, BigInteger constant, PublicKey publicKey) {
        return encryptedValue.modPow(constant, publicKey.nSquared);
    }

    private static BigInteger lcm(BigInteger a, BigInteger b) {
        return a.multiply(b).divide(a.gcd(b));
    }
}
