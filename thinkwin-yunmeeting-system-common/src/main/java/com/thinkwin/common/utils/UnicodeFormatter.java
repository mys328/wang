package com.thinkwin.common.utils;

public class UnicodeFormatter {
	public static char[] HEX_DIGIT = { 
	    '0', '1', '2', '3', '4', '5', '6', '7', 
	    '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };


	  public static String byteToHex(byte b)
	  {
	    char[] array = { HEX_DIGIT[(b >> 4 & 0xF)], HEX_DIGIT[(b & 0xF)] };

	    return new String(array);
	  }

	  public static String charToHex(char c) {
	    byte hi = (byte)(c >>> '\b');
	    byte lo = (byte)(c & 0xFF);

	    return byteToHex(hi) + byteToHex(lo);
	  }

	  public static String parseString(String hexString) {
	    StringMaker sm = new StringMaker();

	    char[] array = hexString.toCharArray();

	    if (array.length % 6 != 0) {
	    	      return hexString;
	    }

	    for (int i = 2; i < hexString.length(); i += 6) {
	      String s = hexString.substring(i, i + 4);
	      try
	      {
	        char c = (char)Integer.parseInt(s, 16);

	        sm.append(c);
	      }
	      catch (Exception e) {
	        

	        return hexString;
	      }
	    }

	    return sm.toString();
	  }

	  public static String toString(char[] array) {
	    StringMaker sm = new StringMaker();

	    for (int i = 0; i < array.length; i++) {
	      sm.append("\\u");
	      sm.append(charToHex(array[i]));
	    }

	    return sm.toString();
	  }

	  public static String toString(String s) {
	    if (s == null) {
	      return null;
	    }

	    return toString(s.toCharArray());
	  }
}
