package dom133.pl.updater;

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
import android.widget.Button;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

public class DownloadService extends Service {

    private Cm cm = new Cm();
    private Notifications notifications;
    private Resources res;
    private Download download;
    private SharedPreferences sPref;
    private DownloadFile downloadFile;
    boolean isCancled = false;

    public DownloadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isCancled = false;
        Log.i("INFO", "Download service onCreate");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        notifications = new Notifications(getApplication());
        res = getResources();
        download = new Download(getApplication());
        downloadFile = new DownloadFile();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        sPref = getSharedPreferences("Updater", Context.MODE_PRIVATE);
        Log.i("INFO", "Download service onCommand");
        if(intent!=null && Objects.equals(intent.getAction(), "ACTION_STOP_SERVICE")) {Log.i("INFO", "Service is Stoped");stopSelf(); isCancled=true; downloadFile.cancel(true); return START_STICKY;}
        else {
            try {
                notifications.sendNotificationDownload("Updater", "", 0, true, 0);
                new File(Environment.getExternalStorageDirectory().getPath() + "/Update.txt").createNewFile();

                if (downloadFile.running) {downloadFile.cancel(true);}

                sPref.edit().putBoolean("isUpdate", true).commit();
                stopService(new Intent(getApplicationContext(), VersionChecker.class));

                return START_STICKY;
            } catch(Exception e) {Log.e("ERROR", e.getMessage());return START_STICKY;}
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("INFO", "Download service stopped");
        File file = new File(Environment.getExternalStorageDirectory().getPath()+"/Update.txt");
        if(file.exists()) {Log.i("INFO", "File deleted"); file.delete();}
        if(isCancled) {notifications.sendNotification("Updater", res.getString(R.string.cancle_message), 0);sPref.edit().putBoolean("isDownError", true);}
        sPref.edit().putBoolean("isUpdate", false).commit();
        downloadFile.cancel(true);
        sPref = null;
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class DownloadFile extends AsyncTask<String, Integer, String> {
        private volatile boolean running = false;
        int progress = 0;
        int downloaded = 0;

        @Override
        protected String doInBackground(String... f_url) {
            sPref.edit().putBoolean("isUpdate", true).commit();
            while(!isCancelled()) {
                running=true;
                int count;
                int files = 1;
                int number = f_url.length;
                int[] get = new int[10];
                int[] url_i = new int[10];
                Log.i("INFO", "Numbers: " + number);
                try {
                    switch (number) {
                        case 4: {
                            url_i[0] = 0;
                            url_i[1] = 2;
                            get[0] = 1;
                            get[1] = 3;
                            break;
                        }
                        case 6: {
                            url_i[0] = 0;
                            url_i[1] = 2;
                            url_i[2] = 4;
                            get[0] = 1;
                            get[1] = 3;
                            get[2] = 5;
                            break;
                        }
                        case 8: {
                            url_i[0] = 0;
                            url_i[1] = 2;
                            url_i[2] = 4;
                            url_i[3] = 6;
                            get[0] = 1;
                            get[1] = 3;
                            get[2] = 5;
                            get[3] = 7;
                            break;
                        }
                        case 10: {
                            url_i[0] = 0;
                            url_i[1] = 2;
                            url_i[2] = 4;
                            url_i[3] = 6;
                            url_i[4] = 8;
                            get[0] = 1;
                            get[1] = 3;
                            get[2] = 5;
                            get[3] = 7;
                            get[4] = 9;
                            break;
                        }
                    }

                    while (!isCancelled()) {
                        notifications.sendNotificationDownload("Updater", "", 0, true, 0);
                        File file = null;
                        if(sPref.getInt("Memory", 0)==0) {file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + f_url[get[files - 1]]);}
                        else {file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + f_url[get[files - 1]]);}

                        if (file.exists()) {
                            Log.i("INFO", "File deleted");
                            file.delete();
                        }

                        Log.i("INFO", "File: " + file.getPath());

                        URL url = new URL(f_url[url_i[files - 1]]);
                        URLConnection connection = url.openConnection();
                        connection.connect();
                        int lenghtoffile = connection.getContentLength();


                        InputStream input = new BufferedInputStream(url.openStream(), 8192);
                        OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/" + f_url[get[files - 1]]);

                        byte data[] = new byte[54 * 1024];
                        long total = 0;

                        notifications.sendNotificationDownload("Updater", "Pobrano: 0% 0MB/" + (lenghtoffile/1048576) + "MB", 0, false, 0);

                        while ((count = input.read(data)) != -1 && !isCancelled()) {
                            total += count;
                            publishProgress((int) ((total * 100) / lenghtoffile), (int) (total / 1048576), (int) (lenghtoffile / 1048576));
                            output.write(data, 0, count);
                        }

                        output.flush();
                        input.close();
                        output.close();
                        if (number == 2) {
                            if (files == 1) break;
                        } else if (number == 4) {
                            if (files == 2) break;
                        } else if (number == 6) {
                            if (files == 3) break;
                        }
                        files++;
                    }
                    return "OK";


                } catch (Exception e) {
                    Log.i("ERROR", e.getMessage());
                    sPref.edit().putBoolean("isDownError", true);
                    if(!isCancled)notifications.sendNotification("Updater", res.getString(R.string.download_incomplete), 0);
                    stopService(new Intent(getApplicationContext(), VersionChecker.class));
                    stopSelf();
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progres) {
            super.onProgressUpdate(progres);
            if(progress!=progres[0]) {notifications.sendNotificationDownload("Updater", "Pobrano: "+progres[0]+"% "+progres[1]+"MB/"+progres[2]+"MB", progres[0], false, 0);}
            else if(downloaded!=progres[1]) {notifications.sendNotificationDownload("Updater", "Pobrano: "+progres[0]+"% "+progres[1]+"MB/"+progres[2]+"MB", progres[0], false, 0);}
            progress = progres[0];
            downloaded = progres[1];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s!=null){notifications.sendNotification("Updater", res.getString(R.string.download_complete), 1); sPref.edit().putBoolean("isDownError", true);}
            else {sPref.edit().putBoolean("isFinishedUpdate", true).commit();}
            stopSelf();
        }

        @Override
        protected void onCancelled() {
            running = false;
            Log.i("INFO", "AsyncTask canclled");
        }
    }
}