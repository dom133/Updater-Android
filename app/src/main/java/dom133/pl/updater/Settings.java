package dom133.pl.updater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar!=null) {actionBar.setDisplayHomeAsUpEnabled(true);}

        sPref = getSharedPreferences("Updater", Context.MODE_APPEND);

        Switch supersu = (Switch) findViewById(R.id.supersu);
        Switch xposed = (Switch) findViewById(R.id.xposed);

        if(sPref.getBoolean("isSuperSU", false)) {supersu.setChecked(true);}
        if(sPref.getBoolean("isXposed", false)) {xposed.setChecked(true);}

        supersu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {sPref.edit().putBoolean("isSuperSU", b).apply();}
                else {sPref.edit().putBoolean("isSuperSU", b).apply();}
                Log.i("INFO", "SuperSU checked "+b);
            }
        });

        xposed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {sPref.edit().putBoolean("isXposed", b).apply();}
                else {sPref.edit().putBoolean("isXposed", b).apply();}
                Log.i("INFO", "Xposed checked "+b);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, Main.class));
        return true;
    }
}
