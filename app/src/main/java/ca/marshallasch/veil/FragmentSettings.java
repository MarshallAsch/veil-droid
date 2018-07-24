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

        //set dark theme toggle to save preference
        Switch darkThemeToggle = view.findViewById(R.id.toggle_dark_theme);
        SharedPreferences preferences = getActivity().
                getSharedPreferences(MainActivity.SYSTEM_PREF, getActivity().MODE_PRIVATE);
        darkThemeToggle.setChecked(preferences.getBoolean(IS_DARK_THEME_TOGGLED, false));

        //on click listener so allows for toggle to reset itself
        darkThemeToggle.setOnClickListener(view1 -> {
            //alert dialog for changing the theme settings
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_alert_message);
            builder.setCancelable(true);

            //closes the dialog and sets the dialog to the current checked state of the toggle
            builder.setPositiveButton(
                    R.string.yes,
                    (dialog, id) -> {
                        toggleDarkTheme(darkThemeToggle.isChecked());
                        dialog.cancel();
                    });

            //close dialog resets the toggle to saved shared prefs
            builder.setNegativeButton(
                    R.string.no,
                    (dialog, id) -> {
                        darkThemeToggle.setChecked(preferences.getBoolean(IS_DARK_THEME_TOGGLED, false));
                        dialog.cancel();
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
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
