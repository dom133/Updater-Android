package dom133.pl.updater;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Report extends AppCompatActivity {

    private int selected;
    MySQL mysql = new MySQL();
    EditText nick;
    EditText email;
    EditText title;
    EditText contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        final ActionBar actionbar = getSupportActionBar();
        if(actionbar!=null) {actionbar.setDisplayHomeAsUpEnabled(true);}

        nick = (EditText) findViewById(R.id.nick_txt);
        email = (EditText) findViewById(R.id.email_txt);
        title = (EditText) findViewById(R.id.title_txt);
        contents = (EditText) findViewById(R.id.contents_txt);

        final Spinner type = (Spinner) findViewById(R.id.type_spinner);
        final Button button = (Button) findViewById(R.id.report_button);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);
        type.setAdapter(adapter);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("INFO", "Spiner array selected: "+i);
                selected = i;
                if(i==0){actionbar.setTitle("Zgłaszanie błędu");button.setText("Zgłoś błąd");}
                else {actionbar.setTitle("Zgłaszanie propozycji");button.setText("Zgłoś propozycję");}
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean error = false;
                email.setError(null);
                title.setError(null);
                contents.setError(null);

                if(TextUtils.isEmpty(email.getText().toString())) {
                    email.setError("To pole nie może być puste!!!");
                    error = true;
                }
                if(TextUtils.isEmpty(title.getText().toString())) {
                    title.setError("To pole nie może być puste!!!");
                    error = true;
                }
                if(TextUtils.isEmpty(contents.getText().toString())) {
                    contents.setError("To pole nie może być puste!!!");
                    error = true;
                }

                if(!error) {
                    Toast.makeText(getApplicationContext(), mysql.add(String.valueOf(selected), nick.getText().toString(), email.getText().toString(), title.getText().toString(), contents.getText().toString()), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, Main.class));
        return true;
    }

}
