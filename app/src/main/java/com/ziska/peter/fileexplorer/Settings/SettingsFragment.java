package com.ziska.peter.fileexplorer.Settings;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.ziska.peter.fileexplorer.R;
import com.ziska.peter.fileexplorer.Utils.FileUtil;
import com.ziska.peter.fileexplorer.Utils.Util;

public class SettingsFragment extends PreferenceFragmentCompat implements SettingViewInt{

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.preference, s);

        EditTextPreference preference = (EditTextPreference) getPreferenceScreen().findPreference("defaultFolderKey");

        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {


            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean returnValue = true;
                if (!FileUtil.isPathCorrect(newValue.toString())) {
                    showError();
                    returnValue = false;
                }
                return returnValue;
            }
        });
    }


    @Override
    public void showError() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Invalid Input");
        builder.setMessage("Entered directory does not exist");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }
}
