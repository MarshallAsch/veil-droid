package ca.marshallasch.veil;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
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


        // buttons and event listeners
        MaterialButton loginBtn = view.findViewById(R.id.enter_btn);
        MaterialButton signupBtn = view.findViewById(R.id.back_btn);

        loginBtn.setOnClickListener(view1 -> {
            Log.i("Fragment Login", "enter button pressed");
            //TODO: check username and password
            //TODO: route to real dash once implemented
            ((MainActivity) getActivity()).navigateTo(new FragmentLogin(), true);

        });

        signupBtn.setOnClickListener(view1 -> {
            Log.i("Fragment Login", "back button pressed");
            ((MainActivity) getActivity()).navigateTo(new FragmentLanding(), true);
        });

        return view;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        ((MainActivity) getActivity()).getSupportActionBar().show();
    }
}
