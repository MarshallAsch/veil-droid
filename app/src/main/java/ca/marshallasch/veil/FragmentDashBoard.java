package ca.marshallasch.veil;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.util.Log;
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


        // buttons and event listeners
        /**MaterialButton login = view.findViewById(R.id.enter_btn);
        MaterialButton cancel = view.findViewById(R.id.back_btn);

        login.setOnClickListener(view1 -> {
            Log.i("Fragment Login", "enter button pressed");
            ((MainActivity) getActivity()).navigateTo(new FragmentLogin(), true);

        });

        cancel.setOnClickListener(view1 -> {
            Log.i("Fragment Login", "back button pressed");
            getFragmentManager().popBackStack();
        });**/

        return view;
    }

}
