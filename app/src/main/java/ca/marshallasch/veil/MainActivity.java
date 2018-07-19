package ca.marshallasch.veil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.services.VeilService;
import io.left.rightmesh.android.AndroidMeshManager;

public class MainActivity extends AppCompatActivity {
    // This port will be used for the DHT to keep all of that traffic separate
    // private static final int DISCOVERY_PORT = 9183;

    //MemoryStore instance - for storing data in local hashtable
    DataStore dataStore = null;

    /** messenger for communicating with {@link VeilService} **/
    Messenger messengerService = null;

    // flag for indicating if we have called bind on service
    boolean isBound;
    private DhtProto.User currentUser = null;

    /**
     * Class for interacting with {@link VeilService}
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        /**
         * Called when connection to service has been established.
         * @param componentName
         * @param service Client side representation of a raw IBinder object
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            messengerService = new Messenger(service);
            isBound = true;
        }

        /**
         * Handles when the service has unexpectedly crashed or disconnects
         * @param componentName
         */
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            messengerService = null;
            isBound = false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataStore = DataStore.getInstance(this);

        //starts RightMesh Service
        Intent intent = new Intent(this, VeilService.class);
        startService(intent);

        navigateTo(new FragmentLanding(), false);

    }

    /**
     * Called when activity is on screen.
     */
    @Override
    protected void onResume() {
        super.onResume();
        sendServiceMessage( VeilService.ACTION_MAIN_RESUME_MESH, null);

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

        //stopping Rightmesh service
        stopService(new Intent(this, VeilService.class));

    }

    @Override
    protected void onStart(){
        super.onStart();
        //bind to service
        bindService(new Intent(this, VeilService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        //unbind from service
        if(isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
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
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                return true;
            case R.id.connected_peers:
                frag = new FragmentPeerList();
                break;
            case R.id.setup:
                sendServiceMessage(VeilService.ACTION_VIEW_MESH_SETTINGS, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        //replace the fragment
        navigateTo(frag, true);

        return true;
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

        if (type == Sync.SyncMessageType.SYNC_DATA) {

            Log.d("DATA_SYNC", "received data sync message");

            dataStore.insertSync(message.getSyncMessage());
            Intent intent = new Intent(NEW_DATA_BROADCAST);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        } else if (type == Sync.SyncMessageType.REQUEST_DATA_V2) {

            Log.d("DATA_REQUEST_v2", "received request for data");
            // if someone sent a message asking for data send a response with everything

            Sync.SyncMessage syncMessage = dataStore.getSyncFor(event.peerUuid);

            // send messages to the peer.
            Sync.Message toSend = Sync.Message.newBuilder()
                    .setType(Sync.SyncMessageType.SYNC_DATA)
                    .setSyncMessage(syncMessage)
                    .build();

            try {
                meshManager.sendDataReliable(event.peerUuid, DATA_PORT, toSend.toByteArray());
            }
            catch (RightMeshException e1) {
                e1.printStackTrace();
            }
        } else if (type == Sync.SyncMessageType.HASH_DATA) {

            Log.d("DATA_RECEIVE",  message.getData().toString());
            dataStore.syncData(message.getData());
        } else if (type == Sync.SyncMessageType.MAPPING_MESSAGE) {
            Log.d("DATA_RECEIVE_MAP",  message.getMapping().toString());
            dataStore.syncDatabase(message.getMapping());
        } else if (type == Sync.SyncMessageType.REQUEST_DATA_V1) {

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
     * Handles peer update events from the mesh - maintains a list of peers and updates the display.
     *
     * @param e event object from mesh
     */
    private void handlePeerChanged(RightMeshEvent e) {

        // Update peer list.
        PeerChangedEvent event = (PeerChangedEvent) e;
        if (event.state != REMOVED) {

            if (event.peerUuid.equals(meshManager.getUuid())) {
                Log.d("FOUND", "found loopback: " + event.peerUuid);
                return;
            }

            Log.d("FOUND", "found user: " + event.peerUuid);

            // send messages to the peer requesting their data.
            try {

                Sync.Message message = Sync.Message.newBuilder()
                        .setType(Sync.SyncMessageType.REQUEST_DATA_V2)
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


    /**
     * Sends messages to {@link VeilService} to do work with.
     * @param command non optional command int defined by static strings in {@link VeilService}
     */
    public void sendServiceMessage(int command, @Nullable Bundle bundle ){
        //return if the service is not bound
        if(!isBound) return;

        Message msg = Message.obtain(null, command, 0, 0);
        msg.setData(bundle);
        try {
            messengerService.send(msg);
        } catch (RemoteException e){
            e.printStackTrace();
        }

    }

}
