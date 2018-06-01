package ca.marshallasch.veil;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import javax.annotation.Nullable;


/**
 * @author Weihan Li
 * @version 1.0
 * @since 2018-05-30
 *
 * Description:
 * This class holds the login UI for the application
 */
public class FragmentLogin extends Fragment {

    public FragmentLogin() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, @android.support.annotation.Nullable Bundle savedInstanceState)  {
        // hides the menu bar at the top so you have a full screen landing page
        ((MainActivity) getActivity()).getSupportActionBar().hide();

        View view = inflater.inflate(R.layout.fragment_login, container,false);

        ConstraintLayout layout = view.findViewById(R.id.login_layout);

        //hides keyboard if white space is pressed
        layout.setOnTouchListener((view12, ev) -> {
            hideKeyboard(view12);
            return false;
        });

        // buttons and event listeners
        MaterialButton login = view.findViewById(R.id.enter_btn);
        MaterialButton cancel = view.findViewById(R.id.back_btn);

        login.setOnClickListener(view1 -> {
            Log.i("Fragment Login", "enter button pressed");
            //TODO: check username and password
            hideKeyboard(view1);
            ((MainActivity) getActivity()).navigateTo(new FragmentDashBoard(), true);

        });

        cancel.setOnClickListener(view1 -> {
            hideKeyboard(view1);
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

    /**
     *
     * @param view
     * Description: hides android's soft keyboard
     */
    protected void hideKeyboard(View view) {
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
