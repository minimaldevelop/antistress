package com.minimaldevelop.antistress;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Preferences extends PreferenceActivity {

	ListPreference prefRemainder1;
	ListPreference prefRemainder2;
	ListPreference prefRemainder3;

	String prefRemainder1Value;
	String prefRemainder2Value;
	String prefRemainder3Value;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.prefs);

		prefRemainder1 = (ListPreference) findPreference("prefRemainder1");
		prefRemainder2 = (ListPreference) findPreference("prefRemainder2");
		prefRemainder3 = (ListPreference) findPreference("prefRemainder3");

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		prefRemainder1Value = prefs.getString("prefRemainder1", getResources()
				.getString(R.string.prefRemainder1DefaultValue));
		prefRemainder2Value = prefs.getString("prefRemainder2", getResources()
				.getString(R.string.prefRemainder2DefaultValue));
		prefRemainder3Value = prefs.getString("prefRemainder3", getResources()
				.getString(R.string.prefRemainder3DefaultValue));

		prefRemainder1.setSummary(getString(R.string.prefRemainderSummary,
				prefRemainder1Value));
		prefRemainder2.setSummary(getString(R.string.prefRemainderSummary,
				prefRemainder2Value));
		prefRemainder3.setSummary(getString(R.string.prefRemainderSummary,
				prefRemainder3Value));

		prefRemainder1
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						prefRemainder1.setSummary(getString(
								R.string.prefRemainderSummary,
								(String) newValue));
						return true;
					}
				});

		prefRemainder2
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						prefRemainder2.setSummary(getString(
								R.string.prefRemainderSummary,
								(String) newValue));
						return true;
					}
				});

		prefRemainder3
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						prefRemainder3.setSummary(getString(
								R.string.prefRemainderSummary,
								(String) newValue));
						return true;
					}
				});
	}
}
