/*******************************************************************************
 * Mirakel is an Android App for managing your ToDo-Lists
 *
 * Copyright (c) 2013-2014 Anatolij Zelenin, Georg Semmler.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package de.azapps.mirakel.settings.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import java.util.List;

import de.azapps.mirakel.helper.Helpers;
import de.azapps.mirakel.helper.MirakelCommonPreferences;
import de.azapps.mirakel.helper.MirakelPreferences;
import de.azapps.mirakel.model.list.ListMirakel;
import de.azapps.mirakel.settings.R;
import de.azapps.mirakel.settings.SettingsActivity;
import de.azapps.mirakel.settings.taskfragment.TaskFragmentSettingsFragment;

public class UISettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings_gui);

        final List<ListMirakel> lists = ListMirakel.all();
        final CharSequence entryValues[] = new String[lists.size()];
        final CharSequence entries[] = new String[lists.size()];
        final CharSequence entryValuesWithDefault[] = new String[lists.size() + 1];
        final CharSequence entriesWithDefault[] = new String[lists.size() + 1];
        int i = 0;
        for (final ListMirakel list : lists) {
            final String id = String.valueOf(list.getId());
            final String name = list.getName();
            entryValues[i] = id;
            entries[i] = name;
            entryValuesWithDefault[i + 1] = id;
            entriesWithDefault[i + 1] = name;
            i++;
        }
        entriesWithDefault[0] = getString(R.string.default_list);
        entryValuesWithDefault[0] = "default";

        // Dark theme
        final CheckBoxPreference darkTheme = (CheckBoxPreference) findPreference("DarkTheme");
        darkTheme
        .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(
                final Preference preference,
                final Object newValue) {
                MirakelCommonPreferences.setIsDark((Boolean) newValue);
                Helpers.restartApp(getActivity());
                return true;
            }
        });

        final ListPreference isTablet = (ListPreference) findPreference("useTabletLayoutNew");
        final String[] values = {"0", "1", "2", "3"};
        final String[] e = getResources().getStringArray(
                               R.array.tablet_options);
        isTablet.setEntries(e);
        isTablet.setEntryValues(values);
        isTablet.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference,
                                              final Object newValue) {
                final int value = Integer.parseInt(newValue.toString());
                isTablet.setSummary(e[value]);
                return true;
            }
        });

        final ListPreference language = (ListPreference) findPreference("language");
        setLanguageSummary(language, MirakelCommonPreferences.getLanguage());
        language.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference,
                                              final Object newValue) {
                setLanguageSummary(language, newValue.toString());
                MirakelPreferences.getEditor()
                .putString("language", newValue.toString())
                .commit();
                Helpers.restartApp(getActivity());
                return false;
            }
        });

        final ListPreference startupListPreference = (ListPreference) findPreference("startupList");
        startupListPreference.setEntries(entries);
        startupListPreference.setEntryValues(entryValues);
        startupListPreference.setEnabled(true);

        final Preference taskFragment = findPreference("task_fragment");
        taskFragment.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((SettingsActivity)getActivity()).startPreferenceFragment(new TaskFragmentSettingsFragment(),
                        false);
                return true;
            }
        });
    }

    private void setLanguageSummary(final ListPreference language,
                                    final String current) {
        final String[] keys = getResources().getStringArray(
                                  R.array.language_keys);
        language.setSummary(keys[0]);
        for (int j = 0; j < keys.length; j++) {
            if (current.equals(keys[j])) {
                language.setSummary(getResources()
                                    .getStringArray(R.array.language_values)[j]);
                break;
            }
        }
    }

}
