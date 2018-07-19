package ca.marshallasch.veil.controllers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.Serializable;
import java.util.Set;

import ca.marshallasch.veil.DataStore;
import ca.marshallasch.veil.R;
import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.proto.Sync;
import io.left.rightmesh.android.AndroidMeshManager;
import io.left.rightmesh.android.MeshService;
import io.left.rightmesh.id.MeshId;
import io.left.rightmesh.mesh.MeshManager;
import io.left.rightmesh.mesh.MeshStateListener;
import io.left.rightmesh.util.RightMeshException;

import static io.left.rightmesh.mesh.MeshManager.DATA_RECEIVED;
import static io.left.rightmesh.mesh.MeshManager.PEER_CHANGED;
import static io.left.rightmesh.mesh.MeshManager.REMOVED;

/**
 *
 * All RightMesh logic is abstracted into this class
 *
 * @author Weihan Li
 * @version 1.0
 * @created on 2018-07-09
 * @name veil-droid
 */

public class RightMeshController implements MeshStateListener{
    //Broadcast public strings
    public static final String NEW_DATA_BROADCAST = "ca.marshallasch.veil.controllers.NEW_DATA_BROADCAST";
    public static final String GET_PEERS_BROADCAST = "ca.marshallasch.veil.controllers.GET_PEERS_BROADCAST";

    //intent filter public strings
    public static final String PEERS_LIST = "ca.marshallasch.veil.controllers.PEERS_LIST";

    // MeshManager instance - interface to the mesh network.
    AndroidMeshManager meshManager = null;
    Context serviceContext = null;
    public static final int DATA_PORT = 9182;

    //MemoryStore instance - for storing data in local HashTable
    DataStore dataStore = null;

    /**
     * Get a {@link AndroidMeshManager} instance and/or start RightMesh if it isn't already
     * @param context service context to bind to
     */
    public void connect(Context context){
        meshManager = AndroidMeshManager.getInstance(context, RightMeshController.this);
        dataStore = DataStore.getInstance(context);
        serviceContext = context;
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
            Log.e("RightmeshContoller", "MeshManager Disconnect Failure.");
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

        Sync.SyncMessageType type = message.getType();

        if (type == Sync.SyncMessageType.HASH_DATA) {

            Log.d("DATA_RECEIVE", message.getData().toString());
            dataStore.syncData(message.getData());
        } else if (type == Sync.SyncMessageType.MAPPING_MESSAGE) {
            Log.d("DATA_RECEIVE_MAP", message.getMapping().toString());

            dataStore.syncDatabase(message.getMapping());

            // notify anyone interested that the data store has been updated.
            Intent intent = new Intent(NEW_DATA_BROADCAST);
            LocalBroadcastManager.getInstance(serviceContext).sendBroadcast(intent);
        } else if (type == Sync.SyncMessageType.REQUEST_DATA) {

            Log.d("DATA_REQUEST", "recived request for data");
            // if someone sent a message asking for data send a responce with everything

            Sync.HashData hashData = dataStore.getDataStore();
            Sync.MappingMessage mappingMessage = dataStore.getDatabase();

            // send messages to the peer.
            Sync.Message toSend = Sync.Message.newBuilder()
                    .setType(Sync.SyncMessageType.HASH_DATA)
                    .setData(hashData)
                    .build();

            try {
                meshManager.sendDataReliable(event.peerUuid, DATA_PORT, toSend.toByteArray());

                toSend = Sync.Message.newBuilder()
                        .setType(Sync.SyncMessageType.MAPPING_MESSAGE)
                        .setMapping(mappingMessage)
                        .build();

                meshManager.sendDataReliable(event.peerUuid, DATA_PORT, toSend.toByteArray());

            }
            catch (RightMeshException e1) {
                e1.printStackTrace();
            }
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
                Log.d("MESH", "initilized");
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
        if (event.state != REMOVED) {

            if (event.peerUuid.equals(meshManager.getUuid())) {
                Log.d("FOUND", "found loopback: " + event.peerUuid);
                return;
            }

            Log.d("FOUND", "found user: " + event.peerUuid);



            Sync.HashData hashData = dataStore.getDataStore();
            Sync.MappingMessage mappingMessage =  dataStore.getDatabase();

            // send messages to the peer.
            try {

                Sync.Message message = Sync.Message.newBuilder()
                        .setType(Sync.SyncMessageType.HASH_DATA)
                        .setData(hashData)
                        .build();
                meshManager.sendDataReliable(event.peerUuid, DATA_PORT, message.toByteArray());

                message = Sync.Message.newBuilder()
                        .setType(Sync.SyncMessageType.MAPPING_MESSAGE)
                        .setMapping(mappingMessage)
                        .build();
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
            Set<MeshId> peers = manager.getPeers(this.DATA_PORT);

            Sync.Message dataRequest = Sync.Message.newBuilder().setType(Sync.SyncMessageType.REQUEST_DATA).build();

            // request an update from everyone
            for (MeshId peer: peers) {

                // do not ask myself for info
                if (peer.equals(manager.getUuid())) {
                    continue;
                }
                manager.sendDataReliable(peer, this.DATA_PORT, dataRequest.toByteArray());
            }

        }
        catch (RightMeshException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(RightMeshController.NEW_DATA_BROADCAST);
        LocalBroadcastManager.getInstance(serviceContext).sendBroadcast(intent);
    }


    public void notifyNewContent(@NonNull DhtProto.Post post, @Nullable DhtProto.Comment comment) {

        // notify other users that there is a new comment or new post
        try {
            Set<MeshId> peers = meshManager.getPeers(DATA_PORT);

            Sync.NewContent.Builder builder = Sync.NewContent.newBuilder();

            builder.setPost(post);

            if (comment != null) {
                builder.setComment(comment);
            }

            Sync.NewContent newContent = builder.build();

            Sync.Message dataRequest = Sync.Message.newBuilder()
                    .setType(Sync.SyncMessageType.NEW_CONTENT)
                    .setNewContent(newContent)
                    .build();

            // request an update from everyone
            for (MeshId peer: peers) {

                // do not ask myself for info
                if (peer.equals(meshManager.getUuid())) {
                    continue;
                }
                meshManager.sendDataReliable(peer, DATA_PORT, dataRequest.toByteArray());
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
        Set<MeshId> peers = null;
        try{
            peers = meshManager.getPeers(DATA_PORT);
        } catch (RightMeshException e){
            e.printStackTrace();
        }

        Intent intent = new Intent(this.GET_PEERS_BROADCAST);
        //MeshId is serializable
        //Ref: https://developer.rightmesh.io/api/latest/reference/io/left/rightmesh/id/MeshID.php
        intent.putExtra(this.PEERS_LIST, (Serializable) peers);
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
}
