package ca.marshallasch.veil;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import java.util.Random;

/**
 * This class holds the UI view for the settings page of the application
 *
 * @author Weihan Li
 * @version 1.0
 * @since 2018-07-18
 */
public class FragmentSettings extends Fragment {

    public static final String PREF_DARK_THEME = "PREF_DARK_THEME";
    public static final String PREF_LOGIN_RAND_VAL = "PREF_LOGIN_RAND_VAL";



    public FragmentSettings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //set dark theme toggle to save preference
        Switch darkThemeToggle = view.findViewById(R.id.toggle_dark_theme);
        SharedPreferences preferences = getActivity().
                getSharedPreferences(MainActivity.SYSTEM_PREF, Context.MODE_PRIVATE);
        darkThemeToggle.setChecked(preferences.getBoolean(PREF_DARK_THEME, false));

        //on click listener so allows for toggle to reset itself
        darkThemeToggle.setOnClickListener(view1 -> {

            toggleDarkTheme(darkThemeToggle.isChecked());
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void toggleDarkTheme(boolean isDarkTheme){
        SharedPreferences preferences = getActivity().
                getSharedPreferences(MainActivity.SYSTEM_PREF, Context.MODE_PRIVATE);

        int random = new Random().nextInt();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_DARK_THEME, isDarkTheme);
        editor.putInt(PREF_LOGIN_RAND_VAL, random);              // to protect against replay attacks
        editor.apply();

        String userID = ((MainActivity) getActivity()).getCurrentUser().getUuid();

        // set the arguments in the intent to automatically login the user
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_LOGGED_IN_RAND, random);
        intent.putExtra(MainActivity.EXTRA_LOGGED_IN_USER_ID, userID);

        getActivity().startActivity(intent);
        getActivity().finish();

    }

}
