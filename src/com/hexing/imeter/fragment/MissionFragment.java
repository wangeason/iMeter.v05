package com.hexing.imeter.fragment;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ResourceCursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.hexing.imeter.Login;
import com.hexing.imeter.R;
import com.hexing.imeter.service.DownloadMissionIntentService;
import com.hexing.imeter.utils.TaskProvider;

public class MissionFragment extends SherlockFragment{
	protected static final int INIT_DATABASE_SUCCESS = 0;
	protected static final int UPLOAD_STATUS_RENEW = 1;
	protected static final int START_DOWNLOAD_TASKS = 2;
	
	private android.support.v4.widget.CursorAdapter spinnerAdapter = null;
	private android.support.v4.widget.CursorAdapter listAdapter = null;
	
	private Spinner spTaskID;
	private ListView lsMeterNumber;
	public ImageButton btnRefresh;
	public ProgressBar pbProgress;
	
	MeterQuery mMeterQuery;
	
	//public SQLiteDatabase db;
	//public TaskProvider dbHelper;
	Dialog MissionStatus;
	Cursor cTaskID;
	Cursor cMeter;
	int mTaskId;
	public static boolean isVisible = false;
	
	public static MissionFragment newInstance(){
		MissionFragment addressFragment = new MissionFragment();
	return addressFragment;
	}
	OnTaskDBClickListener mTaskDBClickCallback;
	
	  
	public interface OnTaskDBClickListener {  
        public boolean onTaskDBClick(long id);  
    }  
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {  
			mTaskDBClickCallback = (OnTaskDBClickListener) activity;  
        } catch (ClassCastException e) {  
            throw new ClassCastException(activity.toString()  
                    + " must implement OnTaskDBClickListener");  
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.mission_fragment, null);
		spTaskID = (Spinner)v.findViewById(R.id.datespinner);
		lsMeterNumber = (ListView)v.findViewById(R.id.missionlistview);
		
		btnRefresh = (ImageButton)v.findViewById(R.id.refresh);
		pbProgress = (ProgressBar)v.findViewById(R.id.indeterminate);
		
		Log.i(this.getClass().getName(), "onCreateView");
		return v;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		spTaskID.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
								
				cTaskID.moveToPosition(arg2);
				
				mTaskId = cTaskID.getInt(cTaskID.getColumnIndex(TaskProvider.C_TASK_ID));
				
				initMeter("",null, mTaskId);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
		
