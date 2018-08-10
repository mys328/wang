package com.thinkwin.common.utils;

public class StringMaker
{
  static boolean collect = false;
  static MakerStats stats;
  static int defaultInitSize;
  private StringBuffer _sb;
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
      stats = new MakerStats(StringMaker.class.toString());
    }

    defaultInitSize = 128;

    String defaultInitSizeString = System.getProperty(
      StringMaker.class.getName() + ".initial.size");

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

  public StringMaker() {
    this._sb = new StringBuffer(defaultInitSize);

    if (collect)
      _getInfo(new Throwable());
  }

  public StringMaker(int capacity) throws NegativeArraySizeException
  {
    this._sb = new StringBuffer(capacity);

    if (collect)
      _getInfo(new Throwable());
  }

  public StringMaker(String s) throws NullPointerException
  {
    if (s == null) {
      throw new NullPointerException();
    }

    this._sb = new StringBuffer(s.length() + defaultInitSize);

    if (collect) {
      _getInfo(new Throwable());
    }

    this._sb.append(s);
  }

  public StringMaker(StringBuffer sb) throws NullPointerException {
    if (sb == null) {
      this._sb = new StringBuffer(defaultInitSize);
    }
    else {
      this._sb = sb;
    }

    if (collect)
      _getInfo(new Throwable());
  }

  public StringMaker append(Object obj)
  {
    this._sb.append(obj);

    return this;
  }

  public StringMaker append(String s) {
    this._sb.append(s);

    return this;
  }

  public StringMaker append(StringBuffer sb) {
    sb.append(sb);

    return this;
  }

  public StringMaker append(boolean b) {
    this._sb.append(b);

    return this;
  }

  public StringMaker append(char c) {
    this._sb.append(c);

    return this;
  }

  public StringMaker append(char[] array) {
    this._sb.append(array);

    return this;
  }

  public StringMaker append(char[] array, int offset, int len) {
    this._sb.append(array, offset, len);

    return this;
  }

  public StringMaker append(double d) {
    this._sb.append(d);

    return this;
  }

  public StringMaker append(float f) {
    this._sb.append(f);

    return this;
  }

  public StringMaker append(int i) {
    this._sb.append(i);

    return this;
  }

  public StringMaker append(long l) {
    this._sb.append(l);

    return this;
  }

  public int capacity() {
    return this._sb.capacity();
  }

  public char charAt(int index) {
    return this._sb.charAt(index);
  }

  public StringMaker delete(int start, int end) {
    this._sb.delete(start, end);

    return this;
  }

  public StringMaker deleteCharAt(int index) {
    this._sb.deleteCharAt(index);

    return this;
  }

  public void ensureCapacity(int minimumCapacity) {
    this._sb.ensureCapacity(minimumCapacity);
  }

  public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
    this._sb.getChars(srcBegin, srcEnd, dst, dstBegin);
  }

  public StringBuffer getStringBuffer() {
    return this._sb;
  }

  public int indexOf(String s) {
    return this._sb.indexOf(s);
  }

  public int indexOf(String s, int fromIndex) {
    return this._sb.indexOf(s, fromIndex);
  }

  public StringMaker insert(int offset, boolean b) {
    this._sb.insert(offset, b);

    return this;
  }

  public StringMaker insert(int offset, double d) {
    this._sb.insert(offset, d);

    return this;
  }

  public StringMaker insert(int offset, float f) {
    this._sb.insert(offset, f);

    return this;
  }

  public StringMaker insert(int offset, int i) {
    this._sb.insert(offset, i);

    return this;
  }

  public StringMaker insert(int offset, long l) {
    this._sb.insert(offset, l);

    return this;
  }

  public StringMaker insert(int index, char[] array, int offset, int len) {
    this._sb.insert(index, array, offset, len);

    return this;
  }

  public StringMaker insert(int offset, Object obj) {
    this._sb.insert(offset, obj);

    return this;
  }

  public StringMaker insert(int offset, String s) {
    this._sb.insert(offset, s);

    return this;
  }

  public StringMaker insert(int offset, char c) {
    this._sb.insert(offset, c);

    return this;
  }

  public StringMaker insert(int offset, char[] array) {
    this._sb.insert(offset, array);

    return this;
  }

  public int lastIndexOf(String s) {
    return this._sb.lastIndexOf(s);
  }

  public int lastIndexOf(String s, int fromIndex) {
    return this._sb.lastIndexOf(s, fromIndex);
  }

  public int length() {
    return this._sb.length();
  }

  public StringMaker replace(int start, int end, String s) {
    this._sb.replace(start, end, s);

    return this;
  }

  public StringMaker reverse() {
    this._sb.reverse();

    return this;
  }

  public void setCharAt(int index, char ch) {
    this._sb.setCharAt(index, ch);
  }

  public void setLength(int len) {
    this._sb.setLength(len);
  }

  public String substring(int start) {
    return this._sb.substring(start);
  }

  public String substring(int start, int end) {
    return this._sb.substring(start, end);
  }

  public String toString() {
    if (collect) {
      stats.add(this._caller, this._initSize, this._sb.length());
    }

    return this._sb.toString();
  }

  private void _getInfo(Throwable t) {
    this._initSize = this._sb.capacity();

    StackTraceElement[] elements = t.getStackTrace();

    if (elements.length > 1) {
      StackTraceElement el = elements[1];

      this._caller = 
        (el.getClassName() + "." + el.getMethodName() + 
        ":" + el.getLineNumber());
    }
  }
}