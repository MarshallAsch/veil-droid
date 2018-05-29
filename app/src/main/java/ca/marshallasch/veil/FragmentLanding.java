package ca.marshallasch.veil;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentLanding extends Fragment {

    public FragmentLanding() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().hide();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_landing, container, false);

        //login button and event listener
        MaterialButton loginBtn = view.findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(view1 -> {
            Log.i("Fragment Landing", "login button pressed");
            ((MainActivity) getActivity()).navigateTo(new SignUp(), falsea);
            ((MainActivity) getActivity()).getSupportActionBar().show();

        });

        //sign up button and event listener
        MaterialButton signupBtn = view.findViewById(R.id.sign_up_btn);
        signupBtn.setOnClickListener(view1 -> {
            Log.i("Fragment Landing", "sign up button pressed");
            ((MainActivity) getActivity()).getSupportActionBar().show();
        });



        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).getSupportActionBar().show();
    }

}
