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
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ca.marshallasch.veil.controllers.RightMeshController;
import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.services.VeilService;

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

    public FragmentDiscoverForums() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_discover_forums, container,false);

        Activity activity = getActivity();
        RecyclerView recyclerView = view.findViewById(R.id.list_view);
        recyclerView.setHasFixedSize(true);


        ActionBar actionBar = ((MainActivity) activity).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        List<DhtProto.Post> posts = DataStore.getInstance(activity).getKnownPosts();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        postListAdapter = new PostListAdapter(posts, activity);
        recyclerView.setAdapter(postListAdapter);


        refreshLayout = view.findViewById(R.id.swiperefresh);

        refreshLayout.setOnRefreshListener(this);

        // register receiver to be notified when the data changes
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                broadcastReceiver, new IntentFilter(RightMeshController.NEW_DATA_BROADCAST));


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // unregister receiver
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onRefresh() {
        ((MainActivity) getActivity()).sendServiceMessage(null, VeilService.ACTION_MAIN_MANUAL_REFRESH);
        postListAdapter.notifyDataSetChanged();
    }

    private final  BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(RightMeshController.NEW_DATA_BROADCAST)) {

                List<DhtProto.Post> posts = DataStore.getInstance(context).getKnownPosts();
                postListAdapter.update(posts);
                postListAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        }
    };

}
