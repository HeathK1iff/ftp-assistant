package com.ytarzimanov.ftp_assistant.models;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.io.IOException;

import com.ytarzimanov.controls.basic.graphics.GraphicObject;
import com.ytarzimanov.controls.navbar.graphics.*;
import com.ytarzimanov.controls.navbar.interfaces.*;
import com.ytarzimanov.controls.navbar.painters.ItemPainter;
import com.ytarzimanov.ftp_assistant.adapters.ModelAdapter;
import com.ytarzimanov.ftp_assistant.models.core.Directory;
import com.ytarzimanov.ftp_assistant.models.core.File;
import com.ytarzimanov.ftp_assistant.net.Downloader;
import com.ytarzimanov.ftp_assistant.net.Downloader.RemoteSource;

public class ViewController implements OnChangeGroupCheckBox, 
    OnClickListener, OnCustomDrawItem{
    public interface OnDownloadListener{
    	public void onDownload(File file);
    }
    private OnDownloadListener listener;
	
	
    public ViewController(ModelAdapter ma){
    }
	
    public void setOnDownloadListener(OnDownloadListener listener){
       this.listener = listener;
    }
    
	@Override
	public void onChangeGroupCheckBox(Group arg0) {
		Directory dir = (Directory) arg0.getObject();
		dir.setCopyToClipBtnChecked(arg0.getCheckState());
	}

	private AlphaComposite makeTransparent(float alpha) {
		  int type = AlphaComposite.SRC_OVER;
		  return(AlphaComposite.getInstance(type, alpha));
	 }
	
	@Override
	public void onCustomDrawItem(Item arg0, Graphics2D arg1, ItemPainter painter) {
		arg1.setColor(painter.getColorFont());
		Downloader item = Downloader.getDownloader(((RemoteSource)arg0.getObject()));
		if ((item != null)&&(!item.isFinished())){
			int ProgressWidth = ((arg0.getWidth() - 2) * item.getProgress()) / 100; 
			Composite orgComposite = arg1.getComposite();
			arg1.setComposite(makeTransparent(0.6F));
			
			arg1.setPaint(new Color(0xBC, 0xE5, 0xFC));
			
			arg1.fillRect(arg0.getLeft()+1, arg0.getTop(), ProgressWidth, arg0.getHeight());
			arg1.setPaint(new Color(0x3C, 0x7F, 0xB1));
			
            arg1.drawRect(arg0.getLeft()+1, arg0.getTop(), arg0.getWidth()-1, arg0.getHeight());
			arg1.setComposite(orgComposite);
			arg1.setColor(painter.getColorFont());
		}
		else
		{
			File file = (File)arg0.getObject();
			if (file != null){
			  if (file.getIsLocalFile()) {
				  arg1.setColor(Color.blue);
			  }
			  else
				if (file.getLocked()){
					arg1.setColor(Color.gray);
				}
			}
		}	
	}


	@Override
	public void onClickMouseLeftButton(GraphicObject Object) {

		
	}


	@Override
	public void onClickMouseRightButton(GraphicObject Object) {
		
	}

	@Override
	public void onClickItem(Item arg0) {
        final File item = (File) arg0.getObject();
    	if ((item != null) && (item.getLocked() == false)){
		  if (Downloader.isDownloading(item)){  
			  item.setIsLocalState(false);
			  Downloader.terminate(item);
		  }else{
			  item.setIsLocalState(new java.io.File(item.getDownloadFilePath()).exists());
			  if (item.getIsLocalFile()){
					new Thread(){
						@Override
						public void run(){
							try {
								Runtime.getRuntime().exec(item.getDownloadFilePath());
							} catch (IOException e) {
								
							}
						}
					}.start();
			  }
			  else
			  {
				  if (listener != null)
					  listener.onDownload(item);
			  }
		  }
		}
	}
}
