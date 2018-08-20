package ca.marshallasch.veil.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import ca.marshallasch.veil.DataStore;

/**
 * @author Weihan Li
 * @version 1.0
 * @created on 2018-08-02
 * @name veil-droid
 */
public class DataSaverService extends IntentService {
    public static final int DataSaverSerivceID = 1120;

    private static final String TAG = "DataSaverService";

    //Services must have a no-arg constructor
    public DataSaverService(){
        super(TAG);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Toast.makeText(this, "DATA SAVER SERVICE STARTED", Toast.LENGTH_SHORT).show();
        DataStore.getInstance(this).runDataSaver();
    }
}
