package com.hexing.imeter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.hexing.imeter.R;
import com.hexing.imeter.fragment.UploadFragment;

public class MyExpandableAdapter extends BaseExpandableListAdapter {

	Context context;
	LayoutInflater mlayoutInflater;
	
	
	public MyExpandableAdapter(Context context)
	{
		this.context=context;
		mlayoutInflater=LayoutInflater.from(context);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return UploadFragment.allchildList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		convertView = mlayoutInflater.inflate(R.layout.childviewitem, null);
		
		TextView meternumber = (TextView) convertView.findViewById(R.id.meternumber);
		
		meternumber.setText(UploadFragment.allchildList.get(groupPosition).get(childPosition).get("meter").toString());
		
					
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return UploadFragment.allchildList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return UploadFragment.allchildList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return UploadFragment.allchildList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		convertView = mlayoutInflater.inflate(R.layout.parentviewitem, null);
		
		TextView textParent = (TextView) convertView.findViewById(R.id.textparent);

		textParent.setText(UploadFragment.parentList.get(groupPosition).get("date").toString());
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}


}
