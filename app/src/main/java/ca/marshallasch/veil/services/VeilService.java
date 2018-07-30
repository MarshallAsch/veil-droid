package ca.marshallasch.veil.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.os.Process;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;

import ca.marshallasch.veil.MainActivity;
import ca.marshallasch.veil.R;
import ca.marshallasch.veil.controllers.RightMeshController;
import ca.marshallasch.veil.proto.DhtProto;

/**
 * Hosts all RightMesh logic on this service thread. Also receives {@link Message}s from
 * {@link ca.marshallasch.veil.MainActivity} to handle work requested from application.
 *
 * @author Weihan Li
 * @version 1.0
 * @since 2018-07-09
 */
public class VeilService extends Service {

    private RightMeshController rightMeshController;

    //Message Strings
    public static final int ACTION_VIEW_MESH_SETTINGS = 1;
    public static final int ACTION_MAIN_RESUME_MESH = 2;
    public static final int ACTION_MAIN_REFRESH_PEER_LIST = 3;
    public static final int ACTION_MAIN_REFRESH_FORUMS_LIST = 4;
    public static final int ACTION_NOTIFY_NEW_DATA = 5;

    // extra fields that can be set in the bundle to set data in the message
    public static final String EXTRA_POST = "EXTRA_POST";
    public static final String EXTRA_COMMENT = "EXTRA_COMMENT";

    //foreground notification id
    private static final int NEW_CONTENT_NOTIFICATION_ID = 1;

    /**
     * Target for clients to send messages to ServiceHandler
     */
    Messenger veilMessenger = null;

    /**
     * ServiceHandler class for the {@link VeilService}
     */
    private final class ServiceHandler extends Handler {

        /**
         * Creates the service handler on the given thread.
         *
         * @param looper the looper for the thread that the work will be run on.
         */
        ServiceHandler (Looper looper){
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
                    rightMeshController.getPeers();
                    break;
                case ACTION_MAIN_REFRESH_FORUMS_LIST:
                    rightMeshController.manualRefresh();
                    break;
                case ACTION_NOTIFY_NEW_DATA:

                    DhtProto.Post post = null;
                    DhtProto.Comment comment = null;
                    Bundle bundle = msg.getData();

                    byte[] postArray = bundle.getByteArray(EXTRA_POST);
                    byte[] commentArray = bundle.getByteArray(EXTRA_COMMENT);

                    try {
                        if (postArray != null) {
                            post = DhtProto.Post.parseFrom(postArray);
                        }

                        if (commentArray != null) {
                            comment = DhtProto.Comment.parseFrom(commentArray);
                        }
                    }
                    catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }

                    rightMeshController.notifyNewContent(post, comment);

                    showNotification("new post");

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Connects to RightMesh when service is started
     */
    @Override
    public void onCreate(){
        super.onCreate();

        //start RightMesh connection through controller using service context
        rightMeshController = new RightMeshController();
        rightMeshController.connect(this);

        //Start up thread and make it background priority so it doesn't disrupt UI
        HandlerThread veilServiceThread = new HandlerThread("VeilServiceThread",
                Process.THREAD_PRIORITY_BACKGROUND);
        veilServiceThread.start();

        veilMessenger = new Messenger(new ServiceHandler(veilServiceThread.getLooper()));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Veil service started.", Toast.LENGTH_SHORT).show();

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
     * @return the default IBinder object for the messenger
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "binding", Toast.LENGTH_SHORT).show();
        return veilMessenger.getBinder();
    }

    private void showNotification(String content){
        //create intent that will start the application
        Intent showAppIntent = new Intent(getApplicationContext(), MainActivity.class);
        showAppIntent.setAction(Intent.ACTION_MAIN);
        showAppIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification newContentNotification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_alert)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .build();
        startForeground(NEW_CONTENT_NOTIFICATION_ID, newContentNotification);

    }
}
