package ca.marshallasch.veil;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-05-28
 */
public class SignUp extends Fragment
{
    /**
     * This is returned from {@link #checkPasswords()} to determine what was wrong with the password
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.signup, container, false);

        firstNameInput = view.findViewById(R.id.first_name_text_edit);
        lastNameInput = view.findViewById(R.id.last_name_text_edit);
        emailAddressInput = view.findViewById(R.id.email_text_edit);
        passwordInput = view.findViewById(R.id.password_text_edit);
        passwordConfInput = view.findViewById(R.id.password_conf_text_edit);
        phoneNumberInput = view.findViewById(R.id.phone_text_edit);

        MaterialButton cancel = view.findViewById(R.id.cancel_button);
        MaterialButton done = view.findViewById(R.id.done_button);

        cancel.setOnClickListener(v -> {
            getFragmentManager().popBackStack();
        });

        done.setOnClickListener(this::onDoneClicked);

        return view;
    }


    private void onDoneClicked(View view)
    {

        int message = R.string.password_mismatch;

        switch (checkPasswords()) {

            case TOO_SIMPLE:
                message = R.string.password_complexity;
                break;
            case MISMATCH:
                message = R.string.password_mismatch;
                break;
            case MISSING:
                message = R.string.password_required;
                break;
            case GOOD:
                message = R.string.password_accepted;
                break;
        }


        Snackbar.make(getActivity().findViewById(R.id.top_view), message, Snackbar.LENGTH_SHORT).show();
    }


    /**
     * This checks that the passwords that have been entered match and meet the
     * minimum complexity requirements.
     *
     * @return {@link PasswordState}
     */
    private PasswordState checkPasswords()
    {

        String password = passwordInput.getText().toString();
        String passwordConf = passwordConfInput.getText().toString();

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
