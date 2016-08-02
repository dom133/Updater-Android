package dom133.pl.updater;

import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Objects;

public class Main extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final Notifications notifications = new Notifications(getApplication());
        final Download download = new Download(getApplication());

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("INFO", "String: " + Build.VERSION.INCREMENTAL + " DownloadString: " + download.DownloadString("https://raw.githubusercontent.com/dom133/Updater-Android-Wersje/master/version.txt"));
                if (Objects.equals(Build.VERSION.INCREMENTAL, download.DownloadString("https://raw.githubusercontent.com/dom133/Updater-Android-Wersje/master/version.txt"))) {
                    Toast.makeText(getApplication(), "Nie znaleziono nowej wersji!!!", Toast.LENGTH_SHORT).show();
                } else if(download.DownloadString("https://raw.githubusercontent.com/dom133/Updater-Android-Wersje/master/version.txt")==null) {
                    Toast.makeText(getApplication(), "Brak połączenia z internetem!!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplication(), "Dostępna jest nowa wersja!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        startService(new Intent(this, VersionChecker.class));

    }


}
