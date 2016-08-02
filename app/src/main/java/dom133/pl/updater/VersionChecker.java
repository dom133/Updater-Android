package dom133.pl.updater;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

public class VersionChecker extends Service {

    Download download;
    Notifications notifications;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("INFO", "Service onCreate");
        notifications = new Notifications(getApplication());
        download = new Download(getApplication());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("INFO", "Service onCommand");
        new NotificationTask().execute("");
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class NotificationTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            while(true) {
                Log.i("INFO", "Task Dziala");
                try {
                    Thread.sleep(60000);
                    if (!Objects.equals(Build.VERSION.INCREMENTAL, download.DownloadString("https://raw.githubusercontent.com/dom133/Updater-Android-Wersje/master/version.txt"))) {
                        notifications.sendNotification("Updater", "Dostępna jest nowa wersja romu!!!");
                        Thread.sleep(1800000);
                    }
                } catch(java.lang.InterruptedException e) {
                    Log.e("ERROR", e.getMessage());
                }
            }
        }

    }

}
