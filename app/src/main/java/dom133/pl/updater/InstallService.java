package dom133.pl.updater;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.Process;

import android.preference.Preference;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;

import java.io.IOException;

public class InstallService extends Service {
    public InstallService() {
    }

    private SharedPreferences pref;
    private Notifications notifications;

    @Override
    public void onCreate() {
        super.onCreate();
        pref = getSharedPreferences("Updater", Context.MODE_PRIVATE);
        notifications = new Notifications(getApplication());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("INFO", "Install service onCommand");
        Toast.makeText(getApplication(), "Instalacja rozpoczęta", Toast.LENGTH_SHORT).show();
        notifications.sendNotificationDownload("Updater", "", 0, true, 1);
        File file = new File(Environment.getExternalStorageDirectory().getPath()+"/Update.txt");
        if(file.exists()) {Log.i("INFO", "File deleted"); file.delete();}

        try {
            File update = new File(Environment.getExternalStorageDirectory().getPath()+"/Install.txt");
            update.createNewFile();
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            InputStream response = su.getInputStream();

            outputStream.writeBytes("echo 'boot-recovery ' > /cache/recovery/command\n");
            outputStream.flush();

            outputStream.writeBytes("echo '--update_package=/sdcard/update.zip' >> /cache/recovery/command\n");
            outputStream.flush();

            if(pref.getBoolean("isSuperSU", false)) {
                outputStream.writeBytes("echo '--update_package=/sdcard/supersu.zip' >> /cache/recovery/command\n");
                outputStream.flush();
            }

            if(pref.getBoolean("isGapps", false)) {
                outputStream.writeBytes("echo '--update_package=/sdcard/gapps.zip' >> /cache/recovery/command\n");
                outputStream.flush();
            }

            if(pref.getBoolean("isXposed", false)) {
                outputStream.writeBytes("echo '--update_package=/sdcard/xposed.zip' >> /cache/recovery/command\n");
                outputStream.flush();
            }

            outputStream.writeBytes("echo '--wipe_cache' >> /cache/recovery/command\n");

            outputStream.writeBytes("reboot recovery\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();

            su.waitFor();
            Log.i("INFO", readFully(response));
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
        return START_NOT_STICKY;
    }

    public static String readFully(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString("UTF-8");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
