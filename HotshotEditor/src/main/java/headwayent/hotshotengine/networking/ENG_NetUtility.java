package headwayent.hotshotengine.networking;

public class ENG_NetUtility {

    private static final ENG_NetworkChecker networkChecker = new ENG_NetworkChecker();

    public static boolean isNetworkAvailable() {
        networkChecker.startMonitoringNetworkStatus();
        return networkChecker.isNetworkAvailable();
    }

    public static void stopMonitoringNetworkAvailability() {
        networkChecker.stopMonitoringNetworkStatus();
    }
}
