package com.hexing.imeter.fragment;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import capture.CaptureActivity;
import cn.handwriting.DialogListener;
import cn.handwriting.WritePadDialog;

import com.actionbarsherlock.app.SherlockFragment;
import com.hexing.imeter.DelArchive;
import com.hexing.imeter.Login;
import com.hexing.imeter.R;
import com.hexing.imeter.ViewPictures;
import com.hexing.imeter.utils.TaskProvider;
import com.hexing.imeter.utils.Utils;

public class EditFragment extends SherlockFragment {
	
	/*SCANNER*/
	public final static int SCAN_REMOVE_DEVICE 	= 1;
	/*PHOTO*/
	public final static int TAKE_PHOTO 			= 2;
	/*SCAN REPLACE METER*/
	public final static int SCAN_INSTALL_DEVICE	= 3;
	public final static int SCAN_METERNUMBER = 4;
	
	
	//public final static String TRANSFOR_METERNUMBER = "TRANSFOR_METERNUMBER";
	public final static String EDIT_METERNUMBER = "EDIT_METERNUMBER";
	public final static String TASK_TITLE = "Task_Title";
	public final static String CUSTOMER = "CUSTOMER";
	public final static String ADDRESS = "ADDRESS";
	public final static String ENERGY_ACTIVE = "ENERGY_ACTIVE";
	public final static String LONGITUDE = "LONGITUDE";
	public final static String LATITUDE = "LATITUDE";
	
	public final static String PICTURE_TIME = "PICTURE_TIME";
		
	//Button b1;
	String workFlowType;
	public final static String INITIAL = "Initial";
	public final static String METER_INSTALL = "Meter_Install";
	public final static String METER_REMOVE = "Meter_Remove";
	public final static String METER_REPLACE_SPECIFIED = "Meter_Replace_Specified";
	public final static String METER_REPLACE_NOTSPECIFIED = "Meter_Replace_NotSpecified";
	public final static String CONCENTRATOR_INSTALL = "Concentrator_Install";
	public final static String CONCENTRATOR_REMOVE = "Concentrator_Remove";
	public final static String CONCENTRATOR_REPLACE_SPECIFIED = "Concentrator_Replace_Specified";
	public final static String CONCENTRATOR_REPLACE_NOTSPECIFIED = "Concentrator_Replace_NotSpecified";
	public static final String NOT_SPECIFIED = "Not Specified";
	
	protected static final int SUCCEED = 1;
	protected static final int FAILED = 0;
	
	//Main Key
	long   _id = -1;
	//Server database Keys
	String taskID;
	int taskIndex;
	String taskIssueTime;
	String operatorName;
	String mission_Type;
	String device_Type;
	String mission_Status;
	String installDeviceNumber;
	String removeDeviceNumber;
	String Remark;
	String Pod;
	String folderName;
	String signFile;
	String photoFile;
	
	EditText etCustomerName;
	EditText etCustomerNumber;
	EditText etAddress;
	EditText etTransformerName;
	EditText etConcentratorNumber;
	EditText etOldPrepaidBalance;
	EditText etOldEndEnergy;
	EditText etNewStartEnergy;
	
	TextView textLongitude;
    TextView textLatitude;
    TextView textGPSStatus;
	
	String onSiteDate;
	String taskDeviceNumber;
	String isInstallSpecified;
	String isInstallRight;
	String isRemoveSpecified;
	String isRemoveRight;
	
	ImageView scanMeter;
	ImageView scanRemoveMeter;
	ImageView scanInstallMeter;
	
	Button btnSucceed;
	Button btnFailed;
	
	TextView textTaskTitle;
	TextView textTaskStatus;
	TextView textInstallDeviceNumber;
	TextView textRemoveDeviceNumber;
	TextView textTaskStatus1;
	TextView textRemark;
	TextView textIsInstallRight;
	TextView textIsRemoveRight;
	
    


	LinearLayout lRemark;
	LinearLayout lCustomerName;
	LinearLayout lCustomerNumber;
	LinearLayout lAddress;
	LinearLayout lRemoveDevice;
	LinearLayout lInstallDevice;
	LinearLayout lTransformer;
	LinearLayout lConcentrator;
	LinearLayout lLocation;
	LinearLayout lOldBanlance;
	LinearLayout lOldEnergy;
	LinearLayout lNewEnergy;
	LinearLayout lSign;
	LinearLayout lButton;
	
	
    
    Location location;
    LocationListener locationListener;
    GridView gridview;
    //显示的图片数组 
    File[] imageFiles;
    
    Context context; 

	private static boolean isNeedToSave = true;
    private Bitmap mSignBitmap;
	private LinearLayout lvSign;
	private ImageView ivSign;
	private taskPath savePath;
	public boolean isVisible = false;
	
	//SQLiteDatabase db;
	//TaskProvider dbHelper;
	Cursor cDevice;
	  
    int checkedItem;
	private TextWatcher TextEditWatcher = new TextWatcher() {  
        
      @Override    
      public void afterTextChanged(Editable s) {     
          isNeedToSave = true;  
      }   
        
      @Override 
      public void beforeTextChanged(CharSequence s, int start, int count,  
              int after) {  
      }  

       @Override    
      public void onTextChanged(CharSequence s, int start, int before,     
              int count) {     
                  
      }

	};                  
 
	
	
	public static EditFragment newInstance(){
		EditFragment editFragment = new EditFragment();
		return editFragment;
	}
	
