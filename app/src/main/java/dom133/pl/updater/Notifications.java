package dom133.pl.updater;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

public class Notifications {
    Application app;
    Resources res;

    public Notifications(Application app)
    {
        this.app = app;
        res = app.getResources();
    }

    public void sendNotification(String title, String txt, int check)
    {
        Notification notification;

        if(check == 0) {
          Intent intent = new Intent(app, DownloadService.class);
          PendingIntent pIntent = PendingIntent.getService(app, 0, intent, 0);

            notification = new NotificationCompat.Builder(app)
                    .setSmallIcon(R.mipmap.ic_updater)
                    .setPriority(2)
                    .setContentTitle(title)
                    .setContentText(txt)
                    .addAction(R.drawable.ic_download_button, "Pobierz", pIntent)
                    .extend(new NotificationCompat.WearableExtender()
                            .setHintShowBackgroundOnly(true))
                    .setPriority(2)
                    .setVibrate(new long[] {100, 400})
                    .build();
        } else if(check == 1) {
            Intent intent = new Intent(app, InstallService.class);
            PendingIntent pIntent = PendingIntent.getService(app, 0, intent, 0);

            notification = new NotificationCompat.Builder(app)
                    .setSmallIcon(R.mipmap.ic_updater)
                    .setPriority(2)
                    .setContentTitle(title)
                    .setContentText(txt)
                    .extend(new NotificationCompat.WearableExtender()
                            .setHintShowBackgroundOnly(true))
                    .setPriority(2)
                    .addAction(R.drawable.ic_install, res.getString(R.string.install_button), pIntent)
                    .setVibrate(new long[] {100, 400})
                    .build();
        } else {
            notification = new NotificationCompat.Builder(app)
                    .setSmallIcon(R.mipmap.ic_updater)
                    .setPriority(2)
                    .setContentTitle(title)
                    .setContentText(txt)
                    .extend(new NotificationCompat.WearableExtender()
                            .setHintShowBackgroundOnly(true))
                    .setPriority(2)
                    .setVibrate(new long[] {100, 400})
                    .build();
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(app);
        int notificationId = 1;
        notificationManager.notify(notificationId, notification);
    }

    public void sendNotificationDownload(String title, String txt, int value, boolean progress)
    {
        Intent intent = new Intent(app, DownloadService.class);
        intent.setAction("ACTION_STOP_SERVICE");
        PendingIntent pIntent = PendingIntent.getService(app, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(app)
                .setSmallIcon(R.mipmap.ic_updater)
                .setPriority(2)
                .setContentTitle(title)
                .setContentText(txt)
                .extend(new NotificationCompat.WearableExtender()
                        .setHintShowBackgroundOnly(true))
                .setPriority(2)
                .setProgress(100, value, progress)
                .addAction(R.drawable.ic_cancle, res.getString(R.string.cancle_button), pIntent)
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(app);
        int notificationId = 1;
        notificationManager.notify(notificationId, notification);
    }


}


