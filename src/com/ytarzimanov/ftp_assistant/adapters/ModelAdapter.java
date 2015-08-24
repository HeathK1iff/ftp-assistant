package com.ytarzimanov.ftp_assistant.adapters;

import com.ytarzimanov.controls.navbar.graphics.Group;
import com.ytarzimanov.controls.navbar.interfaces.*;

abstract public class ModelAdapter implements DataModel, OnChangeGroupState{

	@Override
	abstract public void onChangeGroupState(Group arg0);

	@Override
	abstract public Boolean getGroupChecked(int arg0);

	@Override
	abstract public int getGroupCount();

	@Override
	abstract public Boolean getGroupExpanded(int arg0);

	@Override
	abstract public Object getGroup(int arg0);

	@Override
	abstract public Object getItem(int arg0, int arg1);
	
	@Override
	abstract public int getItemsCount(int arg0);

}
