package se.miun.dahe1501.bathingsites;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Setting fragment - creates preferences used for settings in Settings Activity
 * Specifies URL to download coordinates, URL to download weather and Radius for Maps
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences installningar = PreferenceManager.getDefaultSharedPreferences(getActivity());


        Preference kordinatPref = findPreference(getResources().getString(R.string.pref_coordinates));
        kordinatPref.setSummary(installningar.getString(getResources().getString(R.string.pref_coordinates),getString(R.string.Default_download)));

        Preference weatherPref = findPreference(getResources().getString(R.string.pref_weather));
        weatherPref.setSummary(installningar.getString(getResources().getString(R.string.pref_weather),getString(R.string.url_default)));

        Preference radius = findPreference(getResources().getString(R.string.pref_radius));
        radius.setSummary(installningar.getString(getResources().getString(R.string.pref_radius),getString(R.string.radius_text)));

        installningar.registerOnSharedPreferenceChangeListener(this);




    }

    /* Called when user changes the preference-values */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getResources().getString(R.string.pref_coordinates))){
            Preference kordinatPref = findPreference(key);
            kordinatPref.setSummary(sharedPreferences.getString(key, ""));

        } else if (key.equals(getResources().getString(R.string.pref_weather))){
            Preference weather = findPreference(key);
            weather.setSummary(sharedPreferences.getString(key,""));
        } else if (key.equals(getResources().getString(R.string.pref_radius))) {
            Preference radius = findPreference(key);
            radius.setSummary(sharedPreferences.getString(key,""));
        }
    }
}
