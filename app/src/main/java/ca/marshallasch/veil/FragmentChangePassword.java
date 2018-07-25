package ca.marshallasch.veil;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.protobuf.InvalidProtocolBufferException;

import ca.marshallasch.veil.database.Database;
import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;


/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-07-04
 */
public class FragmentChangePassword extends Fragment
{
    private EditText oldPasswordInput;
    private EditText newPasswordInput;
    private EditText newPasswordConfInput;
    private DhtProto.User currentUser;

    /**
     * Required empty public constructor
     */
    public FragmentChangePassword(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        //setting a white background for overlaying the view post fragment
        view.setBackgroundColor(Color.WHITE);

        Bundle bundle = getArguments();

        try {
            if (bundle != null) {
                currentUser = DhtProto.User.parseFrom(bundle.getByteArray(getString(R.string.user_object_key)));
            } else if (savedInstanceState != null) {
                currentUser = DhtProto.User.parseFrom(savedInstanceState.getByteArray(getString(R.string.user_object_key)));
            }
        }
        catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }


        oldPasswordInput = view.findViewById(R.id.password_current_text_edit);
        newPasswordInput = view.findViewById(R.id.password_text_edit);
        newPasswordConfInput = view.findViewById(R.id.password_conf_text_edit);

        Button cancel = view.findViewById(R.id.cancel_button);
        Button done = view.findViewById(R.id.done_button);

        cancel.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
            Util.hideKeyboard(getActivity());
        });

        done.setOnClickListener(v ->{
            //go to previous screen when done.
            Util.hideKeyboard(getActivity());
            this.onDoneClicked();
        });

        return view;
    }


    /**
     * This handles the password change. will check that the password is valid and update the password.
     */
    private void onDoneClicked()
    {
        String oldPassword = oldPasswordInput.getText().toString();
        String newPassword = newPasswordInput.getText().toString();
        String newPasswordConf = newPasswordConfInput.getText().toString();

        if (oldPassword.length() == 0 ) {
            Snackbar.make(getActivity().findViewById(R.id.top_view), R.string.missing_name, Snackbar.LENGTH_SHORT).show();
            return;
        }


        switch (Util.checkPasswords(newPassword, newPasswordConf)) {

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
        boolean status = db.updatePassword(currentUser.getEmail(), oldPassword, newPassword);
        db.close();

        if (!status) {
            Snackbar.make(getActivity().findViewById(R.id.top_view), R.string.unknown_err, Snackbar.LENGTH_SHORT).show();
        } else {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
