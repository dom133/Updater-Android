package dom133.pl.updater;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MySQL {

    private static String api = "http://app-updater.pl/insert-mysql.php";
    JSONParser jsonParser = new JSONParser();


    public String add(String type, String nick, String email, String title, String contents) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("type", type));
        if(!Objects.equals(nick, ""))params.add(new BasicNameValuePair("nick", nick));
        else params.add(new BasicNameValuePair("nick", "null"));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("title", title));
        params.add(new BasicNameValuePair("contents", contents));
        params.add(new BasicNameValuePair("version", BuildConfig.VERSION_NAME));

        JSONObject json = jsonParser.makeHttpRequest(api, "POST", params);
        try {
            Log.i("INFO", "Json: "+json.toString());
            if(json.getInt("success")==1) {
                return json.getString("message");
            } else {
                return json.getString("message");
            }
        } catch(Exception e) {
            Log.e("INFO", e.getMessage());
            return "Wystąpił nie oczekiwany blad!!!";
        }
    }
}

