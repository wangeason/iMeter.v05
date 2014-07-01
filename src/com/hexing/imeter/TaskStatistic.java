package com.hexing.imeter;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.hexing.imeter.utils.TaskProvider;
import com.hexing.imeter.utils.Utils;

public class TaskStatistic extends SherlockActivity implements SearchView.OnQueryTextListener{
	
	Cursor cTaskID;
	CursorAdapter adapter_Statistic;
	
	private ListView lsTaskID;
	
	private Handler  mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//接受线程发送过来的消息  并在此UI线程中来更新ListView中填充的数据
			switch (msg.what) {
			case 1:
				Toast.makeText(getApplication(), msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
				
				break;
			
			default:
				break;
			}
		};
	};
	
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uploadstatistic);
		//启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
       
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        lsTaskID = (ListView)findViewById(R.id.uploadstatisticlistview);
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initTaskID("");
		lsTaskID.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				cTaskID.moveToPosition(position);
				final String stExportTaskID = cTaskID.getString(cTaskID.getColumnIndex(TaskProvider.C_TASK_ID));
				new Thread(new Runnable() {
					
					@Override
					public void run() {

						Message msg = new Message();
						msg.what = 1;
						Bundle data = new Bundle();
						// TODO Auto-generated method stub
						String[] Columns = {TaskProvider.C_ID,
								TaskProvider.C_TASK_ID, 
								TaskProvider.C_TASK_INDEX, 
								TaskProvider.C_TASK_TYPE, 
								TaskProvider.C_DEVICE_TYPE,
								TaskProvider.C_INSTALL_DEVICE_NO,
								TaskProvider.C_REMOVE_DEVICE_NO,
								TaskProvider.C_CUSTOMER_NO,
								TaskProvider.C_CUSTOMER_NAME,
								TaskProvider.C_ADDRESS,
								TaskProvider.C_UPLINK_CONCENTRATOR,
								TaskProvider.C_TRANSFORMA_NAME,
								TaskProvider.C_LONGITUDE,
								TaskProvider.C_LATITUDE,
								TaskProvider.C_NEW_START_ENERGY,
								TaskProvider.C_OLD_END_ENERGY,
								TaskProvider.C_PREPAID_OLD_BALANCE,
								TaskProvider.C_TASK_STATUS,
								TaskProvider.C_TASK_ANOMALY_REASON,
								TaskProvider.C_POD};
						Cursor cExportCSV = getContentResolver().query(TaskProvider.CONTENT_URI,
								Columns,
								TaskProvider.C_OPERATOR_NAME + "='" + Login.mOperator +"' AND " +
								TaskProvider.C_TASK_ID + "='" +stExportTaskID + "'",
								null,
								TaskProvider.C_TASK_INDEX + " ASC");
						StringBuffer sb = new StringBuffer();
						
						sb.append(TaskProvider.C_TASK_ID + "," +
								TaskProvider.C_TASK_INDEX + "," + 
								TaskProvider.C_TASK_TYPE + "," +
								TaskProvider.C_DEVICE_TYPE + "," +
								TaskProvider.C_INSTALL_DEVICE_NO + "," +
								TaskProvider.C_REMOVE_DEVICE_NO + "," +
								TaskProvider.C_CUSTOMER_NO + "," +
								TaskProvider.C_CUSTOMER_NAME + "," +
								TaskProvider.C_ADDRESS + "," +
								TaskProvider.C_UPLINK_CONCENTRATOR + "," +
								TaskProvider.C_TRANSFORMA_NAME + "," +
								TaskProvider.C_LONGITUDE + "," +
								TaskProvider.C_LATITUDE + "," +
								TaskProvider.C_NEW_START_ENERGY + "," +
								TaskProvider.C_OLD_END_ENERGY + "," +
								TaskProvider.C_PREPAID_OLD_BALANCE + "," +
								TaskProvider.C_TASK_STATUS + "," +
								TaskProvider.C_TASK_ANOMALY_REASON + "," +
								TaskProvider.C_POD +
								"\n");
						for (int i=0; i<cExportCSV.getCount(); i ++) {
							cExportCSV.moveToPosition(i);
							
							SimpleDateFormat    sDateFormat    =   new    SimpleDateFormat("yyyy-MM-dd");
							String    date    =    sDateFormat.format(new    java.util.Date());
							String strFolder = Environment.getExternalStorageDirectory().getPath()+ "/" +
									"Hexing" + "/" + "ExpCSV" + "/" + Login.mOperator;
							String filepath = strFolder + "/" + stExportTaskID + " " + date + ".csv";
							
							
							if (!Utils.existSDcard()) {
								
								data.putString("msg", "Please insert the SD card");
								
							} else if (!Utils.isFolderExists(strFolder)) {
								data.putString("msg", "Can't create the folder");
							} else {
								try {
									
									
									
									//sb.append('[');
									sb.append(cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_TASK_ID)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_TASK_INDEX)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_TASK_TYPE)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_DEVICE_TYPE)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_INSTALL_DEVICE_NO)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_REMOVE_DEVICE_NO)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_CUSTOMER_NO)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_CUSTOMER_NAME)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_ADDRESS)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_UPLINK_CONCENTRATOR)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_TRANSFORMA_NAME)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_LONGITUDE)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_LATITUDE)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_NEW_START_ENERGY)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_OLD_END_ENERGY)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_PREPAID_OLD_BALANCE)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_TASK_STATUS)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_TASK_ANOMALY_REASON)) + "," +
											cExportCSV.getString(cExportCSV.getColumnIndex(TaskProvider.C_POD)) +
											"\n");
									//sb.append(']');
									
									
									FileOutputStream meterArchive = new FileOutputStream(filepath);
									 OutputStreamWriter osw = new OutputStreamWriter(meterArchive, "UTF-8");
						
									byte[] bom ={(byte) 0xEF,(byte) 0xBB,(byte) 0xBF};
									

									osw.write(new String(bom));
									osw.write(new String(sb));
									osw.flush();
									data.putString("msg", "Export CSV succeed");
								} catch (IOException e) {
									data.putString("msg", "Export CSV failed");
								}
							}
						
						}
						msg.setData(data);
						mHandler.sendMessage(msg);
						
					}
					
				}).start();
			}
		});
	}
	private void initTaskID(String queryTaskID) {
		// TODO Auto-generated method stub
		String[] Columns = {TaskProvider.C_ID,TaskProvider.C_TASK_ID, TaskProvider.C_OPERATOR_NAME, TaskProvider.C_TASK_STATUS, TaskProvider.C_TASK_ISSUE_TIME};
		cTaskID = getContentResolver().query(Uri.parse("content://" + TaskProvider.AUTHORITY + "/GroupBy/" + TaskProvider.C_TASK_ID),
					Columns,
					TaskProvider.C_OPERATOR_NAME + "='" + Login.mOperator +"' AND " +
					TaskProvider.C_TASK_ID + " LIKE '%" +queryTaskID + "%'",
					null,
					TaskProvider.C_TASK_ISSUE_TIME + " DESC");
		
		adapter_Statistic = new CursorAdapter(this, cTaskID, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {
			@Override
			public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
				// TODO Auto-generated method stub
				ViewHolderStatistic holder = new ViewHolderStatistic();
				LayoutInflater inflater = getLayoutInflater();
				View inflate = inflater.inflate(R.layout.uploadstatisticlistitem, null);
				
			    holder.tvTaskID = (TextView) inflate.findViewById(R.id.statistictaskid);
			    holder.tvSucceedRate = (TextView)inflate.findViewById(R.id.succeedrate);
			    holder.tvFailedRate = (TextView)inflate.findViewById(R.id.failedrate);;
			    holder.tvRemainRate = (TextView)inflate.findViewById(R.id.remainrate);
			    inflate.setTag(holder);
			    return inflate;
			}
			@Override
			public void bindView(View view, Context arg1, Cursor arg2) {
				// TODO Auto-generated method stub
				ViewHolderStatistic holder = (ViewHolderStatistic) view.getTag();
				String taskID = cTaskID.getString(cTaskID.getColumnIndex(TaskProvider.C_TASK_ID));
				holder.tvTaskID.setText(taskID);
				
				String[] Columns = {TaskProvider.C_ID,TaskProvider.C_TASK_ID, TaskProvider.C_OPERATOR_NAME, TaskProvider.C_TASK_STATUS};
				Cursor cStatus = getContentResolver().query(TaskProvider.CONTENT_URI,
						Columns,
						TaskProvider.C_OPERATOR_NAME + "='" + Login.mOperator +"' AND " +
						TaskProvider.C_TASK_ID + "=" + taskID + " AND " +
						TaskProvider.C_TASK_STATUS + "=" + TaskProvider.STATUS_SUCCEED,
						null,
						TaskProvider.C_TASK_ISSUE_TIME + " DESC");
				
				int iSucceed = cStatus.getCount();
				
				cStatus = getContentResolver().query(TaskProvider.CONTENT_URI,
						Columns,
						TaskProvider.C_OPERATOR_NAME + "='" + Login.mOperator +"' AND " +
						TaskProvider.C_TASK_ID + "=" + taskID + " AND " +
						TaskProvider.C_TASK_STATUS + "=" + TaskProvider.STATUS_FAILED,
						null,
						TaskProvider.C_TASK_ISSUE_TIME + " DESC");
				
				int iFailed = cStatus.getCount();
				
				cStatus = getContentResolver().query(TaskProvider.CONTENT_URI,
						Columns,
						TaskProvider.C_OPERATOR_NAME + "='" + Login.mOperator +"' AND " +
						TaskProvider.C_TASK_ID + "=" + taskID + " AND ( " +
						TaskProvider.C_TASK_STATUS + "=" + TaskProvider.STATUS_INITIAL + " OR " +
						TaskProvider.C_TASK_STATUS + "=" + TaskProvider.STATUS_PENDING + " ) ",
						null,
						TaskProvider.C_TASK_ISSUE_TIME + " DESC");
				
				int iRemain = cStatus.getCount();
				
				int total = iSucceed + iFailed + iRemain;
				// 
			    holder.tvSucceedRate.setText(String.format("%1$d", iSucceed*100/total) + "%(" + String.format("%1$d", iSucceed) + ")");
			    holder.tvFailedRate.setText(String.format("%1$d", iFailed*100/total) + "%(" + String.format("%1$d", iFailed) + ")");
			    holder.tvRemainRate.setText(String.format("%1$d", 100 - iSucceed*100/total - iFailed*100/total) + "%(" + String.format("%1$d", iRemain) + ")");
			    
			    cStatus.close();
			}
		};
		lsTaskID.setAdapter(adapter_Statistic);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Search for Task ID");
        searchView.setOnQueryTextListener(this);
        
        menu.add("Search")
        .setIcon(R.drawable.abs__ic_search)
        .setActionView(searchView)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        
		
		return super.onCreateOptionsMenu(menu);
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
        
        
        default:
        	break;
        }
    	return super.onOptionsItemSelected(item);
    }
	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		initTaskID(query);
		return false;
	}
	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		initTaskID(newText);
		return false;
	}
}
class ViewHolderStatistic{
	TextView tvTaskID;
	TextView tvSucceedRate;
	TextView tvFailedRate;
	TextView tvRemainRate;
}