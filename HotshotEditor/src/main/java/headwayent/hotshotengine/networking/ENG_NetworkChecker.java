package headwayent.hotshotengine.networking;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class ENG_NetworkChecker {

    private static final String NET_CHECK_URL = "http://127.0.0.1";//"http://www.google.com";
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AtomicBoolean networkAvailable = new AtomicBoolean(true);
    private final Checker checker = new Checker();
    private ScheduledFuture<?> schedule;
    private final ReentrantLock lock = new ReentrantLock();
    private boolean monitoringActive;

    private class Checker implements Runnable {

        @Override
        public void run() {
            networkAvailable.set(true);
            // TODO update this for iOS and Android.
//            HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
//            Net.HttpRequest httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url(NET_CHECK_URL).build();
//            httpRequest.setTimeOut(3000);
//            Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
//                @Override
//                public void handleHttpResponse(Net.HttpResponse httpResponse) {
//                    HttpStatus status = httpResponse.getStatus();
//                    networkAvailable.set(true);
//                }
//
//                @Override
//                public void failed(Throwable t) {
//                    networkAvailable.set(false);
//                }
//
//                @Override
//                public void cancelled() {
//
//                }
//            });
        }
    }

    public void startMonitoringNetworkStatus() {
        lock.lock();
        try {
            if (!monitoringActive) {
                schedule = scheduler.scheduleAtFixedRate(checker, 0, 60, TimeUnit.SECONDS);
                monitoringActive = true;
            }
        } finally {
            lock.unlock();
        }
    }

    public void stopMonitoringNetworkStatus() {
        lock.lock();
        try {
            if (monitoringActive) {
                schedule.cancel(false);
                monitoringActive = false;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean isNetworkAvailable() {
        return networkAvailable.get();
    }

    public boolean isMonitoringActive() {
        lock.lock();
        try {
            return monitoringActive;
        } finally {
            lock.unlock();
        }
    }
}
