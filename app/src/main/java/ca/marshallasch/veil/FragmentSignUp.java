package ca.marshallasch.veil;

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
    /**
     * This is returned from {@link #checkPasswords(String, String)} to determine what was wrong with the password
     */
    enum PasswordState
    {
        TOO_SIMPLE,
        MISMATCH,
        MISSING,
        GOOD
    }

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailAddressInput;
    private EditText passwordInput;
    private EditText passwordConfInput;
    private EditText phoneNumberInput;

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
        ((MainActivity) getActivity()).getSupportActionBar().hide();

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
        phoneNumberInput = view.findViewById(R.id.phone_text_edit);

        MaterialButton cancel = view.findViewById(R.id.cancel_button);
        MaterialButton done = view.findViewById(R.id.done_button);

        cancel.setOnClickListener(v -> {
            ((MainActivity) getActivity()).navigateTo(new FragmentLanding(), false);
            Util.hideKeyboard(v, getActivity());
        });

        done.setOnClickListener(v ->{
            //route to login after sign up
            Util.hideKeyboard(v, getActivity());
            this.onDoneClicked();
        });

        return view;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        // return the action bar.
        ((MainActivity) getActivity()).getSupportActionBar().show();
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

        if (!checkEmail()) {
            Snackbar.make(getActivity().findViewById(R.id.top_view), R.string.invalid_email, Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (!checkPhone()) {
            Snackbar.make(getActivity().findViewById(R.id.top_view), R.string.invalid_phone_number, Snackbar.LENGTH_SHORT).show();
            return;
        }

        switch (checkPasswords(password, passwordConf)) {

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

        // create the user in the database
        Database db = Database.getInstance(getActivity());
        DhtProto.User user = db.createUser(firstName, lastName, email, password);
        db.close();

        if (user == null) {
            Snackbar.make(getActivity().findViewById(R.id.top_view), R.string.unknown_err, Snackbar.LENGTH_SHORT).show();
        } else {
            ((MainActivity) getActivity()).setCurrentUser(user);
            ((MainActivity) getActivity()).navigateTo(new FragmentDashBoard(), false);
        }
    }

    /**
     * This checks that the passwords that have been entered match and meet the
     * minimum complexity requirements.
     * @param password      the password that the user entered
     * @param passwordConf  the confirmation of the password
     * @return {@link PasswordState}
     */
    private PasswordState checkPasswords(@NonNull String password, @NonNull String passwordConf)
    {
        if (password.length() == 0 || passwordConf.length() == 0) {
            return PasswordState.MISSING;
        }

        int numUpper = 0;
        int numLower = 0;
        int numDigit = 0;
        int numSpecial = 0;
        int length = password.length();

        // make sure that they match before checking complexity
        if (!password.equals(passwordConf)) {
            return PasswordState.MISMATCH;
        }

        // check complexity
        for (int i = 0; i < length; i++) {

            if (Character.isUpperCase(password.charAt(i))) {
                numUpper++;
            } else if (Character.isLowerCase(password.charAt(i))) {
                numLower++;
            } else if (Character.isDigit(password.charAt(i))) {
                numDigit++;
            } else if (Character.getType(password.charAt(i)) == Character.OTHER_PUNCTUATION) {
                numSpecial++;
            }
        }

        if (length < 8 || numUpper == 0 || numLower == 0 || numDigit == 0 || numSpecial == 0) {
            return PasswordState.TOO_SIMPLE;
        }

        return PasswordState.GOOD;
    }

    /**
     * Simple check of the email format, Note that the email does not need to be  RFC 5322
     * compliment, it just needs to be something@something.something.else.
     * @return true if the email is valid otherwise false.
     */
    private boolean checkEmail() {
        String email = emailAddressInput.getText().toString();

        return email.length() != 0 && email.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
    }

    /**
     * Simple check of the phone number format, Note that the email does not need to be  E.164
     * compliment, it just needs to be some phone number between 10 and 15 digits long
     * @return true if the phone number is valid otherwise false.
     */
    private boolean checkPhone() {
        String phoneNumber = phoneNumberInput.getText().toString();

        return phoneNumber.length() >= 10 && phoneNumber.length() <=15 && phoneNumber.matches("^[0-9]*$");
    }
}
