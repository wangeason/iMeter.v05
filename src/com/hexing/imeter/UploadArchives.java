package com.hexing.imeter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.hexing.imeter.utils.TaskProvider;
import com.hexing.imeter.utils.Utils;



public class UploadArchives  extends SherlockActivity implements SearchView.OnQueryTextListener{
	protected static final int INIT_METERCHOOSE_SUCCESS = 0;
	protected static final int UPLOAD_STATUS_RENEW = 1;
	
	public static String UPLOAD_SUCCEED = "Upload Succeed";
	private static String FILE_NOT_EXIST = "file doesn't exist";
	public static String FAILED ="Failed!";
	private static int DATELIST = 1;
	private static int METERNUMBERLIST = 2;
	
	public static String CHECKED = "IS_CHECKED";
	private static String UNCHECKED = "UN_CHECKED";
	
	int iListLevel;
	
	private ArrayAdapter<String> spinnerAdapter = null;
	private static ArrayList<String> arrayStDate = null;
	
	private UploadListAdapter listAdapter = null;
	public static ArrayList<ListviewMeterStatus> arrayStMapMeter = null;
	
	private UploadHolder holderUploadFiles;
	
	
	
	private Button mBtnBack;
	private Button mBtnUpload;
	private Button mBtnQuery;
	private EditText etQueryMeterNumber;
	private CheckBox cbText;
	private CheckBox cbPhoto;
	private CheckBox cbChooseAllMeters;
	private Spinner spDate;
	private ListView lsMeterNumber;
	private ProgressDialog dialog;
	
	
	String stDate;
	String stQueryMeterNumber;
	
