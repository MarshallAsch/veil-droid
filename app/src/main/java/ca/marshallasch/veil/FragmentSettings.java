package ca.marshallasch.veil;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

/**
 * This class holdes the UI view for the settings page of the application
 */
public class FragmentSettings extends Fragment {

    public static final String IS_DARK_THEME_TOGGLED = "IS_DARK_THEME_TOGGLED";
    public FragmentSettings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Switch darkThemeToggle = view.findViewById(R.id.toggle_dark_theme);
        SharedPreferences preferences = getActivity().
                getSharedPreferences(MainActivity.SYSTEM_PREF, getActivity().MODE_PRIVATE);
        darkThemeToggle.setChecked(preferences.getBoolean(IS_DARK_THEME_TOGGLED, false));
        darkThemeToggle.setOnCheckedChangeListener((compoundButton, isToggled) -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage(R.string.dialog_alert_message);
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    R.string.yes,
                    (dialog, id) -> {
                        toggleDarkTheme(isToggled);
                        dialog.cancel();
                    });

            builder1.setNegativeButton(
                    R.string.no,
                    (dialog, id) -> dialog.cancel());

            AlertDialog alert11 = builder1.create();
            alert11.show();
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void toggleDarkTheme(boolean isDarkTheme){
        SharedPreferences preferences = getActivity().
                getSharedPreferences(MainActivity.SYSTEM_PREF, getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_DARK_THEME_TOGGLED, isDarkTheme);
        editor.apply();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();

    }

}
