package com.hexing.imeter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.widget.SearchView;
import com.hexing.imeter.R.menu;
import com.hexing.imeter.service.CheckVersion;
import com.hexing.imeter.service.DownloadMissionIntentService;
import com.hexing.imeter.utils.TaskProvider;
import com.hexing.imeter.utils.Utils;

public class Login extends SherlockActivity implements OnSharedPreferenceChangeListener {
	
	protected static final int VERIFIED_SUCCEED = 1;
	protected static final int VERIFIED_FAILED = 0;
	private EditText mUser; // 帐号编辑框
	private EditText mPassword; // 密码编辑框
	private CheckBox mSavePassword;
	SharedPreferences prefs;
	SharedPreferences.Editor mprefsEditor;
	
	
	String password;
	public static String mOperator;
	
	public void login(View v) {
	 	getSupportActionBar().show();
	 	setSupportProgressBarIndeterminateVisibility(true);
    	//mUser.setFocusable(false);
    	//mPassword.setFocusable(false);
	 	mOperator = mUser.getText().toString();
	 	password = mPassword.getText().toString();
	 	//Backdoor of Login
	 	if (mOperator.equals(prefs.getString("username", ""))&&password.equals(prefs.getString("password", ""))) {
	 		mHandler.sendEmptyMessage(VERIFIED_SUCCEED);
	 		return;
	 	} else {
	 		mHandler.sendEmptyMessage(VERIFIED_FAILED);
	 	}
	 	/*
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					HttpParams httpParameters = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(httpParameters, 2000);
					HttpConnectionParams.setSoTimeout(httpParameters, 3000);
					
					HttpClient httpclient = new DefaultHttpClient(httpParameters);  
					String actionUrl = "http://" +
							prefs.getString("serverip", "") +
							":" +
							prefs.getString("serverport", "") +
							"/FDM/fdm/login!login.do";
					//你的URL
					HttpPost httppost = new HttpPost(actionUrl); 
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
					//Your DATA 
				    nameValuePairs.add(new BasicNameValuePair("userName", mOperator)); 
				    nameValuePairs.add(new BasicNameValuePair("password", password));
			    		    
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				
				    HttpResponse response; 
				    
			    	response=httpclient.execute(httppost);
			    	if (response.getStatusLine().getStatusCode()==200) {
			    		byte[] bytes = Utils.bytesFromInstream(response.getEntity().getContent());
			            JSONObject JsonResponse = new JSONObject(new String(bytes));
			            String isSuccess = JsonResponse.getString("success");
			            String resultTxt = JsonResponse.getString("msg");
			            if (isSuccess.equals("true")) {
			            	mHandler.sendEmptyMessage(VERIFIED_SUCCEED);
			            } else {
			            	mHandler.sendEmptyMessage(VERIFIED_FAILED);
			            }
			    	} else {
			    		mHandler.sendEmptyMessage(VERIFIED_FAILED);
			    	}
			    	
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mHandler.sendEmptyMessage(VERIFIED_FAILED);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mHandler.sendEmptyMessage(VERIFIED_FAILED);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mHandler.sendEmptyMessage(VERIFIED_FAILED);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mHandler.sendEmptyMessage(VERIFIED_FAILED);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mHandler.sendEmptyMessage(VERIFIED_FAILED);
				}
			}
		}).start();
    	*/
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			//接受线程发送过来的消息  并在此UI线程中来更新ListView中填充的数据
			switch (msg.what) {
			case VERIFIED_SUCCEED:
				setSupportProgressBarIndeterminateVisibility(false);
				//saveUserPassword();
	        	//startService(new Intent(getApplicationContext(), DownloadMissionIntentService.class));
	        	//startService(new Intent(getApplicationContext(), CheckVersion.class));
		      	Intent intent = new Intent();
				intent.setClass(Login.this,MainHexing.class);
				startActivity(intent);
				

				break;
			case VERIFIED_FAILED:
				setSupportProgressBarIndeterminateVisibility(false);

				new AlertDialog.Builder(Login.this)
				.setIcon(getResources().getDrawable(R.drawable.login_error_icon))
				.setTitle("Login Failed")
				.setMessage("username or password is not right，\npls login again.")
				.create().show();

				
				break;
			default:
				break;
			}
		};
	};
		
	private void saveUserPassword() {
			// TODO Auto-generated method stub
			if (mSavePassword.isChecked()) {
				mprefsEditor.putBoolean("savepassword", mSavePassword.isChecked()?true:false);
				mprefsEditor.putString("username",mOperator);
				mprefsEditor.putString("password",password).commit();
			} else {
				mprefsEditor.putBoolean("savepassword", false);
				mprefsEditor.putString("username",mOperator);
				mprefsEditor.putString("password","").commit();
			}
		}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Set the Theme
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		
		//You could also use 
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.login);
		
						
		mUser = (EditText)findViewById(R.id.login_user_edit);
        mPassword = (EditText)findViewById(R.id.login_passwd_edit);
        mSavePassword = (CheckBox)findViewById(R.id.save_passwd);
		
		prefs= PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        
        mprefsEditor = prefs.edit();
		
		mSavePassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (((CheckBox)v).isChecked()) {
					((CheckBox)v).setChecked(true);
				} else {
					((CheckBox)v).setChecked(false);
					mPassword.setText("");
				}
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		
		menu.add(0,1,0,"Server")
        .setIcon(R.drawable.ofm_setting_icon)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT); 
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 1:
			Intent intent = new Intent(this,Preference.class);
			intent.putExtra("CALL_FROM", "Login");
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//显示用户名等登陆信息
		mSavePassword.setChecked(prefs.getBoolean("savepassword", false));        
        mUser.setText(prefs.getString("username", ""));
        setSupportProgressBarIndeterminateVisibility(false);
        
        if (mSavePassword.isChecked()) {
        	mPassword.setText(prefs.getString("password", ""));
        }
        
	}
	
	@Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
