package dom133.pl.updater;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
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
    SharedPreferences sPref;
    private NotificationTask nTask;

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
        nTask = new NotificationTask();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("INFO", "Service onDestroy");
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("INFO", "Service onCommand");
        if(intent!=null && Objects.equals(intent.getAction(), "ACTION_STOP")) {stopSelf(); nTask.cancel(true); return Service.START_NOT_STICKY;}
        else {
            try {
                nTask.execute("");
            } catch(Exception e) {Log.e("ERROR", e.getMessage());}
            return Service.START_STICKY;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class NotificationTask extends AsyncTask<String, Void, String> {

        public  boolean running = true;

        protected String doInBackground(String... params) {
            while(!isCancelled()) {
                File file = new File(Environment.getExternalStorageDirectory().getPath()+"/Update.txt");
                File update = new File(Environment.getExternalStorageDirectory().getPath()+"/Install.txt");
                if(update.exists()) {
                    update.delete();
                    file.delete();
                    sPref.edit().putBoolean("isUpdate", false).commit();
                    new File(Environment.getExternalStorageDirectory()+"/update.zip").delete();
                    new File(Environment.getExternalStorageDirectory()+"/update.zip.md5").delete();
                    new File(Environment.getExternalStorageDirectory().getPath()+"/supersu.zip").delete();
                    new File(Environment.getExternalStorageDirectory().getPath()+"/xposed.zip").delete();
                    new File(Environment.getExternalStorageDirectory().getPath()+"/gapps.zip").delete();
                }

                try {
                    if(!sPref.getBoolean("isUpdate", false)) {
                        if (!download.DownloadString(res.getString(R.string.version_url)).equals(download.getProp("ro.cm.version")) && download.DownloadString(res.getString(R.string.version_url))!=null) {
                            notifications.sendNotification("Updater", res.getString(R.string.version_message), 0);
                            Log.i("INFO", "ROM "+String.valueOf(sPref.getInt("Actu", (1000*60)*30)));
                            Thread.sleep(sPref.getInt("Actu", (1000*60)*30));
                        } else {
                            Log.i("INFO", "Sleep: "+String.valueOf(sPref.getInt("Time", (1000*60))));
                            Thread.sleep(sPref.getInt("Time", (1000*60)));
                        }
                    } else {Log.i("INFO", "File exist");}
                } catch(java.lang.InterruptedException e) {
                    Log.e("ERROR", e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            running=false;
        }
    }

}
