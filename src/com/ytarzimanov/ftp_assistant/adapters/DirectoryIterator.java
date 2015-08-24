package com.ytarzimanov.ftp_assistant.adapters;

import java.util.ArrayList;
import java.util.Iterator;

import com.ytarzimanov.ftp_assistant.models.core.Directory;
import com.ytarzimanov.ftp_assistant.models.core.Server;

public class DirectoryIterator implements Iterator<Directory> {
	private ArrayList<Server> list;
	private int sever_index = 0;
	private int index, done_count = 0;
	
	public DirectoryIterator(ArrayList<Server> list){
		this.list = list;
	}
	
	
	public int getCount(){
		int i = 0;
		int count = 0;
		while (i < list.size()){
			count += list.get(i).size();
			i++;
		}
		return count;
	}
	
	@Override
	public boolean hasNext() {
		return (getDirectory(index) != null);
	}

	private Directory getDirectory(int index){
		int j = done_count;
		while (sever_index < list.size()){
			int i = 0;
			while (i < list.get(sever_index).size()){
				if (j == index){
					index = j;
					return list.get(sever_index).get(i);
				}
				j++;
				i++;
			}
			done_count += list.get(sever_index).size();
			sever_index++;
		}
		return null;
	}
	
	
	@Override
	public Directory next() {
		Directory dir = getDirectory(index);
		if (dir != null){
			index++;
		}
		return dir;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}

}
