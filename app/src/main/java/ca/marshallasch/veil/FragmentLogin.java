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
import android.widget.ProgressBar;

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


    private  LoginTask loginTask = null;
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
                    ((MainActivity) activity).navigateTo(new FragmentLanding(), false);
                    return true;
                }
            }
            return false;
        });

        //hides keyboard if white space is pressed
        layout.setOnTouchListener((view12, ev) -> {
            Util.hideKeyboard( activity);
            return false;
        });

        // buttons and event listeners
        MaterialButton login = view.findViewById(R.id.sort);
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
            Util.hideKeyboard(activity);

            String username = emailAddressInput.getText().toString();
            String password = passwordInput.getText().toString();

            // make sure that a async task is not currently running
            if (loginTask == null) {
                // do the login action in the a async task
                loginTask = new LoginTask();
                loginTask.execute(username, password);
            }

        });

        cancel.setOnClickListener(view1 -> {
            Util.hideKeyboard(activity);
            ((MainActivity) activity).navigateTo(new FragmentLanding(), false);

        });

        return view;
    }

    /**
     * Use this AsyncTask to move the login work into a separate thread to offload some of the
     * work from the main thread.
     */
    private class LoginTask extends AsyncTask<String, Void, DhtProto.User> {

        /**
         * This will run on the UI thread before the task executes.
         * This will show the loading spinner
         */
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            ProgressBar loadingBar =  activity.findViewById(R.id.loadingbar);
            loadingBar.setVisibility(View.VISIBLE);
        }

        /**
         * This will check if the user is valid. And it will be run on a separate thread.
         * It takes 2 string arguments that MUST be given:
         * userName
         * password
         * @param strings the strings that get passed into the function.
         * @return the user that was successfully logged in or null if one was not found
         */
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

        /**
         * Run this on the main thread to update the UI.
         * This will display an error message or go to the dashboard screen.
         * @param user the user that was just logged in or null if none was found
         */
        @Override
        protected void onPostExecute(DhtProto.User user)
        {
            ProgressBar loadingBar =  activity.findViewById(R.id.loadingbar);
            if (loadingBar != null) {
                loadingBar.setVisibility(View.VISIBLE);
            }

            // check that a user was found
            if (user == null) {
                Snackbar.make(activity.findViewById(R.id.top_view), R.string.username_pass_not_match, Snackbar.LENGTH_SHORT).show();
                Util.clearKnownUser(activity);
            } else {

                ((MainActivity) activity).setCurrentUser(user);
                ((MainActivity) activity).navigateTo(new FragmentDashBoard(), false);
            }
            loginTask = null;
        }
    }
}
