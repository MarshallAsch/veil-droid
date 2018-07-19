package ca.marshallasch.veil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
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
    private CheckBox rememberMe;

    private Activity activity;

    public FragmentLogin() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")  // added to remove the linter warning on the setOnTouchListener{line 46}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {


        activity = getActivity();
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
            Util.hideKeyboard(view12, activity);
            return false;
        });

        // buttons and event listeners
        MaterialButton login = view.findViewById(R.id.enter_btn);
        MaterialButton cancel = view.findViewById(R.id.back_btn);


        emailAddressInput = view.findViewById(R.id.username);
        passwordInput = view.findViewById(R.id.password);
        rememberMe = view.findViewById(R.id.remember_me);

        // fill in the username if known
        String knownUserName = Util.getKnownUsername(activity);
        if (knownUserName != null) {
            emailAddressInput.setText(knownUserName);
        }

        // fill in the password if known
        String knownPassword = Util.getKnownPassword(activity);
        if (knownPassword != null) {
            passwordInput.setText(knownPassword);
        }

        // check the remember me box
        if (knownUserName != null && knownPassword != null) {
            rememberMe.setChecked(true);
        }

        login.setOnClickListener(view1 -> {
            Log.i("Fragment Login", "enter button pressed");
            Util.hideKeyboard(view1, activity);

            String username = emailAddressInput.getText().toString();
            String password = passwordInput.getText().toString();

            // do the login action in the a async task
            new LoginTask().execute(username, password);

        });

        cancel.setOnClickListener(view1 -> {
            Util.hideKeyboard(view1, activity);
            ((MainActivity) activity).navigateTo(new FragmentLanding(), false);

        });

        return view;
    }


    /**
     * Use this AsyncTask to move the login work into a separate thread to offload some of the
     * work from the main thread.
     */
    private class LoginTask extends AsyncTask<String, Void, DhtProto.User> {

        @Override
        protected DhtProto.User doInBackground(String... strings)
        {
            String username = strings[0];
            String password = strings[1];

            // check the user account in the database
            // NOTE that this will find the fist matching email + password combination on the
            // device
            Database db = Database.getInstance(activity);
            DhtProto.User user = db.login(username, password);
            db.close();

            if (user != null && rememberMe.isChecked()) {
                Util.rememberUserName(activity, username, password);
            }
            return user;
        }

        @Override
        protected void onPostExecute(DhtProto.User user)
        {
            // check that a user was found
            if (user == null) {
                Snackbar.make(activity.findViewById(R.id.top_view), R.string.username_pass_not_match, Snackbar.LENGTH_SHORT).show();
                Util.clearKnownUser(activity);
            } else {

                ((MainActivity) activity).setCurrentUser(user);
                ((MainActivity) activity).navigateTo(new FragmentDashBoard(), false);
            }
        }
    }
}
