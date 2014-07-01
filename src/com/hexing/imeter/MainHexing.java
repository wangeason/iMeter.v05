package com.hexing.imeter;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.widget.SearchView;
import com.hexing.imeter.adapter.MainFragmentAdapter;
import com.hexing.imeter.fragment.EditFragment;
import com.hexing.imeter.fragment.MissionFragment;
import com.hexing.imeter.fragment.UploadFragment;
import com.hexing.imeter.service.DownloadAPK;
import com.hexing.imeter.service.DownloadMissionIntentService;
import com.hexing.imeter.utils.TaskProvider;
import com.viewpagerindicator.TabPageIndicator;


public class MainHexing extends SherlockFragmentActivity implements 
	MissionFragment.OnTaskDBClickListener,
	SearchView.OnQueryTextListener{

	private FragmentPagerAdapter adapter;
	public static MainHexing instance = null;
	public EditFragment editFrag;
	public UploadFragment archiveFrag;
	public MissionFragment missionFrag;
	Location location;
	public TabPageIndicator indicator;
	MessageReceiver receiver;
	
	SharedPreferences prefs;
	SharedPreferences.Editor mprefsEditor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hexing_main);

		initActionBar();
		initView();
		
		prefs= PreferenceManager.getDefaultSharedPreferences(this);
		mprefsEditor = prefs.edit();
		
		instance = this;
		

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter filter = new IntentFilter("com.hexing.imeter.MissionRenewed");  
        filter.addCategory(Intent.CATEGORY_DEFAULT);  
        receiver = new MessageReceiver(); 
		registerReceiver(receiver, filter);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// UNregister the receiver

		unregisterReceiver(receiver);  

	}
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    		startActivity(new Intent (this,Exit.class));
        }
		return false;
    }
	private void initView(){
		adapter = new MainFragmentAdapter(getSupportFragmentManager(), this);
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);
		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		indicator.setOnPageChangeListener(new OnPageChangeListener() {
			int oldSeleted = -1;
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				//离开MISSION FRAG时,刷新列表, 关闭DB
				if ((oldSeleted == 0)&&(arg0 !=0)){
					Log.i("onPageSelected", "leaving DB");
					
					//missionFrag.db.close();
				}
				//进入MISSION FRAG时打开DB
				if ((oldSeleted != 0)&&(arg0 ==0)){
					// Open the database for writing
					//missionFrag.db = missionFrag.dbHelper.getReadableDatabase();
					if (null == missionFrag) {
						missionFrag = (MissionFragment)getSupportFragmentManager().
								findFragmentByTag(("android:switcher:"+R.id.pager+":" + 0));
					}
					missionFrag.initTaskID();
		        	missionFrag.initMeter("", null, missionFrag.getTaskID());
				}
				//离开EDIT FRAG时保存
				if ((oldSeleted == 1)&&(arg0 !=1)){
					//Log.i("onPageSelected", "leaving Edit");
					
					editFrag.saveArchives();
				}
				//进入EDIT FRAG时
				if ((oldSeleted != 1)&&(arg0 ==1)){
					if (null == editFrag){
						editFrag = (EditFragment)getSupportFragmentManager().
								findFragmentByTag(("android:switcher:"+R.id.pager+":" + 1));
					}
				}
				
				
				//进入EDIT FRAG时do nothing
				//进入Archive FRAG时刷新列表, 搜索置空
				if ((oldSeleted != 2)&&(arg0 ==2)){
					//Log.i("onPageSelected", "leaving Archive");
					if (null == archiveFrag) {
						archiveFrag = (UploadFragment)getSupportFragmentManager().
								findFragmentByTag(("android:switcher:"+R.id.pager+":" + 2));
					}
					archiveFrag.initDate();
					archiveFrag.initTask(null);
					
				}
				oldSeleted = arg0;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				//Log.i("onPageScrolled arg0", arg0+"");
				//Log.i("onPageScrolled arg1", arg1+"");
				//Log.i("onPageScrolled arg2", arg2+"");
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				//Log.i("onPageScrollStateChanged arg0", arg0+"");
			}
		});
	}

	private void initActionBar() {
		//不允许自定义actionbar
		getSupportActionBar().setDisplayShowCustomEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		// 不在actionbar显示logo
		getSupportActionBar().setDisplayShowHomeEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		if (null == missionFrag) {
			missionFrag = (MissionFragment)getSupportFragmentManager().
					findFragmentByTag(("android:switcher:"+R.id.pager+":" + 0));
		}
		
		if (null == editFrag){
			editFrag = (EditFragment)getSupportFragmentManager().
					findFragmentByTag(("android:switcher:"+R.id.pager+":" + 1));
		}

		SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Search for meters");
        searchView.setOnQueryTextListener(this);
        //默认的ID是0
        menu.add("Search")
        .setIcon(R.drawable.abs__ic_search)
        .setActionView(searchView)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		SubMenu addMenu = menu.addSubMenu("add item");
		//addMenu.add(0, 1, 0, this.getString(R.string.savearchive)).setIcon(R.drawable.ofm_group_chat_icon);
		addMenu.add(0, 2, 0, this.getString(R.string.sdarchives)).setIcon(R.drawable.ofm_collect_icon);
		addMenu.add(0, 3, 0, this.getString(R.string.cleantasks)).setIcon(R.drawable.ofm_add_icon);
		addMenu.add(0, 4, 0, this.getString(R.string.installhelp)).setIcon(R.drawable.ofm_qrcode_icon);
		addMenu.add(0, 5, 0, this.getString(R.string.taskstatistic)).setIcon(R.drawable.ofm_photo_icon);
		//addMenu.add(0, 6, 0, this.getString(R.string.sdarchives)).setIcon(R.drawable.ofm_collect_icon);;
		
		MenuItem addItem = addMenu.getItem();
		addItem.setIcon(R.drawable.abs__ic_menu_moreoverflow_holo_dark);
		addItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		SubMenu subMenu = menu.addSubMenu("action item");
		
		subMenu.add(0, 7, 0, prefs.getString("username", ""));
		subMenu.add(0, 8, 0, this.getString(R.string.settings)).setIcon(R.drawable.ofm_setting_icon);
		//subMenu.add(0, 9, 0, this.getString(R.string.suggestion)).setIcon(R.drawable.ofm_mail_icon);
		subMenu.add(0, 10, 0, this.getString(R.string.upgrade)).setIcon(R.drawable.ofm_collect_icon);
		subMenu.add(0, 11, 0, this.getString(R.string.about)).setIcon(R.drawable.ofm_mail_icon);

		MenuItem menuItem = subMenu.getItem();
		menuItem.setIcon(R.drawable.app_panel_setting_icon);
		menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case 1:
			editFrag.saveArchives();
			break;
			
		case 2:
			startActivity(new Intent (this,ListArchives.class));
			break;
			
		case 3:
			
			getContentResolver().delete(TaskProvider.CONTENT_URI, null, null);
			
			missionFrag.initTaskID();
        	missionFrag.initMeter("", null, missionFrag.getTaskID());
			
    		
    		break;
		case 4:
			Intent intent = new Intent (this,InstallHelp.class);
			startActivity(intent); 
			break;
		case 5:
			//startActivity(new Intent (this,UploadDBData.class));
			//startActivity(new Intent (this,UploadArchives.class));
			startActivity(new Intent (this,TaskStatistic.class));
			break;
		case 6:
			startService(new Intent(this, DownloadMissionIntentService.class));
			break;
		case 8:
			Intent intentPre = new Intent(this,Preference.class);
			intentPre.putExtra("CALL_FROM", "MainHexing");
			startActivity(intentPre);
			break;
		case 10:
			Log.i("current_code", (prefs.getInt("current_code", 0))+"");
			Log.i("newest_code", (prefs.getInt("newest_code", 0))+"");
			Log.i("APK_downloaded_version", (prefs.getInt("APK_downloaded_version", 0)+""));
			
			if ((prefs.getInt("current_code", 0) < prefs.getInt("newest_code", 0))
					&&(prefs.getInt("APK_downloaded_version", 0)>=prefs.getInt("newest_code", 0))) {
				Intent update = new Intent(Intent.ACTION_VIEW);  
				update.setDataAndType(Uri.fromFile(new File(Environment  
			            .getExternalStorageDirectory()+"/Hexing/UpdateAPK/iMeter.apk")),  
			            "application/vnd.android.package-archive");  
				startActivity(update);  
			} else if (prefs.getInt("newest_code", 0) == prefs.getInt("newest_code", 0)) {
				int verCode = prefs.getInt("current_code", 0);  
			    String verName = prefs.getString("current_version", "");  
			    StringBuffer sb = new StringBuffer();  
			    sb.append("Current version:");  
			    sb.append(verName);  
			    sb.append(" Code:");  
			    sb.append(verCode);  
			    sb.append(", is the newest!");  
			    Dialog dialog = new AlertDialog.Builder(this).setTitle("Update")  
			            .setMessage(sb.toString())// 设置内容  
			            .setPositiveButton("OK",// 设置确定按钮  
			                    new DialogInterface.OnClickListener() {  
			                        @Override  
			                        public void onClick(DialogInterface dialog,  
			                                int which) {  
			                            dialog.dismiss();  
			                        }  
			                    }).create();// 创建  
			    // 显示对话框  
			    dialog.show();  
			} else {
				startService(new Intent(this, DownloadAPK.class));
				  
			    Dialog dialog = new AlertDialog.Builder(this).setTitle("Update")  
			            .setMessage("Started Downloading Newest APK")// 设置内容  
			            .setPositiveButton("OK",// 设置确定按钮  
			                    new DialogInterface.OnClickListener() {  
			                        @Override  
			                        public void onClick(DialogInterface dialog,  
			                                int which) {  
			                            finish();  
			                        }  
			                    }).create();// 创建  
			    // 显示对话框  
			    dialog.show();  
			}
			break;
		case 11:
			LayoutInflater layoutInflater = LayoutInflater.from(this);
		    View viewAbout = layoutInflater.inflate(R.layout.about_body, null);
		    TextView vCurrentVersion = (TextView)viewAbout.findViewById(R.id.current_version);
		    TextView vNewestVersion = (TextView)viewAbout.findViewById(R.id.newest_version);
		    
		    vCurrentVersion.setText(prefs.getString("current_version", "1.0.0.5"));
		    vNewestVersion.setText("Newest Version:" + prefs.getString("newest_version", "1.0.0.5"));

		    new AlertDialog.Builder(this).setView(
		    		 viewAbout).setInverseBackgroundForced(true).show();
			break;

		default:
			break;
		}
		return true;
	}
	

	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		if (true == UploadFragment.isVisible){
			
			
			archiveFrag = (UploadFragment)getSupportFragmentManager().
					findFragmentByTag(("android:switcher:"+R.id.pager+":" + 2));
			//archiveFrag.initDate();
			archiveFrag.initTask(query);
		} else if(true == MissionFragment.isVisible){
			//搜索DB表号
			
        	missionFrag.initMeter(query, null, missionFrag.getTaskID());
		}
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		if (true == UploadFragment.isVisible){
			
			
			archiveFrag = (UploadFragment)getSupportFragmentManager().
					findFragmentByTag(("android:switcher:"+R.id.pager+":" + 2));
			//archiveFrag.initDate();
			archiveFrag.initTask(newText);
		} else if(true == MissionFragment.isVisible){
			//搜索DB表号
			
        	missionFrag.initMeter(newText, null, missionFrag.getTaskID());
		}
		return false;
	}

	
	@Override
	public boolean onTaskDBClick(long id) {
		// TODO Auto-generated method stub
		
		
		editFrag.refreshFragmentByID(id);
		

		
    	editFrag.initSignature(id);
    	
    	editFrag.initGridView(id);
		
    	
        indicator.setCurrentItem(1);
		
		return true;
	} 

	
	
	public class MessageReceiver extends BroadcastReceiver {  
        @Override  
        public void onReceive(Context context, Intent intent) { 
    		
    		
            if (intent.getIntExtra("msg", 0) == 3) {
            	Log.i("MainHexing", "refresh tasks succeed");
            	Toast.makeText(getApplicationContext(), "refresh tasks succeed", Toast.LENGTH_SHORT).show();
            	missionFrag.initTaskID();
            	missionFrag.initMeter("", null, missionFrag.getTaskID());
            } else if (intent.getIntExtra("msg", 0) == 1) {
            	Log.i("MainHexing", "refresh tasks from file succeed");
            	Toast.makeText(getApplicationContext(), "refresh tasks from file succeed", Toast.LENGTH_SHORT).show();
            	missionFrag.initTaskID();
            	missionFrag.initMeter("", null, missionFrag.getTaskID());
            } else if (intent.getIntExtra("msg", 0) == 2) {
            	Log.i("MainHexing", "refresh tasks from server succeed");
            	Toast.makeText(getApplicationContext(), "refresh tasks from server succeed", Toast.LENGTH_SHORT).show();
            	missionFrag.initTaskID();
            	missionFrag.initMeter("", null, missionFrag.getTaskID());
            } else {
            	Toast.makeText(getApplicationContext(), "refresh tasks failed", Toast.LENGTH_SHORT).show();
            	Log.i("MainHexing", "refresh tasks failed");
            }
            
        	
        	missionFrag.btnRefresh.setVisibility(View.VISIBLE);
        	missionFrag.pbProgress.setVisibility(View.GONE);
        	
        	
        }  
    }
}
