package dom133.pl.updater;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;

public class InformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){actionBar.setDisplayHomeAsUpEnabled(true);actionBar.setTitle("Informacje o aplikacji");}
        TextView version = (TextView) findViewById(R.id.textView);
        String version_txt = (String)version.getText();
        TextView os= (TextView) findViewById(R.id.textView8);
        String os_txt = (String)os.getText();

        Cm cm = new Cm();

        try {
            version.setText(version_txt.replace("$1", BuildConfig.VERSION_NAME));
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }

        try {
            os.setText(os_txt.replace("$1", cm.getCMVersion().toUpperCase()));
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, Main.class));
        return true;
    }
}