	SharedPreferences prefs;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uploaddatelist);
		//����activityʱ���Զ����������
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        arrayStDate = new ArrayList<String>();
        arrayStMapMeter = new ArrayList<ListviewMeterStatus>();
        initDate();
        initMeter(null);
        
        //cbText = (CheckBox)findViewById(R.id.textcheck);
		//cbPhoto = (CheckBox)findViewById(R.id.photocheck);
		cbChooseAllMeters = (CheckBox)findViewById(R.id.chooseall);
		
		spDate = (Spinner)findViewById(R.id.datespinner);
		lsMeterNumber = (ListView)findViewById(R.id.uploadlistview);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		
		spinnerAdapter=new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arrayStDate);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spDate.setAdapter(spinnerAdapter);
		
		listAdapter = new UploadListAdapter(this);			
		lsMeterNumber.setAdapter(listAdapter);
		
		cbChooseAllMeters.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					for (int i = 0; i<arrayStMapMeter.size(); i++){
						arrayStMapMeter.get(i).isChecked = true;
					}
				} else {
					for (int i = 0; i<arrayStMapMeter.size(); i++){
						arrayStMapMeter.get(i).isChecked = false;
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
				stDate = arrayStDate.get(arg2);
				cbChooseAllMeters.setChecked(false);
				initMeter(null);
				
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
		    /*������positionָ������Ŀ���������Ƿ�ѡ�С�
		     * ֻ�е�ѡ��ģʽ�ѱ�����ΪCHOICE_MODE_SINGLE��CHOICE_MODE_MULTIPLEʱ ���������Ч��
		     */     
			ViewHolder holder = (ViewHolder) view.getTag();
			if (holder.checktv_meter.isChecked()) {
				holder.checktv_meter.setChecked(false);
				arrayStMapMeter.get(position).isChecked = false;
			} else {
				holder.checktv_meter.setChecked(true);
				arrayStMapMeter.get(position).isChecked = true;
			}
			
			mHandler.sendEmptyMessage(INIT_METERCHOOSE_SUCCESS); 
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Search for meters");
        searchView.setOnQueryTextListener(this);
        
        menu.add("Search")
        .setIcon(R.drawable.abs__ic_search)
        .setActionView(searchView)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		
		menu.add(0,1,0,"Upload")
        .setIcon(R.drawable.cloud_upload)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT); 
		return super.onCreateOptionsMenu(menu);
	}
		
	@Override
    public boolean onOptionsItemSelected(
    		com.actionbarsherlock.view.MenuItem item) {
    	// TODO Auto-generated method stub
		int id = item.getItemId();
    	switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        case 1:
        	initUploadFiles();
			if (!holderUploadFiles.arrayStMeterNumber.isEmpty()) {
				
	            UpLoad mUpload = new UpLoad();
				mUpload.execute(holderUploadFiles);
			}
        	break;
        default:
        	break;
        }
    	return super.onOptionsItemSelected(item);
    }
	private void initMeter(String queryMeterNumber) {
		// TODO Auto-generated method stub
		arrayStMapMeter.clear();
		File childFiles = new File(Utils.getArchivesFolder(null)+"/" + stDate);
		ArrayList<ListviewMeterStatus> childlist = new ArrayList<ListviewMeterStatus>();
		//��û�е����򷵻�
    	if (null == childFiles.listFiles()) {
    		return;
    	}
		
		for(File meterFile :childFiles.listFiles())
    	{
    		
			ListviewMeterStatus stChildMeter = new ListviewMeterStatus();
    		if ((null == queryMeterNumber)||(meterFile.getName().contains(queryMeterNumber))){
    			stChildMeter.stMeterNumber =  meterFile.getName();
    			//�ϴ�״̬ �Ժ�����ݿ��ȡ
    			stChildMeter.stTextStatus = null;
    			stChildMeter.stSignStatus = null;
    			stChildMeter.stPhotoStatus = null;
    			stChildMeter.isChecked = false;
        		childlist.add(stChildMeter);
    		}
    	}
		arrayStMapMeter = childlist;
	}
	private void initDate() {
		// TODO Auto-generated method stub
		File parentFiles = new File(Utils.getArchivesFolder(null));
		arrayStDate.clear();
		//��û�е����򷵻�
    	if (null == parentFiles.listFiles()) {
    		return;
    	}

    	for(File dateFile : (parentFiles.listFiles()))
    	{
    		//��ɾ�յ�Ŀ¼����ʾ
    		if (0 == dateFile.listFiles().length){
    			continue;
    		}
    		stDate = dateFile.getName();
    		arrayStDate.add(stDate);
    	}
	}
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			//�����̷߳��͹�������Ϣ  ���ڴ�UI�߳���������ListView����������
			switch (msg.what) {
			case INIT_METERCHOOSE_SUCCESS:
				listAdapter.notifyDataSetChanged();

				break;
			case UPLOAD_STATUS_RENEW:
				for (int i = 0; i<holderUploadFiles.arrayStMeterNumber.size();i++) {
					for (int j = 0; j<arrayStMapMeter.size();j++) {
						if (arrayStMapMeter.get(j).stMeterNumber.equals(holderUploadFiles.arrayStMeterNumber.get(i).stMeterNumber)) {
							arrayStMapMeter.get(j).stTextStatus = holderUploadFiles.arrayStMeterNumber.get(i).stTextStatus;
							arrayStMapMeter.get(j).stSignStatus = holderUploadFiles.arrayStMeterNumber.get(i).stSignStatus;
							arrayStMapMeter.get(j).stPhotoStatus = holderUploadFiles.arrayStMeterNumber.get(i).stPhotoStatus;
						}
					}
				}
				
				listAdapter.notifyDataSetChanged();
				
				break;
			default:
				break;
			}
		};
	};
	
	private class UpLoad extends AsyncTask<UploadHolder, Integer, String>
	{

				
		//onPreExecute������execute()��ִ��
        @Override  
        protected void onPreExecute() 
        {  
        	dialog = new ProgressDialog(UploadArchives.this);
            dialog.setTitle("Uploading files");
            dialog.setMessage("�����ϴ�...");
            
            dialog.setMax(100);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.show();
            Log.i("Asyn", "onPreExecute() enter");  
            
        }  
          
        //doInBackground�����ڲ�ִ�к�̨����,�������������UI���������쳣��
        @Override  
        protected String doInBackground(UploadHolder... params) 
        {  
            Log.i("Asyn", "doInBackground(String... params) enter");  
            
            String IP = prefs.getString("serverip", null);
    		String port = prefs.getString("serverport", null);
    		
    		if ((null==IP)||(null==port)) {
    			Toast.makeText(getApplicationContext(), "Pls configure server IP and port", Toast.LENGTH_SHORT).show();
    			return "Server not Configured";
    		}
        	
			String actionUrl = "http://" +
					IP +
					":" +
					port +
					"/FDM/fdm/uploadMission!uploadMission.do";
			
			
			InputStream isFile;
			
			
			
			for (int i = 0; i < holderUploadFiles.arrayStMeterNumber.size(); i ++) {
				ArrayList<String> uploadedText = new ArrayList<String>();
				ArrayList<String> uploadedSign = new ArrayList<String>();
				ArrayList<String> uploadedPhoto = new ArrayList<String>();
				
				
				//ʵ��һ��ѭ��ֻ��һ��text�ĵ���Ϊ�˱��ֿ���չ�ԣ���Ȼʹ��ѭ����
				for (int j = 0; j < holderUploadFiles.arrayStFileNames.get(i).get("Text").size(); j++){
					
					
					try {
						HttpClient httpclient = new DefaultHttpClient(); 
						//���URL
						HttpPost httppost = new HttpPost(actionUrl); 
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
						//Your DATA 
					    nameValuePairs.add(new BasicNameValuePair(TaskProvider.C_OPERATOR_NAME, Login.mOperator)); 
					    isFile =new FileInputStream(holderUploadFiles.arrayStFileNames.get(i).get("Text").get(j).stFileName);
					    String content = new String(Utils.bytesFromInstream(isFile));
					    Log.i("UploadArchives", content);
					    nameValuePairs.add(new BasicNameValuePair("Task_Contents", content)); 

				    
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						
						
					

					    HttpResponse response; 
					    
				    	response=httpclient.execute(httppost);
					    
				    	
				    	if(response.getStatusLine().getStatusCode()==200){ 
				    		byte[] bytes = Utils.bytesFromInstream(response.getEntity().getContent());
				            JSONObject JsonResponse = new JSONObject(new String(bytes));
				            String isSuccess = JsonResponse.getString("success");
				            String resultTxt = JsonResponse.getString("msg");
				            if (isSuccess.equals("true")) {
				            	holderUploadFiles.arrayStFileNames.get(i).get("Text").get(j).stStatus = "succeed";
				            }
				    	} else {
				    		holderUploadFiles.arrayStFileNames.get(i).get("Text").get(j).stStatus = "connection failed";
				    	}
				    	
				    } catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						holderUploadFiles.arrayStFileNames.get(i).get("Text").get(j).stStatus = "connection failed";
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						holderUploadFiles.arrayStFileNames.get(i).get("Text").get(j).stStatus = "connection failed";
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						holderUploadFiles.arrayStFileNames.get(i).get("Text").get(j).stStatus = "connection failed";
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						holderUploadFiles.arrayStFileNames.get(i).get("Text").get(j).stStatus = "connection failed";
					} 
					
					uploadedText.add(holderUploadFiles.arrayStFileNames.get(i).get("Text").get(j).stStatus);

				}
				/*
				//ʵ��һ��ѭ��ֻ��һ��text�ĵ���Ϊ�˱��ֿ���չ�ԣ���Ȼʹ��ѭ����
				for (int j = 0; j < holderUploadFiles.arrayStFileNames.get(i).get("Text").size(); j++){
					Map<String, String> hparams = new HashMap<String, String>();
					hparams.put("Date", holderUploadFiles.stDate);
					hparams.put("UserName", holderUploadFiles.stUserName);
					hparams.put("MeterNumber", holderUploadFiles.arrayStMeterNumber.get(i).stMeterNumber);
					
					try {
						if (!holderUploadFiles.arrayStFileNames.get(i).get("Text").get(j).stFileName.equals(FILE_NOT_EXIST)) {
							isFile =new FileInputStream(holderUploadFiles.arrayStFileNames.get(i).get("Text").get(j).stFileName);
							holderUploadFiles.arrayStFileNames.get(i).get("Text").get(j).stStatus = postFile(actionUrl, 
									hparams, holderUploadFiles.arrayStFileNames.get(i).get("Text").get(j).stFileName, 
									Utils.Base64BytesFromInstream(isFile));
							
						}
						uploadedText.add(holderUploadFiles.arrayStFileNames.get(i).get("Text").get(j).stStatus);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				//ǩ��ͼƬ�ϴ�
				for (int j = 0; j < holderUploadFiles.arrayStFileNames.get(i).get("Sign").size(); j++){
					Map<String, String> hparams = new HashMap<String, String>();
					hparams.put("Date", holderUploadFiles.stDate);
					hparams.put("UserName", holderUploadFiles.stUserName);
					hparams.put("MeterNumber", holderUploadFiles.arrayStMeterNumber.get(i).stMeterNumber);
					
					try {
						if (!holderUploadFiles.arrayStFileNames.get(i).get("Sign").get(j).stFileName.equals(FILE_NOT_EXIST)) {
							isFile =new FileInputStream(holderUploadFiles.arrayStFileNames.get(i).get("Sign").get(j).stFileName);
							holderUploadFiles.arrayStFileNames.get(i).get("Sign").get(j).stStatus = postFile(actionUrl, 
									hparams, holderUploadFiles.arrayStFileNames.get(i).get("Sign").get(j).stFileName, 
									Utils.Base64BytesFromInstream(isFile));
							
						}
						uploadedSign.add(holderUploadFiles.arrayStFileNames.get(i).get("Sign").get(j).stStatus);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//��Ƭ�ϴ�
				if (prefs.getBoolean("uploadphotoes", false)){
					for (int j = 0; j < holderUploadFiles.arrayStFileNames.get(i).get("Photo").size(); j++){
						Map<String, String> hparams = new HashMap<String, String>();
						hparams.put("Date", holderUploadFiles.stDate);
						hparams.put("UserName", holderUploadFiles.stUserName);
						hparams.put("MeterNumber", holderUploadFiles.arrayStMeterNumber.get(i).stMeterNumber);
						
						try {
							if (!holderUploadFiles.arrayStFileNames.get(i).get("Photo").get(j).stFileName.equals(FILE_NOT_EXIST)) {
								isFile =new FileInputStream(holderUploadFiles.arrayStFileNames.get(i).get("Photo").get(j).stFileName);
								holderUploadFiles.arrayStFileNames.get(i).get("Photo").get(j).stStatus = postFile(actionUrl, 
										hparams, holderUploadFiles.arrayStFileNames.get(i).get("Photo").get(j).stFileName, 
										Utils.Base64BytesFromInstream(isFile));
								
							}
							uploadedPhoto.add(holderUploadFiles.arrayStFileNames.get(i).get("Photo").get(j).stStatus);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (uploadedPhoto.contains(FILE_NOT_EXIST)){
						holderUploadFiles.arrayStMeterNumber.get(i).stPhotoStatus = FILE_NOT_EXIST;
					} else if (uploadedPhoto.contains("connection failed")) {
						holderUploadFiles.arrayStMeterNumber.get(i).stPhotoStatus = FAILED;
					} else if (0 == uploadedPhoto.size()) {
						holderUploadFiles.arrayStMeterNumber.get(i).stPhotoStatus = FILE_NOT_EXIST;
					} else {
						holderUploadFiles.arrayStMeterNumber.get(i).stPhotoStatus = UPLOAD_SUCCEED;
					}
				}
				*/
				if (uploadedText.contains(FILE_NOT_EXIST)){
					holderUploadFiles.arrayStMeterNumber.get(i).stTextStatus = FILE_NOT_EXIST;
				} else if (uploadedText.contains("connection failed")) {
					holderUploadFiles.arrayStMeterNumber.get(i).stTextStatus = FAILED;
				} else if (0 == uploadedText.size()) {
					holderUploadFiles.arrayStMeterNumber.get(i).stTextStatus = FILE_NOT_EXIST;
				} else {
					holderUploadFiles.arrayStMeterNumber.get(i).stTextStatus = UPLOAD_SUCCEED;
				}
				/*
				if (uploadedSign.contains(FILE_NOT_EXIST)){
					holderUploadFiles.arrayStMeterNumber.get(i).stSignStatus = FILE_NOT_EXIST;
				} else if (uploadedSign.contains("connection failed")) {
					holderUploadFiles.arrayStMeterNumber.get(i).stSignStatus = FAILED;
				} else if (0 == uploadedSign.size()) {
					holderUploadFiles.arrayStMeterNumber.get(i).stSignStatus = FILE_NOT_EXIST;
				} else {
					holderUploadFiles.arrayStMeterNumber.get(i).stSignStatus = UPLOAD_SUCCEED;
				}
				*/
				
				
				
				//ˢ�½�����
				publishProgress((i+1)*100/holderUploadFiles.arrayStMeterNumber.size());
				//������Ϣˢ���ϴ�״̬
				mHandler.sendEmptyMessage(UPLOAD_STATUS_RENEW);
			}
			  	
    		return "ok";
        }  
          
        //onProgressUpdate�������ڸ��½�����Ϣ  
        @Override  
        protected void onProgressUpdate(Integer... progresses) 
        {  
        	super.onProgressUpdate(progresses);
            Log.i("dialog", "onProgressUpdate(Integer... progresses) enter");  
            dialog.setProgress(progresses[0]);
              
        }  
          
        //onPostExecute����doInBackgroundִ����󣬸��½���UI��
        //result��doInBackground���صĽ��
        @Override  
        protected void onPostExecute(String result)
        {  
            Log.i("Asyn", "onPostExecute(Result result) called");
                        
            dialog.dismiss();
            
        }  
          
        //onCancelled��������ȡ��Taskִ�У�����UI
        @Override  
        protected void onCancelled() 
        {  
            Log.i("Asyn", "onCancelled() called");  
        }
		
	}
	private void initUploadFiles() {
		// TODO Auto-generated method stub
		holderUploadFiles = new UploadHolder();
		holderUploadFiles.arrayStMeterNumber = new ArrayList<ListviewMeterStatus>();
		holderUploadFiles.arrayStFileNames = new ArrayList<HashMap<String,ArrayList<UploadStatus>>>();
										
		
		// ��ʼ����Ҫ�ϴ��ı��
		for (int i = 0; i<arrayStMapMeter.size(); i++) {
			if (arrayStMapMeter.get(i).isChecked){
				
				holderUploadFiles.arrayStMeterNumber.add(arrayStMapMeter.get(i));
				Log.i("checked", arrayStMapMeter.get(i).stMeterNumber);
			}
		}
		// ����Ų�Ϊ���ϴ��������ں��û���
		if (!holderUploadFiles.arrayStMeterNumber.isEmpty()) {
			holderUploadFiles.stDate = stDate;
			holderUploadFiles.stUserName = prefs.getString("username", null);
			//holderUploadFiles.bPhoto = cbPhoto.isChecked()? true :false;
			//Log.i("photo", holderUploadFiles.bPhoto ? "true":"false"); 
			//holderUploadFiles.bText = cbText.isChecked()? true :false;
			//Log.i("text",holderUploadFiles.bText? "true":"false"); 
		}
		// ��ȡ�����ļ���ַ
		String stFileDirector = Environment.getExternalStorageDirectory()  
                + "/Hexing/Archives/" + Login.mOperator + "/" + holderUploadFiles.stDate + "/";
		
		for (int i = 0; i < holderUploadFiles.arrayStMeterNumber.size(); i ++) {
			
			String stMeterNumber = holderUploadFiles.arrayStMeterNumber.get(i).stMeterNumber;
			File fUploadFile = new File(stFileDirector + stMeterNumber + "/");
			
			
			//����ı��ļ��Ƿ����
			//if (holderUploadFiles.bText) {
				File tempFile = new File(stFileDirector + stMeterNumber + "/" + stMeterNumber + ".txt");
				UploadStatus tempFileholder = new UploadStatus();
				ArrayList<UploadStatus> tempArrayFileholder = new ArrayList<UploadStatus>();
				HashMap<String,ArrayList<UploadStatus>> tempHashMap = new HashMap<String,ArrayList<UploadStatus>>();
				if (tempFile.exists()) {
					tempFileholder.stFileName = tempFile.getPath();
					tempFileholder.stStatus = "waiting";
				} else {
					tempFileholder.stFileName = FILE_NOT_EXIST;
					tempFileholder.stStatus = FILE_NOT_EXIST;
				}
				tempArrayFileholder.add(tempFileholder);
				tempHashMap.put("Text", tempArrayFileholder);
				//holderUploadFiles.arrayStFileNames.add(tempHashMap);
			//}
			//���ǩ��ͼƬ�Ƿ����
				tempFile = new File(stFileDirector + stMeterNumber + "/" + stMeterNumber + ".png");
				tempFileholder = new UploadStatus();
				tempArrayFileholder = new ArrayList<UploadStatus>();
				//tempHashMap = new HashMap<String,ArrayList<UploadStatus>>();
				if (tempFile.exists()) {
					tempFileholder.stFileName = tempFile.getPath();
					tempFileholder.stStatus = "waiting";
				} else {
					tempFileholder.stFileName = FILE_NOT_EXIST;
					tempFileholder.stStatus = FILE_NOT_EXIST;
				}
				tempArrayFileholder.add(tempFileholder);
				tempHashMap.put("Sign", tempArrayFileholder);
				//holderUploadFiles.arrayStFileNames.add(tempHashMap);
			//}
			//�����ƬͼƬ�Ƿ����
				
			if (prefs.getBoolean("uploadphotoes", false)) {
				tempFile = new File(stFileDirector + stMeterNumber + "/" + stMeterNumber + ".png");
				tempFileholder = new UploadStatus();
				tempArrayFileholder = new ArrayList<UploadStatus>();
				
				if (0 == fUploadFile.listFiles(new jpgFileFilter()).length) {
					tempFileholder.stFileName = FILE_NOT_EXIST;
					tempFileholder.stStatus = FILE_NOT_EXIST;
					tempArrayFileholder.add(tempFileholder);
				}else {
					for (File itefile :fUploadFile.listFiles(new jpgFileFilter())) {
						tempFileholder = new UploadStatus();
						tempFileholder.stFileName = itefile.getPath();
						tempFileholder.stStatus = "waiting";
						tempArrayFileholder.add(tempFileholder);
					}
				}
				tempHashMap.put("Photo", tempArrayFileholder);
				
				
			}
			holderUploadFiles.arrayStFileNames.add(tempHashMap);
			//holderUploadFiles.arrayStFileNames.add(tempArrayFileholder);
			//dialog.setMessage("uploading" + holderUploadFiles.arrayStMeterNumbers.get(i));
			//setProgress(i*100/holderUploadFiles.arrayStMeterNumbers.size());
		}
	}
	public String postFile(String actionUrl, Map<String, String> hparams,
			String stFileName, byte[] bytesFromInstream) {
		// TODO Auto-generated method stub

        StringBuilder sb2 = null;
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        
        //try {
        URL uri;
		try {
			uri = new URL(actionUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "wrong url";
		}
		try {
	        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
	        conn.setReadTimeout(6 * 1000); // ������ʱ��
	        conn.setDoInput(true);// ��������
	        conn.setDoOutput(true);// �������
	        conn.setUseCaches(false); // ������ʹ�û���
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("connection", "keep-alive");
	        conn.setRequestProperty("Charsert", "UTF-8");
	        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
	
	        // ������ƴ�ı����͵Ĳ���
	        StringBuilder sb = new StringBuilder();
	        sb.append(PREFIX);
	        sb.append(BOUNDARY);
	        sb.append(LINEND);
	        for (Map.Entry<String, String> entry : hparams.entrySet())
	        {
	            sb.append(entry.getKey() + "=" + entry.getValue());
	            sb.append(LINEND);
	        }
	
	        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
	        outStream.write(sb.toString().getBytes());
        
        
        
	        // �����ļ�����
        

            StringBuilder sb1 = new StringBuilder();
            sb1.append(PREFIX);
            sb1.append(BOUNDARY);
            sb1.append(LINEND);
            sb1.append("filename=" + stFileName.substring(stFileName.lastIndexOf("/")+1) + LINEND);
            sb1.append("filelength="+ bytesFromInstream.length + LINEND);
            sb1.append(PREFIX);
            sb1.append(BOUNDARY);
            sb1.append(LINEND);
            
			outStream.write(sb1.toString().getBytes());
			outStream.write(bytesFromInstream);
			
            // ���������־
            byte[] end_data = (PREFIX + BOUNDARY + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();
		    // �õ���Ӧ��
            InputStream in = null;
            int res = conn.getResponseCode();
            if (res == 200)
            {
                in = conn.getInputStream();
                int ch;
                sb2 = new StringBuilder();
                while ((ch = in.read()) != -1)
                {
                    sb2.append((char) ch);
                }
                System.out.println(sb2.toString());
            }
            in.close();
            outStream.close();
            conn.disconnect();
            // ����������������������
            //return ParseJson.getEditMadIconResult(sb2.toString());
            return sb2.toString();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return "connection failed";
		}
        

	}
	public class UploadListAdapter extends BaseAdapter{
		
		private LayoutInflater inflater ;
		
		
		public UploadListAdapter(Context context){
		      super();
		      inflater = LayoutInflater.from(context);
		      
		      
		}
		     

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return UploadArchives.arrayStMapMeter.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return UploadArchives.arrayStMapMeter.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			
		    
			if(convertView == null){
			    convertView = inflater.inflate(R.layout.uploadlistitem, null);
			    holder = new ViewHolder();
			    holder.checktv_meter = (CheckBox) convertView.findViewById(R.id.meternumbercheck);
			    holder.iv_text = (ImageView)convertView.findViewById(R.id.text_upload);
			    holder.iv_signature = (ImageView)convertView.findViewById(R.id.signature_upload);;
			    holder.iv_photo = (ImageView)convertView.findViewById(R.id.photo_upload);
			    convertView.setTag(holder);
			}else{
			    holder = (ViewHolder) convertView.getTag();
			}
			   
			holder.checktv_meter.setText(arrayStMapMeter.get(arg0).stMeterNumber);
			if (null == arrayStMapMeter.get(arg0).stTextStatus) {
				holder.iv_text.setImageBitmap(null);
			}else if (arrayStMapMeter.get(arg0).stTextStatus.equals(UPLOAD_SUCCEED)) {
				holder.iv_text.setImageResource(R.drawable.check);
			} else if(arrayStMapMeter.get(arg0).stTextStatus.equals(FAILED)) {
				holder.iv_text.setImageResource(R.drawable.cross);
			} else {
				holder.iv_text.setImageResource(R.drawable.warning);
			}
			/*
			if (null == arrayStMapMeter.get(arg0).stSignStatus) {
				holder.iv_signature.setImageBitmap(null);
			}else if (arrayStMapMeter.get(arg0).stSignStatus.equals(UPLOAD_SUCCEED)) {
				holder.iv_signature.setImageResource(R.drawable.check);
			} else if(arrayStMapMeter.get(arg0).stSignStatus.equals(FAILED)) {
				holder.iv_signature.setImageResource(R.drawable.cross);
			} else {
				holder.iv_signature.setImageResource(R.drawable.warning);
			}
			
			if (prefs.getBoolean("uploadphotoes", false)){
				if (null == arrayStMapMeter.get(arg0).stPhotoStatus) {
					holder.iv_photo.setImageBitmap(null);
				}else if (arrayStMapMeter.get(arg0).stPhotoStatus.equals(UPLOAD_SUCCEED)) {
					holder.iv_photo.setImageResource(R.drawable.check);
				} else if(arrayStMapMeter.get(arg0).stPhotoStatus.equals(FAILED)) {
					holder.iv_photo.setImageResource(R.drawable.cross);
				} else {
					holder.iv_photo.setImageResource(R.drawable.warning);
				}
			}
			*/
			//����checkMap��position��״̬�����Ƿ�ѡ��
			if (arrayStMapMeter.get(arg0).isChecked == true) {
			    holder.checktv_meter.setChecked(true);
			}else{
			    holder.checktv_meter.setChecked(false);
			}
			   
			   return convertView;
		}
	}
	
	/*
	public class FileNameAndStatus {
    	String stFileName;
    	String stTextStatus;
    	String stSignStatus;
    	String stPhotoStatus;
    }*/
	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		initMeter(query);
		listAdapter.notifyDataSetChanged();
		return false;
	}
	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		initMeter(newText);
		listAdapter.notifyDataSetChanged();
		return false;
	}
}
	class ViewHolder {
		CheckBox checktv_meter;
		ImageView iv_text;
		ImageView iv_signature;
		ImageView iv_photo;
		}
	class UploadStatus {
		String stFileName;
    	String stStatus;
	}
	class ListviewMeterStatus{
		String stMeterNumber;
		boolean isChecked;
		String stTextStatus;
    	String stSignStatus;
    	String stPhotoStatus;
	}
	class UploadHolder {
    	ArrayList<ListviewMeterStatus> arrayStMeterNumber;
    	ArrayList<HashMap<String,ArrayList<UploadStatus>>> arrayStFileNames;
    	String stDate;
    	String stUserName;
    	boolean bText;
    	boolean bPhoto;
    }
	
