package ca.marshallasch.veil;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentLanding extends Fragment {

    public FragmentLanding() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {
        // hides the menu bar at the top so you have a full screen landing page
        ((MainActivity) getActivity()).getSupportActionBar().hide();

        View view = inflater.inflate(R.layout.fragment_landing, container,false);

        // buttons and event listeners
        MaterialButton loginBtn = view.findViewById(R.id.login_btn);
        MaterialButton signupBtn = view.findViewById(R.id.sign_up_btn);

        loginBtn.setOnClickListener(view1 -> {
            Log.i("Fragment Landing", "login button pressed");
            //TODO: route to real fragment for login once its made

        });

        signupBtn.setOnClickListener(view1 -> {
            Log.i("Fragment Landing", "sign up button pressed");
            ((MainActivity) getActivity()).navigateTo(new SignUp(), true);
        });

        return view;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        ((MainActivity) getActivity()).getSupportActionBar().show();
    }

}
