package ca.marshallasch.veil;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;

import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;


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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_comment, container, false);
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


        TextView postTitle = view.findViewById(R.id.post_title);

        MaterialButton cancelBtn = view.findViewById(R.id.cancel_button);
        MaterialButton postBtn = view.findViewById(R.id.post_comment_btn);
        EditText commentInput = view.findViewById(R.id.post_message);

        postTitle.setText(postObject.getTitle());


        //Handle cancel button
        cancelBtn.setOnClickListener(view1 -> {
            Util.hideKeyboard(view1, getActivity());
            getFragmentManager().popBackStack();
        });


        //Handle post button
        postBtn.setOnClickListener(view1 -> {
            String message = commentInput.getText().toString();

            // the message can not be empty
            if (message.length() == 0) {
                Snackbar.make(getActivity().findViewById(R.id.top_view), R.string.comment_too_short, Snackbar.LENGTH_SHORT).show();
                return;
            }

            DhtProto.Comment comment = Util.createComment(message, ((MainActivity) getActivity()).getCurrentUser());

            // save to the data store
            DataStore.getInstance(getActivity()).saveComment(comment, postObject);

            // go back to the post view screen
            Util.hideKeyboard(view1, getActivity());
            getFragmentManager().popBackStack();

        });

        // Inflate the layout for this fragment
        return view;

    }

}
