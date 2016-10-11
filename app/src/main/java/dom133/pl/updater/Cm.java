package dom133.pl.updater;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Cm {

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

    public String getCMVersion(){
        String prop = getProp("ro.cm.version");
        return "cm"+Character.toString(prop.charAt(0))+Character.toString(prop.charAt(1));
    }
}