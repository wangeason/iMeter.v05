package com.hexing.imeter.adapter;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;


import com.hexing.imeter.fragment.InstallHelpFragment;

public class CircleFragmentAdapter extends FragmentPagerAdapter{
	
	public CircleFragmentAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}
	ArrayList<String> imagePathes;
		
	public CircleFragmentAdapter(FragmentManager fm, Context context, ArrayList<String> imagePathes) {
		super(fm);
		this.imagePathes = imagePathes;
	}

	@Override
	public Fragment getItem(int position) {
		return InstallHelpFragment.newInstance(imagePathes);
	}
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		super.destroyItem(container, position, object);
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return POSITION_NONE;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
			
		return imagePathes.size();
	}
}
