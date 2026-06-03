package pa2.client;

public final class PerformanceUtils {

    private PerformanceUtils() {
    }

    public static long now() {
        return System.nanoTime();
    }

    public static long elapsedMs(long startNano) {
        return (System.nanoTime() - startNano) / 1_000_000;
    }

    public static void printLatency(String operationName, long startNano) {
        System.out.println("Latency for " + operationName + ": " + elapsedMs(startNano) + " ms");
    }

    public static void printSection(String title) {
        System.out.println();
        System.out.println("----------------------------------------------------");
        System.out.println(title);
        System.out.println("----------------------------------------------------");
    }
}