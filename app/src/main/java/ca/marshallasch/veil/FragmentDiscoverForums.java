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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Set;

import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.proto.Sync;
import io.left.rightmesh.id.MeshId;
import io.left.rightmesh.mesh.MeshManager;
import io.left.rightmesh.util.RightMeshException;

/**
 *
 * This class holds UI logic for viewing the list of posts.
 *
 * TODO add the ability to do searches for posts
 *
 * @author Weihan Li
 * @version 1.0
 * @since 2018-05-31
 */
public class FragmentDiscoverForums extends Fragment implements  SwipeRefreshLayout.OnRefreshListener {


    private PostListAdapter postListAdapter;
    private SwipeRefreshLayout refreshLayout;
    private LocalBroadcastManager localBroadcastManager;

    public FragmentDiscoverForums() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_discover_forums, container,false);

        Activity activity = getActivity();
        RecyclerView recyclerView = view.findViewById(R.id.list_view);
        recyclerView.setHasFixedSize(true);


        List<DhtProto.Post> posts = DataStore.getInstance(activity).getKnownPosts();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        postListAdapter = new PostListAdapter(posts, activity);
        recyclerView.setAdapter(postListAdapter);


        refreshLayout = view.findViewById(R.id.swiperefresh);

        // register receiver to be notified when the data changes
        localBroadcastManager = LocalBroadcastManager.getInstance(activity);
        localBroadcastManager.registerReceiver(localReceiver, new IntentFilter(MainActivity.NEW_DATA_BROADCAST));



        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        // unregister receiver
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    @Override
    public void onRefresh()
    {
        Log.d("REFRESH", "refreshing the data set");
        try {
            MeshManager manager = ((MainActivity) getActivity()).meshManager;
            Set<MeshId> peers = manager.getPeers(MainActivity.DATA_PORT);

            Sync.Message dataRequest = Sync.Message.newBuilder().setType(Sync.SyncMessageType.REQUEST_DATA).build();

            // request an update from everyone
            for (MeshId peer: peers) {
                manager.sendDataReliable(peer, MainActivity.DATA_PORT, dataRequest.toByteArray());
            }

            postListAdapter.notifyDataSetChanged();

        }
        catch (RightMeshException e) {
            e.printStackTrace();
        }
    }

    private final  BroadcastReceiver localReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            String action = intent.getAction();

            if (action.equals(MainActivity.NEW_DATA_BROADCAST)) {

                List<DhtProto.Post> posts = DataStore.getInstance(context).getKnownPosts();
                postListAdapter.update(posts);
                postListAdapter.notifyDataSetChanged();

            }
        }
    };

}
