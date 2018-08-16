package ca.marshallasch.veil;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.marshallasch.veil.database.Database;
import ca.marshallasch.veil.proto.DhtProto;


/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-07-04
 */
public class FragmentAccountManagement extends Fragment
{
    private DhtProto.User currentUser;

    /**
     * Required empty public constructor
     */
    public FragmentAccountManagement(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_account_management, container, false);

        Activity activity = getActivity();

        currentUser = ((MainActivity) activity).getCurrentUser();

        TextView fullName = view.findViewById(R.id.account_name_text);
        TextView email = view.findViewById(R.id.account_email_text);

        // edit buttons
        MaterialButton editName = view.findViewById(R.id.account_name_edit);
        MaterialButton editEmail = view.findViewById(R.id.account_email_edit);
        MaterialButton editPassword = view.findViewById(R.id.account_password_edit);

        fullName.setText(getString(R.string.full_name_placeholder, currentUser.getFirstName(), currentUser.getLastName()));
        email.setText(currentUser.getEmail());


        editPassword.setOnClickListener(view1 -> {
            Fragment fragment = new FragmentChangePassword();
            Bundle bundle = new Bundle();
            bundle.putByteArray(getString(R.string.user_object_key), currentUser.toByteArray());
            fragment.setArguments(bundle);
            ((MainActivity) getActivity()).animateFragmentSlide(fragment, true);

        });

        // update the users email address
        editEmail.setOnClickListener((View view1) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            EditText emailTextInput = new EditText(activity);

            emailTextInput.setHint(R.string.new_email_label);
            builder.setView(emailTextInput);
            builder.setTitle(R.string.change_email);
            builder.setMessage(R.string.change_email_message);
            builder.setCancelable(false);
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                // cancel do nothing
            });
            builder.setPositiveButton(R.string.save, (dialog, which) -> {
                String newEmail = emailTextInput.getText().toString();

                Database db = Database.getInstance(activity);
                boolean status = db.updateEmail(currentUser.getUuid(), newEmail);
                db.close();

                // update and replace the current user object
                if (status) {
                    currentUser = DhtProto.User.newBuilder(currentUser)
                            .setEmail(newEmail)
                            .build();

                    ((MainActivity) activity).setCurrentUser(currentUser);

                    fullName.setText(getString(R.string.full_name_placeholder, currentUser.getFirstName(), currentUser.getLastName()));
                    email.setText(currentUser.getEmail());
                }
            });

            builder.show();
        });

        editName.setOnClickListener((View view1) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            LinearLayout layout = new LinearLayout(activity);
            layout.setOrientation(LinearLayout.VERTICAL);

            EditText firstNameText = new EditText(activity);
            firstNameText.setHint(R.string.first_name_title);

            EditText lastNameText = new EditText(activity);
            lastNameText.setHint(R.string.last_name_title);

            layout.addView(firstNameText);
            layout.addView(lastNameText);
            builder.setView(layout);

            builder.setTitle(R.string.change_name);
            builder.setMessage(R.string.change_name_message);
            builder.setCancelable(false);
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                // cancel do nothing
            });
            builder.setPositiveButton(R.string.save, (dialog, which) -> {

                String firstName = firstNameText.getText().toString();
                String lastName = lastNameText.getText().toString();


                Database db = Database.getInstance(activity);
                boolean status = db.updateName(currentUser.getUuid(), firstName, lastName);
                db.close();

                // update and replace the current user object
                if (status) {
                    currentUser = DhtProto.User.newBuilder(currentUser)
                            .setFirstName(firstName)
                            .setLastName(lastName)
                            .build();

                    ((MainActivity) activity).setCurrentUser(currentUser);

                    fullName.setText(getString(R.string.full_name_placeholder, currentUser.getFirstName(), currentUser.getLastName()));
                    email.setText(currentUser.getEmail());
                }
            });

            builder.show();
        });


        return view;
    }
}
