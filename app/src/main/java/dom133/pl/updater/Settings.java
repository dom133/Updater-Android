package dom133.pl.updater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
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

import java.io.InputStream;
import java.util.HashSet;
import java.util.Locale;
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
        Spinner memory = (Spinner) findViewById(R.id.spinner_memory);

        ArrayAdapter<CharSequence> time_adapter = ArrayAdapter.createFromResource(this, R.array.time_array, android.R.layout.simple_spinner_item);
        time_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time.setAdapter(time_adapter);
        actu.setAdapter(time_adapter);

        ArrayAdapter<CharSequence> memory_adapter = ArrayAdapter.createFromResource(this, R.array.memory_array, android.R.layout.simple_spinner_item);
        memory_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        memory.setAdapter(memory_adapter);

        supersu.setChecked(sPref.getBoolean("isSuperSU", false));
        xposed.setChecked(sPref.getBoolean("isXposed", false));
        gapps.setChecked(sPref.getBoolean("isGapps", false));
        time.setSelection(sPref.getInt("Time_spinner", 0));
        actu.setSelection(sPref.getInt("Actu_spinner", 3));
        memory.setSelection(sPref.getInt("Memory", 0));

        memory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("INFO", "Memory selected "+i);
                for(String str : getExternalMounts())
                {
                    Log.i("INFO",str);
                    break;
                }
                sPref.edit().putInt("Memory", i).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                sPref.edit().putBoolean("isSuperSU", b).commit();
                Log.i("INFO", "SuperSU checked "+b);
            }
        });

        xposed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sPref.edit().putBoolean("isXposed", b).commit();
                Log.i("INFO", "Xposed checked "+b);
            }
        });

        gapps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sPref.edit().putBoolean("isGapps", b).commit();
                Log.i("INFO", "Gapps checked "+b);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, Main.class));
        return true;
    }

    public HashSet<String> getExternalMounts()
    {

        final HashSet<String> out = new HashSet<String>();
        String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
        String s = "";
        try
        {
            final Process process = new ProcessBuilder().command("mount").redirectErrorStream(true).start();
            process.waitFor();
            final InputStream is = process.getInputStream();
            final byte[] buffer = new byte[1024];
            while(is.read(buffer) != -1)
            {
                s = s + new String(buffer);
            }
            is.close();
        }
        catch(Exception e)
        {
            Log.e("ERROR",e.getMessage());
        }
        final String[] lines = s.split("\n");
        for (String line : lines)
        {
            if(!line.toLowerCase(Locale.US).contains("asec"))
            {
                if(line.matches(reg))
                {
                    String[] parts = line.split(" ");
                    for(String part : parts)
                    {
                        if(part.startsWith("/"))
                        {
                            if(!part.toLowerCase(Locale.US).contains("vold"))
                            {
                                out.add(part);
                            }
                        }
                    }
                }
            }
        }
        return out;
    }
}
