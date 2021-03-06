package ca.marshallasch.veil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import ca.marshallasch.veil.database.Database;
import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;

/**
 * This class contains the the logic for signing up for an account.
 *
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-05-28
 */
public class FragmentSignUp extends Fragment
{
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailAddressInput;
    private EditText passwordInput;
    private EditText passwordConfInput;

    private SignUpTask signUpTask = null;
    /**
     * Required empty public constructor
     */
    public FragmentSignUp(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

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

        firstNameInput = view.findViewById(R.id.first_name_text_edit);
        lastNameInput = view.findViewById(R.id.last_name_text_edit);
        emailAddressInput = view.findViewById(R.id.email_text_edit);
        passwordInput = view.findViewById(R.id.password_text_edit);
        passwordConfInput = view.findViewById(R.id.password_conf_text_edit);

        MaterialButton cancel = view.findViewById(R.id.cancel_button);
        MaterialButton done = view.findViewById(R.id.done_button);

        cancel.setOnClickListener(v -> {
            ((MainActivity) getActivity()).navigateTo(new FragmentLanding(), false);
            Util.hideKeyboard(getActivity());
        });

        done.setOnClickListener(v ->{
            //route to login after sign up
            Util.hideKeyboard(getActivity());
            this.onDoneClicked();
        });

        return view;
    }

    /**
     * This handles the account creation and input validation.
     */
    private void onDoneClicked()
    {
        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String email = emailAddressInput.getText().toString();
        String password = passwordInput.getText().toString();
        String passwordConf = passwordConfInput.getText().toString();

        if (firstName.length() == 0 || lastName.length() == 0) {
            Snackbar.make(getActivity().findViewById(R.id.top_view), R.string.missing_name, Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (!Util.checkEmail(email)) {
            Snackbar.make(getActivity().findViewById(R.id.top_view), R.string.invalid_email, Snackbar.LENGTH_SHORT).show();
            return;
        }

        switch (Util.checkPasswords(password, passwordConf)) {

            case TOO_SIMPLE:
                Snackbar.make(getActivity().findViewById(R.id.top_view), R.string.password_complexity, Snackbar.LENGTH_SHORT).show();
                return;
            case MISMATCH:
                Snackbar.make(getActivity().findViewById(R.id.top_view), R.string.password_mismatch, Snackbar.LENGTH_SHORT).show();
                return;
            case MISSING:
                Snackbar.make(getActivity().findViewById(R.id.top_view), R.string.password_required, Snackbar.LENGTH_SHORT).show();
                return;
            default:
                break;
        }

        // make sure only one task is running at a time.
        if (signUpTask == null) {
            signUpTask = new SignUpTask();
            signUpTask.execute(firstName, lastName, email, password);
        }
    }

    /**
     * Use this AsyncTask to move the account creation work into a separate thread to offload some of
     * the work from the main thread.
     */
    private class SignUpTask extends AsyncTask<String, Void, DhtProto.User>
    {
        /**
         * This will run on the UI thread before the task executes.
         * This will show the loading spinner
         */
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            ProgressBar loadingBar =  getActivity().findViewById(R.id.loadingbar);
            loadingBar.setVisibility(View.VISIBLE);
        }

        /**
         * This function gets called to do the work in a separate thread. It MUST be given 4
         * arguments:
         * FirstName
         * LastName
         * Email
         * Password
         *
         * @param strings list of strings to pass into the function
         * @return The users that was just created.
         */
        @Override
        protected DhtProto.User doInBackground(String... strings)
        {
            String firstName = strings[0];
            String lastName = strings[1];
            String email = strings[2];
            String password = strings[3];

            // create the user in the database
            Database db = Database.getInstance(getActivity());
            DhtProto.User user = db.createUser(firstName, lastName, email, password);
            db.close();

            return user;
        }

        /**
         * Run this on the main thread to update the UI.
         * This will display an error message or go to the dashboard screen.
         * @param user the user that was just created
         */
        @Override
        protected void onPostExecute(DhtProto.User user)
        {
            ProgressBar loadingBar =  getActivity().findViewById(R.id.loadingbar);
            if (loadingBar != null) {
                loadingBar.setVisibility(View.VISIBLE);
            }

            if (user == null) {
                Snackbar.make(getActivity().findViewById(R.id.top_view), R.string.unknown_err, Snackbar.LENGTH_SHORT).show();
            } else {
                ((MainActivity) getActivity()).setCurrentUser(user);
                ((MainActivity) getActivity()).navigateTo(new FragmentDashBoard(), false);
            }

            signUpTask = null;
        }
    }
}
