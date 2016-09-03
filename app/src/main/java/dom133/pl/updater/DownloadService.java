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

import com.google.firebase.crash.FirebaseCrash;

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


    Notifications notifications;
    Resources res;
    Download download;
    SharedPreferences sPref;
    DownloadFile downloadFile;
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
        Log.i("INFO", "Download service onCommand "+intent.getAction());
        if(Objects.equals(intent.getAction(), "ACTION_STOP_SERVICE")) {Log.i("INFO", "Service is Stoped");stopSelf(); isCancled=true; downloadFile.cancel(true); return START_STICKY;}
        else {
            notifications.sendNotificationDownload("Updater", "", 0, true, 0);
            Intent intent_vers = new Intent(this, VersionChecker.class);
            intent_vers.setAction("ACTION_STOP");
            startService(intent_vers);
            if (downloadFile.running) {
                downloadFile.cancel(true);
            }

            Log.i("INFO", "SuperSu: "+sPref.getBoolean("isSuperSU", false)+" Xposed: "+sPref.getBoolean("isXposed", false) + " Gapps: "+sPref.getBoolean("isGapps", false));
            if (sPref.getBoolean("isSuperSU", false)) {
                if (sPref.getBoolean("isXposed", false)) {
                    if(sPref.getBoolean("isGapps", false)) {downloadFile.execute(download.DownloadString(res.getString(R.string.download_url)), "update.zip", res.getString(R.string.supersu_link), "supersu.zip", res.getString(R.string.xposed_link), "xposed.zip", res.getString(R.string.gapps_link), "gapps.zip");}
                    else {downloadFile.execute(download.DownloadString(res.getString(R.string.download_url)), "update.zip", res.getString(R.string.supersu_link), "supersu.zip", res.getString(R.string.xposed_link), "xposed.zip");}
                } else {
                    if(sPref.getBoolean("isGapps", false)) {downloadFile.execute(download.DownloadString(res.getString(R.string.download_url)), "update.zip", res.getString(R.string.supersu_link), "supersu.zip", res.getString(R.string.gapps_link), "gapps.zip");}
                    else {downloadFile.execute(download.DownloadString(res.getString(R.string.download_url)), "update.zip", res.getString(R.string.supersu_link), "supersu.zip");}
                }
            } else {
                if (sPref.getBoolean("isXposed", false)) {
                    if(sPref.getBoolean("isGaaps", false)) {downloadFile.execute(download.DownloadString(res.getString(R.string.download_url)), "update.zip", res.getString(R.string.xposed_link), "xposed.zip", res.getString(R.string.gapps_link), "gapps.zip");}
                    else {downloadFile.execute(download.DownloadString(res.getString(R.string.download_url)), "update.zip", res.getString(R.string.xposed_link), "xposed.zip");}
                } else {downloadFile.execute(download.DownloadString(res.getString(R.string.download_url)), "update.zip");}
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
        if(isCancled)notifications.sendNotification("Updater", res.getString(R.string.cancle_message), 0);
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

                    while (!isCancelled()) {
                        notifications.sendNotificationDownload("Updater", "", 0, true, 0);
                        File file = null;
                        if(sPref.getInt("Memory", 0)==0) {file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + f_url[get[files - 1]]);}
                        else {file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + f_url[get[files - 1]]);}

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

                        notifications.sendNotificationDownload("Updater", "Pobrano: 0% 0Mb/" + (lenghtoffile/1048576) + "Mb", 0, false, 0);

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
                    if(!isCancled)notifications.sendNotification("Updater", res.getString(R.string.download_incomplete), 0);
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
            if(progress!=progres[0]) {notifications.sendNotificationDownload("Updater", "Pobrano: "+progres[0]+"% "+progres[1]+"Mb/"+progres[2]+"Mb", progres[0], false, 0);}
            else if(downloaded!=progres[1]) {notifications.sendNotificationDownload("Updater", "Pobrano: "+progres[0]+"% "+progres[1]+"Mb/"+progres[2]+"Mb", progres[0], false, 0);}
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

        public HashSet<String> getExternalMounts()
        {

            final HashSet<String> out = new HashSet<String>();
            String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
            String s = "";
            try
            {
                final Process process = new ProcessBuilder().command("mount").redirectErrorStream(true).start();
                process.waitFor();
                final InputStream is = process.getInputStream();
                final byte[] buffer = new byte[1024];
                while(is.read(buffer) != -1)
                {
                    s = s + new String(buffer);
                }
                is.close();
            }
            catch(Exception e)
            {
                Log.e("ERROR",e.getMessage());
            }
            final String[] lines = s.split("\n");
            for (String line : lines)
            {
                if(!line.toLowerCase(Locale.US).contains("asec"))
                {
                    if(line.matches(reg))
                    {
                        String[] parts = line.split(" ");
                        for(String part : parts)
                        {
                            if(part.startsWith("/"))
                            {
                                if(!part.toLowerCase(Locale.US).contains("vold"))
                                {
                                    out.add(part);
                                }
                            }
                        }
                    }
                }
            }
            return out;
        }
    }
}
