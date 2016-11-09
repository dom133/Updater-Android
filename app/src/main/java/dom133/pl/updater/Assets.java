package dom133.pl.updater;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Assets extends AppCompatActivity {

    private Resources res;
    private SharedPreferences sPref;
    private ArrayList<Integer> type_list = new ArrayList<>();
    private ArrayList<String> addons_list = new ArrayList<>();
    private ArrayList<String> links_list = new ArrayList<>();
    private ArrayList<String> zip_list = new ArrayList<>();
    private ArrayList<Integer> active_addons_list = new ArrayList<>();
    private ArrayAdapter<String> list_adapter;
    private Download download;
    private Cm cm;
    private Gson gson;
    private int itemID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets);

        //Initial Variable
        res = getResources();
        download = new Download(getApplication());
        cm = new Cm();
        gson = new Gson();
        sPref = getSharedPreferences("Updater", Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {actionBar.setTitle(res.getString(R.string.assets_button));actionBar.setDisplayHomeAsUpEnabled(true);}

        //ListView
        final ListView list = (ListView) findViewById(R.id.items_list);

        //Load addons list
        Log.i("INFO", "Load Addons");
        Type type_integer = new TypeToken<ArrayList<Integer>>() {}.getType(); Type type_string = new TypeToken<ArrayList<String>>() {}.getType();
        if(!Objects.equals(sPref.getString("type", null), null)) {type_list = gson.fromJson(sPref.getString("type", null), type_integer);addons_list = gson.fromJson(sPref.getString("addons", null), type_string);links_list = gson.fromJson(sPref.getString("links", null), type_string);zip_list = gson.fromJson(sPref.getString("zip", null), type_string);active_addons_list = gson.fromJson(sPref.getString("active_addons", null), type_integer);}

        //List Adapter
        list_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addons_list);
        list.setAdapter(list_adapter);

        //Dialog add
        LayoutInflater factory_add = LayoutInflater.from(this);
        final View addDialogView = factory_add.inflate(R.layout.add_assets, null);
        final AlertDialog addDialog = new AlertDialog.Builder(this).create();
        addDialog.setView(addDialogView);
        addDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //Items dialog add
        final Spinner type = (Spinner) addDialogView.findViewById(R.id.type_spinner);
        final Spinner stock_assets = (Spinner) addDialogView.findViewById(R.id.addons_spinner);

        //Dialog edit
        LayoutInflater factory_edit = LayoutInflater.from(this);
        final View editDialogView = factory_edit.inflate(R.layout.edit_assets, null);
        final AlertDialog editDialog = new AlertDialog.Builder(this).create();
        editDialog.setView(editDialogView);
        editDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //Items dialog edit
        final Button action_button = (Button) editDialogView.findViewById(R.id.action_button);
        final Button edit_name_button = (Button) editDialogView.findViewById(R.id.edit_name_assets_button);
        final Button save_name_button = (Button) editDialogView.findViewById(R.id.save_name_assets_button);
        final Button edit_link_button = (Button) editDialogView.findViewById(R.id.edit_link_assets_button);
        final Button save_link_button = (Button) editDialogView.findViewById(R.id.save_link_assets_button);
        final Button delete_button = (Button) editDialogView.findViewById(R.id.delete_button);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDialog.show();
            }
        });

        addDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                ArrayAdapter<CharSequence> type_adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.type_assets_array, android.R.layout.simple_spinner_item);
                type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                type.setAdapter(type_adapter);
                ArrayAdapter<CharSequence> addons_adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.addons_array, android.R.layout.simple_spinner_item);
                addons_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stock_assets.setAdapter(addons_adapter);
            }
        });

        //Add dialog events
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("INFO", "Type selected change: "+i);
                if(i==0) {
                    addDialogView.findViewById(R.id.stock_layout).setVisibility(View.VISIBLE);
                    addDialogView.findViewById(R.id.addon_layout).setVisibility(View.GONE);
                } else {
                    addDialogView.findViewById(R.id.stock_layout).setVisibility(View.GONE);
                    addDialogView.findViewById(R.id.addon_layout).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addDialogView.findViewById(R.id.add_assets_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDialogView.findViewById(R.id.add_assets_button).setEnabled(false);
                if(type.getSelectedItemPosition()==0) {
                    int selected = stock_assets.getSelectedItemPosition();
                    String[] name_array = res.getStringArray(R.array.addons_array);
                    String[] links_array = res.getStringArray(R.array.links_addons_array);
                    String name = name_array[selected];
                    String link = links_array[selected];
                    String zip = name+".zip";

                    Log.i("INFO", "Name: "+name+" Link: "+link+" Zip: "+zip);
                    link = download.DownloadString(link+"-"+cm.getCMVersion()+".txt");
                    Log.i("INFO", "Name: "+name+" Link: "+link+" Zip: "+zip);

                    if(Objects.equals(link, "false")){Toast.makeText(getBaseContext(), res.getString(R.string.null_assets_txt), Toast.LENGTH_LONG).show();}
                    else {
                        if(!CheckExist(name)) {
                            active_addons_list.add(1);
                            type_list.add(type.getSelectedItemPosition());
                            addons_list.add(name);
                            links_list.add(link);
                            zip_list.add(zip);
                            list_adapter.notifyDataSetChanged();
                            addDialog.cancel();
                            Toast.makeText(getBaseContext(), res.getString(R.string.succes_assets_txt), Toast.LENGTH_SHORT).show();
                            Log.i("INFO", "Array: "+String.valueOf(addons_list));
                        } else { Toast.makeText(getBaseContext(), res.getString(R.string.isset_addon_txt), Toast.LENGTH_SHORT).show(); }
                    }
                    addDialogView.findViewById(R.id.add_assets_button).setEnabled(true);

                } else {
                    TextView name = (TextView) addDialogView.findViewById(R.id.name_assets_txt);
                    TextView link = (TextView) addDialogView.findViewById(R.id.link_assets_txt);
                    String zip = name.getText().toString().replaceAll("\\s+", "")+".zip";
                    Log.i("INFO", "Name: "+name.getText().toString()+" Link: "+link.getText().toString()+" Zip: "+zip);
                    if(Objects.equals(name.getText().toString(), "")) { Toast.makeText(getBaseContext(), res.getString(R.string.empty_name_assets_txt), Toast.LENGTH_SHORT).show(); name.setError(res.getString(R.string.empty_name_assets_txt));}
                    else if(Objects.equals(link.getText().toString(), "")) { Toast.makeText(getBaseContext(), res.getString(R.string.empty_link_assets_txt), Toast.LENGTH_SHORT).show(); link.setError(res.getString(R.string.empty_link_assets_txt));}
                    else {
                        active_addons_list.add(1);
                        type_list.add(type.getSelectedItemPosition());
                        addons_list.add(name.getText().toString());
                        links_list.add(link.getText().toString());
                        zip_list.add(zip);
                        list_adapter.notifyDataSetChanged();
                        addDialog.cancel();
                        Toast.makeText(getBaseContext(), res.getString(R.string.succes_assets_txt), Toast.LENGTH_SHORT).show();
                    }
                    addDialogView.findViewById(R.id.add_assets_button).setEnabled(true);
                }
            }
        });

        //Edit dialog events
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                editDialog.show();
                itemID = i;
                return false;
            }
        });

        editDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                //TextView
                TextView type = (TextView) editDialogView.findViewById(R.id.type_txt);
                TextView name = (TextView) editDialogView.findViewById(R.id.name_txt);
                TextView link = (TextView) editDialogView.findViewById(R.id.link_txt);

                Log.i("INFO", "Type: "+type_list.size()+" ID: "+itemID);
                type.setText(res.getStringArray(R.array.type_assets_array)[type_list.get(itemID)]);
                name.setText(" "+addons_list.get(itemID));
                link.setText(" "+links_list.get(itemID));
                action_button.setText(res.getStringArray(R.array.action_edit_assets_button)[active_addons_list.get(itemID)]);
            }
        });

        action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(active_addons_list.get(itemID)==0) {active_addons_list.set(itemID, 1);
                } else {active_addons_list.set(itemID, 0);}
                action_button.setText(res.getStringArray(R.array.action_edit_assets_button)[active_addons_list.get(itemID)]);
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active_addons_list.remove(itemID);type_list.remove(itemID);addons_list.remove(itemID);links_list.remove(itemID);zip_list.remove(itemID);list_adapter.notifyDataSetChanged();
                Toast.makeText(getBaseContext(), res.getString(R.string.delete_addon_txt), Toast.LENGTH_SHORT).show();
                editDialog.cancel();
            }
        });

        edit_name_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDialogView.findViewById(R.id.name_layout_edit).setVisibility(View.GONE);
                editDialogView.findViewById(R.id.name_layout_save).setVisibility(View.VISIBLE);
                TextView name = (TextView) editDialogView.findViewById(R.id.name_edit_txt);
                name.setText(addons_list.get(itemID));
            }
        });

        save_name_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView name = (TextView) editDialogView.findViewById(R.id.name_edit_txt);
                TextView name_edit = (TextView) editDialogView.findViewById(R.id.name_txt);
                TextView type = (TextView) editDialogView.findViewById(R.id.type_txt);
                String zip = name.getText().toString().replaceAll("\\s+", "")+".zip";

                if(!Objects.equals(name.getText(), "")) {
                    editDialogView.findViewById(R.id.name_layout_edit).setVisibility(View.VISIBLE);
                    editDialogView.findViewById(R.id.name_layout_save).setVisibility(View.GONE);
                    name_edit.setText(" "+name.getText());
                    type_list.set(itemID, 1);
                    addons_list.set(itemID, name.getText().toString());
                    zip_list.set(itemID, zip);
                    list_adapter.notifyDataSetChanged();
                    type.setText(res.getStringArray(R.array.type_assets_array)[type_list.get(itemID)]);
                } else {Toast.makeText(getBaseContext(), res.getString(R.string.empty_name_assets_txt), Toast.LENGTH_SHORT).show(); name.setError(res.getString(R.string.empty_name_assets_txt));}
            }
        });

        edit_link_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDialogView.findViewById(R.id.link_layout_edit).setVisibility(View.GONE);
                editDialogView.findViewById(R.id.link_layout_save).setVisibility(View.VISIBLE);
                TextView link = (TextView) editDialogView.findViewById(R.id.link_edit_txt);
                link.setText(links_list.get(itemID));
            }
        });

        save_link_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView link = (TextView) editDialogView.findViewById(R.id.link_edit_txt);
                TextView link_edit = (TextView) editDialogView.findViewById(R.id.link_txt);
                TextView type = (TextView) editDialogView.findViewById(R.id.type_txt);
                if(!Objects.equals(link.getText(), "")) {
                    editDialogView.findViewById(R.id.link_layout_edit).setVisibility(View.VISIBLE);
                    editDialogView.findViewById(R.id.link_layout_save).setVisibility(View.GONE);
                    link_edit.setText(" "+link.getText());
                    type_list.set(itemID, 1);
                    links_list.set(itemID, link.getText().toString());
                    type.setText(res.getStringArray(R.array.type_assets_array)[type_list.get(itemID)]);
                } else {Toast.makeText(getBaseContext(), res.getString(R.string.empty_link_assets_txt), Toast.LENGTH_SHORT).show(); link.setError(res.getString(R.string.empty_link_assets_txt));}
            }
        });
    }

    public void SaveAddons(ArrayList<Integer> type, ArrayList<String> addons, ArrayList<String> links, ArrayList<String> zip, ArrayList<Integer> active_addons) {
        sPref.edit().putString("type", gson.toJson(type)).commit();sPref.edit().putString("addons", gson.toJson(addons)).commit();sPref.edit().putString("links", gson.toJson(links)).commit();sPref.edit().putString("zip", gson.toJson(zip)).commit();sPref.edit().putString("active_addons", gson.toJson(active_addons)).commit();
        Log.i("INFO", "Save addons JSON: "+gson.toJson(type));
    }


    public boolean CheckExist(String name) {
        for(int i=0; i<=(type_list.size()-1); i++) {
            if(type_list.get(i)==0) {
                if(Objects.equals(addons_list.get(i), name)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.assets_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_clear) {
            addons_list.clear(); type_list.clear(); active_addons_list.clear(); links_list.clear(); zip_list.clear(); list_adapter.notifyDataSetChanged();
            Toast.makeText(getBaseContext(), res.getString(R.string.clear_assest_txt), Toast.LENGTH_SHORT).show();
        } else {
            SaveAddons(type_list, addons_list, links_list, zip_list, active_addons_list);
            startActivity(new Intent(this, Main.class));
        }
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart(); SaveAddons(type_list, addons_list, links_list, zip_list, active_addons_list);
    }

    @Override
    protected void onPause() {
        super.onPause(); SaveAddons(type_list, addons_list, links_list, zip_list, active_addons_list);
    }

    @Override
    protected void onStop() {
        super.onStop(); SaveAddons(type_list, addons_list, links_list, zip_list, active_addons_list);
    }
}