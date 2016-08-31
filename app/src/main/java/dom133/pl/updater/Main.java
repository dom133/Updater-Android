package dom133.pl.updater;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.Objects;

public class Main extends AppCompatActivity {

    private Resources res;
    private String update = null;
    private static String TAG = "Permission";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        res = getResources();

        verifyStoragePermissions(this);

        final Download download = new Download(getApplication());
        final Button button = (Button) findViewById(R.id.button);
        final Button button2 = (Button) findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Objects.equals(update, "rom")) {startService(new Intent(getApplicationContext(), DownloadService.class));}
                else {startService(new Intent(getApplicationContext(), AppUpdateService.class));}
                Toast.makeText(getApplication(), "Pobieranie rozpoczęte!!!", Toast.LENGTH_SHORT).show();
                button2.setEnabled(false);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("INFO", "String: " + Build.VERSION.INCREMENTAL + " DownloadString: " + download.DownloadString(res.getString(R.string.version_url)));
                if (Objects.equals(Build.VERSION.INCREMENTAL, download.DownloadString(res.getString(R.string.version_url)))) {
                    if(!Objects.equals(BuildConfig.VERSION_NAME, download.DownloadString(res.getString(R.string.app_version_link))) && download.DownloadString(res.getString(R.string.app_version_link))!=null) {
                        Toast.makeText(getApplication(), res.getString(R.string.version_message_app), Toast.LENGTH_SHORT).show();
                        button2.setVisibility(View.VISIBLE);
                        button.setVisibility(View.GONE);
                        update = "app";
                    } else {
                        Toast.makeText(getApplication(), "Nie znaleziono nowej wersji!!!", Toast.LENGTH_SHORT).show();
                    }
                } else if (download.DownloadString(res.getString(R.string.version_url)) == null) {
                    Toast.makeText(getApplication(), "Brak połączenia z internetem!!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplication(), res.getString(R.string.version_message), Toast.LENGTH_SHORT).show();
                    button2.setVisibility(View.VISIBLE);
                    button.setVisibility(View.GONE);
                    update = "rom";
                }
            }
        });

        startService(new Intent(this, VersionChecker.class));
    }


    public static void verifyStoragePermissions(Activity activity) {
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
        } else if(id == R.id.action_report) {
            startActivity(new Intent(this, Report.class));
        } else if(id == R.id.action_info) {
            startActivity(new Intent(this, InformationActivity.class));
        } else if(id == R.id.action_web) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://app-updater.pl")));
        }
        return true;
    }
}
