package ca.marshallasch.veil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author Weihan Li
 * @version 1.0
 * @created on 2018-08-16
 * @name veil-droid
 *
 * This class handles alarm requests.
 */
public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("ALARMRECEIVER: ", "PING!");
    }
}
