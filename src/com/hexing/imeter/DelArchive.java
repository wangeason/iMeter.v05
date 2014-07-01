package com.hexing.imeter;

import java.io.File;

import com.hexing.imeter.fragment.EditFragment;
import com.hexing.imeter.utils.TaskProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class DelArchive extends Activity {
	private LinearLayout layout;
	private String folderName;
	private String date;
	private String picture;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delarchive);
		
		
		Intent intent = getIntent();
		if (null!= intent.getStringExtra(TaskProvider.C_FOLDER_NAME)) {
	    	folderName = intent.getStringExtra(TaskProvider.C_FOLDER_NAME);
	    	date = intent.getStringExtra(TaskProvider.C_ONSITE_DATE);
		}
		if (null!= intent.getStringExtra(EditFragment.PICTURE_TIME)) {
			picture = intent.getStringExtra(EditFragment.PICTURE_TIME);
	    } else {
	    	picture = null;
	    }
		
		layout=(LinearLayout)findViewById(R.id.del_archive_layout);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！", 
						Toast.LENGTH_SHORT).show();	
			}
		});
		
	
	}
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
	public void toDelte(View v) {
		deleteFolder(date,folderName,picture);
		
    	this.finish();   
    	
      }  
	public void resume(View v) {  
		
		this.finish();
    	
      }
	
	private void deleteFolder(String date, String folderName, String picture) {
		// TODO Auto-generated method stub
		String path;
		
		if (null == picture) {
			path = Environment.getExternalStorageDirectory().getPath()+ "/" +
				"Hexing" + "/" + "Archives" + "/" + Login.mOperator + "/" + date + "/" + folderName;
		} else {
			path = Environment.getExternalStorageDirectory().getPath()+ "/" +
					"Hexing" + "/" + "Archives" + "/" + Login.mOperator + "/" + date + "/" + folderName + "/" + picture;
		}
		File file = new File(path);
		
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					files[i].delete(); // 把每个文件 用这个方法进行迭代
				}
				file.delete();
			}
			
			if ((file.getParentFile().exists())&&(0 == file.getParentFile().listFiles().length)) {
				file.getParentFile().delete();
			}
				
			
		} else {

		}
		
	}		
}  
	

