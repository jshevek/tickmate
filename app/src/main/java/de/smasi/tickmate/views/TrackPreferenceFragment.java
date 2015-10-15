package de.smasi.tickmate.views;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.smasi.tickmate.R;
import de.smasi.tickmate.database.DataSource;
import de.smasi.tickmate.models.Track;
import de.smasi.tickmate.widgets.GroupListPreference;
import de.smasi.tickmate.widgets.LauncherAction;

public class TrackPreferenceFragment extends PreferenceFragment implements
OnSharedPreferenceChangeListener  {

    private static String TAG = "TrackPreferenceFragment";
	private int track_id;
    private Track track;
    private EditTextPreference name;

    private EditTextPreference description;
    private CheckBoxPreference enabled;
    private CheckBoxPreference multiple_entries_enabled;
	private IconPreference icon;
    private GroupListPreference mGroupsPref;
    private Preference mLauncherActionPreference;
    private static DataSource mDataSource = DataSource.getInstance();

    // indicates to onActivityResult that user selected an app for the launcher
    private final static int REQUEST_CODE_DEFINE_LAUNCH_APP = 1001;  

    public TrackPreferenceFragment() {
        super();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.track_preferences);

        track_id = getArguments().getInt("track_id");
        Log.d(TAG, "onCreate given track id " + track_id);
        track = mDataSource.getTrack(track_id);
        Log.d(TAG, " retrieved track with id = " + track.getId());
		loadTrack();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + ")");
        switch (requestCode) {
            case REQUEST_CODE_DEFINE_LAUNCH_APP:
                if (resultCode == Activity.RESULT_OK) {
//                    Log.d(TAG, "Data: " + data.getAction() + "\n" +
//                                    data.getExtras() + "\n" +
//                                    data.getCategories() + "\n" +
//                                    data.getComponent() + "\n" +
//                                    data.getComponent().getPackageName() + "\n" +
//                                    data.getComponent().getClassName() + "\n"
//                    );

                    ComponentName c = data.getComponent();
                    track.setLauncherComponent(c);
                    DataSource.getInstance().storeTrack(track);

                    //  OnSharedPreferenceChanged has been bypassed, so do this here:
                    mLauncherActionPreference.setSummary(track.getLauncherSummary());

                }
                break;
        }
//        super.onActivityResult(requestCode, resultCode, data);  // TODO is this needed?
    }


	private void loadTrack() {
        Log.d(TAG, "Loading track #" + track.getId());
        icon = (IconPreference) findPreference("icon");
        icon.setText(track.getIcon());

        name = (EditTextPreference) findPreference("name");
        name.setText(track.getName());
        name.setSummary(track.getName());

        description = (EditTextPreference) findPreference("description");
        description.setText(track.getDescription());
        description.setSummary(track.getDescription());

        enabled = (CheckBoxPreference) findPreference("enabled");
        enabled.setChecked(track.isEnabled());

        multiple_entries_enabled = (CheckBoxPreference) findPreference("multiple_entries_enabled");
        multiple_entries_enabled.setChecked(track.multipleEntriesEnabled());

        mGroupsPref = (GroupListPreference) findPreference("groups");
        mGroupsPref.setTrack(track);
        mGroupsPref.populate();

        mLauncherActionPreference = findPreference(LauncherAction.sPreferenceKeyLauncherActionDetail);
        mLauncherActionPreference.setSummary(track.getLauncherSummary());

        mLauncherActionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // This allows us to select an app.  May implement 'select a file to open' later.
                Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
                pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
                startActivityForResult(pickIntent, REQUEST_CODE_DEFINE_LAUNCH_APP);
                return true;
            }
        });


//        // TODO following stanze to be removed after testing
//        Preference launchTest = findPreference("launcher_action_button");
//        launchTest.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                Log.d(TAG, "onPreferenceClick");
//                Toast.makeText(getActivity(), "Testing activity launcher", Toast.LENGTH_LONG).show();
//                LauncherAction.launchComponent(getActivity(), track.getLauncherComponent());
//                return true;
//            }
//        });


    }

    public void onResume() {
        super.onResume();

        // Repopulate GroupListPreference because we might resume from Edit groups activity
        mGroupsPref = (GroupListPreference) findPreference("groups");
        mGroupsPref.setTrack(track);
        mGroupsPref.populate();

        getPreferenceScreen().getSharedPreferences()
            .registerOnSharedPreferenceChangeListener(this);
    }

    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
            .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        Preference pref = findPreference(key);
//        Log.v(TAG, "onSharedPreferenceChanged -- " + pref.getTitle());

        if (pref instanceof IconPreference) {
            if (pref.equals(icon)) {
        		track.setIcon(icon.getText());
                //icon.setSummary(track.getIcon());
        	}
        }            
        else if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;
            if (pref.equals(name)) {
            	track.setName(name.getText());
            }
            if (pref.equals(description)) {
            	track.setDescription(description.getText());
            }
            pref.setSummary(etp.getText());
        }
        else if (pref instanceof CheckBoxPreference) {
        	if (pref.equals(enabled)) {
        		track.setEnabled(enabled.isChecked());
        	}
        	if (pref.equals(multiple_entries_enabled)) {
                track.setMultipleEntriesEnabled(multiple_entries_enabled.isChecked());
            }
        } else if (pref instanceof GroupListPreference) {
            GroupListPreference mp = (GroupListPreference) pref;
//            Log.d(TAG, "MultiSelectListPreference changed, with groupIds: " + TextUtils.join(",", mp.getValues()));

            // Convert the Set returned by getValues into a List, as expected by setGroupIdsUsingStrings:
            List<Integer> groupIds = new ArrayList<>();
            for (String value : mp.getValues()) {
                groupIds.add(Integer.valueOf(value));
            }

            mDataSource.linkOneTrackManyGroups(track.getId(), groupIds);
//            Log.d(TAG, "\tUser selected: " + TextUtils.join(",", groupIds));

            mGroupsPref.populate();//setSummary(getGroupNamesForSummary());
//                    + "  \n" + TextUtils.join("\n", mDataSource.getGroups())); // Leaving here for future debugging, until tests are written
//            Log.d(TAG, "Confirm that the group IDs are correct: " + TextUtils.join(",", track.getGroupIdsAsSet()));
//            Log.d(TAG, "Confirm that the group NAMES are correct: " + TextUtils.join(",", track.getGroupNamesAsSet()));

// TODO Confirm this is not needed then remove:
//        } else if (pref.equals(mLauncherActionPreference)) { // TODO will this ever even be reached, since this is handled by the callback?
//            // TODO JS Store the launch action info in track
//            updateLauncherRelatedPreferenceItems();
        }
        mDataSource.storeTrack(track);
    }

// TODO Confirm not needed and remove:
//    protected void updateLauncherRelatedPreferenceItems() {
//        if (mLauncherActionPreference == null) {
//            mLauncherActionPreference = findPreference(LauncherAction.sPreferenceKeyLauncherActionDetail);
//        }
////        mLauncherActionPreference.setSummary("++" + track.getLauncherAction().getActionDetailForDatabase());
//        mLauncherActionPreference.setSummary("TODO");
//    }

}
