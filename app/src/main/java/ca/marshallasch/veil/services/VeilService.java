package ca.marshallasch.veil.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.os.Process;

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

    /**
     * ServiceHandler class for the {@link VeilService}
     */
    private final class ServiceHandler extends Handler {
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
            //TODO: handle msg

            // Stop the service using the startId,
            // prevent stopping in middle of handling another job
            stopSelf(msg.arg1);
        }
    }


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
     * ??????????????????
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //no binding at the moment
        return null;
    }
}
