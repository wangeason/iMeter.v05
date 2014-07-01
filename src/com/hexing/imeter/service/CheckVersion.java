package com.hexing.imeter.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import com.hexing.imeter.utils.Utils;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class CheckVersion extends IntentService{

	public CheckVersion() {
		super("CheckVersion");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String IP = prefs.getString("serverip", null);
		String port = prefs.getString("serverport", null);
		
		SharedPreferences.Editor mprefsEditor = prefs.edit();
		
		if ((null==IP)||(null==port)) {
			Toast.makeText(getApplicationContext(), "Pls configure server IP and port", Toast.LENGTH_SHORT).show();
			return;
		}
		
		String downloadAPK = "http://" +
				IP +
				":" +
				port +
				"/ServerJson/AppUpdate";
		
		URL url;
		try {
			url = new URL(downloadAPK);
		
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			byte[] bytes = Utils.bytesFromInstream(conn.getInputStream());
			conn.disconnect();
			
			JSONObject item = new JSONObject(new String(bytes));
			
			int verCode = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			String verName = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			int serverVerCode = item.getInt("versionCode");
			String serverVerName = item.getString("versionName");
					
			mprefsEditor.putString("current_version", verName);
			mprefsEditor.putInt("current_code", verCode);
			mprefsEditor.putString("newest_version", serverVerName);
			mprefsEditor.putInt("newest_code", serverVerCode);
			mprefsEditor.commit();
			int localVerCode = verCode>prefs.getInt("APK_downloaded_version", 0) ? verCode : prefs.getInt("APK_downloaded_version", 0);
			if ((prefs.getBoolean("wifi_upgrade_child_checkbox", true) == false)
				|| (Utils.isWiFiActive(this) == true)) {
				if ((serverVerCode > localVerCode)
						&&(prefs.getBoolean("auto_upgrade_parent_checkbox", false))) {
					startService(new Intent(this, DownloadAPK.class));
					Log.i("CheckVersion", "Started DownloadAPK");
				}
			
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}

}
