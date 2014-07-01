package com.hexing.imeter;

import com.hexing.imeter.fragment.EditFragment;
import com.hexing.imeter.utils.TaskProvider;
import com.hexing.imeter.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class ViewPictures extends Activity{
	
	
	
	//private MyDialog dialog;
		private ImageView imageView;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.viewpictures);
			//dialog=new MyDialog(this);
			imageView=(ImageView)findViewById(R.id.viewpicture);
			Intent intent = getIntent();
			String folderName = intent.getStringExtra(TaskProvider.C_FOLDER_NAME);
			String editDate = intent.getStringExtra(TaskProvider.C_ONSITE_DATE);
        	String pictureTime = intent.getStringExtra(EditFragment.PICTURE_TIME);
        	String picture = Environment.getExternalStorageDirectory().getPath()+ "/" +
					"Hexing" + "/" + "Archives" + "/" + Login.mOperator + "/"+ editDate + "/" + folderName +"/" +pictureTime;
        	
        	int width = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        	int height = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        	
        	
        	imageView.setImageBitmap(Utils.getImageThumbnail(picture,width,height));
			imageView.setOnClickListener(new OnClickListener() {
				
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
		
		public void exitbutton1(View v) {  
	    	this.finish();    	
	    }  
		

	}
