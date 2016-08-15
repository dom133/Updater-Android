package dom133.pl.updater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.prefs.Preferences;

import static dom133.pl.updater.R.id.action_settings;

public class Settings extends AppCompatActivity {

    SharedPreferences sPref;
    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        res = getResources();

        if(actionBar!=null) {actionBar.setDisplayHomeAsUpEnabled(true); actionBar.setTitle(res.getString(R.string.action_settings));}

        sPref = getSharedPreferences("Updater", Context.MODE_PRIVATE);

        Switch supersu = (Switch) findViewById(R.id.supersu);
        Switch xposed = (Switch) findViewById(R.id.xposed);
        Switch gapps = (Switch) findViewById(R.id.gapps);
        Spinner time = (Spinner) findViewById(R.id.spinner_time);
        Spinner actu = (Spinner) findViewById(R.id.spinner_actu);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.time_array, android.R.layout.simple_spinner_item);
        time.setAdapter(adapter);
        actu.setAdapter(adapter);

        if(sPref.getBoolean("isSuperSU", false)) {supersu.setChecked(true);}
        if(sPref.getBoolean("isXposed", false)) {xposed.setChecked(true);}
        if(sPref.getBoolean("isGapps", false)) {gapps.setChecked(true);}

        time.setSelection(sPref.getInt("Time_spinner", 0));
        actu.setSelection(sPref.getInt("Actu_spinner", 3));

        time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("INFO", "Time selected "+i);
                sPref.edit().putInt("Time_spinner", i).commit();
                if(i==0){sPref.edit().putInt("Time", (1000*60)).commit();}
                else if(i==1) {sPref.edit().putInt("Time", (1000*60)*5).commit();}
                else if(i==2) {sPref.edit().putInt("Time", (1000*60)*10).commit();}
                else if(i==3) {sPref.edit().putInt("Time", (1000*60)*30).commit();}
                else if(i==4) {sPref.edit().putInt("Time", (1000*60)*60).commit();}
                else if(i==5) {sPref.edit().putInt("Time", (1000*60)*120).commit();}
                else if(i==6) {sPref.edit().putInt("Time", (1000*60)*240).commit();}

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        actu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("INFO", "Actu selected "+i);
                sPref.edit().putInt("Actu_spinner", i).commit();
                if(i==0){sPref.edit().putInt("Actu", (1000*60)).commit();}
                else if(i==1) {sPref.edit().putInt("Actu", (1000*60)*5).commit();}
                else if(i==2) {sPref.edit().putInt("Actu", (1000*60)*10).commit();}
                else if(i==3) {sPref.edit().putInt("Actu", (1000*60)*30).commit();}
                else if(i==4) {sPref.edit().putInt("Actu", (1000*60)*60).commit();}
                else if(i==5) {sPref.edit().putInt("Actu", (1000*60)*120).commit();}
                else if(i==6) {sPref.edit().putInt("Actu", (1000*60)*240).commit();}
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        supersu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {sPref.edit().putBoolean("isSuperSU", b).commit();}
                else {sPref.edit().putBoolean("isSuperSU", b).commit();}
                Log.i("INFO", "SuperSU checked "+b);
            }
        });

        xposed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {sPref.edit().putBoolean("isXposed", b).commit();}
                else {sPref.edit().putBoolean("isXposed", b).commit();}
                Log.i("INFO", "Xposed checked "+b);
            }
        });

        gapps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {sPref.edit().putBoolean("isGapps", b).commit();}
                else {sPref.edit().putBoolean("isGapps", b).commit();}
                Log.i("INFO", "Gapps checked "+b);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, Main.class));
        return true;
    }
}
