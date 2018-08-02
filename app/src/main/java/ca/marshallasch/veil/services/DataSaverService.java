package ca.marshallasch.veil.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * @author Weihan Li
 * @version 1.0
 * @created on 2018-08-02
 * @name veil-droid
 */
public class DataSaverService extends IntentService {
    private static final String TAG = "DataSaverService";

    //Services must have a no-arg constructor
    public DataSaverService(){
        super(TAG);

    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
