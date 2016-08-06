package dom133.pl.updater;

import android.app.PendingIntent;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Timer;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class DownloadService extends Service {


    Notifications notifications;
    Resources res;
    Download download;
    SharedPreferences sPref;
    DownloadFile downloadFile;

    public DownloadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("INFO", "Download service onCreate");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        notifications = new Notifications(getApplication());
        res = getResources();
        download = new Download(getApplication());
        sPref = getSharedPreferences("Updater", Context.MODE_APPEND);
        downloadFile = new DownloadFile();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        Log.i("INFO", "Download service onCommand "+intent.getAction());
        if(Objects.equals(intent.getAction(), "ACTION_STOP_SERVICE")) {Log.i("INFO", "Service is Stoped");stopSelf(); return START_NOT_STICKY;}
        else {
            notifications.sendNotificationDownload("Updater", "", 0, true);
            stopService(new Intent(this, VersionChecker.class));
            //TODO: Zmienić to przed kompilacją
            //new DownloadFile().execute("http://192.168.0.100/update.zip", res.getString(R.string.supersu_link));
            if (downloadFile.running) {
                downloadFile.cancel(true);
            }
            if (sPref.getBoolean("isSuperSU", false)) {
                if (sPref.getBoolean("isXposed", false)) {
                    downloadFile.execute("http://192.168.0.100/update.zip", "update.zip", res.getString(R.string.supersu_link), "supersu.zip", res.getString(R.string.xposed_link), "xposed.zip");
                } else {
                    downloadFile.execute("http://192.168.0.100/update.zip", "update.zip", res.getString(R.string.supersu_link), "supersu.zip");
                }
            } else {
                if (sPref.getBoolean("isXposed", false)) {
                    downloadFile.execute("http://192.168.0.100/update.zip", "update.zip", res.getString(R.string.xposed_link), "xposed.zip");
                } else {
                    downloadFile.execute("http://192.168.0.100/update.zip", "update.zip");
                }
            }
            return START_STICKY;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("INFO", "Download service stopped");
        File file = new File(Environment.getExternalStorageDirectory().getPath()+"/Update.txt");
        if(file.exists()) {Log.i("INFO", "File deleted"); file.delete();}
        notifications.sendNotification("Updater", res.getString(R.string.cancle_message), 0);
        downloadFile.cancel(true);
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
            while(!isCancelled()) {
                running=true;
                int count;
                int files = 1;
                int number = f_url.length;
                int[] get = new int[3];
                int[] url_i = new int[3];
                Log.i("INFO", "Numbers: " + number);
                try {
                    switch (number) {
                        case 2: {
                            url_i[0] = 0;
                            get[0] = 1;
                            break;
                        }
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
                    }

                    while (true && !isCancelled()) {
                        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + f_url[get[files - 1]]);

                        if (file.exists()) {
                            Log.i("INFO", "File deleted");
                            file.delete();
                        }

                        File create = new File(Environment.getExternalStorageDirectory().getPath() + "/Update.txt");
                        create.createNewFile();
                        Log.i("INFO", "File: " + file.getPath());

                        URL url = new URL(f_url[url_i[files - 1]]);
                        URLConnection connection = url.openConnection();
                        connection.connect();
                        int lenghtoffile = connection.getContentLength();


                        InputStream input = new BufferedInputStream(url.openStream(), 8192);
                        OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/" + f_url[get[files - 1]]);

                        byte data[] = new byte[54 * 1024];
                        long total = 0;

                        notifications.sendNotificationDownload("Updater", "Pobrano: 0% 0Mb/" + lenghtoffile + "Mb", 0, false);

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
                    notifications.sendNotification("Updater", res.getString(R.string.download_incomplete), 0);
                    startService(new Intent(getApplicationContext(), VersionChecker.class));
                    stopSelf();
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progres) {
            super.onProgressUpdate(progres);
            if(progress!=progres[0]) {notifications.sendNotificationDownload("Updater", "Pobrano: "+progres[0]+"% "+progres[1]+"Mb/"+progres[2]+"Mb", progres[0], false);}
            else if(downloaded!=progres[1]) {notifications.sendNotificationDownload("Updater", "Pobrano: "+progres[0]+"% "+progres[1]+"Mb/"+progres[2]+"Mb", progres[0], false);}
            progress = progres[0];
            downloaded = progres[1];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s!=null)notifications.sendNotification("Updater", res.getString(R.string.download_complete), 1);
            startService(new Intent(getApplicationContext(), VersionChecker.class));
            stopSelf();
        }

        @Override
        protected void onCancelled() {
            running = false;
            Log.i("INFO", "AsyncTask canclled");
        }
    }
}
