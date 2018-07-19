package ca.marshallasch.veil;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Set;

import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.proto.Sync;
import ca.marshallasch.veil.utilities.Util;
import io.left.rightmesh.id.MeshId;
import io.left.rightmesh.mesh.MeshManager;
import io.left.rightmesh.util.RightMeshException;


/**
 * This class holds the view and logic for posting a comment.
 *
 * @author Weihan Li
 * @version 1.0
 * @since 2018-06-22
 */
public class FragmentAddComment extends android.support.v4.app.Fragment {


    public FragmentAddComment() {
        // Required empty public constructor
    }

    DhtProto.Post postObject;
    DhtProto.User currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_comment, container, false);

        Activity activity = getActivity();
        ActionBar actionBar = ((MainActivity) activity).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //setting a white background for overlaying the view post fragment
        view.setBackgroundColor(Color.WHITE);

        //retrieve passed bundle from the FragmentViewPost Class
        Bundle bundle = this.getArguments();

        try {

            if(bundle != null){
                postObject = DhtProto.Post.parseFrom(bundle.getByteArray(getString(R.string.post_object_key)));
            }
            else if(savedInstanceState != null){
                postObject = DhtProto.Post.parseFrom(savedInstanceState.getByteArray(getString(R.string.post_object_key)));
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        currentUser = ((MainActivity) activity).getCurrentUser();

        TextView postTitle = view.findViewById(R.id.post_title);

        MaterialButton cancelBtn = view.findViewById(R.id.cancel_button);
        MaterialButton postBtn = view.findViewById(R.id.post_comment_btn);
        EditText commentInput = view.findViewById(R.id.post_message);
        CheckBox anonymousInput = view.findViewById(R.id.anonymous);

        postTitle.setText(postObject.getTitle());

        // if I am the author of an anonymous post then default make my comments anonymous.
        if (postObject.getAnonymous() && currentUser.getUuid().equals(postObject.getAuthorId())) {
            anonymousInput.setChecked(true);
            Log.d("ANON", "check box");
        }


        //Handle cancel button
        cancelBtn.setOnClickListener(view1 -> {
            Util.hideKeyboard(view1, activity);
            getFragmentManager().popBackStack();
        });


        //Handle post button
        postBtn.setOnClickListener(view1 -> {
            String message = commentInput.getText().toString();
            boolean anonymous = anonymousInput.isChecked();

            // the message can not be empty
            if (message.length() == 0) {
                Snackbar.make(activity.findViewById(R.id.top_view), R.string.comment_too_short, Snackbar.LENGTH_SHORT).show();
                return;
            }

            DhtProto.Comment comment = Util.createComment(message, currentUser, anonymous);

            // save to the data store
            DataStore.getInstance(activity).saveComment(comment, postObject);

            // notify other users that there is a new comment
            try {
                MeshManager manager = ((MainActivity) activity).meshManager;
                Set<MeshId> peers = manager.getPeers(MainActivity.DATA_PORT);


                Sync.NewContent newContent = Sync.NewContent.newBuilder()
                        .setComment(comment)
                        .setPost(postObject)
                        .build();

                Sync.Message dataRequest = Sync.Message.newBuilder()
                        .setType(Sync.SyncMessageType.NEW_CONTENT)
                        .setNewContent(newContent)
                        .build();

                // request an update from everyone
                for (MeshId peer: peers) {

                    // do not ask myself for info
                    if (peer.equals(manager.getUuid())) {
                        continue;
                    }
                    manager.sendDataReliable(peer, MainActivity.DATA_PORT, dataRequest.toByteArray());
                }
            }
            catch (RightMeshException e) {
                e.printStackTrace();
            }

            // go back to the post view screen
            Util.hideKeyboard(view1, activity);
            getFragmentManager().popBackStack();

        });

        // Inflate the layout for this fragment
        return view;

    }

}
