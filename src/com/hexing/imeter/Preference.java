package com.hexing.imeter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.SherlockPreferenceActivity;


public class Preference extends SherlockPreferenceActivity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        String caller = "";
		if (null!= intent.getStringExtra("CALL_FROM")) {
	    	caller = intent.getStringExtra("CALL_FROM");
		}
		addPreferencesFromResource(R.xml.preferences);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		
		if (caller.equals("MainHexing")) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getPreferenceScreen().removePreference(findPreference("serverconfig"));
			//getPreferenceScreen().removePreference(findPreference("userconfig"));

			findPreference("current_version").setSummary(prefs.getString("current_version", "1.0.0.5"));
			findPreference("newest_version").setSummary(prefs.getString("newest_version", "1.0.0.5"));
		} else if (caller.equals("Login")){
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
			//若是LOGIN页面除了server IP可以配置以外全部隐藏
			/*
			PreferenceCategory serverCatogary = (PreferenceCategory) findPreference("version");
			serverCatogary.removePreference(findPreference("auto_upgrade_parent_checkbox"));
			serverCatogary.removePreference(findPreference("wifi_upgrade_child_checkbox"));
			*/
			getPreferenceScreen().removePreference(findPreference("version"));
			getPreferenceScreen().removePreference(findPreference("userconfig"));
			getPreferenceScreen().removePreference(findPreference("uploadconfig"));
			
			findPreference("serverip").setSummary(prefs.getString("serverip", ""));
			findPreference("serverport").setSummary(prefs.getString("serverport", ""));
		}
			
			
				
    }
	
	@Override
    public boolean onOptionsItemSelected(
    		com.actionbarsherlock.view.MenuItem item) {
    	// TODO Auto-generated method stub
    	switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
    	return super.onOptionsItemSelected(item);
    }

}