	// The container Activity must implement this interface so the frag can deliver messages
    public interface OnEditFragmentRenewListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void OnEditFragmentRenew(Bundle bundle);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putLong(TaskProvider.C_ID, _id);
        outState.putString(TaskProvider.C_TASK_ID, taskID);
        outState.putInt(TaskProvider.C_TASK_INDEX, taskIndex);
        outState.putString(TaskProvider.C_TASK_ISSUE_TIME, taskIssueTime);
        outState.putString(TaskProvider.C_OPERATOR_NAME, Login.mOperator);
        outState.putString(TaskProvider.C_INSTALL_DEVICE_NO, installDeviceNumber);
        outState.putString(TaskProvider.C_REMOVE_DEVICE_NO, removeDeviceNumber);    
        outState.putString(TaskProvider.C_DEVICE_TYPE, device_Type);
        outState.putString(TaskProvider.C_TASK_TYPE, mission_Type);
        outState.putString(TaskProvider.C_TASK_STATUS, mission_Status);
        outState.putString(TaskProvider.C_POD, Pod);
        outState.putString(TaskProvider.C_FOLDER_NAME, folderName);
        
        
        outState.putString(TaskProvider.C_CUSTOMER_NAME, etCustomerName.getText().toString());
        outState.putString(TaskProvider.C_CUSTOMER_NO, etCustomerNumber.getText().toString());
        outState.putString(TaskProvider.C_ADDRESS, etAddress.getText().toString());
        outState.putString(TaskProvider.C_TRANSFORMA_NAME, etTransformerName.getText().toString());
        outState.putString(TaskProvider.C_UPLINK_CONCENTRATOR, etTransformerName.getText().toString());
        outState.putString(TaskProvider.C_PREPAID_OLD_BALANCE, etOldPrepaidBalance.getText().toString());
        outState.putString(TaskProvider.C_OLD_END_ENERGY, etOldEndEnergy.getText().toString());
        outState.putString(TaskProvider.C_NEW_START_ENERGY, etNewStartEnergy.getText().toString());
        outState.putString(TaskProvider.C_LONGITUDE, textLongitude.getText().toString());
        outState.putString(TaskProvider.C_LATITUDE, textLatitude.getText().toString());
        outState.putString(TaskProvider.C_TASK_ANOMALY_REASON, textRemark.getText().toString());
        outState.putString(TaskProvider.C_TASK_ANOMALY_REASON, textRemark.getText().toString());
        
        
        outState.putString(TaskProvider.C_ONSITE_DATE, onSiteDate);
        outState.putString(TaskProvider.C_TASK_DEVICE_NO, taskDeviceNumber);
        outState.putString(TaskProvider.C_INSTALL_ISSPECIFIED, isInstallSpecified);
        outState.putString(TaskProvider.C_INSTALL_ISRIGHT, isInstallRight);
    	
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.edit_fragment, null);	
		
		getSherlockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		context=getSherlockActivity();
		// 定义UI组件
		scanRemoveMeter = (ImageView) v.findViewById(R.id.scanremove);
		scanInstallMeter = (ImageView) v.findViewById(R.id.scaninstall);
		scanMeter = (ImageView) v.findViewById(R.id.scan);
		textTaskTitle = (TextView)v.findViewById(R.id.tasktitle);
		textTaskStatus = (TextView)v.findViewById(R.id.taskstatus);
		textTaskStatus1 = (TextView)v.findViewById(R.id.taskstatus1);
		textGPSStatus = (TextView)v.findViewById(R.id.gpsstatus);
		textLongitude = (TextView)v.findViewById(R.id.longitude);
	    textLatitude = (TextView)v.findViewById(R.id.latitude);
	    textInstallDeviceNumber = (TextView)v.findViewById(R.id.installdevicenumber);
	    textIsInstallRight = (TextView)v.findViewById(R.id.isinstallright);
	    textRemoveDeviceNumber = (TextView)v.findViewById(R.id.removedevicenumber);
	    textIsRemoveRight = (TextView)v.findViewById(R.id.isremoveright);
	    textRemark = (TextView)v.findViewById(R.id.inputremark);
	    
		etCustomerName = (EditText)v.findViewById(R.id.inputcustomername);
		etCustomerNumber = (EditText)v.findViewById(R.id.inputcustomernumber);
		etAddress = (EditText)v.findViewById(R.id.inputaddress);
		etTransformerName = (EditText)v.findViewById(R.id.inputtransformaname);
		etConcentratorNumber = (EditText)v.findViewById(R.id.inputconcentratornumber);
		etOldPrepaidBalance = (EditText)v.findViewById(R.id.inputoldbalance);
		etOldEndEnergy  = (EditText)v.findViewById(R.id.inputoldenergy);
		etNewStartEnergy = (EditText)v.findViewById(R.id.inputnewenergy);
		
		etCustomerName.addTextChangedListener(TextEditWatcher);
		etCustomerNumber.addTextChangedListener(TextEditWatcher);
		etAddress.addTextChangedListener(TextEditWatcher);
		etTransformerName.addTextChangedListener(TextEditWatcher);
		etConcentratorNumber.addTextChangedListener(TextEditWatcher);
		etOldPrepaidBalance.addTextChangedListener(TextEditWatcher);
		etOldEndEnergy.addTextChangedListener(TextEditWatcher);
		etNewStartEnergy.addTextChangedListener(TextEditWatcher);
		
		lCustomerName = (LinearLayout)v.findViewById(R.id.customer_name);;
		lCustomerNumber = (LinearLayout)v.findViewById(R.id.customer_number);
		lAddress = (LinearLayout)v.findViewById(R.id.address);
		lRemoveDevice = (LinearLayout)v.findViewById(R.id.removedevice);
		lInstallDevice = (LinearLayout)v.findViewById(R.id.installdevice);
		lTransformer = (LinearLayout)v.findViewById(R.id.transformer);
		lConcentrator = (LinearLayout)v.findViewById(R.id.uplink_concentrator);
		lLocation = (LinearLayout)v.findViewById(R.id.location);
		lOldBanlance = (LinearLayout)v.findViewById(R.id.old_prepaid_balance);
		lOldEnergy = (LinearLayout)v.findViewById(R.id.old_meter_energy);
		lNewEnergy = (LinearLayout)v.findViewById(R.id.new_meter_energy);
		lSign = (LinearLayout)v.findViewById(R.id.linear_sign);
		lRemark = (LinearLayout)v.findViewById(R.id.remark);
		lButton = (LinearLayout)v.findViewById(R.id.button);
		gridview=(GridView)v.findViewById(R.id.gridview);
		
		ivSign =(ImageView)v.findViewById(R.id.iv_sign);
		
        taskDeviceNumber = null;
		
		btnSucceed = (Button)v.findViewById(R.id.succeed_btn);
		btnFailed = (Button)v.findViewById(R.id.failed_btn);
        
        refreshFragment(savedInstanceState);
      
        
        //横竖屏翻转刷新	
        //recreate(savedInstanceState);
        	
                
        //initSignature(taskDeviceNumber, onSiteDate);
        
        //initGridView(taskDeviceNumber, onSiteDate);
        
        
        Log.i(this.getClass().getName(), "onCreateView");
        //Toast.makeText(super.getSherlockActivity(), "Long click picture to delete",Toast.LENGTH_SHORT).show();
        
        //Button btn_GPS = (Button) v.findViewById(R.id.buttongetGPS);
		return v;
	}
	
	


	private void InitialFragment() {
		// TODO Auto-generated method stub
		
		lCustomerName.setVisibility(View.GONE);
		lCustomerNumber.setVisibility(View.GONE);
		lAddress.setVisibility(View.GONE);
		lInstallDevice.setVisibility(View.GONE);
		lRemoveDevice.setVisibility(View.GONE);
		lTransformer.setVisibility(View.GONE);
		lConcentrator.setVisibility(View.GONE);
		lLocation.setVisibility(View.GONE);
		lOldBanlance.setVisibility(View.GONE);
		lOldEnergy.setVisibility(View.GONE);
		lNewEnergy.setVisibility(View.GONE);
		lSign.setVisibility(View.GONE);
		lRemark.setVisibility(View.GONE);
		lButton.setVisibility(View.GONE);
		lvSign.setVisibility(View.GONE);
		gridview.setVisibility(View.GONE);
		
		textTaskStatus.setVisibility(View.GONE);
		
		//Main Key
		_id = -1;
		//Server database Keys
		taskID = "";
		taskIndex = -1;
		taskIssueTime = "";
		operatorName = "";
		mission_Type = "";
		device_Type = "";
		mission_Status = "";
		installDeviceNumber = "";
		removeDeviceNumber = "";
		Remark = "";
		Pod = "";
		
		etCustomerName.setText("");
		etCustomerNumber.setText("");
		etAddress.setText("");
		etTransformerName.setText("");
		etConcentratorNumber.setText("");
		etOldPrepaidBalance.setText("");
		etOldEndEnergy.setText("");
		etNewStartEnergy.setText("");
		
		textLongitude.setText("");
	    textLatitude.setText("");
		
		onSiteDate = "";
		folderName = "";
		taskDeviceNumber = "";
		isInstallSpecified = "";
		isInstallRight = "";
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
        btnFailed.setOnClickListener(new onClickListener());
        btnSucceed.setOnClickListener(new onClickListener());
        scanInstallMeter.setOnClickListener(new onClickListener());
        scanRemoveMeter.setOnClickListener(new onClickListener());
		scanMeter.setOnClickListener(new onClickListener());
        textGPSStatus.setOnClickListener(new onClickListener());
	}
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
		Log.i(this.getClass().getName(), "onResume");
		initGridView(_id);
		initSignature(_id);
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
	
	
	
	private class onClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			
			case R.id.succeed_btn:
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(taskDeviceNumber)) {
					if (mission_Type.equals(TaskProvider.MISSION_REPLACE)&&(TextUtils.isEmpty(installDeviceNumber))) {
						Dialog noReplace = new AlertDialog.Builder(getSherlockActivity())
						.setIcon(android.R.drawable.btn_star)
					   	.setTitle("Replacement not done")
					   	.setMessage("Please scan the barcode of the installed device to confirm.")
					   	.setNeutralButton("OK", 
							new DialogInterface.OnClickListener() {
							    @Override
							    public void onClick(DialogInterface dialog, int which) {
							    	 
							    }
							})
						
						.create();

						noReplace.show();
					}else if (isInstallSpecified.equals(TaskProvider.TRUE)&&isInstallRight.equals(TaskProvider.FALSE)) {
						Dialog replaceWrong = new AlertDialog.Builder(getSherlockActivity())
						.setIcon(android.R.drawable.btn_star)
					   	.setTitle("Replacement not confirmed")
					   	.setMessage("Please scan the barcode of the installed device to confirm.")
					   	.setNeutralButton("OK", 
							new DialogInterface.OnClickListener() {
							    @Override
							    public void onClick(DialogInterface dialog, int which) {
							    	 
							    }
							})
						
						.create();

						replaceWrong.show();
					} else {
						//if (device_Type.equals(TaskProvider.DEVICE_METER)){
							
							WritePadDialog writeTabletDialog = new WritePadDialog(
									context, new DialogListener() {
										@Override
										public void refreshActivity(Object object) {							
											
											mSignBitmap = (Bitmap) object;
											createFile();
																									
											ivSign.setImageBitmap(mSignBitmap);
											
											//mHandler.sendEmptyMessage(SUCCEED);
											//写签名文件和档案文件在不同的进程对同一个目录操作，需要错开时间否则可能出错									
											mHandler.sendEmptyMessageDelayed(SUCCEED,1000);
										}
									});
							writeTabletDialog.show();
							/*
						} else {
							Dialog succeed = new AlertDialog.Builder(getSherlockActivity())
							.setIcon(android.R.drawable.btn_star)
						   	.setTitle("Confirm Mission Status")
						   	.setMessage("Once you confirm you will not be able to modify the information.\n" +
						   			"Are you sure to change the mission status to Succeed?")
						   	.setPositiveButton("Yes",
							     new DialogInterface.OnClickListener() {
								     @Override
								     public void onClick(DialogInterface dialog, int which) {
								    	 mHandler.sendEmptyMessage(SUCCEED);
								     }
							     })
							.setNegativeButton("No", 
								new DialogInterface.OnClickListener() {
								    @Override
								    public void onClick(DialogInterface dialog, int which) {
								    	 
								    }
								})
							
							.create();
	
							succeed.show();
						}*/
					}
				}else {
					//先扫表号
					Intent intent = new Intent(context, CaptureActivity.class);
			        
			        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			        if (mission_Type.equals(TaskProvider.MISSION_INSTALL)) {
			        	startActivityForResult(intent, SCAN_INSTALL_DEVICE);
			        } else {
			        	startActivityForResult(intent, SCAN_REMOVE_DEVICE);
			        }
			         
				}
				break;
			case R.id.failed_btn:
				LayoutInflater inflater = LayoutInflater.from(context);
				
				final View textEntryView = inflater.inflate(R.layout.failed_dialog, null);

				Dialog failed = new AlertDialog.Builder(getSherlockActivity())
				.setIcon(android.R.drawable.btn_star)
			   	.setTitle("Please input the reason of failure:")
			   	.setView(textEntryView)
			   	.setMessage("Once you confirm you will not be able to modify the information.\n" +
			   			"Are you sure to change the mission status to Failed?")
			   	.setPositiveButton("Yes",
				    new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
							EditText edit_text = (EditText)textEntryView.findViewById(R.id.etfailreason);
							Message msg = new Message();
							msg.what = FAILED;
							Bundle data = new Bundle();
							data.putString(TaskProvider.C_TASK_ANOMALY_REASON, (TextUtils.isEmpty(edit_text.getText().toString())?"":edit_text.getText().toString()));
							msg.setData(data);
							mHandler.sendMessage(msg);
						}
				    })
				.setNegativeButton("No", 
					new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					    	 
					    }
					})
				
				.create();

				failed.show();
				break;
			case R.id.scanremove:
				Intent intent = new Intent(context, CaptureActivity.class);
		        
		        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
		        startActivityForResult(intent, SCAN_REMOVE_DEVICE);  
				break;
				
			case R.id.scaninstall:
				Intent scanReplace = new Intent(context, CaptureActivity.class);
		        
				scanReplace.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
		        startActivityForResult(scanReplace, SCAN_INSTALL_DEVICE);  
				break;
			case R.id.scan:
				Intent scan = new Intent(context, CaptureActivity.class);
		        
				scan.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
		        startActivityForResult(scan, SCAN_METERNUMBER);  
				break;
			case R.id.gpsstatus:
				// 获取LocationManager对象
		        LocationManager lm = (LocationManager) getSherlockActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		        
	        	// 定义Criteria对象
	            Criteria criteria = new Criteria();
	            // 设置定位精确度 Criteria.ACCURACY_COARSE 比较粗略， Criteria.ACCURACY_FINE则比较精细
	            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
	            // 设置是否需要海拔信息 Altitude
	            criteria.setAltitudeRequired(false);
	            // 设置是否需要方位信息 Bearing
	            criteria.setBearingRequired(true);
	            // 设置是否允许运营商收费
	            criteria.setCostAllowed(true);
	            // 设置对电源的需求
	            criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

	            // 获取GPS信息提供者
	            String bestProvider = lm.getBestProvider(criteria, true);
	            Log.i("gps", "bestProvider = " + bestProvider);
	            if (lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
	            	// 获取定位信息
	                location = lm.getLastKnownLocation(bestProvider);
	            }

	            setLocation(location);
	            
	            if (lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
	                // 1000毫秒更新一次，忽略位置变化
	                lm.requestLocationUpdates(bestProvider, 5000, 0, new LocationListener() {

	                    // 当位置改变时触发
	                    @Override
	                    public void onLocationChanged(Location location) {
	                            Log.i("gps", location.toString());
	                            setLocation(location);
	                    }

	                    // Provider失效时触发
	                    @Override
	                    public void onProviderDisabled(String arg0) {
	                            Log.i("gps", arg0);
	                    }

	                    // Provider可用时触发
	                    @Override
	                    public void onProviderEnabled(String arg0) {
	                            Log.i("gps", arg0);
	                    }

	                    // Provider状态改变时触发
	                    @Override
	                    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	                            Log.i("gps", "onStatusChanged");
	                    }
	                });
	            }
				break;

			default:
				break;
			}
		}
		
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			//接受线程发送过来的消息  并在此UI线程中来更新ListView中填充的数据
			switch (msg.what) {
			case SUCCEED:
				mission_Status = TaskProvider.STATUS_SUCCEED;
				isNeedToSave = true;
				saveArchives();
				refreshFragmentByID(_id);

				break;
			case FAILED:
				Remark = msg.getData().getString(TaskProvider.C_TASK_ANOMALY_REASON);
				mission_Status = TaskProvider.STATUS_FAILED;
				isNeedToSave = true;
				saveArchives();
				refreshFragmentByID(_id);
				
				break;
			
			default:
				break;
			}
		};
	};
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle;
        if(resultCode == Activity.RESULT_OK){  
	        switch (requestCode) {  
			case SCAN_METERNUMBER:  
            
                bundle = data.getExtras();  
                
                
                Log.i("scan meter", bundle.getString("result"));
                
                cDevice = getSherlockActivity().getContentResolver().query(TaskProvider.CONTENT_URI,
                		null,
                		TaskProvider.C_TASK_DEVICE_NO + " LIKE '%" + bundle.getString("result") +"%'",
                		/*
                																					+" AND ( "
                        		+ TaskProvider.C_TASK_STATUS +" = " + TaskProvider.STATUS_INITIAL + " or "
                        				+ TaskProvider.C_TASK_STATUS +" = " + TaskProvider.STATUS_PENDING + ")",
                        */
                		null, 
                		null);
                Log.i("scan meter", cDevice.getCount()+"");
                if(0 == cDevice.getCount()) {
                	
                	Dialog noSuchTask = new AlertDialog.Builder(context)
                	.setIcon(android.R.drawable.btn_star)
				   	.setTitle("No such mission")
				   	.setMessage("This device is not your mission!")
				   	.setNeutralButton("Ok", 
						new DialogInterface.OnClickListener() {
						    @Override
						    public void onClick(DialogInterface dialog, int which) {
						    	 
						    }
						})
					
					.create();

                	noSuchTask.show();
                } else if (1 == cDevice.getCount()) {
                	cDevice.moveToFirst();
                	long id = cDevice.getLong(cDevice.getColumnIndex(TaskProvider.C_ID));
                	refreshFragmentByID(id);
                	if (mission_Type.equals(TaskProvider.MISSION_REPLACE)||
			    			mission_Type.equals(TaskProvider.MISSION_REMOVE)) {
			    			isRemoveRight = TaskProvider.TRUE;
			    			textIsRemoveRight.setText("Confirmed");
	            			textIsRemoveRight.setTextColor(0xff00bb00);
	            			isNeedToSave = true;
	            			saveArchives();
		    		} else if (mission_Type.equals(TaskProvider.MISSION_INSTALL)) {
		    			isInstallRight = TaskProvider.TRUE;
		    			textIsInstallRight.setText("Confirmed");
            			textIsInstallRight.setTextColor(0xff00ff00);
            			isNeedToSave = true;
            			saveArchives();
		    		} else {
		    			Log.i(this.getClass().getName(), "Should not be here 868");
		    		}
                	initSignature(id);
            		initGridView(id);

                } else {
	                //保存cDevice中信息	
                	String[] stList = new String[cDevice.getCount()];
                	final cursorHolder[] taskInfo = new cursorHolder[cDevice.getCount()];
                	
                	for (int i = 0; i<stList.length; i ++){
                		taskInfo[i] = new cursorHolder();
                		cDevice.moveToPosition(i);						
                		stList[i] = cDevice.getString(cDevice.getColumnIndex(TaskProvider.C_TASK_ID));
                		
                		String deviceType;
						if (cDevice.getString(cDevice.getColumnIndex(TaskProvider.C_DEVICE_TYPE)).equals(TaskProvider.DEVICE_METER)) {
							deviceType = "Meter";
						} else if (cDevice.getString(cDevice.getColumnIndex(TaskProvider.C_DEVICE_TYPE)).equals(TaskProvider.DEVICE_CONCENTRATOR)){
							deviceType = "Concentrator";
						} else if (cDevice.getString(cDevice.getColumnIndex(TaskProvider.C_DEVICE_TYPE)).equals(TaskProvider.DEVICE_COLLECTOR)){
							deviceType = "Collector";
						} else {
							deviceType = "";
						}
						
						String taskType;
						if (cDevice.getString(cDevice.getColumnIndex(TaskProvider.C_TASK_TYPE)).equals(TaskProvider.MISSION_INSTALL)) {
							taskType = "Install";
						} else if (cDevice.getString(cDevice.getColumnIndex(TaskProvider.C_TASK_TYPE)).equals(TaskProvider.MISSION_REMOVE)){
							taskType = "Remove";
						} else if (cDevice.getString(cDevice.getColumnIndex(TaskProvider.C_TASK_TYPE)).equals(TaskProvider.MISSION_REPLACE)){
							taskType = "Replace";
						} else {
							taskType = "";
						}
						
						String taskStatus;
						if (cDevice.getString(cDevice.getColumnIndex(TaskProvider.C_TASK_STATUS)).equals(TaskProvider.STATUS_FAILED)) {
							taskStatus = "Failed";
						} else if (cDevice.getString(cDevice.getColumnIndex(TaskProvider.C_TASK_TYPE)).equals(TaskProvider.STATUS_SUCCEED)){
							taskStatus = "Succeed";
						} else if (cDevice.getString(cDevice.getColumnIndex(TaskProvider.C_TASK_TYPE)).equals(TaskProvider.STATUS_PENDING)){
							taskStatus = "Pending";
						} else {
							taskStatus = "Initial";
						}
						
						taskInfo[i].taskID = new String();
						taskInfo[i].taskDeviceNo = new String();
						taskInfo[i].deviceType = new String();
						taskInfo[i].taskType = new String();
						taskInfo[i].taskStatus = new String();
						
						taskInfo[i].C_ID = cDevice.getLong(cDevice.getColumnIndex(TaskProvider.C_ID));
						taskInfo[i].taskID = cDevice.getString(cDevice.getColumnIndex(TaskProvider.C_TASK_ID));
						taskInfo[i].taskDeviceNo = cDevice.getString(cDevice.getColumnIndex(TaskProvider.C_TASK_DEVICE_NO));
						taskInfo[i].deviceType = deviceType;
						taskInfo[i].taskType = taskType;
						taskInfo[i].taskStatus =taskStatus;
                	}
                	//对话框内列表监听
                	android.content.DialogInterface.OnClickListener listener;
                	listener = new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int i) {
							
								Dialog details = new AlertDialog.Builder(context)
								.setIcon(android.R.drawable.btn_star)
								.setTitle("Task Detals")
								.setMessage(taskInfo[i].taskDeviceNo + "\n"
							   			  + "Device Type: " + taskInfo[i].deviceType + "\n"
							   			  + "Task Type: " + taskInfo[i].taskType + "\n"
							   			  + "Task Status: " + taskInfo[i].taskStatus + "\n")
							   	.create();
								details.show();
								checkedItem = i;
							
						}

					};
					
					//列表选择任务
                	Dialog listTask = new AlertDialog.Builder(context)
                	.setIcon(android.R.drawable.btn_star)
				   	.setTitle("Duplicate mission")
				   	.setSingleChoiceItems(stList, 0, listener)
				   	.setPositiveButton("Ok", 
						new DialogInterface.OnClickListener() {
						    @Override
						    public void onClick(DialogInterface dialog, int which) {
						    	if (checkedItem == -1){
						    		Dialog mustChoose = new AlertDialog.Builder(context)
									.setIcon(android.R.drawable.btn_star)
									.setTitle("Warning")
									.setMessage("You must choose one task to start!")
								   	.create();
						    		mustChoose.show();
						    	} else {
						    		refreshFragmentByID(taskInfo[checkedItem].C_ID);
						    		if (mission_Type.equals(TaskProvider.MISSION_REPLACE)||
						    			mission_Type.equals(TaskProvider.MISSION_REMOVE)) {
						    			isRemoveRight = TaskProvider.TRUE;
						    			textIsRemoveRight.setText("Confirmed");
				            			textIsRemoveRight.setTextColor(0xff00bb00);
				            			isNeedToSave = true;
				            			saveArchives();
						    		} else if (mission_Type.equals(TaskProvider.MISSION_INSTALL)) {
						    			isInstallRight = TaskProvider.TRUE;
						    			textIsInstallRight.setText("Confirmed");
				            			textIsInstallRight.setTextColor(0xff00ff00);
				            			isNeedToSave = true;
				            			saveArchives();
						    		} else {
						    			Log.i(this.getClass().getName(), "Should not be here 868");
						    		}
						    		initSignature(taskInfo[checkedItem].C_ID);
					        		initGridView(taskInfo[checkedItem].C_ID);
						    	}
						    	
						    }
						})
					
					.create();

                	listTask.show();
                }
                
                cDevice.close();
                
                break;
	        case SCAN_REMOVE_DEVICE:  
	        	
	        	bundle = data.getExtras();  
                
                //显示扫描到的内容  
                String scanRemoveDevice = bundle.getString("result");
               
                if (isRemoveSpecified.equals(TaskProvider.TRUE)){
                	//不需要刷新taskDeviceNumber和保存路径
                    if (mission_Type.equals(TaskProvider.MISSION_REPLACE)||
                    		mission_Type.equals(TaskProvider.MISSION_REMOVE)) {
                		int removeLength = removeDeviceNumber.length();
                    	while (scanRemoveDevice.length()<removeLength) {
                    		scanRemoveDevice = "0" +scanRemoveDevice;
                    	}
                    	if (scanRemoveDevice.equals(removeDeviceNumber)) {
                    		isRemoveRight = TaskProvider.TRUE;
                    		textIsRemoveRight.setText("Confirmed");
                			textIsRemoveRight.setTextColor(0xff00bb00);
                		} else {
                    		isRemoveRight = TaskProvider.FALSE;
                    		textIsRemoveRight.setText("Not Confirmed");
                			textIsRemoveRight.setTextColor(0xffff0000);
                    	}
                	} else {
                		Log.i(this.getClass().getName(), "Should not be here 932");
                	}
                } else if (isRemoveSpecified.equals(TaskProvider.FALSE)) {
                	//需要刷新taskDeviceNumber和保存路径
                	if (mission_Type.equals(TaskProvider.MISSION_REPLACE)||
                    		mission_Type.equals(TaskProvider.MISSION_REMOVE)) {
                		if (device_Type.equals(TaskProvider.DEVICE_METER)) {
	                    	while (scanRemoveDevice.length()<12) {
	                    		scanRemoveDevice = "0" +scanRemoveDevice;
	                    	}
                		}
                    	taskDeviceNumber = scanRemoveDevice;
                    	textRemoveDeviceNumber.setText(scanRemoveDevice);
                    	removeDeviceNumber = scanRemoveDevice;
                    	//修改保存文件夹名
                    	renameTaskFolder();
                    	
                    	
                	} else {
                		Log.i(this.getClass().getName(), "Should not be here 965");
                	}
                } else {
                	Log.i(this.getClass().getName(), "Should not be here 932");
                }
                
                
                
                isNeedToSave = true;
                saveArchives();
                
                break;
	        case SCAN_INSTALL_DEVICE:  
	            
                bundle = data.getExtras();  
                
                //显示扫描到的内容  
                String scanInstallDevice = bundle.getString("result");
                int installLength;
                //this.taskDeviceNumber=  bundle.getString("result");
                if (isInstallSpecified.equals(TaskProvider.TRUE)){
                	installLength = installDeviceNumber.length();
                	while (scanInstallDevice.length()<installLength) {
                		scanInstallDevice = "0" +scanInstallDevice;
                	}
                	if (scanInstallDevice.equals(installDeviceNumber)) {
                		isInstallRight = TaskProvider.TRUE;
                		textIsInstallRight.setText("Confirmed");
            			textIsInstallRight.setTextColor(0xff00ff00);
            			
                	} else {
                		isInstallRight = TaskProvider.FALSE;
                		textIsInstallRight.setText("Not Confirmed");
            			textIsInstallRight.setTextColor(0xffff0000);
                	}
                } else {
                	if (device_Type.equals(TaskProvider.DEVICE_METER)) {
	                	while (scanInstallDevice.length()<12) {
	                		scanInstallDevice = "0" +scanInstallDevice;
	                	}
                	}
                	if (mission_Type.equals(TaskProvider.MISSION_INSTALL)) {
                    	taskDeviceNumber = scanInstallDevice;
                    	renameTaskFolder();
                    }
                	
                	textInstallDeviceNumber.setText(scanInstallDevice);
                	installDeviceNumber = scanInstallDevice;
                	//not necessary, have done it in refreshFragmentByID
                	//
                	//if (TextUtils.isEmpty(onSiteDate)) {
	                //	SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	            		//ONSITE DATE IS DETERMINED BY THE FIRST SCAN
	            	//	onSiteDate = sDateFormat.format(new java.util.Date());
                	//}
                }
                isNeedToSave = true;
                saveArchives();
                break;
                
                
	        case TAKE_PHOTO:
	        	//renew the photo_files in db.
	        	new Thread(new Runnable(){  
	        	    public void run(){  
	        	        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
	        	        File file = new File(Environment.getExternalStorageDirectory().getPath()+ "/" +
	        					"Hexing" + "/" + "Archives" + "/" + Login.mOperator + "/" + onSiteDate + "/" + folderName);
        	        	
        	        	StringBuilder sb = new StringBuilder();
        	        	
        	        	sb.append("{");
        	        	for (File itefile :file.listFiles(new jpgFileFilter())) {
        	        		sb.append(itefile.getName()).append(",");
        	        	}
        	        	sb.deleteCharAt(sb.length()-1);
        	        	sb.append("}");
        	        	ContentValues values = new ContentValues();
        	        	values.put(TaskProvider.C_PHOTO_FILE, new String(sb));
        	        	getSherlockActivity().getContentResolver().update(TaskProvider.CONTENT_URI, 
        	        			values, 
        	        			TaskProvider.C_ID + "=" + _id, 
        	        			null);
	        	        	  
	        	    }  
	        	}).start();
	        	
	        	
	        	
        		break;
            default:
            	break;
            }  
        }  
    }
	

	private void renameTaskFolder() {
		// TODO Auto-generated method stub
		String oldPath = new String(Environment.getExternalStorageDirectory().getPath()+ "/" +
				"Hexing" + "/" + "Archives" + "/" + Login.mOperator + "/" + onSiteDate + "/" + folderName);
    	if (!Utils.existSDcard()) {
			Toast.makeText(getActivity(), "pls insert SD card",
					Toast.LENGTH_SHORT).show();
		} else if (!Utils.isFolderExists(oldPath)) {
			Toast.makeText(getActivity(), "can not create the folder",
					Toast.LENGTH_SHORT).show();
		} else {
			File saveFolder = new File(oldPath);
			if (true == saveFolder.renameTo(new File(Environment.getExternalStorageDirectory().getPath()+ "/" +
				"Hexing" + "/" + "Archives" + "/" + Login.mOperator + "/" + onSiteDate + "/" + getFolderName()))) {
				folderName = getFolderName();
			} else {
				Toast.makeText(getActivity(), "can not rename the folder, pls rename this task folder manually",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private String getFolderName() {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(taskID)||TextUtils.isEmpty(taskIndex+"")){
			Log.i(this.getClass().getName(), "Should not be here, getFolderName");
			return null;
		} else if (TextUtils.isEmpty(taskDeviceNumber)) {
			return taskID + "-" + taskIndex;
		} else {
			return taskID + "-" + taskIndex + " " + taskDeviceNumber;
		}
	}

	private void refreshFragment(Bundle refreshData) {
		// TODO Auto-generated method stub
		if (null != refreshData) {
			_id = refreshData.getLong(TaskProvider.C_ID);
			taskDeviceNumber = refreshData.getString(TaskProvider.C_TASK_DEVICE_NO);
			taskID = refreshData.getString(TaskProvider.C_TASK_ID);
			taskIndex = refreshData.getInt(TaskProvider.C_TASK_INDEX);
        	taskIssueTime = refreshData.getString(TaskProvider.C_TASK_ISSUE_TIME);
        	operatorName = refreshData.getString(TaskProvider.C_OPERATOR_NAME);
			installDeviceNumber = refreshData.getString(TaskProvider.C_INSTALL_DEVICE_NO);
			removeDeviceNumber = refreshData.getString(TaskProvider.C_REMOVE_DEVICE_NO);
			onSiteDate =  refreshData.getString(TaskProvider.C_ONSITE_DATE);
			device_Type =  refreshData.getString(TaskProvider.C_DEVICE_TYPE);
			mission_Type =  refreshData.getString(TaskProvider.C_TASK_TYPE);
			mission_Status =  refreshData.getString(TaskProvider.C_TASK_STATUS);
			isInstallSpecified = refreshData.getString(TaskProvider.C_INSTALL_ISSPECIFIED);
			isInstallRight = refreshData.getString(TaskProvider.C_INSTALL_ISRIGHT);
			Pod = refreshData.getString(TaskProvider.C_POD);
			Remark = refreshData.getString(TaskProvider.C_TASK_ANOMALY_REASON);
			//new
			folderName = refreshData.getString(TaskProvider.C_FOLDER_NAME);
			signFile = refreshData.getString(TaskProvider.C_SIGNATURE_FILE);
			photoFile = refreshData.getString(TaskProvider.C_PHOTO_FILE);
			isRemoveRight = refreshData.getString(TaskProvider.C_REMOVE_ISRIGHT);
			isRemoveSpecified = refreshData.getString(TaskProvider.C_REMOVE_ISSPECIFIED);
			
			if (_id == -1){
	    		//没有任务则设空，显示hint
	    		textTaskTitle.setText("");
	    		textTaskStatus.setVisibility(View.INVISIBLE);
	    		textTaskStatus1.setText("");
	    		lCustomerName.setVisibility(View.GONE);
	    		lCustomerNumber.setVisibility(View.GONE);
	    		lAddress.setVisibility(View.GONE);
	    		lRemoveDevice.setVisibility(View.GONE);
	    		lInstallDevice.setVisibility(View.GONE);
	    		lTransformer.setVisibility(View.GONE);
	    		lConcentrator.setVisibility(View.GONE);
	    		lLocation.setVisibility(View.GONE);
	    		lOldBanlance.setVisibility(View.GONE);
	    		lOldEnergy.setVisibility(View.GONE);
	    		lNewEnergy.setVisibility(View.GONE);
	    		//lSign.setVisibility(View.GONE);
	    		lRemark.setVisibility(View.GONE);
	    		
	    		gridview.setVisibility(View.GONE);
	    		
	    		workFlowType = INITIAL;
	    	//确定工作流	
	    	}else if ((device_Type.equals(TaskProvider.DEVICE_METER))&&(mission_Type.equals(TaskProvider.MISSION_INSTALL))){
	    		textTaskTitle.setText("Install New Meter");
	    		textTaskStatus.setVisibility(View.VISIBLE);
	    		textTaskStatus1.setText(mission_Status);
	    		lCustomerName.setVisibility(View.VISIBLE);
	    		lCustomerNumber.setVisibility(View.VISIBLE);
	    		lAddress.setVisibility(View.VISIBLE);
	    		lRemoveDevice.setVisibility(View.GONE);
	    		lInstallDevice.setVisibility(View.VISIBLE);
	    		lTransformer.setVisibility(View.VISIBLE);
	    		lConcentrator.setVisibility(View.VISIBLE);
	    		lLocation.setVisibility(View.VISIBLE);
	    		lOldBanlance.setVisibility(View.GONE);
	    		lOldEnergy.setVisibility(View.GONE);
	    		lNewEnergy.setVisibility(View.VISIBLE);
	    		//lSign.setVisibility(View.VISIBLE);
	    		gridview.setVisibility(View.VISIBLE);
	    		workFlowType = METER_INSTALL;
	    	}else if ((device_Type.equals(TaskProvider.DEVICE_METER))&&(mission_Type.equals(TaskProvider.MISSION_REMOVE))){
	    		textTaskTitle.setText("Remove Old Meter");
	    		textTaskStatus.setVisibility(View.VISIBLE);
	    		textTaskStatus1.setText(mission_Status);
	    		lCustomerName.setVisibility(View.VISIBLE);
	    		lCustomerNumber.setVisibility(View.VISIBLE);
	    		lAddress.setVisibility(View.VISIBLE);
	    		lRemoveDevice.setVisibility(View.VISIBLE);
	    		lInstallDevice.setVisibility(View.GONE);
	    		lTransformer.setVisibility(View.VISIBLE);
	    		lConcentrator.setVisibility(View.VISIBLE);
	    		lLocation.setVisibility(View.GONE);
	    		lOldBanlance.setVisibility(View.VISIBLE);
	    		lOldEnergy.setVisibility(View.VISIBLE);
	    		lNewEnergy.setVisibility(View.GONE);
	    		//lSign.setVisibility(View.VISIBLE);
	    		gridview.setVisibility(View.VISIBLE);
	    		workFlowType = METER_REMOVE;
	    	}else if ((device_Type.equals(TaskProvider.DEVICE_METER))&&(mission_Type.equals(TaskProvider.MISSION_REPLACE))&&isInstallSpecified.equals(TaskProvider.TRUE)){
	    		textTaskTitle.setText("Replace Old Meter");
	    		textTaskStatus.setVisibility(View.VISIBLE);
	    		textTaskStatus1.setText(mission_Status);
	    		lCustomerName.setVisibility(View.VISIBLE);
	    		lCustomerNumber.setVisibility(View.VISIBLE);
	    		lAddress.setVisibility(View.VISIBLE);
	    		lRemoveDevice.setVisibility(View.VISIBLE);
	    		lInstallDevice.setVisibility(View.VISIBLE);
	    		lTransformer.setVisibility(View.VISIBLE);
	    		lConcentrator.setVisibility(View.VISIBLE);
	    		lLocation.setVisibility(View.VISIBLE);
	    		lOldBanlance.setVisibility(View.VISIBLE);
	    		lOldEnergy.setVisibility(View.VISIBLE);
	    		lNewEnergy.setVisibility(View.VISIBLE);
	    		//lSign.setVisibility(View.VISIBLE);
	    		gridview.setVisibility(View.VISIBLE);
	    		workFlowType = METER_REPLACE_SPECIFIED;
	    	//no use now
	    	}else if ((device_Type.equals(TaskProvider.DEVICE_METER))&&(mission_Type.equals(TaskProvider.MISSION_REPLACE))){
	    		textTaskTitle.setText("Replace Old Meter");
	    		textTaskStatus.setVisibility(View.VISIBLE);
	    		textTaskStatus1.setText(mission_Status);
	    		lCustomerName.setVisibility(View.VISIBLE);
	    		lCustomerNumber.setVisibility(View.VISIBLE);
	    		lAddress.setVisibility(View.VISIBLE);
	    		lRemoveDevice.setVisibility(View.VISIBLE);
	    		lInstallDevice.setVisibility(View.VISIBLE);
	    		lTransformer.setVisibility(View.VISIBLE);
	    		lConcentrator.setVisibility(View.VISIBLE);
	    		lLocation.setVisibility(View.VISIBLE);
	    		lOldBanlance.setVisibility(View.VISIBLE);
	    		lOldEnergy.setVisibility(View.VISIBLE);
	    		lNewEnergy.setVisibility(View.VISIBLE);
	    		//lSign.setVisibility(View.VISIBLE);
	    		gridview.setVisibility(View.VISIBLE);
	    		workFlowType = METER_REPLACE_NOTSPECIFIED;
	    	}else if ((device_Type.equals(TaskProvider.DEVICE_CONCENTRATOR))&&(mission_Type.equals(TaskProvider.MISSION_INSTALL))){
	    		textTaskTitle.setText("Install New Concentrator");
	    		textTaskStatus.setVisibility(View.VISIBLE);
	    		textTaskStatus1.setText(mission_Status);
	    		lCustomerName.setVisibility(View.GONE);
	    		lCustomerNumber.setVisibility(View.GONE);
	    		lAddress.setVisibility(View.VISIBLE);
	    		lRemoveDevice.setVisibility(View.GONE);
	    		lInstallDevice.setVisibility(View.VISIBLE);
	    		lTransformer.setVisibility(View.VISIBLE);
	    		lConcentrator.setVisibility(View.GONE);
	    		lLocation.setVisibility(View.VISIBLE);
	    		lOldBanlance.setVisibility(View.GONE);
	    		lOldEnergy.setVisibility(View.GONE);
	    		lNewEnergy.setVisibility(View.GONE);
	    		//lSign.setVisibility(View.GONE);
	    		gridview.setVisibility(View.VISIBLE);
	    		workFlowType = CONCENTRATOR_INSTALL;
	    	}else if ((device_Type.equals(TaskProvider.DEVICE_CONCENTRATOR))&&(mission_Type.equals(TaskProvider.MISSION_REMOVE))){
	    		textTaskTitle.setText("Remove Old Concentrator");
	    		textTaskStatus.setVisibility(View.VISIBLE);
	    		textTaskStatus1.setText(mission_Status);
	    		lCustomerName.setVisibility(View.GONE);
	    		lCustomerNumber.setVisibility(View.GONE);
	    		lAddress.setVisibility(View.VISIBLE);
	    		lRemoveDevice.setVisibility(View.VISIBLE);
	    		lInstallDevice.setVisibility(View.GONE);
	    		lTransformer.setVisibility(View.VISIBLE);
	    		lConcentrator.setVisibility(View.GONE);
	    		lLocation.setVisibility(View.GONE);
	    		lOldBanlance.setVisibility(View.GONE);
	    		lOldEnergy.setVisibility(View.GONE);
	    		lNewEnergy.setVisibility(View.GONE);
	    		//lSign.setVisibility(View.GONE);
	    		gridview.setVisibility(View.VISIBLE);
	    		workFlowType = CONCENTRATOR_REMOVE;
	    	}else if ((device_Type.equals(TaskProvider.DEVICE_CONCENTRATOR))&&(mission_Type.equals(TaskProvider.MISSION_REPLACE)&&isInstallSpecified.equals(TaskProvider.TRUE))){
	    		textTaskTitle.setText("Replace Old Concentrator");
	    		textTaskStatus.setVisibility(View.VISIBLE);
	    		textTaskStatus1.setText(mission_Status);
	    		lCustomerName.setVisibility(View.GONE);
	    		lCustomerNumber.setVisibility(View.GONE);
	    		lAddress.setVisibility(View.VISIBLE);
	    		lRemoveDevice.setVisibility(View.VISIBLE);
	    		lInstallDevice.setVisibility(View.VISIBLE);
	    		lTransformer.setVisibility(View.GONE);
	    		lConcentrator.setVisibility(View.GONE);
	    		lLocation.setVisibility(View.VISIBLE);
	    		lOldBanlance.setVisibility(View.GONE);
	    		lOldEnergy.setVisibility(View.GONE);
	    		lNewEnergy.setVisibility(View.GONE);
	    		//lSign.setVisibility(View.GONE);
	    		gridview.setVisibility(View.VISIBLE);
	    		workFlowType = CONCENTRATOR_REPLACE_SPECIFIED;
	    	//no use now
	    	}else if ((device_Type.equals(TaskProvider.DEVICE_CONCENTRATOR))&&(mission_Type.equals(TaskProvider.MISSION_REPLACE))){
	    		textTaskTitle.setText("Replace Old Concentrator");
	    		textTaskStatus.setVisibility(View.VISIBLE);
	    		textTaskStatus1.setText(mission_Status);
	    		lCustomerName.setVisibility(View.GONE);
	    		lCustomerNumber.setVisibility(View.GONE);
	    		lAddress.setVisibility(View.VISIBLE);
	    		lRemoveDevice.setVisibility(View.VISIBLE);
	    		lInstallDevice.setVisibility(View.VISIBLE);
	    		lTransformer.setVisibility(View.GONE);
	    		lConcentrator.setVisibility(View.GONE);
	    		lLocation.setVisibility(View.VISIBLE);
	    		lOldBanlance.setVisibility(View.GONE);
	    		lOldEnergy.setVisibility(View.GONE);
	    		lNewEnergy.setVisibility(View.GONE);
	    		//lSign.setVisibility(View.GONE);
	    		gridview.setVisibility(View.VISIBLE);
	    		workFlowType = CONCENTRATOR_REPLACE_NOTSPECIFIED;
	    	}
					
        	
        	//初始化时和已完成任务不可编辑
        	if  (workFlowType.equals(INITIAL) ||((mission_Status != null)&&((mission_Status.equals(TaskProvider.STATUS_FAILED))||(mission_Status.equals(TaskProvider.STATUS_SUCCEED))))) {
        		etCustomerName.setFocusableInTouchMode(false);
            	etCustomerNumber.setFocusableInTouchMode(false);
            	etAddress.setFocusableInTouchMode(false);
            	etTransformerName.setFocusableInTouchMode(false);
            	etConcentratorNumber.setFocusableInTouchMode(false);
            	etOldPrepaidBalance.setFocusableInTouchMode(false);
            	etOldEndEnergy.setFocusableInTouchMode(false);
            	etNewStartEnergy.setFocusableInTouchMode(false);
            	lLocation.setFocusableInTouchMode(false);
            	lSign.setFocusableInTouchMode(false);
            	scanInstallMeter.setClickable(false);
            	scanRemoveMeter.setClickable(false);
            	etCustomerName.clearFocus();
            	etCustomerNumber.clearFocus();
            	etAddress.clearFocus();
            	etTransformerName.clearFocus();
            	etConcentratorNumber.clearFocus();
            	etOldPrepaidBalance.clearFocus();
            	etOldEndEnergy.clearFocus();
            	etNewStartEnergy.clearFocus();
            	lLocation.clearFocus();
            	lSign.clearFocus();
            	lButton.setVisibility(View.GONE);
            	textGPSStatus.setVisibility(View.GONE);
        	} else {
        		etCustomerName.setFocusableInTouchMode(true);        		
            	etCustomerNumber.setFocusableInTouchMode(true);            	
            	etAddress.setFocusableInTouchMode(true);
            	etTransformerName.setFocusableInTouchMode(true);
            	etConcentratorNumber.setFocusableInTouchMode(true);
            	etOldPrepaidBalance.setFocusableInTouchMode(true);
            	etOldEndEnergy.setFocusableInTouchMode(true);
            	etNewStartEnergy.setFocusableInTouchMode(true);
            	lLocation.setFocusableInTouchMode(true);
            	scanInstallMeter.setClickable(true);
            	scanRemoveMeter.setClickable(true);
            	lButton.setVisibility(View.VISIBLE);
            	textGPSStatus.setVisibility(View.VISIBLE);
        	}
        	
        	
        	//失败任务显示原因
        	if ((mission_Status != null)&&(mission_Status.equals(TaskProvider.STATUS_FAILED))) {
        		lRemark.setVisibility(View.VISIBLE);
        	} else {
        		lRemark.setVisibility(View.GONE);
        	}
        	//成功的表计任务显示签名
        	if ((mission_Status != null)&&(mission_Status.equals(TaskProvider.STATUS_SUCCEED))) {
        		lSign.setVisibility(View.VISIBLE);
        	} else {
        		lSign.setVisibility(View.GONE);
        	}
        	//替换表计根据任务显示新装表计是否正确
        	if ((isInstallSpecified != null)&&(isInstallSpecified.equals(TaskProvider.TRUE))) {
        		textIsInstallRight.setVisibility(View.VISIBLE);
        		if (isInstallRight.equals(TaskProvider.TRUE)) {
        			textIsInstallRight.setText("Confirmed");
        			textIsInstallRight.setTextColor(0xff00bb00);
        		} else {
        			textIsInstallRight.setText("Not Confirmed");
        			textIsInstallRight.setTextColor(0xffff0000);
        		}
        	} else {
        		textIsInstallRight.setVisibility(View.GONE);
        	}
        	
        	//new
        	if ((isRemoveSpecified != null)&&(isRemoveSpecified.equals(TaskProvider.TRUE))) {
        		textIsRemoveRight.setVisibility(View.VISIBLE);
        		if (isRemoveRight.equals(TaskProvider.TRUE)) {
        			textIsRemoveRight.setText("Confirmed");
        			textIsRemoveRight.setTextColor(0xff00bb00);
        		} else {
        			textIsRemoveRight.setText("Not Confirmed");
        			textIsRemoveRight.setTextColor(0xffff0000);
        		}
        	} else {
        		textIsRemoveRight.setVisibility(View.GONE);
        	}
        	
        	
        	
        	etCustomerName.setText(refreshData.getString(TaskProvider.C_CUSTOMER_NAME));
        	etCustomerNumber.setText(refreshData.getString(TaskProvider.C_CUSTOMER_NO));
        	etAddress.setText(refreshData.getString(TaskProvider.C_ADDRESS));
        	etTransformerName.setText(refreshData.getString(TaskProvider.C_TRANSFORMA_NAME));
        	etConcentratorNumber.setText(refreshData.getString(TaskProvider.C_UPLINK_CONCENTRATOR));
        	etOldPrepaidBalance.setText(refreshData.getString(TaskProvider.C_PREPAID_OLD_BALANCE));
        	etOldEndEnergy.setText(refreshData.getString(TaskProvider.C_OLD_END_ENERGY));
        	etNewStartEnergy.setText(refreshData.getString(TaskProvider.C_NEW_START_ENERGY));
        	
        	textRemoveDeviceNumber.setText(refreshData.getString(TaskProvider.C_REMOVE_DEVICE_NO));
        	textInstallDeviceNumber.setText(refreshData.getString(TaskProvider.C_INSTALL_DEVICE_NO));
            textLongitude.setText(refreshData.getString(TaskProvider.C_LONGITUDE));
            textLatitude.setText(refreshData.getString(TaskProvider.C_LATITUDE));
            textRemark.setText(Remark);
		} else {
			textTaskStatus.setVisibility(View.GONE);
			lCustomerName.setVisibility(View.GONE);
    		lCustomerNumber.setVisibility(View.GONE);
    		lAddress.setVisibility(View.GONE);
    		lRemoveDevice.setVisibility(View.GONE);
    		lInstallDevice.setVisibility(View.GONE);
    		lTransformer.setVisibility(View.GONE);
    		lConcentrator.setVisibility(View.GONE);
    		lLocation.setVisibility(View.GONE);
    		lOldBanlance.setVisibility(View.GONE);
    		lOldEnergy.setVisibility(View.GONE);
    		lNewEnergy.setVisibility(View.GONE);
    		lSign.setVisibility(View.GONE);
    		gridview.setVisibility(View.GONE);
	    	lRemark.setVisibility(View.GONE);
	    	lButton.setVisibility(View.GONE);
    		workFlowType = INITIAL;
		}
		
		if ((null == mission_Status)||(mission_Status.equals(""))) {
			textTaskStatus1.setText("");
		} else if (mission_Status.equals(TaskProvider.STATUS_SUCCEED)) {
			textTaskStatus1.setText("Succeed");
			textTaskStatus1.setTextColor(0xff00bb00);
		} else if (mission_Status.equals(TaskProvider.STATUS_FAILED)) {
			textTaskStatus1.setText("Failed");
			textTaskStatus1.setTextColor(0xffff0000);
		} else if (mission_Status.equals(TaskProvider.STATUS_PENDING)) {
			textTaskStatus1.setText("Pending");
			textTaskStatus1.setTextColor(0xff000000);
		} else if (mission_Status.equals(TaskProvider.STATUS_INITIAL)) {
			textTaskStatus1.setText("Initial");
			textTaskStatus1.setTextColor(0xff000000);
		} else {
			textTaskStatus1.setText("Status Abnormal");
			textTaskStatus1.setTextColor(0xffff0000);
		}
		
	}
	public void refreshFragmentByID(long _id) {
		// TODO Auto-generated method stub
		//dbHelper = new TaskProvider(getSherlockActivity().getApplicationContext());
    	
    	// Open the database for writing
        //db = dbHelper.getReadableDatabase();
        
        Bundle Device = new Bundle();
        
        Cursor tempCursor;
        tempCursor = getSherlockActivity().getContentResolver().query(TaskProvider.CONTENT_URI, 
        		null, 
        		TaskProvider.C_ID + " = '" + _id +"'", 
        		null, 
        		null);
        if(0 == tempCursor.getCount()) {
        	Device.putLong(TaskProvider.C_ID, -1);
        	
        } else if (1 == tempCursor.getCount()) {
        	tempCursor.moveToFirst();
        	Device.putLong(TaskProvider.C_ID, tempCursor.getLong(tempCursor.getColumnIndex(TaskProvider.C_ID)));
        	Device.putString(TaskProvider.C_TASK_ID, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_TASK_ID)));
        	Device.putInt(TaskProvider.C_TASK_INDEX, tempCursor.getInt(tempCursor.getColumnIndex(TaskProvider.C_TASK_INDEX)));
        	Device.putString(TaskProvider.C_POD, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_POD)));
        	Device.putString(TaskProvider.C_TASK_ISSUE_TIME, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_TASK_ISSUE_TIME)));
        	Device.putString(TaskProvider.C_OPERATOR_NAME, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_OPERATOR_NAME)));
        	Device.putString(TaskProvider.C_TASK_DEVICE_NO, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_TASK_DEVICE_NO)));
        	Device.putString(TaskProvider.C_INSTALL_DEVICE_NO, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_INSTALL_DEVICE_NO)));
        	Device.putString(TaskProvider.C_DEVICE_TYPE, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_DEVICE_TYPE)));
        	Device.putString(TaskProvider.C_TASK_TYPE, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_TASK_TYPE)));
        	Device.putString(TaskProvider.C_INSTALL_ISSPECIFIED, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_INSTALL_ISSPECIFIED)));
        	Device.putString(TaskProvider.C_INSTALL_ISRIGHT, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_INSTALL_ISRIGHT)));
        	Device.putString(TaskProvider.C_CUSTOMER_NAME, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_CUSTOMER_NAME)));
        	Device.putString(TaskProvider.C_CUSTOMER_NO, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_CUSTOMER_NO)));
        	Device.putString(TaskProvider.C_ADDRESS, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_ADDRESS)));
        	Device.putString(TaskProvider.C_TRANSFORMA_NAME, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_TRANSFORMA_NAME)));
        	Device.putString(TaskProvider.C_UPLINK_CONCENTRATOR, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_UPLINK_CONCENTRATOR)));
        	Device.putString(TaskProvider.C_PREPAID_OLD_BALANCE, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_PREPAID_OLD_BALANCE)));
        	Device.putString(TaskProvider.C_OLD_END_ENERGY, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_OLD_END_ENERGY)));
        	Device.putString(TaskProvider.C_NEW_START_ENERGY, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_NEW_START_ENERGY)));
        	Device.putString(TaskProvider.C_REMOVE_DEVICE_NO, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_REMOVE_DEVICE_NO)));
        	Device.putString(TaskProvider.C_LONGITUDE, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_LONGITUDE)));
        	Device.putString(TaskProvider.C_LATITUDE, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_LATITUDE)));
        	Device.putString(TaskProvider.C_TASK_ANOMALY_REASON, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_TASK_ANOMALY_REASON)));
        	
        	Device.putString(TaskProvider.C_FOLDER_NAME, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_FOLDER_NAME)));
        	Device.putString(TaskProvider.C_SIGNATURE_FILE, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_SIGNATURE_FILE)));
        	Device.putString(TaskProvider.C_PHOTO_FILE, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_PHOTO_FILE)));
        	Device.putString(TaskProvider.C_REMOVE_ISSPECIFIED, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_REMOVE_ISSPECIFIED)));
        	Device.putString(TaskProvider.C_REMOVE_ISRIGHT, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_REMOVE_ISRIGHT)));
        	
        	
        	
        	if (tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_TASK_STATUS)).equals(TaskProvider.STATUS_INITIAL)) {
        		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        		//ONSITE DATE IS DETERMINED BY THE FIRST SCAN
        		Device.putString(TaskProvider.C_ONSITE_DATE, sDateFormat.format(new java.util.Date()));
        		Device.putString(TaskProvider.C_TASK_STATUS, TaskProvider.STATUS_PENDING);
        		ContentValues values = new ContentValues();
        		values.put(TaskProvider.C_ONSITE_DATE, sDateFormat.format(new java.util.Date()));
        		values.put(TaskProvider.C_TASK_STATUS, TaskProvider.STATUS_PENDING);
        		//db.update(TaskProvider.TABLE, values, TaskProvider.C_ID + "=" + cDevice.getLong(cDevice.getColumnIndex(TaskProvider.C_ID)), null);
        		getSherlockActivity().getContentResolver().update(TaskProvider.CONTENT_URI, values, TaskProvider.C_ID + "=" + tempCursor.getLong(tempCursor.getColumnIndex(TaskProvider.C_ID)), null);
        	} else {
        		Device.putString(TaskProvider.C_ONSITE_DATE, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_ONSITE_DATE)));
        		Device.putString(TaskProvider.C_TASK_STATUS, tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_TASK_STATUS)));
        	}
        } else {
        	//impossible to be here
        	Log.i(this.getClass().getName(), "Should not be here.");
        }
        tempCursor.close();
        //db.close();
        //dbHelper.close();
        refreshFragment(Device);
        
	}
	public void initSignature(long id) {
		// TODO Auto-generated method stub
		Cursor tempCursor = getSherlockActivity().getContentResolver().query(TaskProvider.CONTENT_URI, 
				new String[] {TaskProvider.C_ID, TaskProvider.C_ONSITE_DATE, TaskProvider.C_SIGNATURE_FILE} , 
				TaskProvider.C_ID + "=" + id, 
				null, 
				null);
		if ((null != tempCursor)&&(tempCursor.getCount() == 1)){
			tempCursor.moveToFirst();
			String editDate = tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_ONSITE_DATE));
			String signFileName = tempCursor.getString(tempCursor.getColumnIndex(TaskProvider.C_SIGNATURE_FILE));
			if (new File(Environment.getExternalStorageDirectory().getPath()+ "/" +
					"Hexing" + "/" + "Archives" + "/" + Login.mOperator + "/"+ editDate + "/" + folderName + File.separator +
					signFileName).exists()) {
	        	ivSign.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+ "/" +
					"Hexing" + "/" + "Archives" + "/" + Login.mOperator + "/"+ editDate + "/" + folderName + File.separator +
					signFileName));
	        } else {
	        	ivSign.setImageResource(R.drawable.signuphere);
	        }
		}
		if (null != tempCursor) {
			tempCursor.close();
		}
	}
	public void initGridView(long id) {
		
		initPictures(folderName,onSiteDate);
		
//		final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
		int height = context.getResources().getDisplayMetrics().heightPixels*1/6;
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gridview.getLayoutParams(); // 取控件mGrid当前的布局参数
//		linearParams.height = (int) (60*((int)((imageFiles.length-1)/4)+1)*scale+0.5);// 当控件的高强制设成75象素
		linearParams.height = (int) (height*((int)((imageFiles.length-1)/4)+1)+height/4);// 当控件的高强制设成75象素
		gridview.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
		
		gridview.setAdapter(new ImageAdapter(context)); 
        gridview.setOnItemClickListener(new OnItemClickListener(){//监听事件  
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id)   
        	{	
        		if (position == imageFiles.length - 1){
        			startCamera(null);
        		} else {
        			Intent intent = new Intent(context,ViewPictures.class);
                	
        	        intent.putExtra(TaskProvider.C_FOLDER_NAME, folderName);
        	        intent.putExtra(TaskProvider.C_ONSITE_DATE, onSiteDate);
        	        intent.putExtra(PICTURE_TIME, imageFiles[position].getName());
        	        startActivity(intent);
        			Toast.makeText(context, imageFiles[position].getName(),Toast.LENGTH_SHORT).show();//显示信息;
        		}
        		  
        	}  
        }); 
        gridview.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position < imageFiles.length - 1){
        			Intent intent = new Intent (context,DelArchive.class);
                	
        	        intent.putExtra(TaskProvider.C_FOLDER_NAME, folderName);
        	        intent.putExtra(TaskProvider.C_ONSITE_DATE, onSiteDate);
        	        intent.putExtra(PICTURE_TIME, imageFiles[position].getName());
        	        startActivity(intent);
        			Toast.makeText(context, imageFiles[position].getName(),Toast.LENGTH_SHORT).show();//显示信息;
        		}
				return false;
			}
        	
        });
	}
	private void initPictures(String folderName, String date) {
		// TODO Auto-generated method stub
		
			
		if ((!TextUtils.isEmpty(folderName))&&(!TextUtils.isEmpty(date))) {
			String path = Environment.getExternalStorageDirectory().getPath()+ "/" +
					"Hexing" + "/" + "Archives" + "/" + Login.mOperator + "/" + date + "/" + folderName;
			
			File file = new File(path);
			
			if (file.exists()) { // 判断文件是否存在
				if (file.isFile()) { // 判断是否是文件
					file.delete(); // delete()方法 你应该知道 是删除的意思;
				} else if (file.isDirectory()) { // 否则如果它是一个目录
					int i = 0;
					imageFiles = new File[file.listFiles(new jpgFileFilter()).length + 1];
					for (File itefile :file.listFiles(new jpgFileFilter())) {
						imageFiles[i] = itefile;
						i++;
					}
					
					imageFiles[file.listFiles(new jpgFileFilter()).length] = null;
				}
			} else {
				imageFiles = new File[1];
			}
		} else {
			imageFiles = new File[1];
		}
	}
	
	public void startCamera (View view){
    	// Do something in response to button
        SimpleDateFormat    sTimeFormat    =   new    SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String    time    =    sTimeFormat.format(new    java.util.Date());
        if (null == onSiteDate){
        	SimpleDateFormat    sDateFormat    =   new    SimpleDateFormat("yyyy-MM-dd");
			onSiteDate    =    sDateFormat.format(new    java.util.Date());
        }
		if (!TextUtils.isEmpty(getFolderName())) {
			Intent openCameraIntent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			openCameraIntent.addCategory(Intent.CATEGORY_DEFAULT);
	    	
			String strFolder = Environment.getExternalStorageDirectory().getPath()+ "/" +
					"Hexing" + "/" + "Archives" + "/" + Login.mOperator + "/"+ onSiteDate + "/" + getFolderName();
			if (!Utils.existSDcard()) {
				Toast.makeText(context, "pls insert SD card",
						Toast.LENGTH_SHORT).show();
			} else if (!Utils.isFolderExists(strFolder)) {
				Toast.makeText(context, "can not creat the folder",
						Toast.LENGTH_SHORT).show();
			} else {
				File fImage = new File(strFolder + "/" + time +".jpg");
				Uri uri=Uri.fromFile(fImage);
				
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				
		        startActivityForResult(openCameraIntent, TAKE_PHOTO);  
			}
		}else {
			Toast.makeText(context, "pls input device number first",
					Toast.LENGTH_SHORT).show();
		}
    	
    }
	
	private void createFile() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				ByteArrayOutputStream baos = null;
				String _path = null;
				String signFileName = new String(getFolderName()+".png");
				if (null == onSiteDate){
		        	SimpleDateFormat    sDateFormat    =   new    SimpleDateFormat("yyyy-MM-dd");
					onSiteDate    =    sDateFormat.format(new    java.util.Date());
		        }
				try {
					
					String sign_dir = Environment.getExternalStorageDirectory().getPath()+ "/" +
							"Hexing" + "/" + "Archives" + "/" + Login.mOperator + "/"+ onSiteDate + "/" + getFolderName() + File.separator;
					_path = sign_dir + signFileName;
					if (!Utils.isFolderExists(sign_dir)) {
						Toast.makeText(getActivity(), "save signature failed",
								Toast.LENGTH_SHORT).show();
					}
					baos = new ByteArrayOutputStream();
					mSignBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					byte[] photoBytes = baos.toByteArray();
					if (photoBytes != null) {
						new FileOutputStream(new File(_path)).write(photoBytes);
					}
					ContentValues values = new ContentValues();
					values.put(TaskProvider.C_SIGNATURE_FILE, signFileName);
					getSherlockActivity().getContentResolver().update(TaskProvider.CONTENT_URI, 
							values, 
							TaskProvider.C_ID + "=" + _id, 
							null);
			
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (baos != null)
							baos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public String getEditMeterNumber(){
		return taskDeviceNumber;
	}
	public String getEditDate(){
		return onSiteDate;
	}
	
	public void setLocation(Location location){
		LocationManager lm = (LocationManager) getSherlockActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		if (!lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
        	textGPSStatus.setText("Pls open GPS");
        } else if (location != null) {
        	textLongitude.setText(location.getLongitude()+"");
            textLatitude.setText(location.getLatitude()+"");
            textGPSStatus.setText("Obtained");
        } else {
        	textGPSStatus.setText("Obtaining...");        	
        }
		
	}
	/*
	public void setEditMeterNumber(String editMeterNumber){
		if ((editMeterNumber!=null)&&(!editMeterNumber.equals(""))){
			this.taskDeviceNumber = editMeterNumber;
			scanMeter.setVisibility(View.INVISIBLE);
		} else {
			this.taskDeviceNumber = null;
			scanMeter.setVisibility(View.VISIBLE);
		}
	}
	public void setEditDate(String editDate){
		this.onSiteDate = editDate;
	}
	public void setCustomerNumber(String customerNumber) {
		textinputCustomerNumber.setText(customerNumber);
	}
	public void setInputAddress(String address) {
		textinputAddress.setText(address);
	}
    public void setEnergyActive(String energy) {
    	textinputMeterEnergyActive.setText(energy);
    }
    public void setSignature(String pathName){
    	if (pathName == null) {
    		ivSign.setImageResource(R.drawable.signuphere);
    		return;
    	}
    	File file = new File(pathName);
    	if ((file != null)&& file.exists()){
    		ivSign.setImageBitmap(BitmapFactory.decodeFile(pathName));
    	} else {
    		ivSign.setImageResource(R.drawable.signuphere);
    	}
    }
    */
	
	private class ImageAdapter extends BaseAdapter{  
        private Context mContext;  
  
        public ImageAdapter(Context context) {  
            this.mContext=context;  
        }  
  
        @Override  
        public int getCount() {  
            return imageFiles.length;  
        }  
  
        @Override  
        public Object getItem(int position) {  
            return imageFiles[position];  
        }  
  
        @Override  
        public long getItemId(int position) {  
            // TODO Auto-generated method stub  
            return 0;  
        }  
  
        @Override  
        public View getView(int position, View convertView, ViewGroup parent) {  
            //定义一个ImageView,显示在GridView里  
        	int width = context.getResources().getDisplayMetrics().widthPixels*1/6;
        	int height = context.getResources().getDisplayMetrics().heightPixels*1/6;
            ImageView imageView;  
            if(convertView==null){  
                imageView=new ImageView(mContext);  
                imageView.setLayoutParams(new GridView.LayoutParams(width, height));  
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);  
                imageView.setPadding(8, 8, 8, 8);  
            }else{  
                imageView = (ImageView) convertView;  
            }
            if (position == imageFiles.length -1) {
            	imageView.setImageResource(R.drawable.add_pic);
            } else {
            	Bitmap bitmap= Utils.getImageThumbnail(imageFiles[position].getPath(),width,height);
            	imageView.setImageBitmap(bitmap);
            }
            return imageView;  
        }  
          
  
          
    }
	/*
	public void updateArticleView(Bundle bundle) {
		// TODO Auto-generated method stub
		this.taskDeviceNumber=  bundle.getString("result");
		textinputMeterNumber.setText(taskDeviceNumber);
		scanMeter.setVisibility(View.GONE);
	}*/
	
	public void saveArchives(){
		
		if (!isNeedToSave) {
			Log.i("EditFragment", "isNeedToSave = false");
			return;
		}
			
		
		SimpleDateFormat    sDateFormat    =   new    SimpleDateFormat("yyyy-MM-dd");
		String    date    =    sDateFormat.format(new    java.util.Date());
		
		
        
        if (TextUtils.isEmpty(taskID)||taskIndex == -1) {
        	
        	Log.i(this.getClass().getName(), "taskID or taskIndex invalid");
        	return;
        }
        
        if (null != onSiteDate){
        	date = onSiteDate;
        }
        //失败的任务可能没有任何表号
        //if (taskDeviceNumber.equals("")&&(mission_Status == TaskProvider.STATUS_FAILED)) {
		//	taskDeviceNumber = taskID + "-" + taskIndex;
		//} 
    	
        
        final ContentValues values = new ContentValues();
        values.put(TaskProvider.C_TASK_ID, taskID);
        values.put(TaskProvider.C_TASK_ISSUE_TIME, taskIssueTime);
        values.put(TaskProvider.C_TASK_INDEX, taskIndex);
        values.put(TaskProvider.C_OPERATOR_NAME, Login.mOperator);
        values.put(TaskProvider.C_DEVICE_TYPE, device_Type);
        values.put(TaskProvider.C_TASK_TYPE, mission_Type);
        values.put(TaskProvider.C_TASK_STATUS, mission_Status);
        values.put(TaskProvider.C_INSTALL_DEVICE_NO, installDeviceNumber);
        values.put(TaskProvider.C_REMOVE_DEVICE_NO, removeDeviceNumber);
        values.put(TaskProvider.C_TASK_ANOMALY_REASON, Remark);
        values.put(TaskProvider.C_CUSTOMER_NAME, etCustomerName.getText().toString());
        values.put(TaskProvider.C_CUSTOMER_NO, etCustomerNumber.getText().toString());
        values.put(TaskProvider.C_ADDRESS, etAddress.getText().toString());
        values.put(TaskProvider.C_TRANSFORMA_NAME, etTransformerName.getText().toString());
        values.put(TaskProvider.C_UPLINK_CONCENTRATOR, etConcentratorNumber.getText().toString());
        values.put(TaskProvider.C_PREPAID_OLD_BALANCE, etOldPrepaidBalance.getText().toString());
        values.put(TaskProvider.C_OLD_END_ENERGY, etOldEndEnergy.getText().toString());
        values.put(TaskProvider.C_LONGITUDE, textLongitude.getText().toString());
        values.put(TaskProvider.C_LATITUDE, textLatitude.getText().toString());
        values.put(TaskProvider.C_NEW_START_ENERGY, etNewStartEnergy.getText().toString());
        values.put(TaskProvider.C_POD, Pod);
        
        values.put(TaskProvider.C_ONSITE_DATE, onSiteDate);
        values.put(TaskProvider.C_TASK_DEVICE_NO, taskDeviceNumber);
        values.put(TaskProvider.C_INSTALL_ISSPECIFIED, isInstallSpecified);
        values.put(TaskProvider.C_INSTALL_ISRIGHT, isInstallRight);
        
        values.put(TaskProvider.C_FOLDER_NAME, folderName);
        
        //new Thread(new Runnable() {
			//@Override
			//public void run() {
				//dbHelper = new TaskProvider(getSherlockActivity().getApplicationContext());
		    	
		    	// Open the database for writing
		        //db = dbHelper.getWritableDatabase();
				// TODO Auto-generated method stub
				//if (1 == db.update(TaskProvider.TABLE, values, TaskProvider.C_ID + "=" + _id, null)) {
		if (1 == getSherlockActivity().getContentResolver().update(TaskProvider.CONTENT_URI, values, TaskProvider.C_ID + "=" + _id, null)) {	
	    	Log.i("EditFragment", "Update database succeed");
	    	
	    	String strFolder = Environment.getExternalStorageDirectory().getPath()+ "/" +
					"Hexing" + "/" + "Archives" + "/" + Login.mOperator + "/" + date + "/" + getFolderName();
	    	
	    	
			String filepath = strFolder + "/" + getFolderName() + ".txt";
			
			if ((mission_Status == TaskProvider.STATUS_FAILED)||(mission_Status ==TaskProvider.STATUS_SUCCEED)) {
				if (!Utils.existSDcard()) {
					Toast.makeText(getActivity(), "pls insert SD card",
							Toast.LENGTH_SHORT).show();
				} else if (!Utils.isFolderExists(strFolder)) {
					Toast.makeText(getActivity(), "can not creat the folder",
							Toast.LENGTH_SHORT).show();
				} else {
					try {
						
						StringBuffer sb = new StringBuffer();
						
						//sb.append('[');
						sb.append("{" + "\"" + TaskProvider.C_ID + "\":\"" + _id + "\"," +
								"\"" + TaskProvider.C_TASK_ID + "\":\"" + taskID + "\"," +
								"\"" + TaskProvider.C_TASK_INDEX + "\":\"" + taskIndex + "\"," +
								"\"" + TaskProvider.C_POD + "\":\"" + Pod + "\"," +
								"\"" + TaskProvider.C_TASK_ISSUE_TIME + "\":\"" + taskIssueTime + "\"," +
								"\"" + TaskProvider.C_CUSTOMER_NAME + "\":\"" + etCustomerName.getText().toString() + "\"," +
								"\"" + TaskProvider.C_CUSTOMER_NO + "\":\"" + etCustomerNumber.getText().toString() + "\"," +
								"\"" + TaskProvider.C_TASK_TYPE + "\":\"" + mission_Type + "\"," +
								"\"" + TaskProvider.C_DEVICE_TYPE + "\":\"" + device_Type + "\"," +
								"\"" + TaskProvider.C_TASK_STATUS + "\":\"" + mission_Status + "\"," +
								"\"" + TaskProvider.C_OPERATOR_NAME + "\":\"" + Login.mOperator + "\"," +
								"\"" + TaskProvider.C_INSTALL_DEVICE_NO + "\":\"" + installDeviceNumber + "\"," +
								"\"" + TaskProvider.C_ADDRESS + "\":\"" + etAddress.getText().toString() + "\"," +
								"\"" + TaskProvider.C_LONGITUDE + "\":\"" + textLongitude.getText().toString() + "\"," +
								"\"" + TaskProvider.C_LATITUDE + "\":\"" + textLatitude.getText().toString() + "\"," +
								"\"" + TaskProvider.C_NEW_START_ENERGY + "\":\"" + etNewStartEnergy.getText().toString() + "\"," +
								"\"" + TaskProvider.C_REMOVE_DEVICE_NO + "\":\"" + (TextUtils.isEmpty(removeDeviceNumber)?"":removeDeviceNumber)+ "\"," +
								"\"" + TaskProvider.C_UPLINK_CONCENTRATOR + "\":\"" + etConcentratorNumber.getText().toString() + "\"," +
								"\"" + TaskProvider.C_OLD_END_ENERGY + "\":\"" + etOldEndEnergy.getText().toString() + "\"," +
								"\"" + TaskProvider.C_PREPAID_OLD_BALANCE + "\":\"" + etOldPrepaidBalance.getText().toString() + "\"," +
								"\"" + TaskProvider.C_TRANSFORMA_NAME + "\":\"" + etTransformerName.getText().toString() + "\"," +
								"\"" + TaskProvider.C_TASK_ANOMALY_REASON + "\":\"" + Remark + "\"" +
								"}");
						//sb.append(']');
						
						
						FileOutputStream meterArchive = new FileOutputStream(filepath);
						meterArchive.write(new String(sb).getBytes());
						meterArchive.close();
						
						Toast.makeText(getActivity(), "Output file succeed",
								Toast.LENGTH_SHORT).show();
					} catch (IOException e) {
						Toast.makeText(getActivity(), "Output file failed",
								Toast.LENGTH_SHORT).show();
					}
				}
			} else {
		    	Log.i("EditFragment", "No need to save in SD card");
		    }
			isNeedToSave = false;
		}else {
			Log.i("EditFragment", "Update database failed");
		}
		
	}

	
}

class jpgFileFilter implements FileFilter{  
	  
    @Override  
    public boolean accept(File pathname) {  
        String filename = pathname.getName().toLowerCase();  
        if(filename.contains(".jpg")){  
            return true;  
        }else{  
            return false;  
        }  
    }  
} 
class txtFileFilter implements FileFilter{  
	  
    @Override  
    public boolean accept(File pathname) {  
        String filename = pathname.getName().toLowerCase();  
        if(filename.contains(".txt")){  
            return true;  
        }else{  
            return false;  
        }  
    }  
} 
class cursorHolder {
	long C_ID;
	String taskID;
	String deviceType;
	String taskType;
	String taskStatus;
	String taskDeviceNo;
}
class taskPath {
	String taskID;
	int	   taskIndex;
	String taskDeviceNumber;
}

