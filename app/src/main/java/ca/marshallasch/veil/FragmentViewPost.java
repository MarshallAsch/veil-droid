package ca.marshallasch.veil;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.List;

import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;


/**
 * This fragment holds the UI for the expanded view of {@link PostListAdapter}'s table cell
 *
 * //TODO: Jun 8, 2018: Add in support for comments and possibly add in UI for Original Poster's profile
 * @author Weihan
 * @version 1.0
 * @since 2018-06-17
 */
public class FragmentViewPost extends Fragment {
    private DhtProto.Post postObject;

    private CommentListAdapter listAdapter;

    public FragmentViewPost() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);

        getActivity().getSupportFragmentManager().addOnBackStackChangedListener(listener);

        //retrieve passed bundle from the PostListAdapter Class
        Bundle bundle = this.getArguments();

        //if the bundle is not null then use the data from there else use the data from the saved instance
        if(bundle != null) {
           try{
               postObject = DhtProto.Post.parseFrom(bundle.getByteArray(getString(R.string.post_object_key)));
           } catch (InvalidProtocolBufferException e){
               e.printStackTrace();
           }
        } else if (savedInstanceState != null){
            try{
                postObject = DhtProto.Post.parseFrom(savedInstanceState.getByteArray(getString(R.string.post_object_key)));
            } catch (InvalidProtocolBufferException e){
                e.printStackTrace();
            }
        }

        //setting post information
        TextView viewTitle = view.findViewById(R.id.title);
        TextView viewContent = view.findViewById(R.id.post_content);
        TextView viewPostHash = view.findViewById(R.id.post_hash);
        TextView viewAuthorName = view.findViewById(R.id.author_name);
        TextView viewDate = view.findViewById(R.id.date);


        if(postObject != null){
            viewTitle.setText(postObject.getTitle());
            viewContent.setText(postObject.getMessage());
            viewPostHash.setText(postObject.getUuid());
            viewDate.setText(Util.timestampToDate(postObject.getTimestamp()).toString());

            // check if the post is anonymous before displaying it.
            String authorName = postObject.getAnonymous() ? getString(R.string.anonymous) : postObject.getAuthorName();
            viewAuthorName.setText(authorName);
        }

        //recycler view logic for displaying comments
        Activity activity = getActivity();
        RecyclerView recyclerView = view.findViewById(R.id.comment_list);
        recyclerView.setHasFixedSize(true);

        // load the actual comments for the post
        List<DhtProto.Comment> comments = DataStore.getInstance(getActivity()).getCommentsForPost(postObject.getUuid());

        //Setting the recycler view to hold comments for the post
        LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        listAdapter = new CommentListAdapter(activity, comments);
        recyclerView.setAdapter(listAdapter);

        //click listener for comment bar
        ImageView commentBar = view.findViewById(R.id.comment_bar);
        commentBar.setOnClickListener(view1 -> {
            FragmentAddComment addCommentFragment = new FragmentAddComment();

            Bundle addCommentBundle = new Bundle();
            addCommentBundle.putByteArray(getString(R.string.post_object_key), postObject.toByteArray());
            addCommentFragment.setArguments(addCommentBundle);

            ((MainActivity) getActivity()).animateFragmentSlide(addCommentFragment, true);
        });

        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        getActivity().getSupportFragmentManager().removeOnBackStackChangedListener(listener);
    }

    /**
     * This listener will refresh the list of comments for the post when the user navigates
     * back to the post view fragment after creating a new comment.
     */
    private FragmentManager.OnBackStackChangedListener listener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged()
        {
            // update the comment list
            listAdapter.update(DataStore.getInstance(getActivity()).getCommentsForPost(postObject.getUuid()));
            listAdapter.notifyDataSetChanged();
        }
    };
}
