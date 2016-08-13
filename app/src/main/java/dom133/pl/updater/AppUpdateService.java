package dom133.pl.updater;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class AppUpdateService extends Service {

    private Download download;
    private Notifications notifications;
    private Resources res;
    public boolean isCancelled=false;
    private DownloadFile downloadFile;


    public AppUpdateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("INFO", "Service app onCreate");
        download = new Download(getApplication());
        notifications = new Notifications(getApplication());
        res = getResources();
        downloadFile = new DownloadFile();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("INFO", "Service app onDestroy");
        File file = new File(Environment.getExternalStorageDirectory().getPath()+"/Update.txt");
        if(file.exists()){Log.i("INFO", "File deleted"); file.delete();}
        if(isCancelled)notifications.sendNotification("Update", res.getString(R.string.cancle_message) , 2);
        startService(new Intent(getApplicationContext(), VersionChecker.class));
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("INFO", "Service app onCommand");
        Log.i("INFO", "Intent action: "+intent.getAction());
        if(Objects.equals(intent.getAction(), "ACTION_STOP_SERVICE")) {
            stopSelf();
            downloadFile.cancel(true);
            isCancelled= true;
            return START_STICKY;
        } else {
            stopService(new Intent(getApplicationContext(), VersionChecker.class));
            if(downloadFile.running) {downloadFile.cancel(true);}
            notifications.sendNotificationDownload("Updater", "", 0, false, 1);
            downloadFile.execute("http://app-updater.pl/updates/update.apk");
            return START_STICKY;
        }
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
        protected String doInBackground(String... strings) {
            int count = 0;
            while(!isCancelled()) {
                running = true;
                try {
                    File file = new File(Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_DOWNLOADS+"/update.apk");
                    if(file.exists()) {Log.i("INFO", "File deleted"); file.delete();}

                    URL url = new URL(strings[0]);
                    URLConnection urlConnection = url.openConnection();
                    int lenghtoffile =  urlConnection.getContentLength();

                    Log.i("INFO", "File path: "+file.getPath());

                    notifications.sendNotificationDownload("Updater", "Pobrano: 0% 0Mb/"+(lenghtoffile/ 1048576)+"Mb", 0, false, 1);

                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream(), 8192);
                    OutputStream outputStream = new FileOutputStream(file.getPath());

                    byte data[] = new byte[54*1024];
                    long total =0;

                    while ((count = inputStream.read(data)) !=-1 && !isCancelled()) {
                        total += count;
                        publishProgress((int) ((total * 100) / lenghtoffile), (int) (total / 1048576), (int) (lenghtoffile / 1048576));
                        outputStream.write(data, 0, count);
                    }
                    outputStream.flush();
                    inputStream.close();
                    outputStream.close();
                    return "OK";

                } catch(Exception e) {
                    FirebaseCrash.log(e.getMessage());
                    Log.e("ERROR", e.getMessage());
                    if(!isCancelled)notifications.sendNotification("Update", res.getString(R.string.download_incomplete), 2);
                    stopSelf();
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progres) {
            super.onProgressUpdate(progres);
            if(progress!=progres[0]) {notifications.sendNotificationDownload("Updater", "Pobrano: "+progres[0]+"% "+progres[1]+"Mb/"+progres[2]+"Mb", progres[0], false, 1);}
            else if(downloaded!=progres[1]) {notifications.sendNotificationDownload("Updater", "Pobrano: "+progres[0]+"% "+progres[1]+"Mb/"+progres[2]+"Mb", progres[0], false, 1);}
            progress = progres[0];
            downloaded = progres[1];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s!=null){notifications.sendNotification("Updater", res.getString(R.string.download_complete), 3);}
            startService(new Intent(getApplicationContext(), VersionChecker.class));
            stopSelf();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            running = false;
            Log.i("INFO", "AsyncTask canclled");
        }
    }

}
