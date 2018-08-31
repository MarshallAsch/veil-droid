package ca.marshallasch.veil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Weihan Li
 * @version 1.0
 * @since 2018-08-16
 *
 * This class handles alarm requests.
 */
public class AlarmReceiver extends BroadcastReceiver{

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("ALARM_RECEIVER: ", "PING!");

        this.context = context;


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean runDataSaver = preferences.getBoolean(FragmentSettings.PREF_MEMORY_SAVER, false);

        if (runDataSaver) {
            new DataSaver().execute();
        }
    }

    /**
     * This class will run the data deletion in the background every time the alarm gets fired.
     */
    private class DataSaver extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute()
        {
            Toast.makeText(context, "DATA SAVER SERVICE STARTED", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            DataStore.getInstance(context).runDataSaver();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Log.d("DATA SAVER", "done deletion");
        }
    }
}
