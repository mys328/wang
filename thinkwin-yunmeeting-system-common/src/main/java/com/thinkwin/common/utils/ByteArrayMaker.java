package com.thinkwin.common.utils;

import java.io.ByteArrayOutputStream;

public class ByteArrayMaker  extends ByteArrayOutputStream {
	   static boolean collect = false;
	  static MakerStats stats;
	  static int defaultInitSize;
	  private int _initSize;
	  private String _caller;

	  static
	  {
	    String collectString = System.getProperty(MakerStats.class.getName());

	    if ((collectString != null) && 
	      (collectString.equals("true"))) {
	      collect = true;
	    }

	    stats = null;

	    if (collect) {
	      stats = new MakerStats(ByteArrayMaker.class.toString());
	    }

	    defaultInitSize = 8000;

	    String defaultInitSizeString = System.getProperty(
	      ByteArrayMaker.class.getName() + ".initial.size");

	    if (defaultInitSizeString != null)
	      try {
	        defaultInitSize = Integer.parseInt(defaultInitSizeString);
	      }
	      catch (Exception e) {
	        e.printStackTrace();
	      }
	  }

	  public static MakerStats getStatistics()
	  {
	    return stats;
	  }

	  public ByteArrayMaker() {
	    super(defaultInitSize);

	    if (collect)
	      _getInfo(new Throwable());
	  }

	  public ByteArrayMaker(int size)
	  {
	    super(size);

	    if (collect)
	      _getInfo(new Throwable());
	  }

	  public byte[] toByteArray()
	  {
	    if (collect) {
	      stats.add(this._caller, this._initSize, this.count);
	    }

	    return super.toByteArray();
	  }

	  public String toString() {
	    return super.toString();
	  }

	  private void _getInfo(Throwable t) {
	    this._initSize = this.buf.length;

	    StackTraceElement[] elements = t.getStackTrace();

	    if (elements.length > 1) {
	      StackTraceElement el = elements[1];

	      this._caller = 
	        (el.getClassName() + "." + el.getMethodName() + 
	        ":" + el.getLineNumber());
	    }
	  }
}
