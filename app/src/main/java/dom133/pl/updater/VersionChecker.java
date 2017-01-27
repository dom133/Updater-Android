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
import java.util.ArrayList;
import java.util.Objects;

public class VersionChecker extends Service {

    private Download download;
    private Cm cm = new Cm();
    private Notifications notifications;
    private Resources res;
    private SharedPreferences sPref;
    private NotificationTask nTask;
    private Addons addons;

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
        addons = new Addons(getApplication());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("INFO", "Service onDestroy");
        nTask.cancel(true);
        startService(new Intent(this, VersionChecker.class));
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
                try {
                    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Update.txt");
                    File update = new File(Environment.getExternalStorageDirectory().getPath() + "/Install.txt");
                    if (update.exists()) {
                        ArrayList<String> zip = new ArrayList<>();
                        zip.add("update.zip"); zip.add("update.zip.md5");
                        zip.addAll(addons.getAddons(0));
                        if(sPref.getBoolean("isChange", false)) {sPref.edit().putBoolean("isChangelog", true).commit();}
                        new File(Environment.getExternalStorageDirectory().getPath() + "/Updater").mkdir();
                        update.delete();
                        file.delete();
                        sPref.edit().putBoolean("isUpdate", false).commit();
                        new File(Environment.getExternalStorageDirectory() + "/Updater/update.zip").delete();
                        new File(Environment.getExternalStorageDirectory() + "/Updater/update.zip.md5").delete();
                        for(int i=0; i<=zip.size()-1; i++) {
                            new File(Environment.getExternalStorageDirectory() + "/Updater/" + zip.get(i)).delete();
                        }
                        sPref.edit().putBoolean("isDownError", false);
                    }

                    //Boolean variables
                    boolean isDownError = sPref.getBoolean("isDownError", false);
                    boolean isUpdate = sPref.getBoolean("isUpdate", false);
                    boolean isFinished = sPref.getBoolean("isFinishedUpdate", false);

                    Log.i("INFO", "isDownError: "+isDownError+" isUpdate: "+isUpdate+" isFinished: "+isFinished);

                    if (!isDownError) { //Check is download error
                        if (!isUpdate) {
                            if(!isFinished) {
                                if (!download.DownloadString(res.getString(R.string.version_url) + "-" + cm.getCMVersion() + ".txt").equals(cm.getProp("ro.cm.version")) && download.DownloadString(res.getString(R.string.version_url) + "-" + cm.getCMVersion() + ".txt") != null && !Objects.equals(download.DownloadString(res.getString(R.string.version_url) + "-" + cm.getCMVersion() + ".txt"), "false")) {
                                    notifications.sendNotification("Updater", res.getString(R.string.version_message), 0);
                                    Log.i("INFO", "ROM " + String.valueOf(sPref.getInt("Time", (1000 * 60) * 30)));
                                    sPref.edit().putBoolean("isDownError", true).commit();
                                    Thread.sleep(sPref.getInt("Time", (1000 * 60) * 30));
                                    sPref.edit().putBoolean("isDownError", false).commit();
                                } else {
                                    Log.i("INFO", "Sleep: " + String.valueOf(sPref.getInt("Time", (1000 * 60))));
                                    Thread.sleep(sPref.getInt("Time", (1000 * 60)));
                                }
                            } else {Log.i("INFO", "isFinishedUpdate"); Thread.sleep(sPref.getInt("Time", (1000 * 60))); sPref.edit().putBoolean("isFinishedUpdate", false).commit();}
                        } else {Log.i("INFO", "isUpdate"); Thread.sleep(sPref.getInt("Time", (1000 * 60)));}
                    } else {Log.i("INFO", "isError");Thread.sleep(sPref.getInt("Time", (1000 * 60))); sPref.edit().putBoolean("isDownError", false).commit();}
                } catch (Exception e) {Log.e("ERROR", e.getMessage());return null;}
            }
            return null;
        }

        @Override
        protected void onCancelled() {super.onCancelled();running=false;}
    }
}