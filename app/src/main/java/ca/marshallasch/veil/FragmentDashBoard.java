package ca.marshallasch.veil;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.annotation.Nullable;

/**
 * @author Weihan Li
 * @version 1.0
 * @since 2018-05-31
 *
 * Description:
 * This class holds the dashboard UI for the application
 */

public class FragmentDashBoard extends Fragment {

    public FragmentDashBoard() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, @android.support.annotation.Nullable Bundle savedInstanceState)  {
        // hides the menu bar at the top so you have a full screen landing page
        View view = inflater.inflate(R.layout.fragment_dash_board, container,false);

        //disabling back press logic for this fragment
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //do nothing
                    return true;
                }
            }
            return false;
        });

        // buttons and event listeners
        MaterialButton logoutBtn = view.findViewById(R.id.logout_btn);
        MaterialButton discoverForumsBtn = view.findViewById(R.id.discover_fourms_btn);
        MaterialButton personalForumsBtn = view.findViewById(R.id.personal_forums_btn);

        logoutBtn.setOnClickListener(view1 -> {
            Log.i("Fragment Dashboard", "logout button pressed");
            getFragmentManager().popBackStack();

        });

        discoverForumsBtn.setOnClickListener(view1 -> {
            Log.i("Fragment Dashboard", "discover forums pressed");
            ((MainActivity) getActivity()).navigateTo(new FragmentDiscoverForums(), true);
        });

        personalForumsBtn.setOnClickListener(view1 -> {
            Log.i("Fragment Dashboard", "Your forums button pressed");
        });

        return view;
    }


}
