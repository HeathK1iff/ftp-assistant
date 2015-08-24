package com.ytarzimanov.ftp_assistant.adapters;

import java.util.ArrayList;

import com.ytarzimanov.controls.navbar.graphics.Group;
import com.ytarzimanov.controls.navbar.graphics.Jackdaw.JackState;
import com.ytarzimanov.ftp_assistant.models.core.Directory;
import com.ytarzimanov.ftp_assistant.models.core.Server;

public class ModelSourceAdapter extends ModelAdapter{
    protected ArrayList<Server> data;
    Directory item = null;
    DirectoryIterator interator;
    
	public ModelSourceAdapter(ArrayList<Server> data){
		this.data = data;
	}

	@Override
	public Boolean getGroupChecked(int arg0) {
		return ((Directory)getGroup(arg0)).getCopyToClipBtnChecked();
	}

	@Override
	public int getGroupCount() {
		int count =  0;
		if (data.size() > 0){
		  count =  data.get(0).size();
		}
		return count;
	}

	@Override
	public Boolean getGroupExpanded(int arg0) {
		return ((Directory)getGroup(arg0)).getExpandedGroup();
	}

	@Override
	public Object getItem(int arg0, int arg1) {
		return ((Directory)getGroup(arg0)).get(arg1);
	}

	@Override
	public int getItemsCount(int arg0) {
		Directory dir = (Directory) getGroup(arg0);
		
		int itemscount = dir.size();
	   
	   if (dir.getVisibleCountItems() != 0){
		   if (dir.size() > dir.getVisibleCountItems()){
			   itemscount = dir.getVisibleCountItems();
		   }
	   }
		
	  return itemscount;
	}

		
	@Override
	public Object getGroup(int arg0) {
		return data.get(0).get(arg0);
	}

	@Override
	public void onChangeGroupState(Group arg0) {
		Directory dir = (Directory) arg0.getObject();
		if (dir != null){
			dir.setExpandedGroup((arg0.getGroupState() == JackState.gsExpand));
		}
	}

}
