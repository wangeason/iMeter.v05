package com.hexing.imeter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.hexing.imeter.utils.TaskProvider;
import com.hexing.imeter.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 
 * @author geniuseoe2012
 *  更多精彩，请关注我的CSDN博客http://blog.csdn.net/geniuseoe2012
 *  android开发交流群：200102476
 */
public class ListArchives extends SherlockActivity implements SearchView.OnQueryTextListener{
    /** Called when the activity is first created. */

	
	public final static String TRANSFOR_METERNUMBER = "TRANSFOR_METERNUMBER";
	
	
	
	private MyExpandableAdapter adapter = null;
	private static List<Map<String, Object>> parentList;
	private static ArrayList<ArrayList<HashMap<String, Object>>> allchildList;
	
	
    public void onCreate(Bundle savedInstanceState) {
    	setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_archives);
        //启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
       
        
        parentList = new ArrayList<Map<String,Object>>();
        allchildList = new ArrayList<ArrayList<HashMap<String,Object>>>();
        
        
        
    }
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Search");
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
    protected void onResume() {
    	super.onResume();
    	initData(null);
    	adapter = new MyExpandableAdapter(this);
    	ExpandableListView expandlistview = (ExpandableListView)findViewById(R.id.expandlistview);
        expandlistview.setAdapter(adapter);
        
        //Toast.makeText(getApplication(), "Long click item to delete",Toast.LENGTH_SHORT).show();
                
        expandlistview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
                
                return false;
            }
        });
        //长按响应
        expandlistview.setOnItemLongClickListener(new OnItemLongClickListener()
        {
	        public boolean onItemLongClick(AdapterView<?> parent, View childView, int flatPos, long id)
	        {
	        	if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
	        	{
			        long packedPos = ((ExpandableListView) parent).getExpandableListPosition(flatPos);
			        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPos);
			        int childPosition = ExpandableListView.getPackedPositionChild(packedPos);
			        
	            	Log.i("date", parentList.get(groupPosition).get("date").toString());
	            	Log.i("meter", allchildList.get(groupPosition).get(childPosition).get("meter").toString());
	            	Intent intent = new Intent (ListArchives.this,DelArchive.class);
	            	
	    	        intent.putExtra(TaskProvider.C_FOLDER_NAME, allchildList.get(groupPosition).get(childPosition).get("meter").toString());
	    	        intent.putExtra(TaskProvider.C_ONSITE_DATE, parentList.get(groupPosition).get("date").toString());
	    	        startActivity(intent);
			        return true;
			        }
	        	return false;
	        }


        });
    }
    

    
    public void initData(String queryMeterNumber)
    {
    	File parentFiles = new File(Utils.getArchivesFolder(null));
    	parentList.clear();
    	allchildList.clear();
    	for(File dateFile : (parentFiles.listFiles()))
    	{
    		//被删空的目录不显示
    		if (0 == dateFile.listFiles().length){
    			continue;
    		}
    		boolean isParentHaveElement = false;
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		map.put("date", dateFile.getName());
    		
    		
    	
    		ArrayList<HashMap<String, Object>> childlist = new ArrayList<HashMap<String,Object>>();
    		for(File meterFile :dateFile.listFiles())
        	{
        		
        		HashMap<String, Object> childmap = new HashMap<String, Object>();
        		if ((null == queryMeterNumber)||(meterFile.getName().contains(queryMeterNumber))){
	        		childmap.put("meter", meterFile.getName());
	        		childlist.add(childmap);
	        		isParentHaveElement = true;
        		} 
        	}
    		//没有搜索到则不显示父目录
    		if (isParentHaveElement) {
    			parentList.add(map);
    			allchildList.add(childlist);
    		}
    		
    		
    	}
    	
    }
  
    
    class MyExpandableAdapter extends BaseExpandableListAdapter
    {
    	Context context;
    	LayoutInflater mlayoutInflater;
    	
    	
    	MyExpandableAdapter(Context context)
    	{
    		this.context=context;
    		mlayoutInflater=LayoutInflater.from(context);
    	}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return allchildList.get(groupPosition).get(childPosition);
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
			
			meternumber.setText(allchildList.get(groupPosition).get(childPosition).get("meter").toString());
			
						
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return allchildList.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return allchildList.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return allchildList.size();
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

			textParent.setText(parentList.get(groupPosition).get("date").toString());
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


	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		initData(query);
		adapter.notifyDataSetChanged();
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		initData(newText);
		adapter.notifyDataSetChanged();
		return false;
	} 
}