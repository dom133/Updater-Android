package dom133.pl.updater;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
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
                while ((inputLine = in.readLine()) != null)
                    return inputLine;
                in.close();
            } catch (java.io.IOException e) {
                FirebaseCrash.log(e.getStackTrace().toString());
                Log.i("INFO", e.getStackTrace().toString());
                return null;
            }
            return null;
    }
}
