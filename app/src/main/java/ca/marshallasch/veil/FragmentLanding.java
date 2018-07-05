package ca.marshallasch.veil;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * This class holds the landing page for the application. Where you choose if you are going to
 * sign up or login.
 *
 * @author Weihan Li
 * @version 1.0
 * @since 2018-05-29
 */
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

        Button peer = view.findViewById(R.id.go_to_peers);
        peer.setOnClickListener(v -> {
            ((MainActivity) getActivity()).navigateTo(new FragmentPeerList(), true);

        });

        loginBtn.setOnClickListener(view1 -> {
            Log.i("Fragment Landing", "login button pressed");
            ((MainActivity) getActivity()).navigateTo(new FragmentLogin(), false);

        });

        signupBtn.setOnClickListener(view1 -> {
            Log.i("Fragment Landing", "sign up button pressed");
            ((MainActivity) getActivity()).navigateTo(new FragmentSignUp(), false);
        });

        return view;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        // return the action bar.
        ((MainActivity) getActivity()).getSupportActionBar().show();
    }

}
