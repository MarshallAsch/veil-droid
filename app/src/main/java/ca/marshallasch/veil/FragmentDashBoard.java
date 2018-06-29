package ca.marshallasch.veil;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {
        // hides the menu bar at the top so you have a full screen landing page
        View view = inflater.inflate(R.layout.fragment_dash_board, container,false);

        // buttons and event listeners
        MaterialButton logoutBtn = view.findViewById(R.id.logout_btn);
        MaterialButton discoverForumsBtn = view.findViewById(R.id.discover_forums_btn);
        MaterialButton personalForumsBtn = view.findViewById(R.id.personal_forums_btn);
        FloatingActionButton newPostButton = view.findViewById(R.id.new_post);

        TextView name = view.findViewById(R.id.name);
        TextView emailAddress = view.findViewById(R.id.user_email);

        DhtProto.User currentUser = ((MainActivity) getActivity()).getCurrentUser();

        if (currentUser != null) {
            emailAddress.setText(currentUser.getEmail());
            name.setText(getString(R.string.full_name_placeholder, currentUser.getFirstName(), currentUser.getLastName()));

        }

        logoutBtn.setOnClickListener(view1 -> {
            Log.i(TAG, "logout button pressed");
            //clears entire back stack without calling the onCreate of each fragment
            getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            Util.clearKnownUser(getActivity());
            ((MainActivity) getActivity()).navigateTo(new FragmentLanding(), false);
        });

        discoverForumsBtn.setOnClickListener(view1 -> {
            Log.i(TAG, "discover forums pressed");
            ((MainActivity) getActivity()).navigateTo(new FragmentDiscoverForums(), true);
        });

        personalForumsBtn.setOnClickListener(view1 -> {
            Log.i(TAG, "Your forums button pressed");

            // TODO: 2018-06-06 Do something here other then just logging a message
        });

        newPostButton.setOnClickListener(view1 -> {
            ((MainActivity) getActivity()).navigateTo(new FragmentCreatePost(), true);

        });

        return view;
    }


}
