package ca.marshallasch.veil;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ca.marshallasch.veil.proto.DhtProto;

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
public class FragmentDiscoverForums extends Fragment {

    public FragmentDiscoverForums() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {

        View view = inflater.inflate(R.layout.fragment_discover_forums, container,false);

        Activity activity = getActivity();
        RecyclerView recyclerView = view.findViewById(R.id.list_view);
        recyclerView.setHasFixedSize(true);


        List<DhtProto.Post> posts = DataStore.getInstance(activity).getKnownPosts();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerView.Adapter recyclerAdapter = new PostListAdapter(posts, activity);
        recyclerView.setAdapter(recyclerAdapter);

        return view;
    }
}
