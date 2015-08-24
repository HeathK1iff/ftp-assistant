package com.ytarzimanov.ftp_assistant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {
	public static String convertByPattern(String regexp, String outputformat, String inputtext){
		String result = inputtext;  
		
		if (regexp.equals("")) regexp = "(.+)";
		Matcher matcher = Pattern.compile(regexp).matcher(inputtext);
		
		if (outputformat.equals("")) outputformat = "@g1";
		
		result = outputformat;
		if (matcher.find()){
	      for (int i = 1 ;i <= matcher.groupCount(); i++){  
	     
	        if (result.contains("@g"+String.valueOf(i)+"+")) {
	          try {
	            int intGroup = Integer.parseInt(matcher.group(i));
	            intGroup++;
	            result = new String(result.replace("@g"+String.valueOf(i)+"+", String.valueOf(intGroup)));
	          } catch (NumberFormatException e){}
	        
	        } else {
	        	result = new String(result.replace("@g"+String.valueOf(i), matcher.group(i)));
	        }
	        
		  }
	      result = result.replace("@13", System.getProperty("line.separator"));		
	    }
		return result;
	  }
	
	public static String ExtractFileNameFromFilePath(String filename) {
		   int lastSeparator = filename.lastIndexOf(java.io.File.separator);
	       if( lastSeparator >= 0  ) {
	           return new String(filename.substring(lastSeparator + 1, filename.length()));
	       }
	       return filename;
	}
	
	public static String encode(String text, String key){
		byte[] txt = text.getBytes();
		byte[] mykey = key.getBytes();
		byte[] res = new byte[text.length()];
		
		for (int i = 0; i < text.length(); i++){
			res[i] = (byte) ((txt[i] ^ mykey[i % mykey.length]) + 1);
		}
		return new String(res);
	}


	public static String decode(String text, String key){
		byte[] mtext = text.getBytes();
		byte[] res = new byte[mtext.length];
		byte[] mykey = key.getBytes();
		
		for (int i = 0; i < mtext.length; i++){
			res[i] = (byte) (((mtext[i] - 1) ^ mykey[i % mykey.length]));
		}
		return new String(res);
	}
}
