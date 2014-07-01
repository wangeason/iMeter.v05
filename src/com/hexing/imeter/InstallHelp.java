package com.hexing.imeter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.hexing.imeter.adapter.CircleFragmentAdapter;
import com.hexing.imeter.utils.Utils;
import com.viewpagerindicator.CirclePageIndicator;

public class InstallHelp extends SherlockFragmentActivity implements ActionBar.OnNavigationListener {
    
    CircleFragmentAdapter installHelpAdapter;
    CirclePageIndicator circleIndicator;
    
    static String SEPRATOR = File.separator;
    public int iOldPageNumber = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar); //Used for theme switching in samples
        super.onCreate(savedInstanceState);

        setContentView(R.layout.install_help);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        Context context = getSupportActionBar().getThemedContext();
        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.metertype, R.layout.sherlock_spinner_item);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);
        
        
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
        }
    	return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        //mSelected.setText("Selected: " + mMeterTypes[itemPosition]);
    	String subFolder = null;
    	switch (itemPosition) {
    	case 0:
    		subFolder = "HXE310";
    		break;
    	case 1:
    		subFolder = "HXE100";
    		break;
    	default:
    		break;
    	}
    	ArrayList<String> imagePathes = new ArrayList<String>();
    	
    	String path = Environment.getExternalStorageDirectory()  
                + "/Hexing/Guides/" + subFolder;
		AssetManager am = getApplicationContext().getResources().getAssets();
		String[] stAsset= null;
		try {
			stAsset = am.list(subFolder);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		File file = new File(path);
	
		if (!file.exists()) {
			file.mkdirs();
		}
		
		File[] imageFiles = file.listFiles();
		for (int j = 0; j < imageFiles.length; j++){
			boolean isFileRight = false;
			for (int k=0 ; k < stAsset.length; k++) {
				if(imageFiles[j].getName().equals(stAsset[k])) {
					isFileRight = true;
				}
			}
			if (!isFileRight) {
				imageFiles[j].delete();
			}
		}
		
		for (int i=0; i<stAsset.length; i++){
			if (!(new File(path + "/" +stAsset[i])).exists()) {
				Bitmap bitmap = Utils.getImageFromAssetsFile (subFolder+ SEPRATOR +stAsset[i], this.getApplicationContext());
				try {
					Utils.saveFile(bitmap, path+"/"+stAsset[i]);
				} catch (IOException e) {
					
				}
			}
		}
		for (File itefile : file.listFiles(new jpgFileFilter())) {
			imagePathes.add(itefile.getPath()) ;
		}

		/*
		保证切换帮助内容时，之前的fragment被清除*/
		for (int i = 0; i < iOldPageNumber; i ++) {
			if (null != getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":" + i)) {
				getSupportFragmentManager().beginTransaction().
				remove(getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":" + i)).commit();
			}
		}
				
    	installHelpAdapter = new CircleFragmentAdapter(getSupportFragmentManager(), this, imagePathes);
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(installHelpAdapter);
		circleIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		circleIndicator.setViewPager(pager);
		iOldPageNumber = imagePathes.size();
    	
        return true;
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

