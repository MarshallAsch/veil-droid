package ca.marshallasch.veil;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.services.VeilService;
import ca.marshallasch.veil.tagList.TagListAdapter;
import ca.marshallasch.veil.utilities.Util;

/**
 * This is the class to create a new post item and will add it to the data stores.
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-11
 */
public class FragmentCreatePost extends Fragment
{
    private EditText titleInput;
    private EditText messageInput;
    private CheckBox anonymousInput;

    private DhtProto.User currentUser;
    private Activity activity;
    private TagListAdapter tagListAdapter;

    public FragmentCreatePost() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_create_post, container,false);

        activity = getActivity();
        ActionBar actionBar = ((MainActivity) activity).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleInput = view.findViewById(R.id.title_text_edit);
        messageInput = view.findViewById(R.id.post_message);
        anonymousInput = view.findViewById(R.id.anonymous);

        MaterialButton submit = view.findViewById(R.id.save);
        MaterialButton cancel = view.findViewById(R.id.cancel_button);
        Spinner spinner = view.findViewById(R.id.tags_input);

        tagListAdapter = new TagListAdapter(activity);
        spinner.setAdapter(tagListAdapter);

        // get the current logged in user
        currentUser = ((MainActivity) activity).getCurrentUser();

        cancel.setOnClickListener(view1 -> {
            Util.hideKeyboard(activity);
            getFragmentManager().popBackStack();
        });

        submit.setOnClickListener(view1 -> {
            Util.hideKeyboard(activity);
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
        String title = titleInput.getText().toString();
        String message = messageInput.getText().toString();
        boolean anonymous = anonymousInput.isChecked();

        // check input
        if (title.length() == 0 || message.length() == 0 || currentUser == null) {
            return false;
        }

        ArrayList<String> tagList = tagListAdapter.getSelectedTags();

        DhtProto.Post post = Util.createPost(title, message, currentUser, tagList, anonymous);

        DataStore dataStore = DataStore.getInstance(getContext());

        if (dataStore.savePost(post)) {

            // notify other users of a new post
            Bundle bundle = new Bundle();
            bundle.putByteArray(VeilService.EXTRA_POST, post.toByteArray());

            ((MainActivity) activity).sendServiceMessage(VeilService.ACTION_NOTIFY_NEW_DATA, bundle);

            return true;
        } else {
            Snackbar.make(activity.findViewById(R.id.top_view), R.string.failed_to_save_data, Snackbar.LENGTH_SHORT).show();
            return false;
        }
    }
}
