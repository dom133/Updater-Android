package dom133.pl.updater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootUpReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent VersionChecker = new Intent(context, VersionChecker.class);
            context.startService(VersionChecker);
        }
    }

}
