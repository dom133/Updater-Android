package dom133.pl.updater;

import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Main extends AppCompatActivity {

    Download download = new Download();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final Notifications notifications = new Notifications(getApplication());
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.i("INFO", Build.VERSION.INCREMENTAL);
                //notifications.sendNotificationDownload("Test", download.DownloadString("https://raw.githubusercontent.com/dom133/Updater-Android-Wersje/master/version.txt"), 50);
                if(Build.VERSION.INCREMENTAL != download.DownloadString("https://raw.githubusercontent.com/dom133/Updater-Android-Wersje/master/version.txt")) {
                    Toast.makeText(getApplication(), "Nie znaleziono nowej wersji!!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplication(), "Dostępna jest nowa wersja!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
