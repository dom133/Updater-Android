package dom133.pl.updater;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
                Log.i("INFO", e.getMessage());
                return null;
            }
        return null;
    }

    public static ArrayList<String> getChangelog() {
        try {
            ArrayList<String> changes = new ArrayList<>();
            URL url2 = new URL("http://app-updater.pl/updates/txt/changelog.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(url2.openStream()));
            String line;
            while ((line = br.readLine()) != null) {
                changes.add(line);
            }
            return changes;

        } catch (Exception e) {Log.e("ERROR", e.getMessage()); return null;}
    }

    public String getProp(String name) {

        try {
            Process process = Runtime.getRuntime().exec("getprop "+name);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();
            process.waitFor();

            String out = output.toString();
            out = out.replaceAll("\\s+","");
            return out;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
