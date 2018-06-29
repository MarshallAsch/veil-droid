package ca.marshallasch.veil;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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


        peerList = view.findViewById(R.id.peer_list);

        refreshList();

        Button refresh = view.findViewById(R.id.refresh_peers);

        // refresh the list when the button is pressed
        refresh.setOnClickListener(view1 -> refreshList());

        return view;
    }


    /**
     * Refresh the list of connected peers.
     */
    private void refreshList() {

        peerList.setText("Peers:\n");

        Set<MeshId> peers = null;
        try {
            peers = ((MainActivity) getActivity()).meshManager.getPeers(9182);
        }
        catch (RightMeshException e) {
            e.printStackTrace();
        }

        if (peers != null) {
            for (MeshId peer : peers) {
                peerList.append("\n" + peer.toString());
            }
        }
    }
}