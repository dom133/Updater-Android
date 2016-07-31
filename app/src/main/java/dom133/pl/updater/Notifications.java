package dom133.pl.updater;

import android.app.Application;
import android.app.Notification;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

public class Notifications {
    Application app;

    public Notifications(Application app)
    {
        this.app = app;
    }

    public void cancleNotification()
    {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(app);
        notificationManager.cancel(1);
    }

    public void sendNotification(String title, String txt)
    {
        Notification notification = new NotificationCompat.Builder(app)
                .setSmallIcon(R.mipmap.ic_updater)
                .setPriority(2)
                .setContentTitle(title)
                .setContentText(txt)
                .extend(new NotificationCompat.WearableExtender()
                        .setHintShowBackgroundOnly(true))
                .setPriority(2)
                .setVibrate(new long[] {500, 3000})
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(app);
        int notificationId = 1;
        notificationManager.notify(notificationId, notification);
    }

    public void sendNotificationDownload(String title, String txt, int value)
    {
        Notification notification = new NotificationCompat.Builder(app)
                .setSmallIcon(R.mipmap.ic_updater)
                .setPriority(2)
                .setContentTitle(title)
                .setContentText(txt)
                .extend(new NotificationCompat.WearableExtender()
                        .setHintShowBackgroundOnly(true))
                .setPriority(2)
                .setProgress(100, value, false)
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(app);
        int notificationId = 1;
        notificationManager.notify(notificationId, notification);
    }
}
