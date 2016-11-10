package dom133.pl.updater;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class Addons {

    private SharedPreferences sPref;
    private ArrayList<String> links_list = new ArrayList<>();
    private ArrayList<String> zip_list = new ArrayList<>();
    private ArrayList<Integer> active_addons_list = new ArrayList<>();
    private Gson gson = new Gson();
    private Application app;

    public Addons(Application app) {
        this.app = app;
        sPref = app.getSharedPreferences("Updater", Context.MODE_PRIVATE);
    }

    public ArrayList<String> getAddons(int type) {
        Type type_integer = new TypeToken<ArrayList<Integer>>() {}.getType(); Type type_string = new TypeToken<ArrayList<String>>() {}.getType();
        if(!Objects.equals(sPref.getString("type", null), null)) {links_list = gson.fromJson(sPref.getString("links", null), type_string);zip_list = gson.fromJson(sPref.getString("zip", null), type_string);active_addons_list = gson.fromJson(sPref.getString("active_addons", null), type_integer);}
        ArrayList<String> zip = new ArrayList<>();
        ArrayList<String> links = new ArrayList<>();

        switch(type) {
            case 0: {
                for(int i=0; i<=links.size()-1; i++) {
                    if(Objects.equals(active_addons_list.get(i), 1)) {
                        zip.add(zip_list.get(i));
                    }
                }
                return zip;
            }

            case 1: {
                for(int i=0; i<=links.size()-1; i++) {
                    if(Objects.equals(active_addons_list.get(i), 1)) {
                        links.add(zip_list.get(i));
                    }
                }
                return links;
            }
        }

        return null;
    }
}