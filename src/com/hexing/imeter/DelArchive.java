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
				Toast.makeText(getApplicationContext(), "��ʾ����������ⲿ�رմ��ڣ�", 
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
		
		if (file.exists()) { // �ж��ļ��Ƿ����
			if (file.isFile()) { // �ж��Ƿ����ļ�
				file.delete(); // delete()���� ��Ӧ��֪�� ��ɾ������˼;
			} else if (file.isDirectory()) { // �����������һ��Ŀ¼
				File files[] = file.listFiles(); // ����Ŀ¼�����е��ļ� files[];
				for (int i = 0; i < files.length; i++) { // ����Ŀ¼�����е��ļ�
					files[i].delete(); // ��ÿ���ļ� ������������е���
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
	

