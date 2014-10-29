package ru.lsv.gwtlib.downloader.android;

import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;

/**
 * Fragment ��� ��������� ���������� ��� ����������� �������� � ActionBar'�� <br/>
 * ��. https://github.com/kolavar/android-support-v4-preferencefragment
 * 
 * @author lsv
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.preferencies);
    }

}
