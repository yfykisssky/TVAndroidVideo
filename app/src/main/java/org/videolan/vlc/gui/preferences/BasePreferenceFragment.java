package org.videolan.vlc.gui.preferences;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by yfykisssky on 16/8/7.
 */
public abstract class BasePreferenceFragment extends PreferenceFragmentCompat {

    protected abstract int getXml();
    protected abstract int getTitleId();

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(getXml());
    }

    @Override
    public void onStart() {
        super.onStart();
        final AppCompatActivity activity = (AppCompatActivity)getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(getString(getTitleId()));
        }
    }

    protected void loadFragment(Fragment fragment) {
       /* getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_placeholder, fragment)
                .addToBackStack("main")
                .commit();*/
    }
}

