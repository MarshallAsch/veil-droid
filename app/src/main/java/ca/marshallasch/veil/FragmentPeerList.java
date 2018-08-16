package ca.marshallasch.veil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ca.marshallasch.veil.controllers.RightMeshController;
import ca.marshallasch.veil.services.VeilService;
import io.left.rightmesh.id.MeshId;


/**
 * This class is used for testing to view the peers that are connected to me.
 *
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-28
 */
public class FragmentPeerList extends Fragment
{
    private TextView peerList;

    public FragmentPeerList() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_peer_list, container,false);

        Activity activity = getActivity();
        ActionBar actionBar = ((MainActivity) activity).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        peerList = view.findViewById(R.id.peer_list);

        refreshList();

        Button refresh = view.findViewById(R.id.refresh_peers);

        // refresh the list when the button is pressed
        refresh.setOnClickListener(view1 -> refreshList());

        LocalBroadcastManager.getInstance(activity).registerReceiver(
                broadcastReceiver, new IntentFilter(RightMeshController.GET_PEERS_BROADCAST));

        return view;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        //unregister broadcast receiver
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);

    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MeshId[] peers = (MeshId[]) intent.getSerializableExtra(RightMeshController.EXTRA_PEERS_LIST);
            if (peers != null) {
                for (MeshId peer : peers) {
                    peerList.append("\n" + peer.toString());
                }
            }
        }
    };

    /**
     * Refresh the list of connected peers.
     */
    private void refreshList() {
        peerList.setText("Peers:\n");
        ((MainActivity) getActivity()).sendServiceMessage( VeilService.ACTION_MAIN_REFRESH_PEER_LIST, null);
    }
}
