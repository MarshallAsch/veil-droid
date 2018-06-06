package ca.marshallasch.veil;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import ca.marshallasch.veil.utilities.Util;

/**
 * This class holds the login UI for the application.
 *
 * @author Weihan Li
 * @version 1.0
 * @since 2018-05-30
 */
public class FragmentLogin extends Fragment {

    public FragmentLogin() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")  // added to remove the linter warning on the setOnTouchListener{line 46}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {
        // hides the menu bar at the top so you have a full screen landing page
        ((MainActivity) getActivity()).getSupportActionBar().hide();

        View view = inflater.inflate(R.layout.fragment_login, container,false);

        ConstraintLayout layout = view.findViewById(R.id.login_layout);

        //hides keyboard if white space is pressed
        layout.setOnTouchListener((view12, ev) -> {
            Util.hideKeyboard(view12, getActivity());
            return false;
        });

        // buttons and event listeners
        MaterialButton login = view.findViewById(R.id.enter_btn);
        MaterialButton cancel = view.findViewById(R.id.back_btn);

        login.setOnClickListener(view1 -> {
            Log.i("Fragment Login", "enter button pressed");
            //TODO: check username and password
            Util.hideKeyboard(view1, getActivity());
            ((MainActivity) getActivity()).navigateTo(new FragmentDashBoard(), true);

        });

        cancel.setOnClickListener(view1 -> {
            Util.hideKeyboard(view1, getActivity());
            Log.i("Fragment Login", "back button pressed");
            getFragmentManager().popBackStack();
        });

        return view;
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        ((MainActivity) getActivity()).getSupportActionBar().show();
    }
}
