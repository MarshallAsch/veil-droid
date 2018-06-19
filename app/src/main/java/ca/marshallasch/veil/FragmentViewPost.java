package ca.marshallasch.veil;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
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
    private String postHash, authorName,postDate;

    DhtProto.User currentUser;


    public FragmentViewPost() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);

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


        //Grab current user
        currentUser = ((MainActivity) getActivity()).getCurrentUser();

        //if the post object is not null set values of post else set filler values
        String postTitle;
        String postContent;
        if(postObject != null){
            postTitle = postObject.getTitle();
            postContent = postObject.getMessage();
            postHash = postObject.getUuid();
            postDate = Util.timestampToDate(postObject.getTimestamp()).toString();
            authorName = postObject.getAuthorName();



        }
        else{
            postTitle = getString(R.string.failed_to_load_title);
            postContent = getString(R.string.failed_to_load_content);
        }


        //setting post information
        TextView viewTitle = view.findViewById(R.id.title);
        TextView viewContent = view.findViewById(R.id.post_content);
        TextView viewPostHash = view.findViewById(R.id.post_hash);
        TextView viewAuthorName = view.findViewById(R.id.author_name);
        TextView viewDate = view.findViewById(R.id.date);

        viewTitle.setText(postTitle);
        viewContent.setText(postContent);
        viewPostHash.setText(postHash);
        viewAuthorName.setText(authorName);
        viewDate.setText(postDate);


        //recycler view logic for displaying comments
        Activity activity = getActivity();
        RecyclerView recyclerView = view.findViewById(R.id.comment_list);
        recyclerView.setHasFixedSize(true);

        //TODO START: replace this with real data when commenting adding is added in
        List<DhtProto.Comment> comments = new ArrayList<>();

        DhtProto.Comment comment  = DhtProto.Comment.newBuilder()
                .setMessage("WOW IM A COMMENT PLS WORK!")
                .setAuthorName("marshall asch")
                .setTimestamp(Util.millisToTimestamp(System.currentTimeMillis()))
                .build();
        comments.add(comment);
        //TODO END: replace this with real data when commenting adding is added in


        //Setting the recycler view to hold comments for the post
        LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerView.Adapter recyclerAdapter = new CommentListAdapter(comments);
        recyclerView.setAdapter(recyclerAdapter);


        return view;

    }

}
