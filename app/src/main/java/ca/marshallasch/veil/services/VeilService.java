package ca.marshallasch.veil.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;

import ca.marshallasch.veil.controllers.RightMeshController;

/**
 * @author Weihan Li
 * @version 1.0
 * @created on 2018-07-09
 * @name veil-droid
 */
public class VeilService extends IntentService {
    public static final String START_FOREGROUND_ACTION = "ca.marshallasch.veil.action.startforeground";
    public static final String STOP_FOREGROUND_ACTION = "ca.marshallasch.veil.action.stopforeground";
    public static final int FOREGROUND_SERVICE_ID = 101;

    private RightMeshController rightMeshController;
    /**
     * Default constructor that names the worker thread
     */
    public VeilService() {
        super("VeilService");
    }

    /**
     * Connects to RightMesh when service is started
     */
    @Override
    public void onCreate(){
        super.onCreate();
        Intent stopForegroundIntent = new Intent(this, VeilService.class);
        stopForegroundIntent.setAction(STOP_FOREGROUND_ACTION);
        PendingIntent pendingIntent
                = PendingIntent.getService(this, 0, stopForegroundIntent, 0);

        //start RightMesh connection through controller using service context
        rightMeshController = new RightMeshController();
        rightMeshController.connect(this);
    }

    /**
     * Disconnects from RightMesh when service is stopped
     */

    @Override
    public void onDestroy(){
        super.onDestroy();
        rightMeshController.disconnect();
        rightMeshController = null;

        //force kill service so it has no chance of recreating itself
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * IntentService calls this method from the default worker thread
     * @param intent the intent that starts this service
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //TODO: add in work
    }
}
