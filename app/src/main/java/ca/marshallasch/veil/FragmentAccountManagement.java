package ca.marshallasch.veil;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
        Button editName = view.findViewById(R.id.account_name_edit);
        Button editEmail = view.findViewById(R.id.account_email_edit);
        Button editPassword = view.findViewById(R.id.account_password_edit);

        fullName.setText(getString(R.string.full_name_placeholder, currentUser.getFirstName(), currentUser.getLastName()));
        email.setText(currentUser.getEmail());


        editPassword.setOnClickListener(view1 -> {
            Fragment fragment = new FragmentChangePassword();
            Bundle bundle = new Bundle();
            bundle.putByteArray(getString(R.string.user_object_key), currentUser.toByteArray());
            fragment.setArguments(bundle);
            ((MainActivity) getActivity()).animateFragmentSlide(fragment, true);

        });

        return view;
    }
}
