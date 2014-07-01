package com.hexing.imeter.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hexing.imeter.Login;
import com.hexing.imeter.utils.TaskProvider;
import com.hexing.imeter.utils.Utils;

public class DownloadMissionIntentService extends IntentService {
	
	//TaskProvider dbHelper;
	//SQLiteDatabase db;
	Cursor cCheck;


	public DownloadMissionIntentService() {
		super("RenewMissionDB");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String IP = prefs.getString("serverip", null);
		String port = prefs.getString("serverport", null);
		
		if ((null==IP)||(null==port)) {
			
			Log.i("DownloadMission", "No IP");
			return;
		}
		
		String downloadMission = "http://" + IP + ":" +port +"/FDM/fdm/downloadMission!getMissions.do";
		boolean isSuccess = false;
		int isUpdateSuccess = 0;
		int isFileSuccess = 0;
        //String resultTxt = "failed";
		
		try {
	    	//dbHelper = new TaskProvider(getApplicationContext()) ;
	    	
	    	
	    	// Open the database for writing
	        //db = dbHelper.getWritableDatabase();
	        

			// TODO Auto-generated method stub
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 2000);
			HttpConnectionParams.setSoTimeout(httpParameters, 30000);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters); 
			
			//你的URL
			HttpPost httppost = new HttpPost(downloadMission); 
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
			//Your DATA 
		    nameValuePairs.add(new BasicNameValuePair(TaskProvider.C_OPERATOR_NAME, Login.mOperator)); 
		    //nameValuePairs.add(new BasicNameValuePair("Model", null)); 

		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 

		    HttpResponse response; 
		    
		    
	    	response=httpclient.execute(httppost);
	    	
