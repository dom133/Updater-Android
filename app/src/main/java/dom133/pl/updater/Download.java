package dom133.pl.updater;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Download {

    private Application app;

    public Download(Application app){
        this.app = app;
    }

    public String DownloadString(String url) {
            try {
                URL url2 = new URL(url);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url2.openStream()));

                String inputLine;
                String inputLine2;
                while ((inputLine = in.readLine()) != null){
                    while ((inputLine2 = in.readLine()) != null){
                        return inputLine+inputLine2;
                    }
                    return inputLine;
				}
                in.close();
            } catch (Exception e) {
                Log.i("ERROR", e.getMessage());
                return null;
            }
        return null;
    }

    @Nullable
    public static ArrayList<String> getChangelog(String link) {
        try {
            ArrayList<String> changes = new ArrayList<>();
            URL url2 = new URL(link);
            BufferedReader br = new BufferedReader(new InputStreamReader(url2.openStream()));
            String line;
            while ((line = br.readLine()) != null) {
                changes.add(line);
            }
            return changes;

        } catch (Exception e) {Log.e("ERROR", e.getMessage()); return null;}
    }
}
