package com.hexing.imeter.fragment;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.widget.SearchView;
import com.hexing.imeter.Login;
import com.hexing.imeter.R;
import com.hexing.imeter.utils.TaskProvider;
import com.hexing.imeter.utils.Utils;

public class UploadFragment extends SherlockFragment{
	
	
	
	
	public final static String TRANSFOR_METERNUMBER = "TRANSFOR_METERNUMBER";
	public final static String EDIT_METERNUMBER = "EDIT_METERNUMBER";
	public final static String ARCHIVE_DATE = "ARCHIVE_DATE";
	
	
	public static List<Map<String, Object>> parentList;
	public static ArrayList<ArrayList<HashMap<String, Object>>> allchildList;
	
	public static boolean isVisible = false;
	
	public static String UPLOAD_SUCCEED = "Upload Succeed";
	public static String UPLOAD_FAILED ="Failed!";
	
	protected static final int INIT_METERCHOOSE_SUCCESS = 0;
	protected static final int UPLOAD_STATUS_RENEW = 1;
	
	private ArrayList<Long> uploadTaskID;

	
	Cursor cDate;
	Cursor cTask;
	String stQueryMeterNumber;
	String stDate;
	
	private static HashMap<Object, Object> isCheckboxChecked = new HashMap<Object, Object>();
	SharedPreferences prefs;
	
	private Spinner spDate;
	private ListView lsMeterNumber;
	private ProgressDialog dialog;
	ImageView imUpload;
	
	
	private CheckBox cbChooseAllMeters;
	private SimpleCursorAdapter spinnerAdapter;
	private CursorAdapter listAdapter;
	
	
	public static UploadFragment newInstance(){
		UploadFragment foundFragment = new UploadFragment();
		return foundFragment;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.uploaddatelist, null);
		
		cbChooseAllMeters = (CheckBox)v.findViewById(R.id.chooseall);
		
