package ca.marshallasch.veil;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

/**
 * This class holdes the UI view for the settings page of the application
 */
public class FragmentSettings extends Fragment {
    public FragmentSettings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Switch darkThemeToggle = view.findViewById(R.id.toggle_dark_theme);

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            darkThemeToggle.setChecked(true);
        }
        else{
            darkThemeToggle.setChecked(false);
        }
        darkThemeToggle.setOnCheckedChangeListener((compoundButton, isToggled) -> toggleDarkTheme(isToggled));

        // Inflate the layout for this fragment
        return view;
    }

    private void toggleDarkTheme(boolean isDarkTheme){
        if(isDarkTheme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        Intent intent = new Intent(getActivity(), MainActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();

    }

}
