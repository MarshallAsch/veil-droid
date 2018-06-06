package ca.marshallasch.veil;

import android.app.Fragment;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import ca.marshallasch.veil.database.Database;
import ca.marshallasch.veil.proto.DhtProto;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {
        // hides the menu bar at the top so you have a full screen landing page
        ((MainActivity) getActivity()).getSupportActionBar().hide();

        View view = inflater.inflate(R.layout.fragment_login, container,false);

        ConstraintLayout layout = view.findViewById(R.id.login_layout);

        //hides keyboard if white space is pressed
        layout.setOnTouchListener((view12, ev) -> {
            hideKeyboard(view12);
            return false;
        });

        // buttons and event listeners
        MaterialButton login = view.findViewById(R.id.enter_btn);
        MaterialButton cancel = view.findViewById(R.id.back_btn);


        emailAddressInput = view.findViewById(R.id.username);
        passwordInput = view.findViewById(R.id.password);

        login.setOnClickListener(view1 -> {
            Log.i("Fragment Login", "enter button pressed");

            hideKeyboard(view1);

            Database db = Database.getInstance(getActivity());

            DhtProto.User user = db.login(emailAddressInput.getText().toString(), passwordInput.getText().toString());
            db.close();

            if (user == null) {
                Snackbar.make(getActivity().findViewById(R.id.top_view), R.string.username_pass_not_match, Snackbar.LENGTH_SHORT).show();
            } else {
                ((MainActivity) getActivity()).setCurrentUser(user);
                ((MainActivity) getActivity()).navigateTo(new FragmentDashBoard(), true);
            }
        });

        cancel.setOnClickListener(view1 -> {
            hideKeyboard(view1);
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

    /**
     * Hides Android's soft keyboard.
     *
     * @param view
     */
    protected void hideKeyboard(View view) {
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
