package ca.marshallasch.veil.controllers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.HashSet;
import java.util.UUID;

import ca.marshallasch.veil.DataStore;
import ca.marshallasch.veil.FragmentSettings;
import ca.marshallasch.veil.MainActivity;
import ca.marshallasch.veil.R;
import ca.marshallasch.veil.database.Database;
import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.proto.Sync;
import ca.marshallasch.veil.utilities.Util;
import io.left.rightmesh.android.AndroidMeshManager;
import io.left.rightmesh.id.MeshId;
import io.left.rightmesh.mesh.MeshManager;
import io.left.rightmesh.mesh.MeshStateListener;
import io.left.rightmesh.util.RightMeshException;

import static android.content.Context.NOTIFICATION_SERVICE;
import static ca.marshallasch.veil.FragmentSettings.PREF_NOTIFY_COMMENT;
import static ca.marshallasch.veil.FragmentSettings.PREF_NOTIFY_POST;
import static ca.marshallasch.veil.database.SyncStatsContract.SYNC_MESSAGE_V1;
import static ca.marshallasch.veil.database.SyncStatsContract.SYNC_MESSAGE_V2;
import static ca.marshallasch.veil.proto.Sync.SyncMessageType.REQUEST_DATA_V1;
import static ca.marshallasch.veil.proto.Sync.SyncMessageType.REQUEST_DATA_V2;
import static io.left.rightmesh.mesh.MeshManager.ADDED;
import static io.left.rightmesh.mesh.MeshManager.DATA_RECEIVED;
import static io.left.rightmesh.mesh.MeshManager.PEER_CHANGED;
import static io.left.rightmesh.mesh.MeshManager.REMOVED;
import static io.left.rightmesh.mesh.MeshManager.UPDATED;



/**
 *
 * All RightMesh logic is abstracted into this class
 *
 * @author Weihan Li
 * @version 1.0
 * @since 2018-07-09
 */
public class RightMeshController implements MeshStateListener{
    //Broadcast public strings
    public static final String NEW_DATA_BROADCAST = "ca.marshallasch.veil.controllers.NEW_DATA_BROADCAST";
    public static final String GET_PEERS_BROADCAST = "ca.marshallasch.veil.controllers.GET_PEERS_BROADCAST";

    //the key for access the list of peers in the broadcast message
    public static final String EXTRA_PEERS_LIST = "ca.marshallasch.veil.controllers.EXTRA_PEERS_LIST";

    private static final int DATA_PORT = 9182;

    // MeshManager instance - interface to the mesh network.
    private AndroidMeshManager meshManager = null;
    private Context serviceContext = null;

    private SharedPreferences preferences;
    private DataStore dataStore = null;

    private final HashSet<MeshId> discovered = new HashSet<>();


    //Notification intent action
    public static final String NOTIFICATION_ACTION = "NOTIFICATION_ACTION";

    /**
     * Get a {@link AndroidMeshManager} instance and/or start RightMesh if it isn't already
     * @param context service context to bind to
     */
    public void connect(Context context){
        meshManager = AndroidMeshManager.getInstance(context, RightMeshController.this);
        dataStore = DataStore.getInstance(context);
        serviceContext = context;

        preferences = PreferenceManager.getDefaultSharedPreferences(serviceContext);

    }

    /**
     * Close the RightMesh connection and/or stopping the service if the app is no longer using
     * RightMesh
     */
    public void disconnect(){
        try{
            if(meshManager != null){
                meshManager.stop();
            }
        } catch (RightMeshException e){
            Log.e("RightmeshController", "MeshManager Disconnect Failure.");
            e.printStackTrace();
        }
    }