		spDate = (Spinner)v.findViewById(R.id.datespinner);
		lsMeterNumber = (ListView)v.findViewById(R.id.uploadlistview);
		imUpload = (ImageView)v.findViewById(R.id.upload);
		prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
		
		
		return v;
	}
	public void initDate() {
		// TODO Auto-generated method stub

		String[] Columns = {TaskProvider.C_ID,TaskProvider.C_ONSITE_DATE};//, TaskProvider.C_TASK_STATUS};
		
		cDate= getSherlockActivity().getContentResolver().query(Uri.parse("content://" + TaskProvider.AUTHORITY + "/GroupBy/" + TaskProvider.C_ONSITE_DATE), 
				Columns, 
				TaskProvider.C_TASK_STATUS + "=" + TaskProvider.STATUS_FAILED +" OR " + TaskProvider.C_TASK_STATUS + "=" + TaskProvider.STATUS_SUCCEED,
				null, 
				TaskProvider.C_ONSITE_DATE + " DESC");
		Log.i(this.getClass().getName(), "cDate count: " + cDate.getCount());

		spinnerAdapter=new SimpleCursorAdapter(getSherlockActivity(),
						android.R.layout.simple_spinner_item,
						cDate,
						new String[] {TaskProvider.C_ONSITE_DATE},
						new int[] {android.R.id.text1});
		
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spDate.setAdapter(spinnerAdapter);
		if (cDate.getCount()!=0){
			cDate.moveToFirst();
			spDate.setSelection(0);
			stDate = cDate.getString(cDate.getColumnIndex(TaskProvider.C_ONSITE_DATE));
		}
	}
	@SuppressWarnings("deprecation")
	public void initTask(String stQueryMeterNumber) {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(stQueryMeterNumber)) {
			cTask = getActivity().getContentResolver().query(TaskProvider.CONTENT_URI, 
										null, 
										TaskProvider.C_OPERATOR_NAME + "='" + Login.mOperator +"'" + " AND " +  TaskProvider.C_ONSITE_DATE + "='" + stDate +"'" + " AND (" + TaskProvider.C_TASK_STATUS + "=" + TaskProvider.STATUS_FAILED +" OR " + TaskProvider.C_TASK_STATUS + "=" + TaskProvider.STATUS_SUCCEED + ")", 
										null, 
										null);
		} else {
			cTask = getActivity().getContentResolver().query(TaskProvider.CONTENT_URI, 
					null, 
					TaskProvider.C_OPERATOR_NAME + "='" + Login.mOperator +"'" + " AND " +  TaskProvider.C_ONSITE_DATE + "='" + stDate +"'" + " AND " + TaskProvider.C_TASK_DEVICE_NO + " LIKE '%" +stQueryMeterNumber + "%'" + " AND (" + TaskProvider.C_TASK_STATUS + "=" + TaskProvider.STATUS_FAILED +" OR " + TaskProvider.C_TASK_STATUS + "=" + TaskProvider.STATUS_SUCCEED + ")", 
					null, 
					null);
		}
		
		
		listAdapter = new android.support.v4.widget.CursorAdapter(getSherlockActivity(), cTask) {

			@Override
			public void bindView(View view, Context arg1, Cursor arg2) {
				// TODO Auto-generated method stub
				ViewHolderDB holder = (ViewHolderDB) view.getTag();
				String taskDeviceNumber = cTask.getString(cTask.getColumnIndex(TaskProvider.C_TASK_DEVICE_NO));
				String taskID = cTask.getString(cTask.getColumnIndex(TaskProvider.C_TASK_ID));
				int taskIndex = cTask.getInt(cTask.getColumnIndex(TaskProvider.C_TASK_INDEX));
				if (TextUtils.isEmpty(taskDeviceNumber)) {
					holder.checktv_meter.setText(taskID + "-" +taskIndex);
				} else {
					holder.checktv_meter.setText(taskDeviceNumber);
				}
				
				holder.C_ID = cTask.getLong(cTask.getColumnIndex(TaskProvider.C_ID));
				
				Log.i(this.getClass().getName(), "C_ID" + holder.C_ID);
				
				String textUploadStatus = cTask.getString(cTask.getColumnIndex(TaskProvider.C_UPLOAD_TEXT));
				if (TextUtils.isEmpty(textUploadStatus)) {
					holder.iv_text.setImageBitmap(null);
				}else if (textUploadStatus.equals(UPLOAD_SUCCEED)) {
					holder.iv_text.setImageResource(R.drawable.check);
				} else if(textUploadStatus.equals(UPLOAD_FAILED)) {
					holder.iv_text.setImageResource(R.drawable.cross);
				} else{
					holder.iv_text.setImageResource(R.drawable.warning);
				}
				
				if ((isCheckboxChecked.get(holder.C_ID) != null)&&( (Boolean) isCheckboxChecked.get(holder.C_ID))) {
					holder.checktv_meter.setChecked(true);
				} else {
					holder.checktv_meter.setChecked(false);
					isCheckboxChecked.put(holder.C_ID, false);
				}
			}

			@Override
			public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
				// TODO Auto-generated method stub
				ViewHolderDB holder = new ViewHolderDB();
				LayoutInflater inflater = getActivity().getLayoutInflater();
				View inflate = inflater.inflate(R.layout.uploadlistitem, null);
				
			    holder.checktv_meter = (CheckBox) inflate.findViewById(R.id.meternumbercheck);
			    holder.iv_text = (ImageView)inflate.findViewById(R.id.text_upload);
			    holder.iv_signature = (ImageView)inflate.findViewById(R.id.signature_upload);;
			    holder.iv_photo = (ImageView)inflate.findViewById(R.id.photo_upload);
			    inflate.setTag(holder);
			    return inflate;
			}
			
		};			
		lsMeterNumber.setAdapter(listAdapter);
		Log.i(this.getClass().getName(), "cTask count: " + cTask.getCount());
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		imUpload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initUploadTask();
	        	if (uploadTaskID.size() != 0) {
					UpLoad mUpload = new UpLoad();
					mUpload.execute(uploadTaskID);
	        	}
			}
		});
		cbChooseAllMeters.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					cTask.moveToFirst();
					while (!cTask.isAfterLast()) {
						isCheckboxChecked.put(cTask.getLong(cTask.getColumnIndex(TaskProvider.C_ID)), true);
						cTask.moveToNext();
					}
				} else {
					cTask.moveToFirst();
					while (!cTask.isAfterLast()) {
						isCheckboxChecked.put(cTask.getLong(cTask.getColumnIndex(TaskProvider.C_ID)), false);
						cTask.moveToNext();
					}
				}
				mHandler.sendEmptyMessage(INIT_METERCHOOSE_SUCCESS);
			}
		});
		spDate.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				cDate.moveToPosition(arg2);
				stDate = cDate.getString(cDate.getColumnIndex(TaskProvider.C_ONSITE_DATE));
				Log.i(this.getClass().getName(), "stDate: " + "+" + stDate + "+");
				isCheckboxChecked = new HashMap<Object, Object>();
				
				cbChooseAllMeters.setChecked(false);
				initTask(stQueryMeterNumber);
				
				listAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		lsMeterNumber.setOnItemClickListener(new OnItemClickListener() {
		   @Override
		   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    /*对于由position指定的项目，返回其是否被选中。
		     * 只有当选择模式已被设置为CHOICE_MODE_SINGLE或CHOICE_MODE_MULTIPLE时 ，结果才有效。
		     */     
			ViewHolderDB holder = (ViewHolderDB) view.getTag();
			if (holder.checktv_meter.isChecked()) {
				holder.checktv_meter.setChecked(false);
				isCheckboxChecked.put(holder.C_ID, false);
			} else {
				holder.checktv_meter.setChecked(true);
				isCheckboxChecked.put(holder.C_ID, true);
			}
			
			mHandler.sendEmptyMessage(INIT_METERCHOOSE_SUCCESS); 
			}
		});
	}
	private void initUploadTask() {
		// TODO Auto-generated method stub
		cTask.moveToFirst();
		uploadTaskID = new ArrayList<Long>();
		while (!cTask.isAfterLast()){
			long _id = cTask.getLong(cTask.getColumnIndex(TaskProvider.C_ID));
			if ((Boolean) isCheckboxChecked.get(_id)) {
				uploadTaskID.add(_id);
			}

			cTask.moveToNext();
		}
		
	}
	private class UpLoad extends AsyncTask<ArrayList<Long>, Integer, String>
	{

				
		//onPreExecute方法在execute()后执行
        @Override  
        protected void onPreExecute() 
        {  
        	dialog = new ProgressDialog(getActivity());
            dialog.setTitle("Uploading files");
            dialog.setMessage("Uploading...");
            
            dialog.setMax(100);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.show();
            Log.i("Asyn", "onPreExecute() enter");  
            
        }  
          
        

		//doInBackground方法内部执行后台任务,不能在里面更新UI，否则有异常。
        @Override  
        protected String doInBackground(ArrayList<Long>... params) 
        {  
            Log.i("Asyn", "doInBackground(String... params) enter");  
            
            String IP = prefs.getString("serverip", null);
    		String port = prefs.getString("serverport", null);
    		
    		if ((null==IP)||(null==port)) {
    			Toast.makeText(getActivity(), "Pls configure server IP and port", Toast.LENGTH_SHORT).show();
    			return "Server not Configured";
    		}
        	
			String actionUrl = "http://" +
					IP +
					":" +
					port +
					"/FDM/fdm/uploadMission!uploadMission.do";
			
			
			//InputStream isFile;
			
			
			
			for (int i = 0; i < uploadTaskID.size(); i ++) {
				ContentValues values = new ContentValues();
				//上传文本
				try {
					HttpParams httpParameters = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(httpParameters, 2000);
					HttpConnectionParams.setSoTimeout(httpParameters, 30000);
					
					HttpClient httpclient = new DefaultHttpClient(httpParameters); 
					//你的URL
					HttpPost httppost = new HttpPost(actionUrl); 
					
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
					//Your DATA 
				    nameValuePairs.add(new BasicNameValuePair(TaskProvider.C_OPERATOR_NAME, Login.mOperator)); 
				    //isFile =new FileInputStream(holderUploadFiles.arrayStFileNames.get(i).get("Text").get(j).stFileName);
				    Cursor c = getActivity().getContentResolver().query(TaskProvider.CONTENT_URI, 
							null, 
							TaskProvider.C_ID + "=" + uploadTaskID.get(i), 
							null, 
							null);
				    
				    c.moveToFirst();
				    StringBuffer sb = new StringBuffer();
					
					//sb.append('[');
					sb.append("{" + "\"" + TaskProvider.C_TASK_INDEX + "\":\"" + c.getInt(c.getColumnIndex(TaskProvider.C_TASK_INDEX)) + "\"," +
							"\"" + TaskProvider.C_TASK_ID + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_TASK_ID)))?"":c.getString(c.getColumnIndex(TaskProvider.C_TASK_ID))) + "\"," +
							"\"" + TaskProvider.C_TASK_ISSUE_TIME + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_TASK_ISSUE_TIME)))?"":c.getString(c.getColumnIndex(TaskProvider.C_TASK_ISSUE_TIME))) + "\"," +
							"\"" + TaskProvider.C_CUSTOMER_NAME + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_CUSTOMER_NAME)))?"":c.getString(c.getColumnIndex(TaskProvider.C_CUSTOMER_NAME))) + "\"," +
							"\"" + TaskProvider.C_CUSTOMER_NO + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_CUSTOMER_NO)))?"":c.getString(c.getColumnIndex(TaskProvider.C_CUSTOMER_NO))) + "\"," +
							"\"" + TaskProvider.C_TASK_TYPE + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_TASK_TYPE)))?"":c.getString(c.getColumnIndex(TaskProvider.C_TASK_TYPE))) + "\"," +
							"\"" + TaskProvider.C_DEVICE_TYPE + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_DEVICE_TYPE)))?"":c.getString(c.getColumnIndex(TaskProvider.C_DEVICE_TYPE))) + "\"," +
							"\"" + TaskProvider.C_TASK_STATUS + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_TASK_STATUS)))?"":c.getString(c.getColumnIndex(TaskProvider.C_TASK_STATUS))) + "\"," +
							"\"" + TaskProvider.C_OPERATOR_NAME + "\":\"" + Login.mOperator + "\"," +
							"\"" + TaskProvider.C_INSTALL_DEVICE_NO + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_INSTALL_DEVICE_NO)))?"":c.getString(c.getColumnIndex(TaskProvider.C_INSTALL_DEVICE_NO))) + "\"," +
							"\"" + TaskProvider.C_ADDRESS + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_ADDRESS)))?"":c.getString(c.getColumnIndex(TaskProvider.C_ADDRESS))) + "\"," +
							"\"" + TaskProvider.C_LONGITUDE + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_LONGITUDE)))?"":c.getString(c.getColumnIndex(TaskProvider.C_LONGITUDE))) + "\"," +
							"\"" + TaskProvider.C_LATITUDE + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_LATITUDE)))?"":c.getString(c.getColumnIndex(TaskProvider.C_LATITUDE))) + "\"," +
							"\"" + TaskProvider.C_NEW_START_ENERGY + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_NEW_START_ENERGY)))?"":c.getString(c.getColumnIndex(TaskProvider.C_NEW_START_ENERGY))) + "\"," +
							"\"" + TaskProvider.C_REMOVE_DEVICE_NO + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_REMOVE_DEVICE_NO)))?"":c.getString(c.getColumnIndex(TaskProvider.C_REMOVE_DEVICE_NO)))+ "\"," +
							"\"" + TaskProvider.C_UPLINK_CONCENTRATOR + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_UPLINK_CONCENTRATOR)))?"":c.getString(c.getColumnIndex(TaskProvider.C_UPLINK_CONCENTRATOR))) + "\"," +
							"\"" + TaskProvider.C_OLD_END_ENERGY + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_OLD_END_ENERGY)))?"":c.getString(c.getColumnIndex(TaskProvider.C_OLD_END_ENERGY))) + "\"," +
							"\"" + TaskProvider.C_PREPAID_OLD_BALANCE + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_PREPAID_OLD_BALANCE)))?"":c.getString(c.getColumnIndex(TaskProvider.C_PREPAID_OLD_BALANCE))) + "\"," +
							"\"" + TaskProvider.C_TRANSFORMA_NAME + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_TRANSFORMA_NAME)))?"":c.getString(c.getColumnIndex(TaskProvider.C_TRANSFORMA_NAME))) + "\"," +
							"\"" + TaskProvider.C_TASK_ANOMALY_REASON + "\":\"" + (TextUtils.isEmpty(c.getString(c.getColumnIndex(TaskProvider.C_TASK_ANOMALY_REASON)))?"":c.getString(c.getColumnIndex(TaskProvider.C_TASK_ANOMALY_REASON))) + "\"" +
							"}");
					//sb.append(']');
				    String content = new String(sb);
				    Log.i("UploadArchives", content);
				    nameValuePairs.add(new BasicNameValuePair("Task_Contents", content)); 

			    
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					
					
				

				    HttpResponse response; 
				    
			    	response=httpclient.execute(httppost);
				    
			    	
			    	if(response.getStatusLine().getStatusCode()==200){ 
			    		byte[] bytes = Utils.bytesFromInstream(response.getEntity().getContent());
			    		String test = new String(bytes);
			            JSONObject JsonResponse = new JSONObject(test);
			            String isSuccess = JsonResponse.getString("success");
			            String resultTxt = JsonResponse.getString("msg");
			            if (isSuccess.equals("true")) {
			            	values.put(TaskProvider.C_UPLOAD_TEXT, UPLOAD_SUCCEED);
							
			            	//holderUploadFiles.arrayStFileNames.get(i).get("Text").get(j).stStatus = "succeed";
			            }
			    	} else {
			    		values.put(TaskProvider.C_UPLOAD_TEXT, UPLOAD_FAILED);
			    	}
			    	
			    } catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					values.put(TaskProvider.C_UPLOAD_TEXT, UPLOAD_FAILED);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					values.put(TaskProvider.C_UPLOAD_TEXT, UPLOAD_FAILED);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					values.put(TaskProvider.C_UPLOAD_TEXT, UPLOAD_FAILED);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					values.put(TaskProvider.C_UPLOAD_TEXT, UPLOAD_FAILED);
				} finally {
					Log.i(this.getClass().getName(), values.getAsString(TaskProvider.C_UPLOAD_TEXT));
					
					getActivity().getContentResolver().update(TaskProvider.CONTENT_URI, values, TaskProvider.C_ID + "=" + uploadTaskID.get(i), null);

					//刷新进度条
					publishProgress((i+1)*100/uploadTaskID.size());
					//发送消息刷新上传状态
					mHandler.sendEmptyMessage(UPLOAD_STATUS_RENEW);
				}
			}
			  	
    		return "ok";
        }  
          
        //onProgressUpdate方法用于更新进度信息  
        @Override  
        protected void onProgressUpdate(Integer... progresses) 
        {  
        	super.onProgressUpdate(progresses);
            Log.i("dialog", "onProgressUpdate(Integer... progresses) enter");  
            dialog.setProgress(progresses[0]);
              
        }  
          
        //onPostExecute用于doInBackground执行完后，更新界面UI。
        //result是doInBackground返回的结果
        @Override  
        protected void onPostExecute(String result)
        {  
            Log.i("Asyn", "onPostExecute(Result result) called");
                        
            dialog.dismiss();
            
        }  
          
        //onCancelled方法用于取消Task执行，更新UI
        @Override  
        protected void onCancelled() 
        {  
            Log.i("Asyn", "onCancelled() called");  
        }
		
	}
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser == true) {
			isVisible = true;
        }
        else if (isVisibleToUser == false) {
        	isVisible = false;
        }
	}
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			//接受线程发送过来的消息  并在此UI线程中来更新ListView中填充的数据
			switch (msg.what) {
			case INIT_METERCHOOSE_SUCCESS:
				initTask(stQueryMeterNumber);

				break;
			case UPLOAD_STATUS_RENEW:
				
				initTask(stQueryMeterNumber);
				
				break;
			default:
				break;
			}
		};
	};
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i(this.getClass().getName(), "onStart");
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initDate();
		initTask(stQueryMeterNumber);
		
		Log.i(this.getClass().getName(), "onResume");
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(this.getClass().getName(), "onPause");
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(this.getClass().getName(), "onStop");
	}
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.i(this.getClass().getName(), "onDestroyView");
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(this.getClass().getName(), "onDestroy");
	}
	/*
	public static void initData(String queryMeterNumber)
    {
    	File parentFiles = new File(Utils.getArchivesFolder(null));
    	parentList.clear();
    	allchildList.clear();
    	//若没有档案则返回
    	if (null == parentFiles.listFiles()) {
    		return;
    	}
    	
    	for(File dateFile : (parentFiles.listFiles()))
    	{
    		//被删空的目录不显示
    		if (0 == dateFile.listFiles().length){
    			continue;
    		}
    		boolean isParentHaveElement = false;
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		map.put("date", dateFile.getName());
    		
    		
    	
    		ArrayList<HashMap<String, Object>> childlist = new ArrayList<HashMap<String,Object>>();
    		for(File meterFile :dateFile.listFiles())
        	{
        		
        		HashMap<String, Object> childmap = new HashMap<String, Object>();
        		if ((null == queryMeterNumber)||(meterFile.getName().contains(queryMeterNumber))){
	        		childmap.put("meter", meterFile.getName());
	        		childlist.add(childmap);
	        		isParentHaveElement = true;
        		} 
        	}
    		//没有搜索到则不显示父目录
    		if (isParentHaveElement) {
    			parentList.add(map);
    			allchildList.add(childlist);
    		}
    		
    		
    	}
    	
    }
	public static void setExpAdapter(){
		adapter.notifyDataSetChanged();
	}*/



	
}
class ViewHolderDB {
	long		C_ID;
	CheckBox checktv_meter;
	ImageView iv_text;
	ImageView iv_signature;
	ImageView iv_photo;
}
