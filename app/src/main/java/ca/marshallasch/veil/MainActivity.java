package ca.marshallasch.veil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
    //System pref name string
    public static final String SYSTEM_PREF = "SYSTEM_PREF";
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
        SharedPreferences preferences = getSharedPreferences(SYSTEM_PREF, MODE_PRIVATE);

        if(preferences.getBoolean(FragmentSettings.IS_DARK_THEME_TOGGLED, false)) {
            setTheme(R.style.AppTheme_Dark);
        }
        else{
            setTheme(R.style.AppTheme_Light);
        }
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
            case R.id.app_settings:
                frag = new FragmentSettings();
                break;
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
     * @param bundle the optional bundle to include in the message
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
