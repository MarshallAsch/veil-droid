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

import java.util.Set;

import io.left.rightmesh.id.MeshId;
import io.left.rightmesh.util.RightMeshException;


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

        /**
         * registering broadcast recevier to recevier messages
         * from {@link ca.marshallasch.veil.services.VeilService}
         */
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                broadcastReceiver, new IntentFilter("getPeers"));

        Activity activity = getActivity();
        ActionBar actionBar = ((MainActivity) activity).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        peerList = view.findViewById(R.id.peer_list);

        refreshList();

        Button refresh = view.findViewById(R.id.refresh_peers);

        // refresh the list when the button is pressed
        refresh.setOnClickListener(view1 -> refreshList());

        return view;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Set<MeshId> peers = (Set<MeshId>) intent.getSerializableExtra("peersList");
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
    }
}
