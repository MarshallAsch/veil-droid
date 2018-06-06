package ca.marshallasch.veil;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentDiscoverForums extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Activity activity;

    public FragmentDiscoverForums() {
        // Required empty public constructor
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {

        View view = inflater.inflate(R.layout.fragment_discover_forums, container,false);

        activity = getActivity();
        recyclerView = view.findViewById(R.id.list_view);
        recyclerView.setHasFixedSize(true);

        linearLayoutManager  = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerAdapter = new PostListAdapter(Data.getTitles(), Data.getContent());
        recyclerView.setAdapter(recyclerAdapter);


        return view;
    }

}