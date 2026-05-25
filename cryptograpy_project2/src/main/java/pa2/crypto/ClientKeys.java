package pa2.crypto;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Properties;

public class ClientKeys {
    public SecretKey aesKey;
    public SecretKey indexKey;
    public SecretKey integrityKey;
    public SecretKey opeKey;

    public PaillierCrypto.PublicKey paillierPublicKey;
    public PaillierCrypto.PrivateKey paillierPrivateKey;

    public KeyPair ecdsaKeyPair;

    public static ClientKeys loadOrCreate(Path keyFile) throws Exception {
        if (Files.exists(keyFile)) {
            return load(keyFile);
        }

        ClientKeys keys = create();
        save(keys, keyFile);
        return keys;
    }

    public static ClientKeys create() throws Exception {
        ClientKeys keys = new ClientKeys();

        keys.aesKey = AESCrypto.generateKey();
        keys.indexKey = randomHmacKey();
        keys.integrityKey = randomHmacKey();
        keys.opeKey = randomHmacKey();

        PaillierCrypto paillier = new PaillierCrypto();
        PaillierCrypto.KeyPair paillierKeys = paillier.generateKeyPair(2048);
        keys.paillierPublicKey = paillierKeys.publicKey;
        keys.paillierPrivateKey = paillierKeys.privateKey;

        keys.ecdsaKeyPair = ECDSAUtils.generateKeyPair();

        return keys;
    }

    private static SecretKey randomHmacKey() {
        byte[] key = new byte[32];
        new java.security.SecureRandom().nextBytes(key);
        return new SecretKeySpec(key, "HmacSHA256");
    }

    public static void save(ClientKeys keys, Path keyFile) throws Exception {
        Files.createDirectories(keyFile.getParent());

        Properties p = new Properties();

        p.setProperty("aesKey", b64(keys.aesKey.getEncoded()));
        p.setProperty("indexKey", b64(keys.indexKey.getEncoded()));
        p.setProperty("integrityKey", b64(keys.integrityKey.getEncoded()));
        p.setProperty("opeKey", b64(keys.opeKey.getEncoded()));

        p.setProperty("paillier.n", keys.paillierPublicKey.n.toString());
        p.setProperty("paillier.g", keys.paillierPublicKey.g.toString());
        p.setProperty("paillier.lambda", keys.paillierPrivateKey.lambda.toString());
        p.setProperty("paillier.mu", keys.paillierPrivateKey.mu.toString());

        p.setProperty("ecdsa.public", b64(keys.ecdsaKeyPair.getPublic().getEncoded()));
        p.setProperty("ecdsa.private", b64(keys.ecdsaKeyPair.getPrivate().getEncoded()));

        try (OutputStream out = Files.newOutputStream(keyFile)) {
            p.store(out, "Client-side keys. Do not upload this file to the server.");
        }
    }

    public static ClientKeys load(Path keyFile) throws Exception {
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(keyFile)) {
            p.load(in);
        }

        ClientKeys keys = new ClientKeys();

        keys.aesKey = new SecretKeySpec(fromB64(p.getProperty("aesKey")), "AES");
        keys.indexKey = new SecretKeySpec(fromB64(p.getProperty("indexKey")), "HmacSHA256");
        keys.integrityKey = new SecretKeySpec(fromB64(p.getProperty("integrityKey")), "HmacSHA256");
        keys.opeKey = new SecretKeySpec(fromB64(p.getProperty("opeKey")), "HmacSHA256");

        BigInteger n = new BigInteger(p.getProperty("paillier.n"));
        BigInteger g = new BigInteger(p.getProperty("paillier.g"));
        BigInteger lambda = new BigInteger(p.getProperty("paillier.lambda"));
        BigInteger mu = new BigInteger(p.getProperty("paillier.mu"));

        keys.paillierPublicKey = new PaillierCrypto.PublicKey(n, g);
        keys.paillierPrivateKey = new PaillierCrypto.PrivateKey(lambda, mu);

        KeyFactory kf = KeyFactory.getInstance("EC");
        PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(fromB64(p.getProperty("ecdsa.public"))));
        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(fromB64(p.getProperty("ecdsa.private"))));

        keys.ecdsaKeyPair = new KeyPair(publicKey, privateKey);

        return keys;
    }

    private static String b64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static byte[] fromB64(String value) {
        return Base64.getDecoder().decode(value);
    }
}