    /**
     * Handles incoming data events from the mesh
     *
     * @param e event object from mesh
     */
    private void handleDataReceived(MeshManager.RightMeshEvent e) {
        final MeshManager.DataReceivedEvent event = (MeshManager.DataReceivedEvent) e;

        Sync.Message message;
        try {
            message = Sync.Message.parseFrom(event.data);
        }
        catch (InvalidProtocolBufferException e1) {
            e1.printStackTrace();
            return;
        }

        Intent intent = new Intent(NEW_DATA_BROADCAST);
        Sync.Message toSend;
        Sync.SyncMessage syncMessage;

        // check what message was received and do the appropriate action.
        switch (message.getType()){
            case SYNC_DATA_V1:
                Log.d("SYNC_DATA_V1", "received data sync message");
            case SYNC_DATA_V2:
                Log.d("SYNC_DATA_V2", "received data sync message (v2 or v1)");

                syncMessage = message.getSyncMessage();
                Database.getInstance(serviceContext).updateLogSync(message.getDataID(), message.getSerializedSize(), syncMessage.getEntriesCount());
                dataStore.insertSync(syncMessage);
                LocalBroadcastManager.getInstance(serviceContext).sendBroadcast(intent);
                break;
            case NEW_CONTENT:
                Log.d("NEW_CONTENT", "received new content");
                Sync.NewContent newContent = message.getNewContent();

                // get the post or comment from the message
                DhtProto.Post post = newContent.getPost();
                DhtProto.Comment comment = newContent.getComment();

                if (comment != null) {
                    dataStore.saveComment(comment);

                    // notify anyone interested that the data store has been updated.
                    LocalBroadcastManager.getInstance(serviceContext).sendBroadcast(intent);

                    String authorName = comment.getAnonymous() ? serviceContext.getString(R.string.anonymous) : comment.getAuthorName();

                    if (preferences.getBoolean(PREF_NOTIFY_COMMENT, true)) {
                        showNotification(comment.getMessage(), authorName);
                    }
                } else if (post != null) {
                    dataStore.savePost(post);

                    String authorName = post.getAnonymous() ? serviceContext.getString(R.string.anonymous) : post.getAuthorName();

                    if (preferences.getBoolean(PREF_NOTIFY_POST, true)) {
                        showNotification(post.getTitle(), authorName);
                    }

                    // notify anyone interested that the data store has been updated.
                    LocalBroadcastManager.getInstance(serviceContext).sendBroadcast(intent);
                } else {
                    Log.d("INVALID_CONTENT", "New content message is missing content");
                }

                break;
            case REQUEST_DATA_V1:

                Log.d("REQUEST_DATA_V1", "received request for data");
                // if someone sent a message asking for data send a response with everything

                syncMessage = dataStore.getSync(event.peerUuid, SYNC_MESSAGE_V1);

                // send messages to the peer.
                toSend = Sync.Message.newBuilder()
                        .setType(Sync.SyncMessageType.SYNC_DATA_V1)
                        .setSyncMessage(syncMessage)
                        .setDataID(message.getDataID())
                        .build();

                try {
                    meshManager.sendDataReliable(event.peerUuid, DATA_PORT, toSend.toByteArray());
                }
                catch (RightMeshException e1) {
                    e1.printStackTrace();
                }

                break;
            case REQUEST_DATA_V2:

                Log.d("REQUEST_DATA_V2", "received request for data");
                // if someone sent a message asking for data send a response with everything

                syncMessage = dataStore.getSync(event.peerUuid, SYNC_MESSAGE_V2);

                // send messages to the peer.
                toSend = Sync.Message.newBuilder()
                        .setType(Sync.SyncMessageType.SYNC_DATA_V2)
                        .setSyncMessage(syncMessage)
                        .setDataID(message.getDataID())
                        .build();

                try {
                    meshManager.sendDataReliable(event.peerUuid, DATA_PORT, toSend.toByteArray());
                }
                catch (RightMeshException e1) {
                    e1.printStackTrace();
                }
                break;
            default:
                Log.d("UNKNOWN_COMMAND", "handleDataReceived: unknown command received");
        }
    }

