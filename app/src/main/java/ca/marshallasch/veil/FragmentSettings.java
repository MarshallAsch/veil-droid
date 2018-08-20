package ca.marshallasch.veil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import java.util.Random;

import static ca.marshallasch.veil.database.SyncStatsContract.SYNC_MESSAGE_V1;
import static ca.marshallasch.veil.database.SyncStatsContract.SYNC_MESSAGE_V2;

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
    public static final String PREF_SYNC_VERSION = "PREF_SYNC_VERSION";
    public static final String PREF_MEMORY_SAVER = "PREF_MEMORY_SAVER";

    public FragmentSettings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //set dark theme toggle to save preference
        Switch darkThemeToggle = view.findViewById(R.id.toggle_dark_theme);
        //set the sync protocal version
        Switch protocolVersionToggle = view.findViewById(R.id.toggle_sync_protocal);
        //set the memory saver option
        Switch memorySaverToggle = view.findViewById(R.id.memory_save_toggle);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        darkThemeToggle.setChecked(preferences.getBoolean(PREF_DARK_THEME, false));
        protocolVersionToggle.setChecked(preferences.getInt(PREF_SYNC_VERSION, SYNC_MESSAGE_V1) == SYNC_MESSAGE_V2);
        memorySaverToggle.setChecked(preferences.getBoolean(PREF_MEMORY_SAVER, false));


        //on click listener so allows for toggle to reset itself
        darkThemeToggle.setOnClickListener(view1 -> {
            toggleDarkTheme(darkThemeToggle.isChecked());
        });

        protocolVersionToggle.setOnClickListener(view1 -> {
            toggleProtocol(protocolVersionToggle.isChecked());
        });

        memorySaverToggle.setOnClickListener(view1 -> {
            toggleMemorySaver(memorySaverToggle.isChecked());
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void toggleDarkTheme(boolean isDarkTheme){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

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


    private void toggleProtocol(boolean isV2) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_SYNC_VERSION, isV2 ? SYNC_MESSAGE_V2 : SYNC_MESSAGE_V1);
        editor.apply();
    }

    private void toggleMemorySaver(boolean isMemorySaver){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_MEMORY_SAVER, isMemorySaver);
        editor.apply();

    }
}
