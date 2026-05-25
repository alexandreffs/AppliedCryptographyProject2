package pa2.crypto;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public final class ECDSAUtils {
    private ECDSAUtils() {}

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(256);
        return generator.generateKeyPair();
    }

    public static String sign(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    public static boolean verify(String data, String signatureBase64, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initVerify(publicKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return signature.verify(Base64.getDecoder().decode(signatureBase64));
    }
}
