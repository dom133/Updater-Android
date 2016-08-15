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

import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.Objects;

public class VersionChecker extends Service {

    Download download;
    Notifications notifications;
    Resources res;
    SharedPreferences sPref;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("INFO", "Service onCreate");
        notifications = new Notifications(getApplication());
        download = new Download(getApplication());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        res = getResources();
        sPref = getSharedPreferences("Updater", Context.MODE_PRIVATE);
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
                    file.delete();
                    new File(Environment.getExternalStorageDirectory()+"/update.zip").delete();
                    new File(Environment.getExternalStorageDirectory().getPath()+"/supersu.zip").delete();
                    new File(Environment.getExternalStorageDirectory().getPath()+"/xposed.zip").delete();
                    new File(Environment.getExternalStorageDirectory().getPath()+"/gapps.zip").delete();
                }

                try {
                    if(!file.exists()) {
                        if (!Objects.equals(Build.VERSION.INCREMENTAL, download.DownloadString(res.getString(R.string.version_url))) && download.DownloadString(res.getString(R.string.version_url))!=null) {
                            notifications.sendNotification("Updater", res.getString(R.string.version_message), 0);
                            Log.i("INFO", "ROM "+String.valueOf(sPref.getInt("Actu", (1000*60)*30)));
                            Thread.sleep(sPref.getInt("Actu", (1000*60)*30));
                        } else if(!Objects.equals(BuildConfig.VERSION_NAME, download.DownloadString(res.getString(R.string.app_version_link))) && download.DownloadString(res.getString(R.string.app_version_link))!=null) {
                            notifications.sendNotification("Updater", res.getString(R.string.app_message), 2);
                            Log.i("INFO", "App "+String.valueOf(sPref.getInt("Actu", (1000*60)*30)));
                            Thread.sleep(sPref.getInt("Actu", (1000*60)*30));
                        }
                    } else {Log.i("INFO", "File exist");}
                    Log.i("INFO", "Sleep: "+String.valueOf(sPref.getInt("Time", (1000*60))));
                    Thread.sleep(sPref.getInt("Time", (1000*60)));
                } catch(java.lang.InterruptedException e) {
                    Log.e("ERROR", e.getMessage());
                }
            }
        }

    }

}
