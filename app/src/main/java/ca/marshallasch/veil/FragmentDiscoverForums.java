package ca.marshallasch.veil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Spinner;

import java.util.List;

import ca.marshallasch.veil.controllers.RightMeshController;
import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.services.VeilService;
import ca.marshallasch.veil.tagList.TagListAdapter;

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
public class FragmentDiscoverForums extends Fragment implements SwipeRefreshLayout.OnRefreshListener, PopupMenu.OnMenuItemClickListener {

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

        MaterialButton sort = view.findViewById(R.id.sort);

        sort.setOnClickListener(view1 -> {
            PopupMenu popupMenu = new PopupMenu(view1.getContext(), view1);
            popupMenu.inflate(R.menu.sort_menu);
            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(FragmentDiscoverForums.this);
        });

        ActionBar actionBar = ((MainActivity) activity).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        List<DhtProto.Post> posts = DataStore.getInstance(activity).getKnownPosts();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        postListAdapter = new PostListAdapter(posts, activity);
        recyclerView.setAdapter(postListAdapter);

        refreshLayout = view.findViewById(R.id.swiperefresh);

        Spinner spinner = view.findViewById(R.id.filter);
        TagListAdapter tagListAdapter = new TagListAdapter(activity);
        spinner.setAdapter(tagListAdapter);

        tagListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged()
            {
                List<String> tags = tagListAdapter.getSelectedTags();

                StringBuilder filter = new StringBuilder();

                for (String tag:tags) {
                    filter.append(tag).append(":");
                }

                postListAdapter.getFilter().filter(filter.toString());
            }
        });

        refreshLayout.setOnRefreshListener(this);

        // register receiver to be notified when the data changes
        LocalBroadcastManager.getInstance(activity).registerReceiver(
                broadcastReceiver, new IntentFilter(RightMeshController.NEW_DATA_BROADCAST));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // unregister receiver
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    /**
     * This is called when the list of posts is refreshed.
     */
    @Override
    public void onRefresh() {
        ((MainActivity) getActivity()).sendServiceMessage(VeilService.ACTION_MAIN_REFRESH_FORUMS_LIST, null);
    }

    /**
     * Will be called when one of the popup menu options is clicked.
     * @param menuItem the menu item that was selected
     * @return false so other event handlers can act on this event.
     */
    @Override
    public boolean onMenuItemClick(MenuItem menuItem)
    {
        switch (menuItem.getItemId()){
            case R.id.age_asc:
                postListAdapter.sort(PostListAdapter.SortOption.AGE_ASC);
                break;
            case R.id.age_desc:
                postListAdapter.sort(PostListAdapter.SortOption.AGE_DESC);
                break;
            case R.id.auth_asc:
                postListAdapter.sort(PostListAdapter.SortOption.ALPHA_AUTH_ASC);
                break;
            case R.id.auth_desc:
                postListAdapter.sort(PostListAdapter.SortOption.ALPHA_AUTH_DESC);
                break;
            case R.id.title_asc:
                postListAdapter.sort(PostListAdapter.SortOption.ALPHA_TITLE_ASC);
                break;
            case R.id.title_desc:
                postListAdapter.sort(PostListAdapter.SortOption.ALPHA_TITLE_DESC);
                break;
        }

        return false;
    }

    private final  BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(RightMeshController.NEW_DATA_BROADCAST)) {

                List<DhtProto.Post> posts = DataStore.getInstance(context).getKnownPosts();
                postListAdapter.update(posts);
                refreshLayout.setRefreshing(false);
            }
        }
    };
}