    /**
     * Called by the {@link MeshService} when the mesh state changes. Initializes mesh connection
     * on first call.
     *
     * @param meshId our own user id on first detecting
     * @param state state which indicates SUCCESS or an error code
     */
    @Override
    public void meshStateChanged(MeshId meshId, int state) {
        switch(state) {
            case SUCCESS: // Begin connecting

                try {
                    meshManager.bind(DATA_PORT);

                    meshManager.on(DATA_RECEIVED, this::handleDataReceived);
                    meshManager.on(PEER_CHANGED, this::handlePeerChanged);

                } catch (RightMeshException e) {
                    String status = R.string.error_initializing_the_library + e.toString();
                    e.printStackTrace();
                    Toast.makeText(serviceContext, status, Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("MESH", "initialized");
            case FAILURE:  // Mesh connection unavailable,
            case DISABLED: // time for Plan B.
                break;
        }
    }

    /**
     * Handles peer update events from the mesh - maintains a list of peers and updates the display.
     *
     * @param e event object from mesh
     */
    private void handlePeerChanged(MeshManager.RightMeshEvent e) {

        // Update peer list.
        MeshManager.PeerChangedEvent event = (MeshManager.PeerChangedEvent) e;


        if (!discovered.contains(event.peerUuid) && (event.state == ADDED || event.state == UPDATED)) {
            discovered.add(event.peerUuid);
        } else if (event.state == REMOVED) {
            discovered.remove(event.peerUuid);
        }


        if (event.state != REMOVED) {

            if (event.peerUuid.equals(meshManager.getUuid())) {
                Log.d("FOUND", "found loopback: " + event.peerUuid);
                return;
            }

            Log.d("FOUND", "found user: " + event.peerUuid);

            // send messages to the peer.
            try {

                int syncVersion = preferences.getInt(FragmentSettings.PREF_SYNC_VERSION, SYNC_MESSAGE_V2);

                Sync.Message message = Sync.Message.newBuilder()
                        .setType(syncVersion == SYNC_MESSAGE_V2 ? REQUEST_DATA_V2 : REQUEST_DATA_V1)
                        .setDataID(UUID.randomUUID().toString())
                        .build();

                Database.getInstance(serviceContext).logSync(message.getDataID(), event.peerUuid.toString(), message.getSerializedSize(), syncVersion);

                meshManager.sendDataReliable(event.peerUuid, DATA_PORT, message.toByteArray());
            }
            catch (RightMeshException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Requests data from connected peers over the mesh and sends a broadcast notifying that
     * data has been updated.
     */
    public void manualRefresh(){
        try {
            MeshManager manager = this.meshManager;
            MeshId[] peers = discovered.toArray(new MeshId[0]);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(serviceContext);
            int syncVersion = preferences.getInt(FragmentSettings.PREF_SYNC_VERSION, SYNC_MESSAGE_V2);

            Sync.Message dataRequest = Sync.Message.newBuilder()
                    .setType(syncVersion == SYNC_MESSAGE_V2 ? REQUEST_DATA_V2 : REQUEST_DATA_V1)
                    .setDataID(UUID.randomUUID().toString())
                    .build();

            // request an update from everyone
            for (MeshId peer: peers) {

                // do not ask myself for info
                if (peer.equals(manager.getUuid())) {
                    continue;
                }

                Database.getInstance(serviceContext).logSync(dataRequest.getDataID(), peer.toString(), dataRequest.getSerializedSize(), syncVersion);
                manager.sendDataReliable(peer, DATA_PORT, dataRequest.toByteArray());
            }

        }
        catch (RightMeshException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function will notify all connected devices that a new post or a new comment was created.
     * @param post required post object
     * @param comment optional comment object, if this is null then it is notifying of a new post
     *                not a new comment
     */
    public void notifyNewContent(@NonNull DhtProto.Post post, @Nullable DhtProto.Comment comment) {

        // notify other users that there is a new comment or new post
        try {
            MeshId[] peers = discovered.toArray(new MeshId[0]);

            Sync.NewContent.Builder builder = Sync.NewContent.newBuilder();

            builder.setPost(post);

            if (comment != null) {
                builder.setComment(comment);
            }

            Sync.NewContent newContent = builder.build();

            Sync.Message message = Sync.Message.newBuilder()
                    .setType(Sync.SyncMessageType.NEW_CONTENT)
                    .setNewContent(newContent)
                    .build();

            // request an update from everyone
            for (MeshId peer: peers) {

                // do not send message to myself
                if (peer.equals(meshManager.getUuid())) {
                    continue;
                }
                meshManager.sendDataReliable(peer, DATA_PORT, message.toByteArray());
            }
        }
        catch (RightMeshException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a set of {@link MeshId} objects as a serialized set over a local broadcast.
     */
    public void getPeers(){
        MeshId[] peers = discovered.toArray(new MeshId[0]);

        Intent intent = new Intent(GET_PEERS_BROADCAST);
        //MeshId is serializable
        //Ref: https://developer.rightmesh.io/api/latest/reference/io/left/rightmesh/id/MeshID.php
        intent.putExtra(EXTRA_PEERS_LIST, peers);
        LocalBroadcastManager.getInstance(serviceContext).sendBroadcast(intent);

    }

    /**
     * Shows activity for RightMesh Settings
     */
    public void showMeshSettings(){
        try {
            meshManager.showSettingsActivity();
        } catch (RightMeshException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resumes Mesh functionality
     */
    public void resumeMeshManager(){
        try {
            meshManager.resume();
        } catch (RightMeshException e){
            e.printStackTrace();
        }
    }

    /**
     * Sends a notification to the foreground.
     * @param postTitle the title of the notification
     * @param postAuthor the content of the notification
     */
    private void showNotification(String postTitle, String postAuthor){
        //create intent that will start the application
        Intent showAppIntent = new Intent(serviceContext.getApplicationContext(), MainActivity.class);
        showAppIntent.setAction(NOTIFICATION_ACTION);
        showAppIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                serviceContext.getApplicationContext(),
                Util.getRandomRequestCode(),
                showAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager mNotifyManager = (NotificationManager) serviceContext.getSystemService(NOTIFICATION_SERVICE);

        Notification newContentNotification = new Notification.Builder(serviceContext.getApplicationContext())
                .setContentTitle(postTitle)
                .setContentText(serviceContext.getString(R.string.by) +" "+ postAuthor)
                .setSmallIcon(R.drawable.ic_alert)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();
        mNotifyManager.notify(Util.getRandomRequestCode(), newContentNotification);

    }
}
