package ca.marshallasch.veil;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.protobuf.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import ca.marshallasch.veil.database.Database;
import ca.marshallasch.veil.database.KnownHashesContract;
import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-11
 */
public class FragmentCreatePost extends Fragment
{
    private EditText titleInput;
    private EditText messageInput;
    private EditText tagsInput;

    ForumStorage dataStore = null;

    DhtProto.User currentUser;

    public FragmentCreatePost() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_create_post, container,false);

        titleInput = view.findViewById(R.id.title_text_edit);
        messageInput = view.findViewById(R.id.post_message);
        tagsInput = view.findViewById(R.id.tags_text_edit);

        MaterialButton submit = view.findViewById(R.id.save);
        MaterialButton cancel = view.findViewById(R.id.cancel_button);

        // get the current logged in user
        currentUser = ((MainActivity) getActivity()).getCurrentUser();


        dataStore = null;   // TODO: 2018-06-12 replace this with the concrete data store

        cancel.setOnClickListener(view1 -> {
            Util.hideKeyboard(view1, getActivity());
            getFragmentManager().popBackStack();
        });

        submit.setOnClickListener(view1 -> {
            Util.hideKeyboard(view1, getActivity());
            boolean status = submitPost();

            // if it was successful then go to the dashboard
            if (status) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    /**
     * This function will create the post object and insert it into the data stores.
     * @return true if it was created and inserted successfully, false otherwise
     */
    private boolean submitPost() {

        // get all the values for the post
        String tags = tagsInput.getText().toString();
        String title = titleInput.getText().toString();
        String message = messageInput.getText().toString();
        String authorName = currentUser.getFirstName() + " " + currentUser.getLastName();
        String authorID = currentUser.getUuid();
        String uuid = UUID.randomUUID().toString();
        String hash = null;
        Date now = new Date();
        Timestamp timestamp = Util.millisToTimestamp(now.getTime());

        // check input
        if (title.length() == 0 || message.length() == 0) {
            return false;
        }

        ArrayList<String> tagList = (ArrayList<String>) Arrays.asList(tags.split(","));

        // make the actual post object
        DhtProto.Post post = DhtProto.Post.newBuilder()
                .setUuid(uuid)
                .setAuthorId(authorID)
                .setAuthorName(authorName)
                .setTitle(title)
                .setMessage(message)
                .addAllTags(tagList)
                .setTimestamp(timestamp)
                .build();

        if (dataStore != null) {
            // insert it into the data store
            hash = dataStore.insertPost(post);

            // add it know the known hash list
            Database db = Database.getInstance(getActivity());
            db.insertKnownHash(hash, uuid, now, KnownHashesContract.KnownHashesEntry.TYPE_POST);
            db.close();
            return true;
        } else {
            Snackbar.make(getActivity().findViewById(R.id.top_view), R.string.failed_to_save_data, Snackbar.LENGTH_SHORT).show();
            return false;
        }
    }
}
