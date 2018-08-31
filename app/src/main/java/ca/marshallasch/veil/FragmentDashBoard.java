package ca.marshallasch.veil;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;

/**
 *
 * This class holds the dashboard UI for the application.
 *
 * @author Weihan Li
 * @version 1.0
 * @since 2018-05-31
 */
public class FragmentDashBoard extends Fragment {

    private static final String TAG = "Fragment Dashboard";

    public FragmentDashBoard() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Activity activity = getActivity();

        // return the action bar.
        ((MainActivity) activity).getSupportActionBar().show();

        View view = inflater.inflate(R.layout.fragment_dash_board, container,false);

        ActionBar actionBar = ((MainActivity) activity).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        // buttons and event listeners
        MaterialButton logoutBtn = view.findViewById(R.id.logout_btn);
        MaterialButton discoverForumsBtn = view.findViewById(R.id.discover_forums_btn);
        MaterialButton personalForumsBtn = view.findViewById(R.id.personal_forums_btn);
        FloatingActionButton newPostButton = view.findViewById(R.id.new_post);
        ImageButton manageAccount = view.findViewById(R.id.edit_profile_button);

        TextView name = view.findViewById(R.id.name);
        TextView emailAddress = view.findViewById(R.id.user_email);

        DhtProto.User currentUser = ((MainActivity) activity).getCurrentUser();

        if (currentUser != null) {
            emailAddress.setText(currentUser.getEmail());
            name.setText(getString(R.string.full_name_placeholder, currentUser.getFirstName(), currentUser.getLastName()));
        }

        manageAccount.setOnClickListener(view1 -> {
            ((MainActivity) getActivity()).navigateTo(new FragmentAccountManagement(), true);
        });

        logoutBtn.setOnClickListener(view1 -> {
            Log.i(TAG, "logout button pressed");
            //clears entire back stack without calling the onCreate of each fragment
            getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            Util.clearKnownUser(activity);
            ((MainActivity) activity).navigateTo(new FragmentLanding(), false);
        });

        discoverForumsBtn.setOnClickListener(view1 -> {
            Log.i(TAG, "discover forums pressed");
            FragmentDiscoverForums frag = new FragmentDiscoverForums();

            Bundle bundle = new Bundle();
            bundle.putBoolean(activity.getString(R.string.post_list_key), false);
            frag.setArguments(bundle);
            ((MainActivity) activity).navigateTo(frag, true);
        });

        personalForumsBtn.setOnClickListener(view1 -> {
            Log.i(TAG, "Your forums button pressed");

            FragmentDiscoverForums frag = new FragmentDiscoverForums();

            Bundle bundle = new Bundle();
            bundle.putBoolean(activity.getString(R.string.post_list_key), true);
            frag.setArguments(bundle);
            ((MainActivity) activity).navigateTo(frag, true);
        });

        newPostButton.setOnClickListener(view1 -> {
            ((MainActivity) activity).navigateTo(new FragmentCreatePost(), true);
        });

        return view;
    }
}
