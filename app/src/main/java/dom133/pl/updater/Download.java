package dom133.pl.updater;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Download {

    public String DownloadString(String url)
    {
        try {
            URL url2 = new URL(url);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url2.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                return inputLine;
            in.close();
        } catch(java.io.IOException e){
            Log.i("INFO", e.getMessage());
            return null;
        }
        return null;
    }
}
