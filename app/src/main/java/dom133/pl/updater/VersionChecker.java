package dom133.pl.updater;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

public class VersionChecker extends Service {

    Download download;
    Notifications notifications;
    Resources res;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("INFO", "Service onCreate");
        notifications = new Notifications(getApplication());
        download = new Download(getApplication());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        res = getResources();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("INFO", "Service onDestroy");
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
                File file = new File(Environment.getExternalStorageDirectory().getPath()+"/Update.txt");
                File update = new File(Environment.getExternalStorageDirectory().getPath()+"/Install.txt");
                if(update.exists()) {
                    update.delete();
                    new File(Environment.getExternalStorageDirectory()+"/update.zip").delete();
                    new File(Environment.getExternalStorageDirectory().getPath()+"/supersu.zip").delete();
                    new File(Environment.getExternalStorageDirectory().getPath()+"/xposed.zip").delete();
                }
                try {
                    Log.i("INFO", "1");
                    Thread.sleep(60000);
                    if(!file.exists()) {
                        if (!Objects.equals(Build.VERSION.INCREMENTAL, download.DownloadString(res.getString(R.string.version_url)))) {
                            notifications.sendNotification("Updater", res.getString(R.string.version_message), 0);
                            Log.i("INFO", "30");
                            Thread.sleep(1800000);
                        }
                    } else {Log.i("INFO", "File exist");}
                } catch(java.lang.InterruptedException e) {
                    Log.e("ERROR", e.getMessage());
                }
            }
        }

    }

}
