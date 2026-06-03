package pa2.client;

public final class CryptoMetrics {

    public static long aesTimeNs = 0;
    public static long hmacIndexTimeNs = 0;
    public static long paillierTimeNs = 0;
    public static long opeTimeNs = 0;
    public static long integrityHmacTimeNs = 0;
    public static long ecdsaTimeNs = 0;

    private CryptoMetrics() {
    }

    public static void print() {
        System.out.println();
        System.out.println("============== Encryption Algorithm Timings ==============");
        System.out.println("AES encryption total:          " + nsToMs(aesTimeNs) + " ms");
        System.out.println("HMAC index generation total:   " + nsToMs(hmacIndexTimeNs) + " ms");
        System.out.println("Paillier encryption total:     " + nsToMs(paillierTimeNs) + " ms");
        System.out.println("OPE encryption total:          " + nsToMs(opeTimeNs) + " ms");
        System.out.println("Record HMAC total:             " + nsToMs(integrityHmacTimeNs) + " ms");
        System.out.println("ECDSA signature total:         " + nsToMs(ecdsaTimeNs) + " ms");
        System.out.println("===========================================================");
    }

    private static long nsToMs(long ns) {
        return ns / 1_000_000;
    }
}