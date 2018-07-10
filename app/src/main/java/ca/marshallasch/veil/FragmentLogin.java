package ca.marshallasch.veil;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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


        View view = inflater.inflate(R.layout.fragment_login, container,false);

        ConstraintLayout layout = view.findViewById(R.id.login_layout);

        // navigate back to the landing screen
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if(keyCode == KeyEvent.KEYCODE_BACK) {
                    ((MainActivity) getActivity()).navigateTo(new FragmentLanding(), false);
                    return true;
                }
            }
            return false;
        });

        //hides keyboard if white space is pressed
        layout.setOnTouchListener((view12, ev) -> {
            Util.hideKeyboard(view12, getActivity());
            return false;
        });

        // buttons and event listeners
        MaterialButton login = view.findViewById(R.id.enter_btn);
        MaterialButton cancel = view.findViewById(R.id.back_btn);
        CheckBox rememberMe = view.findViewById(R.id.remember_me);

        emailAddressInput = view.findViewById(R.id.username);
        passwordInput = view.findViewById(R.id.password);

        // fill in the username if known
        String knownUserName = Util.getKnownUsername(getActivity());
        if (knownUserName != null) {
            emailAddressInput.setText(knownUserName);
        }

        // fill in the password if known
        String knownPassword = Util.getKnownPassword(getActivity());
        if (knownPassword != null) {
            passwordInput.setText(knownPassword);
        }

        // check the remember me box
        if (knownUserName != null && knownPassword != null) {
            rememberMe.setChecked(true);
        }

        login.setOnClickListener(view1 -> {
            Log.i("Fragment Login", "enter button pressed");
            Util.hideKeyboard(view1, getActivity());

            String username = emailAddressInput.getText().toString();
            String password = passwordInput.getText().toString();
            // check the user account in the database
            // NOTE that this will find the fist matching email + password combination on the
            // device
            Database db = Database.getInstance(getActivity());
            DhtProto.User user = db.login(username, password);
            db.close();

            // check that a user was found
            if (user == null) {
                Snackbar.make(getActivity().findViewById(R.id.top_view), R.string.username_pass_not_match, Snackbar.LENGTH_SHORT).show();
                Util.clearKnownUser(getActivity());
            } else {

                if (rememberMe.isChecked()) {
                    Util.rememberUserName(getActivity(), username, password);
                }

                ((MainActivity) getActivity()).setCurrentUser(user);
                ((MainActivity) getActivity()).navigateTo(new FragmentDashBoard(), false);
            }
        });

        cancel.setOnClickListener(view1 -> {
            Util.hideKeyboard(view1, getActivity());
            ((MainActivity) getActivity()).navigateTo(new FragmentLanding(), false);

        });

        return view;
    }
}
