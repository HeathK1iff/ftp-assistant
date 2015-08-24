package com.ytarzimanov.ftp_assistant.models;

import java.util.ArrayList;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import com.ytarzimanov.ftp_assistant.models.core.Directory;
import com.ytarzimanov.ftp_assistant.models.core.Global;
import com.ytarzimanov.ftp_assistant.models.core.Server;

public class TreeModel implements javax.swing.tree.TreeModel {
	private Global global;

	public TreeModel(Global global){
		this.global = global;	
	}
	
	@Override
	public void addTreeModelListener(TreeModelListener arg0) {

	}

	@Override
	public Object getChild(Object arg0, int arg1) {
		if (arg0 instanceof Server){
			return ((Server)arg0).get(arg1);
		}		
		else
		  if (arg0 instanceof Global){
			  return (((Global)(arg0)).getServerList()).get(arg1);
		  }
			  
		return null;
	}

	@Override
	public int getChildCount(Object arg0) {
		if (arg0 instanceof Server){
			return ((Server)arg0).size();
		}
		else
		if (arg0 instanceof Global){
			return (((Global)(arg0)).getServerList().size());
		}

		return 0;
	}

	@Override
	public int getIndexOfChild(Object arg0, Object arg1) {
		if (arg0 instanceof ArrayList){
			for (int i = 0; i < (((Global)(arg0)).getServerList()).size(); i++){
				if (arg1 == (((Global)(arg0)).getServerList().get(i))) return i;
			}
		}
		else
		if (arg0 instanceof Server){
			for (int i = 0; i < ((Server)(arg0)).size(); i++){
				if (arg1==((Server)(arg0)).get(i)) return i;
			}
		}
		return -1;
	}

	@Override
	public Object getRoot() {
		return global;
	}

	@Override
	public boolean isLeaf(Object arg0) {
		if (arg0 instanceof Directory){
			return true;
		};
		return false;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener arg0) {
	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
	}

}
