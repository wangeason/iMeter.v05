package com.hexing.imeter.fragment;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragment;
import com.hexing.imeter.R;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class InstallHelpFragment extends SherlockFragment{
	static ArrayList<String> imagePath;
	public static InstallHelpFragment newInstance(ArrayList<String> imagePathes) {
		// TODO Auto-generated method stub
		imagePath = imagePathes;
		InstallHelpFragment installHelpPage = new InstallHelpFragment();
		Log.i("newInstance", "1");
		return installHelpPage;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.installhelp_frag, null);
		ImageView imageView = (ImageView)v.findViewById(R.id.installhelppage);
		String position = getTag().substring(getTag().lastIndexOf(":")+":".length());
		int index = Integer.parseInt(position);
		imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath.get(index)));
		
		return v;
	}
	

}
