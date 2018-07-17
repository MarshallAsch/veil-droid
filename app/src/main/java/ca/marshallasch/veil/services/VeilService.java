package ca.marshallasch.veil.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import ca.marshallasch.veil.controllers.RightMeshController;

/**
 * @author Weihan Li
 * @version 1.0
 * @created on 2018-07-09
 * @name veil-droid
 */
public class VeilService extends Service {

    private RightMeshController rightMeshController;
    private Looper veilServiceLooper;
    private ServiceHandler veilServiceHandler;

    //Message Strings
    public static final int ACTION_VIEW_MESH_SETTINGS = 1;
    public static final int ACTION_MAIN_RESUME_MESH = 2;
    public static final int ACTION_MAIN_REFRESH_PEER_LIST = 3;


    /**
     * ServiceHandler class for the {@link VeilService}
     */
    private final class ServiceHandler extends Handler {
        public ServiceHandler() {

        }

        /**
         * function for creating the ServiceHandler
         * @param looper thread's looper
         */
        public ServiceHandler(Looper looper){
            super(looper);
        }

        /**
         * Where messages to do work on the thread is processed
         * @param msg work message
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ACTION_VIEW_MESH_SETTINGS:
                    rightMeshController.showMeshSettings();
                    break;
                case ACTION_MAIN_RESUME_MESH:
                    rightMeshController.resumeMeshManager();
                    break;
                case ACTION_MAIN_REFRESH_PEER_LIST:
                    sendLocalBroadcast(rightMeshController.getPeers());
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target for clients to send messages to ServiceHandler
     */
    final Messenger veilMessenger = new Messenger(new ServiceHandler());


    /**
     * Connects to RightMesh when service is started
     */
    @Override
    public void onCreate(){
        super.onCreate();

        //Start up thread and make it background priority so it doesn't disrupt UI
        HandlerThread veilServiceThread = new HandlerThread("VeilServiceThread",
                Process.THREAD_PRIORITY_BACKGROUND);
        veilServiceThread.start();

        //Get the handlerThread's Looper and use it for handler
        veilServiceLooper = veilServiceThread.getLooper();
        veilServiceHandler = new ServiceHandler(veilServiceLooper);


        //start RightMesh connection through controller using service context
        rightMeshController = new RightMeshController();
        rightMeshController.connect(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Veil service started.", Toast.LENGTH_SHORT).show();

        Message msg = veilServiceHandler.obtainMessage();
        msg.arg1 = startId;
        veilServiceHandler.sendMessage(msg);

        return START_STICKY;
    }

    /**
     * Disconnects from RightMesh when service is stopped
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(this, "Veil service ended.", Toast.LENGTH_SHORT).show();
        rightMeshController.disconnect();
        rightMeshController = null;
        //force kill service so it has no chance of recreating itself
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * Allows for binding to {@link VeilService} returning an interface to the messenger
     * @param intent an intent call from the client side
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "binding", Toast.LENGTH_SHORT).show();
        return veilMessenger.getBinder();
    }

    /**
     * Uses the service's instance to send a local broadcast informing listeners that a requested
     * task is complete. It also sends back data if the service call requested data.
     * @param intent the intent that holds the message and/or data.
     */
    private void sendLocalBroadcast(Intent intent){
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
