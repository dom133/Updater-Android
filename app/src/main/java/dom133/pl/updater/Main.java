package dom133.pl.updater;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class Main extends AppCompatActivity {

    private Resources res;
    private SharedPreferences sPref;
    private Cm cm = new Cm();
    private boolean isUpdate=false;
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

        sPref = getSharedPreferences("Updater", Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        res = getResources();

        verifyStoragePermissions(this);

        final Download download = new Download(getApplication());
        final Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isUpdate) {
                    Log.i("INFO", "CM: "+cm.getCMVersion()+" String: " + cm.getProp("ro.cm.version") + " DownloadString: " + download.DownloadString(res.getString(R.string.version_url)+"-"+cm.getCMVersion()+".txt") + " True: " + Objects.equals(cm.getProp("ro.cm.version"), download.DownloadString(res.getString(R.string.version_url)+"-"+cm.getCMVersion()+".txt")));
                    if (Objects.equals(cm.getProp("ro.cm.version"), download.DownloadString(res.getString(R.string.version_url)+"-"+cm.getCMVersion()+".txt"))) {
                        Toast.makeText(getApplication(), "Nie znaleziono nowej wersji!!!", Toast.LENGTH_SHORT).show();
                    } else if (download.DownloadString(res.getString(R.string.version_url)+"-"+cm.getCMVersion()+".txt") == null) {
                        Toast.makeText(getApplication(), "Brak połączenia z internetem!!!", Toast.LENGTH_SHORT).show();
                    } else if(Objects.equals(download.DownloadString(res.getString(R.string.version_url) + "-" + cm.getCMVersion() + ".txt"), "false")) {
                        Toast.makeText(getApplication(), "Dla tej wersji systemu aktualizacja jest aktualnie wyłączona", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplication(), res.getString(R.string.version_message), Toast.LENGTH_SHORT).show();
                        button.setText("Pobierz");
                        isUpdate = true;
                    }
                } else {
                    startService(new Intent(getApplicationContext(), DownloadService.class));
                    Toast.makeText(getApplication(), "Pobieranie rozpoczęte!!!", Toast.LENGTH_SHORT).show();
                    button.setEnabled(false);
                }
            }
        });


        //Changelog Dialog
        LayoutInflater factory = LayoutInflater.from(this);
        final View changelogDialogView = factory.inflate(R.layout.changelog_dialog, null);
        final AlertDialog changelogDialog = new AlertDialog.Builder(this).create();
        changelogDialog.setView(changelogDialogView);
        changelogDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ListView changes = (ListView) changelogDialogView.findViewById(R.id.changelog_list);
                ArrayList<String> changes_list = Download.getChangelog("http://app-updater.pl/updates/txt/changelog-"+cm.getCMVersion()+".txt");
                ArrayAdapter<String> changes_adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, changes_list);
                changes.setAdapter(changes_adapter);
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.equals(download.DownloadString(res.getString(R.string.version_url) + "-" + cm.getCMVersion() + ".txt"), "false")) { Toast.makeText(getBaseContext(), "Changelog dla tej wersji systemu jest aktualnie wyłączony", Toast.LENGTH_SHORT).show();}
                else if(Download.getChangelog("http://app-updater.pl/updates/txt/changelog-"+cm.getCMVersion()+".txt")!=null) {changelogDialog.show();}
                else {Toast.makeText(getApplication(), "Brak połączenia z internetem!!!", Toast.LENGTH_SHORT).show();}
            }
        });

        changelogDialogView.findViewById(R.id.close_changelog_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changelogDialog.cancel();
            }
        });

        //Show Assets Menu
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {startActivity(new Intent(getApplication(), Assets.class));}
        });

        startService(new Intent(this, VersionChecker.class));

        //Show Changelog
        if(sPref.getBoolean("isChangelog", false)) {if(Download.getChangelog("http://app-updater.pl/updates/txt/changelog-"+cm.getCMVersion()+".txt")!=null) {changelogDialog.show(); sPref.edit().putBoolean("isChangelog", false).commit();}} //Show changelog
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
