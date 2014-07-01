package com.hexing.imeter.service;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class DownloadAPK extends Service{
	
	private static final String TAG = "UpdaterService";

	static final int DELAY = 60000*120; 

	private boolean runFlag = false;  
	
	String downloadAPK;
	boolean wifiUpdate;
	
	SharedPreferences.Editor mprefsEditor;
	SharedPreferences prefs;
	
	private Updater updater;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub

	    Log.d(TAG, "onCreated");
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mprefsEditor = prefs.edit();
		String IP = prefs.getString("serverip", null);
		String port = prefs.getString("serverport", null);
		wifiUpdate = prefs.getBoolean("wifi_upgrade_child_checkbox", true);
		
		downloadAPK = "http://" +
				IP +
				":" +
				port +
				"/ServerJson/AppUpdate";
				
		this.updater = new Updater();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		if (this.runFlag == false) {
			this.runFlag = true;
			    
		    this.updater.start();
	 
		    Log.d(TAG, "onStarted");
	
		    return START_STICKY;
		}
		
		return START_STICKY_COMPATIBILITY;  

	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		this.runFlag = false; // http://dev.icybear.net/learning-android-cn/images/5.png
	    
	    this.updater.interrupt();
	    
	    this.updater = null;

	    Log.d(TAG, "onDestroyed");
	}
	
	private class Updater extends Thread {
		public Updater() {
			super("Updater");
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				

            	// TODO Auto-generated method stub
				HttpClient httpclient = new DefaultHttpClient(); 
				//你的URL
				HttpPost httppost = new HttpPost(downloadAPK);
				
				HttpResponse response = httpclient.execute(httppost);
		    	
		    	if(response.getStatusLine().getStatusCode()==200){  
		            
		            String stFileDirectory = Environment.getExternalStorageDirectory()  
		                    + "/Hexing/UpdateAPK/";
		        	String stFilePath 	= stFileDirectory + "iMeter.apk";//item.getString("apkname");
		        	File Directory = new File(stFileDirectory);
		        	if (!Directory.exists()) {
		        		Directory.mkdirs();
		        	}
		        	
		        	OutputStream os = new FileOutputStream(stFilePath);
			        InputStream is = response.getEntity().getContent();
			        
			        byte[] buff = new byte[1024];
		            int readCount = 0;
		            
		            readCount = is.read(buff);
		            while (readCount != -1){
		               os.write(buff, 0, readCount); 
		               readCount = is.read(buff);
		            }
			        
                    os.flush();
                    if (os != null) {  
                    	os.close();  
                    }
                    //保存下载的版本号
                    mprefsEditor.putInt("APK_downloaded_version", prefs.getInt("newest_code",0)).commit();
                    Log.i("DownloadAPK", "succeed");
				}	      


			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("DownloadAPK", "failed");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("DownloadAPK", "failed");
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("DownloadAPK", "failed");
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("DownloadAPK", "failed");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("DownloadAPK", "failed");
			}  
		}
	}

}
