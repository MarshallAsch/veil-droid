package ca.marshallasch.veil;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ca.marshallasch.veil.database.Database;
import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;

/**
 * This class holds the login UI for the application.
 *
 * @author Weihan Li
 * @version 1.0
 * @since 2018-05-30
 */
public class FragmentLogin extends Fragment {

    private EditText emailAddressInput;
    private EditText passwordInput;

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


        emailAddressInput = view.findViewById(R.id.username);
        passwordInput = view.findViewById(R.id.password);

        login.setOnClickListener(view1 -> {
            Log.i("Fragment Login", "enter button pressed");
            Util.hideKeyboard(view1, getActivity());

            // check the user account in the database
            // NOTE that this will find the fist matching email + password combination on the
            // device
            Database db = Database.getInstance(getActivity());
            DhtProto.User user = db.login(emailAddressInput.getText().toString(), passwordInput.getText().toString());
            db.close();

            // check that a user was found

            //TODO: (DEBUGGING CODE) [START] RESTORE AFTER FINISH TESTING ===============================================
            /*if (user == null) {
                Snackbar.make(getActivity().findViewById(R.id.top_view), R.string.username_pass_not_match, Snackbar.LENGTH_SHORT).show();
            } else {
                ((MainActivity) getActivity()).setCurrentUser(user);
                ((MainActivity) getActivity()).navigateTo(new FragmentDashBoard(), true);
            }*/

            ((MainActivity) getActivity()).setCurrentUser(user);
            ((MainActivity) getActivity()).navigateTo(new FragmentDashBoard(), true);
            //TODO: (DEBUGGING CODE) [END] RESTORE AFTER FINISH TESTING ===============================================
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