	    	if(response.getStatusLine().getStatusCode()==200){  
	            byte[] bytes = Utils.bytesFromInstream(response.getEntity().getContent());
	            JSONObject JsonResponse = new JSONObject(new String(bytes));
	            isSuccess = JsonResponse.getString("success").equals("true")?true:false;
	            //resultTxt = JsonResponse.getString("msg");
	            
	            if (isSuccess) {
	            	JSONArray array = new JSONArray(new String(JsonResponse.getString("dataObject")));
		            ContentValues values = new ContentValues();
		            for (int i = 0; i < array.length(); i++) {
						JSONObject item = array.getJSONObject(i);
						
						// Insert into database
			            values.clear(); //http://dev.icybear.net/learning-android-cn/images/7.png
			            
			            //values.put(TaskProvider.C_ID, item.getInt("Task_Index"));
		
			            values.put(TaskProvider.C_TASK_ID, item.getString(TaskProvider.C_TASK_ID));
			            
			            values.put(TaskProvider.C_TASK_INDEX, item.getInt(TaskProvider.C_TASK_INDEX));
		
			            values.put(TaskProvider.C_TASK_ISSUE_TIME, item.getString(TaskProvider.C_TASK_ISSUE_TIME));
			            
			            values.put(TaskProvider.C_OPERATOR_NAME, item.getString(TaskProvider.C_OPERATOR_NAME));
		
			            values.put(TaskProvider.C_CUSTOMER_NAME, item.getString(TaskProvider.C_CUSTOMER_NAME));
			            
			            values.put(TaskProvider.C_CUSTOMER_NO, item.getString(TaskProvider.C_CUSTOMER_NO));
		
			            values.put(TaskProvider.C_TASK_TYPE, item.getString(TaskProvider.C_TASK_TYPE));
			            
			            values.put(TaskProvider.C_DEVICE_TYPE, item.getString(TaskProvider.C_DEVICE_TYPE));
			            
			            values.put(TaskProvider.C_TASK_STATUS, item.getString(TaskProvider.C_TASK_STATUS));
			            
			            values.put(TaskProvider.C_POD, item.getString(TaskProvider.C_POD));
			            
			            values.put(TaskProvider.C_INSTALL_DEVICE_NO, item.getString(TaskProvider.C_INSTALL_DEVICE_NO));
			            
			            values.put(TaskProvider.C_REMOVE_DEVICE_NO, item.getString(TaskProvider.C_REMOVE_DEVICE_NO));
		
			            values.put(TaskProvider.C_ADDRESS, item.getString(TaskProvider.C_ADDRESS));
			            
			            values.put(TaskProvider.C_LONGITUDE, item.getString(TaskProvider.C_LONGITUDE));
			            
			            values.put(TaskProvider.C_LATITUDE, item.getString(TaskProvider.C_LATITUDE));
		
			            values.put(TaskProvider.C_NEW_START_ENERGY, item.getString(TaskProvider.C_NEW_START_ENERGY));
			            
			            values.put(TaskProvider.C_PREPAID_OLD_BALANCE, item.getString(TaskProvider.C_PREPAID_OLD_BALANCE));
		
			            values.put(TaskProvider.C_OLD_END_ENERGY, item.getString(TaskProvider.C_OLD_END_ENERGY));
			            
			            values.put(TaskProvider.C_UPLINK_CONCENTRATOR, item.getString(TaskProvider.C_UPLINK_CONCENTRATOR));
			            
			            values.put(TaskProvider.C_TRANSFORMA_NAME, item.getString(TaskProvider.C_TRANSFORMA_NAME));
			            
			            values.put(TaskProvider.C_WORK_FLOW_STEP, 0);
		
			            values.put(TaskProvider.C_ABNORMAL, "");
			            
			            values.put(TaskProvider.C_UPLOAD_TEXT, "");
			            
			            values.put(TaskProvider.C_UPLOAD_SIGN, "");
			            
			            values.put(TaskProvider.C_UPLOAD_PIC, "");
			            
			            values.put(TaskProvider.C_ONSITE_DATE, "");
			            
			            //确定taskDeviceNumber
			            String taskDeviceNOString; 
			            if (item.getString(TaskProvider.C_TASK_TYPE).equals(TaskProvider.MISSION_INSTALL)){
			            	taskDeviceNOString = item.getString(TaskProvider.C_INSTALL_DEVICE_NO);
			            	values.put(TaskProvider.C_TASK_DEVICE_NO, taskDeviceNOString);
			            } else {
			            	taskDeviceNOString = item.getString(TaskProvider.C_REMOVE_DEVICE_NO);
			            	values.put(TaskProvider.C_TASK_DEVICE_NO, taskDeviceNOString);
			            }
			            //根据taskDeviceNumber初始化folderName
			            if (TextUtils.isEmpty(taskDeviceNOString)) {
			            	values.put(TaskProvider.C_FOLDER_NAME, item.getString(TaskProvider.C_TASK_ID) + "-" + item.getString(TaskProvider.C_TASK_INDEX));
			            } else {
			            	values.put(TaskProvider.C_FOLDER_NAME, item.getString(TaskProvider.C_TASK_ID) + "-" + item.getString(TaskProvider.C_TASK_INDEX) + " " + taskDeviceNOString);
			            }
			            
			            if (item.getString(TaskProvider.C_INSTALL_DEVICE_NO).equals("")) {
			            	values.put(TaskProvider.C_INSTALL_ISSPECIFIED, TaskProvider.FALSE);
			            } else {
			            	values.put(TaskProvider.C_INSTALL_ISSPECIFIED, TaskProvider.TRUE);
			            }
			            
			            if (item.getString(TaskProvider.C_REMOVE_DEVICE_NO).equals("")) {
			            	values.put(TaskProvider.C_REMOVE_ISSPECIFIED, TaskProvider.FALSE);
			            } else {
			            	values.put(TaskProvider.C_REMOVE_ISSPECIFIED, TaskProvider.TRUE);
			            }
			            
			        	values.put(TaskProvider.C_INSTALL_ISRIGHT, TaskProvider.FALSE);
			        	
			        	values.put(TaskProvider.C_REMOVE_ISRIGHT, TaskProvider.FALSE);
			            
			            values.put(TaskProvider.C_TASK_ANOMALY_REASON, item.getString(TaskProvider.C_TASK_ANOMALY_REASON));
			            try {
			            	insertOrThrowTaskIDCheck(TaskProvider.TABLE, null, values); 
			            	
			            	
			            } catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							
							
						}
			            
					}
		            isUpdateSuccess = 2;
		            
	    		} else {
	    			isUpdateSuccess = 0;
	    		}
	            
	    	} else {
	    		isUpdateSuccess = 0;
	    	}
	    	
		
	    
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    		isUpdateSuccess = 0;
	    } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			isUpdateSuccess = 0;
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			isUpdateSuccess = 0;
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			isUpdateSuccess = 0;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			isUpdateSuccess = 0;
		} 
		
		String FilePath = Environment.getExternalStorageDirectory().getPath()+ "/" + "Hexing" + "/" + "Tasks";
		
		if (false == Utils.isFolderExists(FilePath)) {
			Log.i("DownloadMission", "Can not find the folder");
			isFileSuccess = 0;
		} else {
		
			File taskFolder = new File(FilePath);
			File tasks[] = taskFolder.listFiles();
			
			if ((tasks == null)||(tasks.length == 0)) {
				Log.i("DownloadMission", "No local task");
				isFileSuccess = 0;
			}
			
			for (int j = 0; j < tasks.length; j++) { // 遍历目录下所有的文件
				try {
					FileInputStream fileJson = new FileInputStream (tasks[j]);
				
					byte[] bytes = Utils.bytesFromInstream(fileJson);
					
		        	JSONArray array = new JSONArray(new String(bytes));
		            ContentValues values = new ContentValues();
		            for (int i = 0; i < array.length(); i++) {
						JSONObject items = array.getJSONObject(i);
						
						// Insert into database
			            values.clear(); //http://dev.icybear.net/learning-android-cn/images/7.png
		
			            values.put(TaskProvider.C_TASK_ID, items.getString(TaskProvider.C_TASK_ID));
			            
			            values.put(TaskProvider.C_TASK_INDEX, items.getString(TaskProvider.C_TASK_INDEX));
			            
			            values.put(TaskProvider.C_POD, items.getString(TaskProvider.C_POD));
		
			            values.put(TaskProvider.C_TASK_ISSUE_TIME, items.getString(TaskProvider.C_TASK_ISSUE_TIME));
			            
			            values.put(TaskProvider.C_OPERATOR_NAME, items.getString(TaskProvider.C_OPERATOR_NAME));
		
			            values.put(TaskProvider.C_CUSTOMER_NAME, items.getString(TaskProvider.C_CUSTOMER_NAME));
			            
			            values.put(TaskProvider.C_CUSTOMER_NO, items.getString(TaskProvider.C_CUSTOMER_NO));
		
			            values.put(TaskProvider.C_TASK_TYPE, items.getString(TaskProvider.C_TASK_TYPE));
			            
			            values.put(TaskProvider.C_DEVICE_TYPE, items.getString(TaskProvider.C_DEVICE_TYPE));
			            
			            values.put(TaskProvider.C_TASK_STATUS, items.getString(TaskProvider.C_TASK_STATUS));
			            
			            //需要操作的目标表号
			            //values.put(TaskProvider.C_TASK_DEVICE_NO, items.getString(TaskProvider.C_REMOVE_DEVICE_NO).equals("")?items.getString(TaskProvider.C_INSTALL_DEVICE_NO):items.getString(TaskProvider.C_REMOVE_DEVICE_NO));
			        	
			            values.put(TaskProvider.C_INSTALL_DEVICE_NO, items.getString(TaskProvider.C_INSTALL_DEVICE_NO));
			            
			            values.put(TaskProvider.C_REMOVE_DEVICE_NO, items.getString(TaskProvider.C_REMOVE_DEVICE_NO));
		
			            values.put(TaskProvider.C_ADDRESS, items.getString(TaskProvider.C_ADDRESS));
			            
			            values.put(TaskProvider.C_LONGITUDE, items.getString(TaskProvider.C_LONGITUDE));
			            
			            values.put(TaskProvider.C_LATITUDE, items.getString(TaskProvider.C_LATITUDE));
		
			            values.put(TaskProvider.C_NEW_START_ENERGY, items.getString(TaskProvider.C_NEW_START_ENERGY));
			            
			            values.put(TaskProvider.C_PREPAID_OLD_BALANCE, items.getString(TaskProvider.C_PREPAID_OLD_BALANCE));
		
			            values.put(TaskProvider.C_OLD_END_ENERGY, items.getString(TaskProvider.C_OLD_END_ENERGY));
			            
			            values.put(TaskProvider.C_UPLINK_CONCENTRATOR, items.getString(TaskProvider.C_UPLINK_CONCENTRATOR));
			            
			            values.put(TaskProvider.C_TRANSFORMA_NAME, items.getString(TaskProvider.C_TRANSFORMA_NAME));
			            
			            values.put(TaskProvider.C_WORK_FLOW_STEP, 0);
		
			            values.put(TaskProvider.C_ABNORMAL, "");
			            
			            values.put(TaskProvider.C_UPLOAD_TEXT, "");
			            
			            values.put(TaskProvider.C_UPLOAD_SIGN, "");
			            
			            values.put(TaskProvider.C_UPLOAD_PIC, "");
			            
			            values.put(TaskProvider.C_ONSITE_DATE, "");
			            
			          //确定taskDeviceNumber
			            String taskDeviceNOString; 
			            if (items.getString(TaskProvider.C_TASK_TYPE).equals(TaskProvider.MISSION_INSTALL)){
			            	taskDeviceNOString = items.getString(TaskProvider.C_INSTALL_DEVICE_NO);
			            	values.put(TaskProvider.C_TASK_DEVICE_NO, taskDeviceNOString);
			            } else {
			            	taskDeviceNOString = items.getString(TaskProvider.C_REMOVE_DEVICE_NO);
			            	values.put(TaskProvider.C_TASK_DEVICE_NO, taskDeviceNOString);
			            }
			            //根据taskDeviceNumber初始化folderName
			            if (TextUtils.isEmpty(taskDeviceNOString)) {
			            	values.put(TaskProvider.C_FOLDER_NAME, items.getString(TaskProvider.C_TASK_ID) + "-" + items.getString(TaskProvider.C_TASK_INDEX));
			            } else {
			            	values.put(TaskProvider.C_FOLDER_NAME, items.getString(TaskProvider.C_TASK_ID) + "-" + items.getString(TaskProvider.C_TASK_INDEX) + " " + taskDeviceNOString);
			            }
			            
			            if (items.getString(TaskProvider.C_INSTALL_DEVICE_NO).equals("")) {
			            	values.put(TaskProvider.C_INSTALL_ISSPECIFIED, TaskProvider.FALSE);
			            } else {
			            	values.put(TaskProvider.C_INSTALL_ISSPECIFIED, TaskProvider.TRUE);
			            }
			            
			            if (items.getString(TaskProvider.C_REMOVE_DEVICE_NO).equals("")) {
			            	values.put(TaskProvider.C_REMOVE_ISSPECIFIED, TaskProvider.FALSE);
			            } else {
			            	values.put(TaskProvider.C_REMOVE_ISSPECIFIED, TaskProvider.TRUE);
			            }
			            
			        	values.put(TaskProvider.C_INSTALL_ISRIGHT, TaskProvider.FALSE);
			        	
			        	values.put(TaskProvider.C_REMOVE_ISRIGHT, TaskProvider.FALSE);
			            
			            values.put(TaskProvider.C_TASK_ANOMALY_REASON, items.getString(TaskProvider.C_TASK_ANOMALY_REASON));
			            try {
			            	//getContentResolver().insert(TaskProvider.CONTENT_URI, values);
			            	insertOrThrowTaskIDCheck(TaskProvider.TABLE, null, values); 
			            	//getContentResolver().update(TaskProvider.CONTENT_URI, values, TaskProvider.C_ID + "=" + _id, null))
			            	isFileSuccess = 1;
	
			            } catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							isFileSuccess = 0;
						}
					}
		            
		    		
		    		
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					isFileSuccess = 0;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					isFileSuccess = 0;
				}
		             
			}
		}
		
		Intent broadcastIntent = new Intent();  
        broadcastIntent.setAction("com.hexing.imeter.MissionRenewed");  
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);  
        broadcastIntent.putExtra("msg", isUpdateSuccess+isFileSuccess);
        sendBroadcast(broadcastIntent);
		
	}
	

	private void insertOrThrowTaskIDCheck(String table,
			Object object, ContentValues values) {
		/*
		if (values.getAsString(TaskProvider.C_DEVICE_TYPE).equals(TaskProvider.DEVICE_METER)) {
			cCheck = getContentResolver().query(TaskProvider.CONTENT_URI, 
					new String[] {TaskProvider.C_ID, TaskProvider.C_TASK_ID, TaskProvider.C_POD, TaskProvider.C_TASK_TYPE, TaskProvider.C_DEVICE_TYPE}, 
					TaskProvider.C_TASK_ID + "='" +values.getAsString(TaskProvider.C_TASK_ID) + "' AND " +
					//TaskProvider.C_TASK_DEVICE_NO  + "='" +values.getAsString(TaskProvider.C_TASK_DEVICE_NO) + "' AND " +
					TaskProvider.C_POD  + "='" +values.getAsString(TaskProvider.C_POD) + "' AND " +
					TaskProvider.C_TASK_TYPE  + "='" +values.getAsString(TaskProvider.C_TASK_TYPE) + "' AND " +
					TaskProvider.C_DEVICE_TYPE  + "='" +values.getAsString(TaskProvider.C_DEVICE_TYPE)+ "'",  
					null, 
					null);
		} else {
			cCheck = getContentResolver().query(TaskProvider.CONTENT_URI, 
				new String[] {TaskProvider.C_ID, TaskProvider.C_TASK_ID, TaskProvider.C_ADDRESS, TaskProvider.C_TASK_TYPE, TaskProvider.C_DEVICE_TYPE}, 
				TaskProvider.C_TASK_ID + "='" +values.getAsString(TaskProvider.C_TASK_ID) + "' AND " +
				//TaskProvider.C_TASK_DEVICE_NO  + "='" +values.getAsString(TaskProvider.C_TASK_DEVICE_NO) + "' AND " +
				TaskProvider.C_ADDRESS  + "='" +values.getAsString(TaskProvider.C_ADDRESS) + "' AND " +
				TaskProvider.C_TASK_TYPE  + "='" +values.getAsString(TaskProvider.C_TASK_TYPE) + "' AND " +
				TaskProvider.C_DEVICE_TYPE  + "='" +values.getAsString(TaskProvider.C_DEVICE_TYPE)+ "'",  
				null, 
				null);
		}*/
		cCheck = getContentResolver().query(TaskProvider.CONTENT_URI, 
				new String[] {TaskProvider.C_ID, TaskProvider.C_TASK_INDEX, TaskProvider.C_TASK_ID}, 
				TaskProvider.C_TASK_ID + "='" +values.getAsString(TaskProvider.C_TASK_ID) + "' AND " +
						TaskProvider.C_TASK_INDEX  + "='" +values.getAsString(TaskProvider.C_TASK_INDEX)+ "'",  
				null, 
				null);
		if (0 == cCheck.getCount()) {
			getContentResolver().insert(TaskProvider.CONTENT_URI, values);
		} else {
			Log.i("DownloadMissionIntentService", "Downloaded duplicate mission");
		}

    	cCheck.close();
	}
}
