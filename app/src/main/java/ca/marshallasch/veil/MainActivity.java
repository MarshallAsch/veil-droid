package ca.marshallasch.veil;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashSet;

import io.left.rightmesh.android.AndroidMeshManager;
import io.left.rightmesh.android.MeshService;
import io.left.rightmesh.id.MeshId;
import io.left.rightmesh.mesh.MeshManager.PeerChangedEvent;
import io.left.rightmesh.mesh.MeshManager.RightMeshEvent;
import io.left.rightmesh.mesh.MeshStateListener;
import io.left.rightmesh.util.RightMeshException;

import static io.left.rightmesh.mesh.MeshManager.DATA_RECEIVED;
import static io.left.rightmesh.mesh.MeshManager.PEER_CHANGED;
import static io.left.rightmesh.mesh.MeshManager.REMOVED;

public class MainActivity extends AppCompatActivity implements MeshStateListener
{

    private static final int DATA_PORT = 9182;
   // private static final int DISCOVERY_PORT = 9183;       // This is not used yet, would be for
                                                            // the local doc discovery network
    // Set to keep track of peers connected to the mesh.
    HashSet<MeshId> users = new HashSet<>();

    // MeshManager instance - interface to the mesh network.
    AndroidMeshManager meshManager = null;

    private boolean meshActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       // getFragmentManager().beginTransaction().add(R.id.fragment_container,  /* Put the frag here*/).commit();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new FragmentLanding());
        transaction.commit();

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
        Fragment frag = null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sign_up:
                frag = new SignUp();
                break;
            case R.id.login:
                frag = new Fragment();
                break;
            case R.id.landing:
                frag = new FragmentLanding();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        //replace the fragment
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_container, frag);
        transaction.commit();

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
     * @param addToBackStack Whether or not the current fragment should be added to the backstack.
     */
    public void navigateTo(Fragment fragment, boolean addToBackStack){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if(addToBackStack){
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    /**
     * Handles incoming data events from the mesh
     *
     * @param e event object from mesh
     */
    private void handleDataReceived(RightMeshEvent e) {
        // TODO: 2018-05-28 Add in logic to handle the incoming data
    }

    /**
     * Handles peer update events from the mesh - maintains a list of peers and updates the display.
     *
     * @param e event object from mesh
     */
    private void handlePeerChanged(RightMeshEvent e) {

        // Update peer list.
        PeerChangedEvent event = (PeerChangedEvent) e;
        if (event.state != REMOVED && !users.contains(event.peerUuid)) {
            users.add(event.peerUuid);
        } else if (event.state == REMOVED){
            users.remove(event.peerUuid);
        }
    }
}
