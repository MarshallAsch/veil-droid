package ca.marshallasch.veil;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;

import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.proto.Sync;
import io.left.rightmesh.android.AndroidMeshManager;
import io.left.rightmesh.android.MeshService;
import io.left.rightmesh.id.MeshId;
import io.left.rightmesh.mesh.MeshManager.DataReceivedEvent;
import io.left.rightmesh.mesh.MeshManager.PeerChangedEvent;
import io.left.rightmesh.mesh.MeshManager.RightMeshEvent;
import io.left.rightmesh.mesh.MeshStateListener;
import io.left.rightmesh.util.RightMeshException;

import static io.left.rightmesh.mesh.MeshManager.DATA_RECEIVED;
import static io.left.rightmesh.mesh.MeshManager.PEER_CHANGED;
import static io.left.rightmesh.mesh.MeshManager.REMOVED;

public class MainActivity extends AppCompatActivity implements MeshStateListener
{

    public static final String NEW_DATA_BROADCAST = "ca.marshallasch.veil.NEW_DATA_BROADCAST";

    public static final int DATA_PORT = 9182;
    // private static final int DISCOVERY_PORT = 9183;       // This port will be used for the DHT
                                                            // to keep all of that traffic separate

    // MeshManager instance - interface to the mesh network.
    AndroidMeshManager meshManager = null;

    //MemoryStore instance - for storing data in local hashtable
    DataStore dataStore = null;

    private DhtProto.User currentUser = null;

    private boolean meshActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataStore = DataStore.getInstance(this);

        navigateTo(new FragmentLanding(), false);

        // Gets an instance of the Android-specific MeshManager singleton.
        meshManager = AndroidMeshManager.getInstance(this, this);
    }

    /**
     * Called when activity is on screen.
     */
    @Override
    protected void onResume() {
        try {
            super.onResume();
            meshManager.resume();
        } catch (MeshService.ServiceDisconnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the app is being closed (not just navigated away from). Shuts down
     * the {@link AndroidMeshManager} instance.
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        dataStore.save(this);
        dataStore.close();

        try {
            meshManager.stop();
        }
        catch (MeshService.ServiceDisconnectedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        dataStore.save(this);
    }

    /**
     * Creates the menu bar in the activity pane
     *
     * @param menu the menu that it is being inflated into
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * The click listener for the menu items.
     * Gets called when an item in the main menu is clicked.
     *
     * @param item The menu item that was selected
     * @return true when an action is done
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Fragment frag;

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.connected_peers:
                frag = new FragmentPeerList();
                break;
            case R.id.setup:
                try {
                    meshManager.showSettingsActivity();
                }
                catch (RightMeshException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        //replace the fragment
        navigateTo(frag, true);

        return true;
    }

    /**
     * Called by the {@link MeshService} when the mesh state changes. Initializes mesh connection
     * on first call.
     *
     * @param meshId our own user id on first detecting
     * @param state state which indicates SUCCESS or an error code
     */
    @Override
    public void meshStateChanged(MeshId meshId, int state)
    {
        switch(state) {
            case SUCCESS: // Begin connecting

                try {
                    meshManager.bind(DATA_PORT);

                    meshManager.on(DATA_RECEIVED, this::handleDataReceived);
                    meshManager.on(PEER_CHANGED, this::handlePeerChanged);

                } catch (RightMeshException e) {
                    String status = "Error initializing the library" + e.toString();
                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("MESH", "initilized");

            case RESUME:  // over the mesh!

                meshActive = true;
                break;
            case FAILURE:  // Mesh connection unavailable,
            case DISABLED: // time for Plan B.
                break;
        }
    }

    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackStack Whether or not the current fragment should be added to the back stack.
     */
    public void navigateTo(Fragment fragment, boolean addToBackStack){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(addToBackStack){
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    /**
     * Animates the given fragment up from the bottom and on pop animates it back down.
     * NOTE: this function does not replace the current fragment but overlays it.
     *
     * @param fragment the fragment to overlay with
     * @param addToBackStack Whether or not the current fragment should be added to the back stack.
     */
    public void animateFragmentSlide(Fragment fragment, boolean addToBackStack){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(addToBackStack){
            transaction.addToBackStack(null);
        }
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.do_nothing, R.anim.do_nothing, R.anim.slide_in_down);
        //note fragment is layered due to this
        transaction.add(R.id.fragment_container, fragment);
        transaction.commit();
    }


    /**
     * Handles incoming data events from the mesh
     *
     * @param e event object from mesh
     */
    private void handleDataReceived(RightMeshEvent e)
    {
        // TODO: 2018-05-28 Add in logic to handle the incoming data
        final DataReceivedEvent event = (DataReceivedEvent) e;

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
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else if (type == Sync.SyncMessageType.REQUEST_DATA) {

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
     * Handles peer update events from the mesh - maintains a list of peers and updates the display.
     *
     * @param e event object from mesh
     */
    private void handlePeerChanged(RightMeshEvent e) {

        // Update peer list.
        PeerChangedEvent event = (PeerChangedEvent) e;
        if (event.state != REMOVED) {
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
     * Gets the current user.
     * @return currentUser
     */
    @Nullable
    public DhtProto.User getCurrentUser()
    {
        return currentUser;
    }

    /**
     * Sets the current users, called when logging in.
     * @param currentUser the user who is logged in
     */
    public void setCurrentUser(@NonNull DhtProto.User currentUser)
    {
        this.currentUser = currentUser;
    }
}