		lsMeterNumber.setOnItemClickListener(new OnItemClickListener() {
		    private String Long;
			private String Lat;
			LocationManager lm;
			Location location;

			@Override
		    public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				// TODO Auto-generated method stub
				cMeter.moveToPosition(position);
				if (TaskProvider.MISSION_INSTALL.equals(cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_TASK_TYPE)))){
					 Long = cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_LONGITUDE));
					 Lat = cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_LATITUDE));
				} else {
					 Long = cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_LONGITUDE));
					 Lat = cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_LATITUDE));
				}
		    	lm = (LocationManager) getSherlockActivity().getApplication().getSystemService(Context.LOCATION_SERVICE);
		    			        
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
	            final String bestProvider = lm.getBestProvider(criteria, true);
	            Log.i("gps", "bestProvider = " + bestProvider);
	            
	            // 获取定位信息
                location = lm.getLastKnownLocation(bestProvider);
	            
				if (lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
	                LocationListener locationListener = new LocationListener() {

	                    // 当位置改变时触发
	                    @Override
	                    public void onLocationChanged(Location location) {
	                            Log.i("gps", location.toString());
	                            location = lm.getLastKnownLocation(bestProvider);
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
	                };
					// 1000毫秒更新一次，忽略位置变化
	                lm.requestLocationUpdates(bestProvider, 1000, 0, locationListener);
	            }
				
				
				
				StringBuilder taskType = new StringBuilder();
				if (cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_TASK_TYPE)).equals(TaskProvider.MISSION_INSTALL)) {
					taskType.append("Install ");
				} else if (cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_TASK_TYPE)).equals(TaskProvider.MISSION_REMOVE)){
					taskType.append("Remove ");
				} else if (cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_TASK_TYPE)).equals(TaskProvider.MISSION_REPLACE)){
					taskType.append("Replace ");
				} else {
					taskType.append("");
				}
				
				if (cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_DEVICE_TYPE)).equals(TaskProvider.DEVICE_METER)) {
					taskType.append("Meter");
				} else if (cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_DEVICE_TYPE)).equals(TaskProvider.DEVICE_CONCENTRATOR)){
					taskType.append("Concentrator");
				} else if (cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_DEVICE_TYPE)).equals(TaskProvider.DEVICE_COLLECTOR)){
					taskType.append("Collector");
				} else {
					taskType.append("");
				}
				
				
		    	Dialog dialog = new AlertDialog.Builder(getSherlockActivity())
			   	.setIcon(android.R.drawable.btn_star)
			   	.setTitle("Start Working")
			   	.setMessage("Task Type: " + taskType + "\n" 
			   			  + cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_ADDRESS)) + "\n"
			   			  + "Location: " + "\n"
			   			  + Long + "\n"
			   			  + Lat + "\n")
			   	.setPositiveButton("Track",
				     new DialogInterface.OnClickListener() {
					     @Override
					     public void onClick(DialogInterface dialog, int which) {
					    	
					    	 if (null == location) {
					    		 Intent i = new Intent(   
					    	                Intent.ACTION_VIEW,   
					    	                Uri.parse("http://ditu.google.com/maps?hl=zh&mrt=loc&q=" +
					    	                		Lat + "," + Long));   
					    	        startActivity(i); 
					    	 } else {
					    		 Intent i = new Intent(   
						                 Intent.ACTION_VIEW,   
						                 Uri.parse("http://ditu.google.com/maps?f=d&source=s_d&saddr=" +
						                		 location.getLatitude() + "," + location.getLongitude() +
						                 		"&daddr=" + Lat + "," + Long + "&hl=zh"
						                		 ));   
						         startActivity(i);  
					    	 }
					     }
				     })
				.setNeutralButton("Open",
					     new DialogInterface.OnClickListener() {
				     @Override
				     public void onClick(DialogInterface dialog, int which) {
				    	
				    	 mTaskDBClickCallback.onTaskDBClick(cMeter.getLong(cMeter.getColumnIndex(TaskProvider.C_ID)));
				     }
			     })
				.create();

		    	dialog.show();
				
			}
			
		});
		lsMeterNumber.setOnItemLongClickListener(new OnItemLongClickListener() {
			private String meterNo;
			long _id;

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		    	cMeter.moveToPosition(position);
		    	_id = id;
		    	
				meterNo = cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_TASK_DEVICE_NO));
				
				    			    	
			    Dialog dialog = new AlertDialog.Builder(getSherlockActivity())
				   	.setIcon(android.R.drawable.btn_star)
				   	.setTitle("Modify Mission Status")
				   	.setMessage(meterNo + "\n"
				   			  + cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_TASK_ANOMALY_REASON)) + "\n"
				   			  + "Change the mission status to:")
				   	.setPositiveButton("Succeed",
					     new DialogInterface.OnClickListener() {
						     @Override
						     public void onClick(DialogInterface dialog, int which) {
						    	 ContentValues values = new ContentValues();
							     values.put(TaskProvider.C_TASK_STATUS, TaskProvider.STATUS_SUCCEED);
							     //db.update(TaskProvider.TABLE, values, TaskProvider.C_ID + "=" + _id, null);
							     getSherlockActivity().getContentResolver().update(TaskProvider.CONTENT_URI, values, TaskProvider.C_ID + "=" + _id,
							 			null);
							     mHandler.sendEmptyMessage(INIT_DATABASE_SUCCESS);
						     }
					     })
					.setNegativeButton("Failed", 
						new DialogInterface.OnClickListener() {
						    @Override
						    public void onClick(DialogInterface dialog, int which) {
						    	 ContentValues values = new ContentValues();
							     values.put(TaskProvider.C_TASK_STATUS, TaskProvider.STATUS_FAILED);
							     //db.update(TaskProvider.TABLE, values, TaskProvider.C_ID + "=" + _id, null);
							     getSherlockActivity().getContentResolver().update(TaskProvider.CONTENT_URI, values, TaskProvider.C_ID + "=" + _id,
								 			null);
							     mHandler.sendEmptyMessage(INIT_DATABASE_SUCCESS);
						    }
						})
					.setNeutralButton("Pending", 
						new DialogInterface.OnClickListener() {
						    @Override
						    public void onClick(DialogInterface dialog, int which) {
							    ContentValues values = new ContentValues();
							    values.put(TaskProvider.C_TASK_STATUS, TaskProvider.STATUS_PENDING);
							    //db.update(TaskProvider.TABLE, values, TaskProvider.C_ID + "=" + _id, null);
							    getSherlockActivity().getContentResolver().update(TaskProvider.CONTENT_URI, values, TaskProvider.C_ID + "=" + _id,
							 			null);
							    mHandler.sendEmptyMessage(INIT_DATABASE_SUCCESS);
						    }
						})
					.create();

			    dialog.show();
			    return false;
			}
			
			
		});
		
		btnRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(START_DOWNLOAD_TASKS);
			}
		});
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
		//读取任务时间
		initTaskID();
		
		if (0 != cTaskID.getCount()){
			cTaskID.moveToPosition(spTaskID.getSelectedItemPosition());
			mTaskId = cTaskID.getInt(cTaskID.getColumnIndex(TaskProvider.C_TASK_ID));
			Log.i(this.getClass().getName(), "TaskID: " + mTaskId);
			initMeter("",null,mTaskId);
		} else {
			
		}
				
		Log.i(this.getClass().getName(), "onResume");
	}
	
	public void initTaskID() {
		// TODO Auto-generated method stub
		String[] Columns = {TaskProvider.C_ID,TaskProvider.C_TASK_ID};
		
		//cTaskID = db.query(true, TaskProvider.TABLE, Columns, null, null, TaskProvider.C_TASK_ID, null, TaskProvider.C_TASK_ID + " DESC", null);
		cTaskID = getSherlockActivity().getContentResolver().query(Uri.parse("content://" + TaskProvider.AUTHORITY + "/GroupBy/" + TaskProvider.C_TASK_ID), Columns, null, null, TaskProvider.C_TASK_ID + " DESC");
		/*spinnerAdapter=new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_spinner_item,
				cTaskID,
				new String[] {TaskProvider.C_TASK_ID},
				new int[] {android.R.id.text1});*/
		spinnerAdapter = new android.support.v4.widget.CursorAdapter (getSherlockActivity(), cTaskID, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {
			
			@Override
			public View newView(Context context, Cursor cursor, ViewGroup parent) {
				// TODO Auto-generated method stub
                LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
                View inflate = inflater.inflate(android.R.layout.simple_spinner_item, null);
                TextView tvTaskID = (TextView) inflate.findViewById(android.R.id.text1);
                inflate.setTag(tvTaskID);
                return inflate;//返回的view传给bindView。
				
			}
			
			@Override
			public void bindView(View view, Context context, Cursor cursor) {
				// TODO Auto-generated method stub
				TextView tvTaskID = (TextView)view.getTag();
				tvTaskID.setText(cTaskID.getString(cTaskID.getColumnIndex(TaskProvider.C_TASK_ID)));
			}
		};
		
		//spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spTaskID.setAdapter(spinnerAdapter);
		spTaskID.setSelection(0);
	}

	@Override
	public void onPause() {
		
		// TODO Auto-generated method stub
		super.onPause();
		if (cTaskID != null) {
			cTaskID.close();
		}
		if (cMeter != null) {
			cMeter.close();
		}
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
		// TODO Auto-generated method stub\
		
		super.onDestroyView();
		/*
		if (db.isOpen()){
			db.close();
		}
		dbHelper.close();
		*/
		Log.i(this.getClass().getName(), "onDestroyView");
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// Remove all Runnable and Message.

		mHandler.removeCallbacksAndMessages(null);
	
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
	public int getTaskID() {
		return mTaskId;
	}
	public void initMeter(String queryMeterNumber, String sStatus, int TaskId) {
		// TODO Auto-generated method stub
		// 从数据库重新查询数据并刷新列表
		mMeterQuery = new MeterQuery();
		mMeterQuery.queryMeterNumber = queryMeterNumber;
		mMeterQuery.status = sStatus;
		mMeterQuery.taskID = TaskId;
		
		String[] Columns = {TaskProvider.C_ID,TaskProvider.C_TASK_DEVICE_NO, TaskProvider.C_POD, TaskProvider.C_TASK_INDEX, TaskProvider.C_TASK_TYPE, TaskProvider.C_DEVICE_TYPE, TaskProvider.C_TASK_STATUS, TaskProvider.C_TASK_ID, TaskProvider.C_TASK_ANOMALY_REASON, TaskProvider.C_LATITUDE, TaskProvider.C_LONGITUDE, TaskProvider.C_ADDRESS};

		if (( "".equals(queryMeterNumber))&&(null == sStatus)) {
			//cMeter = db.query(TaskProvider.TABLE, Columns, TaskProvider.C_OPERATOR_NAME + "='" + Login.mOperator +"'" + " AND " +  TaskProvider.C_TASK_ID + "='" + TaskId +"'", null, null, null, TaskProvider.C_TASK_DEVICE_NO + " DESC");
			cMeter = getSherlockActivity().getContentResolver().query(TaskProvider.CONTENT_URI, Columns, TaskProvider.C_OPERATOR_NAME + "='" + Login.mOperator +"'" + " AND " +  TaskProvider.C_TASK_ID + "='" + TaskId +"'", null, TaskProvider.C_TASK_INDEX + " ASC");
			//cMeter = (Cursor) new CursorLoader(getActivity(), TaskProvider.CONTENT_URI, Columns, TaskProvider.C_OPERATOR_NAME + "='" + Login.mOperator +"'" + " AND " +  TaskProvider.C_TASK_ID + "='" + TaskId +"'", null, TaskProvider.C_TASK_DEVICE_NO + " DESC");
		} else if (!("".equals(queryMeterNumber))&&(null == sStatus)) {
			//cMeter = db.query(TaskProvider.TABLE, Columns, TaskProvider.C_OPERATOR_NAME + "='" + Login.mOperator +"'" + " AND " +  TaskProvider.C_TASK_ID + "='" + TaskId +"'" + " AND " + TaskProvider.C_TASK_DEVICE_NO + " LIKE '%" +queryMeterNumber + "%'", null, null, null, TaskProvider.C_TASK_DEVICE_NO + " DESC");
			cMeter = getSherlockActivity().getContentResolver().query(TaskProvider.CONTENT_URI, Columns, TaskProvider.C_OPERATOR_NAME + "='" + Login.mOperator +"'" + " AND " +  TaskProvider.C_TASK_ID + "='" + TaskId +"'" + " AND " + TaskProvider.C_TASK_DEVICE_NO + " LIKE '%" +queryMeterNumber + "%'", null, TaskProvider.C_TASK_INDEX + " ASC");
		} else if (("".equals(queryMeterNumber))&&(null != sStatus)) {
			//cMeter = db.query(TaskProvider.TABLE, Columns, TaskProvider.C_OPERATOR_NAME + "='" + Login.mOperator +"'" + " AND " +  TaskProvider.C_TASK_ID + "='" + TaskId +"'" + " AND " + TaskProvider.C_TASK_STATUS + "=" + sStatus, null, null, null, TaskProvider.C_TASK_DEVICE_NO + " DESC");
			cMeter = getSherlockActivity().getContentResolver().query(TaskProvider.CONTENT_URI, Columns, TaskProvider.C_OPERATOR_NAME + "='" + Login.mOperator +"'" + " AND " +  TaskProvider.C_TASK_ID + "='" + TaskId +"'" + " AND " + TaskProvider.C_TASK_STATUS + "=" + sStatus, null, TaskProvider.C_TASK_INDEX + " ASC");
		} else {
			//cMeter = db.query(TaskProvider.TABLE, Columns, TaskProvider.C_OPERATOR_NAME + "='" + Login.mOperator +"'" + " AND " +  TaskProvider.C_TASK_ID + "='" + TaskId +"'" + " AND " + TaskProvider.C_TASK_DEVICE_NO + " LIKE '%" +queryMeterNumber + "%'" + " AND " + TaskProvider.C_TASK_STATUS + "=" + sStatus, null, null, null, TaskProvider.C_TASK_DEVICE_NO + " DESC");
			cMeter = getSherlockActivity().getContentResolver().query(TaskProvider.CONTENT_URI, Columns, TaskProvider.C_OPERATOR_NAME + "='" + Login.mOperator +"'" + " AND " +  TaskProvider.C_TASK_ID + "='" + TaskId +"'" + " AND " + TaskProvider.C_TASK_DEVICE_NO + " LIKE '%" +queryMeterNumber + "%'" + " AND " + TaskProvider.C_TASK_STATUS + "=" + sStatus, null, TaskProvider.C_TASK_INDEX + " ASC");
		}
		/*
		listAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.missionlistitem,
				cMeter,
				new String[] {TaskProvider.C_TASK_DEVICE_NO, TaskProvider.C_TASK_STATUS},
				new int[] {R.id.meternumber, R.id.missionstatus});
				
		/*/
		//listAdapter = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder)
		listAdapter = new android.support.v4.widget.CursorAdapter(getSherlockActivity(), cMeter, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {
			
			@Override
			public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
				// TODO Auto-generated method stub
				ViewHolder holder = new ViewHolder();
                LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
                View inflate = inflater.inflate(R.layout.missionlistitem, null);
                holder.tvIndex = (TextView) inflate.findViewById(R.id.taskindex);
                holder.tvDeviceNo = (TextView) inflate.findViewById(R.id.meternumber);
                holder.tvTaskType = (TextView) inflate.findViewById(R.id.tasktype);
                holder.tvDeviceType = (TextView) inflate.findViewById(R.id.devicetype);
                holder.tvStatus = (TextView) inflate.findViewById(R.id.missionstatus);
                inflate.setTag(holder);
                return inflate;//返回的view传给bindView。
			}
			
			@Override
			public void bindView(View view, Context arg1, Cursor cursor) {
				// TODO Auto-generated method stub
				ViewHolder holder = (ViewHolder) view.getTag();
				holder.tvIndex.setText(cMeter.getInt(cMeter.getColumnIndex(TaskProvider.C_TASK_INDEX))+"");
				
				if (!TextUtils.isEmpty(cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_TASK_DEVICE_NO)))) {
					holder.tvDeviceNo.setText(cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_TASK_DEVICE_NO)));
				} else if (cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_DEVICE_TYPE)).equals(TaskProvider.DEVICE_METER)){
					holder.tvDeviceNo.setText("----------");
				} else if (cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_DEVICE_TYPE)).equals(TaskProvider.DEVICE_CONCENTRATOR)){
					holder.tvDeviceNo.setText("------");
				} else {
					holder.tvDeviceNo.setText("Wrong Task");
				}
				
				String stTaskType = cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_TASK_TYPE));
				if (stTaskType.equals(TaskProvider.MISSION_INSTALL)) {
					holder.tvTaskType.setText("Install");
				} else if (stTaskType.equals(TaskProvider.MISSION_REMOVE)) {
					holder.tvTaskType.setText("Remove");
				} else if (stTaskType.equals(TaskProvider.MISSION_REPLACE)) {
					holder.tvTaskType.setText("Replace");
				} else {
					holder.tvDeviceNo.setText("Wrong Task");
				}
				
				String stDeviceType = cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_DEVICE_TYPE));
				if (stDeviceType.equals(TaskProvider.DEVICE_METER)) {
					holder.tvDeviceType.setText("M");
				} else if (stDeviceType.equals(TaskProvider.DEVICE_CONCENTRATOR)) {
					holder.tvDeviceType.setText("DC");
				} else if (stDeviceType.equals(TaskProvider.DEVICE_COLLECTOR)) {
					holder.tvDeviceType.setText("C");
				} else {
					holder.tvDeviceNo.setText("Wrong Task");
				}
                
                
                
                String stStatus = cMeter.getString(cMeter.getColumnIndex(TaskProvider.C_TASK_STATUS));
                if (stStatus.equals(TaskProvider.STATUS_INITIAL)) {
                	holder.tvStatus.setText("Initial");//给该控件设置数据(数据从集合类中来)  
                	holder.tvStatus.setTextColor(0xff000000);
                } else if (stStatus.equals(TaskProvider.STATUS_FAILED)) {
                	holder.tvStatus.setText("Failed");//给该控件设置数据(数据从集合类中来)  
                	holder.tvStatus.setTextColor(0xffff0000);
                } else if (stStatus.equals(TaskProvider.STATUS_SUCCEED)) {
                	holder.tvStatus.setText("Succeed");//给该控件设置数据(数据从集合类中来)  
                	holder.tvStatus.setTextColor(0xff00bb00);
                } else {
                	holder.tvStatus.setText("Pending");//给该控件设置数据(数据从集合类中来)  
                	holder.tvStatus.setTextColor(0xff000000);
                }
			}
		};
				
		lsMeterNumber.setAdapter(listAdapter);
	}
	
	private Handler mHandler = new Handler() {
		
		public void handleMessage(android.os.Message msg) {
			//接受线程发送过来的消息  并在此UI线程中来更新ListView中填充的数据
			switch (msg.what) {
			case INIT_DATABASE_SUCCESS:
				initMeter(mMeterQuery.queryMeterNumber, mMeterQuery.status, mMeterQuery.taskID);

				break;
			
			case START_DOWNLOAD_TASKS:
				getSherlockActivity().startService(new Intent(getSherlockActivity(), DownloadMissionIntentService.class));
				btnRefresh.setVisibility(View.GONE);
				pbProgress.setVisibility(View.VISIBLE);
				break;
			
			default:
				break;
			}
		};
	};
	private class MeterQuery {
		String queryMeterNumber;
		String status;
		int taskID;
	}
	private class ViewHolder {
		TextView tvIndex;
		TextView tvDeviceNo;
		TextView tvTaskType;
		TextView tvDeviceType;
		TextView tvStatus;
	}
}
