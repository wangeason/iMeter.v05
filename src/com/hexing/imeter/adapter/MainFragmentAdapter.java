package com.hexing.imeter.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hexing.imeter.R;
import com.hexing.imeter.fragment.MissionFragment;
import com.hexing.imeter.fragment.EditFragment;
import com.hexing.imeter.fragment.UploadFragment;

public class MainFragmentAdapter extends FragmentPagerAdapter{
	
	private String[] titleStr = {"1","2","3"};
	
	public MainFragmentAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public MainFragmentAdapter(FragmentManager fm, Context context) {
		super(fm);
		titleStr[0] = context.getString(R.string.fragment1);
		titleStr[1] = context.getString(R.string.fragment2);
		titleStr[2] = context.getString(R.string.fragment3);
	}

	@Override
	public Fragment getItem(int position) {
		
		switch (position) {
		case 0:
			return MissionFragment.newInstance();
		case 1:
			return EditFragment.newInstance();
		case 2:
			return UploadFragment.newInstance();
		}
		return null;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return titleStr[position];
	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return POSITION_NONE;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return titleStr.length;
	}

}
