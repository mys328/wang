package com.thinkwin.common.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringUtil
{
  private static Log _log = LogFactory.getLog(StringUtil.class);

  public static int count(String s, String text)
  {
    if ((s == null) || (text == null)) {
      return 0;
    }

    int count = 0;

    int pos = s.indexOf(text);

    while (pos != -1) {
      pos = s.indexOf(text, pos + text.length());

      count++;
    }

    return count;
  }

  public static int indexof(String s, String subStr, int iNo)
  {
    int iIndex = 0;
    if ((isEmpty(s)) || (isEmpty(subStr))) {
      return 0;
    }
    iIndex = s.indexOf(subStr);
    int iCount = 1;
    while (iIndex != -1) {
      if (iCount == iNo) return iIndex;
      iIndex = s.indexOf(subStr, iIndex + subStr.length());
      iCount++;
    }

    return iIndex;
  }

  public static String arrayToStr(String[] arrays)
  {
    if (arrays == null) return null;
    StringBuffer result = new StringBuffer("");
    for (int i = 0; i < arrays.length; i++) {
      result.append(arrays[i]);
      if (i < arrays.length - 1)
        result.append(",");
    }
    return result.toString();
  }

  private static String reverse(String s) {
    if (s == null) {
      return null;
    }

    char[] c = s.toCharArray();
    char[] reverse = new char[c.length];

    for (int i = 0; i < c.length; i++) {
      reverse[i] = c[(c.length - i - 1)];
    }

    return new String(reverse);
  }

  public static boolean startsWith(String s, char begin)
  {
    return startsWith(s, new Character(begin).toString());
  }

  public static boolean startsWith(String s, String start)
  {
    if ((s == null) || (start == null)) {
      return false;
    }

    if (start.length() > s.length()) {
      return false;
    }

    String temp = s.substring(0, start.length());

    if (temp.equalsIgnoreCase(start)) {
      return true;
    }

    return false;
  }

  public static boolean contains(String str, CharSequence s)
  {
    return str.indexOf(s.toString()) > -1;
  }

  private static final boolean isEmptyOrWhitespaceOnly(String str) {
    if ((str == null) || (str.length() == 0)) {
      return true;
    }

    int length = str.length();

    for (int i = 0; i < length; i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return false;
      }
    }

    return true;
  }

  public static String bytesToHexString(byte[] bytes)
  {
    StringMaker sm = new StringMaker(bytes.length * 2);

    for (int i = 0; i < bytes.length; i++) {
      String hex = Integer.toHexString(
        256 + (bytes[i] & 0xFF)).substring(1);

      if (hex.length() < 2) {
        sm.append("0");
      }

      sm.append(hex);
    }

    return sm.toString();
  }

  public static boolean endsWith(String s, char end)
  {
    return endsWith(s, new Character(end).toString());
  }

  public static boolean endsWith(String s, String end)
  {
    if ((s == null) || (end == null)) {
      return false;
    }

    if (end.length() > s.length()) {
      return false;
    }

    String temp = s.substring(s.length() - end.length(), s.length());

    if (temp.equalsIgnoreCase(end)) {
      return true;
    }

    return false;
  }

  private static String extractChars(String s)
  {
    if (s == null) {
      return "";
    }

    StringMaker sm = new StringMaker();

    char[] c = s.toCharArray();

    for (int i = 0; i < c.length; i++) {
      if (Validator.isChar(c[i])) {
        sm.append(c[i]);
      }
    }

    return sm.toString();
  }

  private static String extractDigits(String s) {
    if (s == null) {
      return "";
    }

    StringMaker sm = new StringMaker();

    char[] c = s.toCharArray();

    for (int i = 0; i < c.length; i++) {
      if (Validator.isDigit(c[i])) {
        sm.append(c[i]);
      }
    }

    return sm.toString();
  }

  private static String extractFirst(String s, String delimiter) {
    if (s == null) {
      return null;
    }

    String[] array = split(s, delimiter);

    if (array.length > 0) {
      return array[0];
    }

    return null;
  }

  private static String extractLast(String s, String delimiter)
  {
    if (s == null) {
      return null;
    }

    String[] array = split(s, delimiter);

    if (array.length > 0) {
      return array[(array.length - 1)];
    }

    return null;
  }

  private static String highlight(String s, String keywords)
  {
    return highlight(s, keywords, "<span class=\"highlight\">", "</span>");
  }

  private static String highlight(String s, String keywords, String highlight1, String highlight2)
  {
    if (s == null) {
      return null;
    }

    StringMaker sm = new StringMaker(" ");

    StringTokenizer st = new StringTokenizer(s);

    while (st.hasMoreTokens()) {
      String token = st.nextToken();

      if (token.equalsIgnoreCase(keywords)) {
        sm.append(highlight1);
        sm.append(token);
        sm.append(highlight2);
      }
      else {
        sm.append(token);
      }

      if (st.hasMoreTokens()) {
        sm.append(" ");
      }
    }

    return sm.toString();
  }

  public static boolean isNotNull(String s)
  {
    return s != null;
  }

  public static boolean isEmpty(String s)
  {
    if ((s == null) || (s.length() == 0) || (s.equals(""))) {
      return true;
    }

    return false;
  }

  public static String lowerCase(String s)
  {
    if (s == null) {
      return null;
    }

    return s.toLowerCase();
  }

  private static String merge(List list)
  {
    return merge(list, ",");
  }

  private static String merge(List list, String delimiter) {
    return merge(list.toArray(
      new Object[list.size()]), delimiter);
  }

  private static String merge(Object[] array) {
    return merge(array, ",");
  }

  private static String merge(Object[] array, String delimiter) {
    if (array == null) {
      return null;
    }

    StringMaker sm = new StringMaker();

    for (int i = 0; i < array.length; i++) {
      sm.append(String.valueOf(array[i]).trim());

      if (i + 1 != array.length) {
        sm.append(delimiter);
      }
    }

    return sm.toString();
  }



  private static String read(ClassLoader classLoader, String name)
    throws IOException
  {
    return read(classLoader, name, false);
  }

  private static String read(ClassLoader classLoader, String name, boolean all)
    throws IOException
  {
    if (all) {
      StringMaker sm = new StringMaker();

      Enumeration enu = classLoader.getResources(name);

      while (enu.hasMoreElements()) {
        URL url = (URL)enu.nextElement();

        InputStream is = url.openStream();

        String s = read(is);

        if (s != null) {
          sm.append(s);
          sm.append("\n");
        }

        is.close();
      }

      return sm.toString().trim();
    }

    InputStream is = classLoader.getResourceAsStream(name);

    String s = read(is);

    is.close();

    return s;
  }

  private static String read(InputStream is) throws IOException
  {
    StringMaker sm = new StringMaker();

    BufferedReader br = new BufferedReader(new InputStreamReader(is));

    String line = null;

    while ((line = br.readLine()) != null) {
      sm.append(line).append('\n');
    }

    br.close();

    return sm.toString().trim();
  }

  private static String replace(String s, char oldSub, char newSub)
  {
    return replace(s, oldSub, new Character(newSub).toString());
  }

  private static String replace(String s, char oldSub, String newSub) {
    if ((s == null) || (newSub == null)) {
      return null;
    }

    StringMaker sm = new StringMaker();

    char[] c = s.toCharArray();

    for (int i = 0; i < c.length; i++) {
      if (c[i] == oldSub) {
        sm.append(newSub);
      }
      else {
        sm.append(c[i]);
      }
    }

    return sm.toString();
  }

  public static String replace(String s, String oldSub, String newSub) {
    if ((s == null) || (oldSub == null) || (newSub == null)) {
      return null;
    }

    int y = s.indexOf(oldSub);

    if (y >= 0) {
      StringMaker sm = new StringMaker();

      int length = oldSub.length();
      int x = 0;

      while (x <= y) {
        sm.append(s.substring(x, y));
        sm.append(newSub);
        x = y + length;
        y = s.indexOf(oldSub, x);
      }

      sm.append(s.substring(x));

      return sm.toString();
    }

    return s;
  }

  private static String replace(String s, String[] oldSubs, String[] newSubs)
  {
    if ((s == null) || (oldSubs == null) || (newSubs == null)) {
      return null;
    }

    if (oldSubs.length != newSubs.length) {
      return s;
    }

    for (int i = 0; i < oldSubs.length; i++) {
      s = replace(s, oldSubs[i], newSubs[i]);
    }

    return s;
  }

  private static String replaceValues(String s, String begin, String end, Map values)
  {
    if ((s == null) || (begin == null) || (end == null) || 
      (values == null) || (values.size() == 0))
    {
      return s;
    }

    StringMaker sm = new StringMaker(s.length());

    int pos = 0;
    while (true)
    {
      int x = s.indexOf(begin, pos);
      int y = s.indexOf(end, x + begin.length());

      if ((x == -1) || (y == -1)) {
        sm.append(s.substring(pos, s.length()));

        break;
      }

      sm.append(s.substring(pos, x + begin.length()));

      String oldValue = s.substring(x + begin.length(), y);

      String newValue = (String)values.get(oldValue);

      if (newValue == null) {
        newValue = oldValue;
      }

      sm.append(newValue);

      pos = y;
    }

    return sm.toString();
  }

  private static String safePath(String path) {
    return replace(
      path, "//", "/");
  }

  private static String shorten(String s) {
    return shorten(s, 20);
  }

  private static String shorten(String s, int length) {
    return shorten(s, length, "...");
  }

  private static String shorten(String s, String suffix) {
    return shorten(s, 20, suffix);
  }

  private static String shorten(String s, int length, String suffix) {
    if ((s == null) || (suffix == null)) {
      return null;
    }

    if (s.length() > length) {
      for (int j = length; j >= 0; j--) {
        if (Character.isWhitespace(s.charAt(j))) {
          length = j;

          break;
        }
      }

      StringMaker sm = new StringMaker();

      sm.append(s.substring(0, length));
      sm.append(suffix);

      s = sm.toString();
    }

    return s;
  }

  private static String[] split(String s) {
    return split(s, ",");
  }

  private static String[] split(String s, String delimiter) {
    if ((s == null) || (delimiter == null)) {
      return new String[0];
    }

    s = s.trim();

    if (!s.endsWith(delimiter)) {
      StringMaker sm = new StringMaker();

      sm.append(s);
      sm.append(delimiter);

      s = sm.toString();
    }

    if (s.equals(delimiter)) {
      return new String[0];
    }

    List nodeValues = new ArrayList();

    if ((delimiter.equals("\n")) || (delimiter.equals("\r"))) {
      try {
        BufferedReader br = new BufferedReader(new StringReader(s));

        String line = null;

        while ((line = br.readLine()) != null) {
          nodeValues.add(line);
        }

        br.close();
      }
      catch (IOException ioe) {
        _log.error(ioe.getMessage());
      }
    }
    else {
      int offset = 0;
      int pos = s.indexOf(delimiter, offset);

      while (pos != -1) {
        nodeValues.add(new String(s.substring(offset, pos)));

        offset = pos + delimiter.length();
        pos = s.indexOf(delimiter, offset);
      }
    }

    return (String[])nodeValues.toArray(new String[nodeValues.size()]);
  }

  private static boolean[] split(String s, boolean x) {
    return split(s, ",", x);
  }

  private static boolean[] split(String s, String delimiter, boolean x) {
    String[] array = split(s, delimiter);
    boolean[] newArray = new boolean[array.length];

    for (int i = 0; i < array.length; i++) {
      boolean value = x;
      try
      {
        value = Boolean.valueOf(array[i]).booleanValue();
      }
      catch (Exception localException)
      {
      }
      newArray[i] = value;
    }

    return newArray;
  }

  private static double[] split(String s, double x) {
    return split(s, ",", x);
  }

  private static double[] split(String s, String delimiter, double x) {
    String[] array = split(s, delimiter);
    double[] newArray = new double[array.length];

    for (int i = 0; i < array.length; i++) {
      double value = x;
      try
      {
        value = Double.parseDouble(array[i]);
      }
      catch (Exception localException)
      {
      }
      newArray[i] = value;
    }

    return newArray;
  }

  private static float[] split(String s, float x) {
    return split(s, ",", x);
  }

  private static float[] split(String s, String delimiter, float x) {
    String[] array = split(s, delimiter);
    float[] newArray = new float[array.length];

    for (int i = 0; i < array.length; i++) {
      float value = x;
      try
      {
        value = Float.parseFloat(array[i]);
      }
      catch (Exception localException)
      {
      }
      newArray[i] = value;
    }

    return newArray;
  }

  private static int[] split(String s, int x) {
    return split(s, ",", x);
  }

  private static int[] split(String s, String delimiter, int x) {
    String[] array = split(s, delimiter);
    int[] newArray = new int[array.length];

    for (int i = 0; i < array.length; i++) {
      int value = x;
      try
      {
        value = Integer.parseInt(array[i]);
      }
      catch (Exception localException)
      {
      }
      newArray[i] = value;
    }

    return newArray;
  }

  private static long[] split(String s, long x) {
    return split(s, ",", x);
  }

  private static long[] split(String s, String delimiter, long x) {
    String[] array = split(s, delimiter);
    long[] newArray = new long[array.length];

    for (int i = 0; i < array.length; i++) {
      long value = x;
      try
      {
        value = Long.parseLong(array[i]);
      }
      catch (Exception localException)
      {
      }
      newArray[i] = value;
    }

    return newArray;
  }

  private static short[] split(String s, short x) {
    return split(s, ",", x);
  }

  private static short[] split(String s, String delimiter, short x) {
    String[] array = split(s, delimiter);
    short[] newArray = new short[array.length];

    for (int i = 0; i < array.length; i++) {
      short value = x;
      try
      {
        value = Short.parseShort(array[i]);
      }
      catch (Exception localException)
      {
      }
      newArray[i] = value;
    }

    return newArray;
  }

  private static String stripBetween(String s, String begin, String end)
  {
    if ((s == null) || (begin == null) || (end == null)) {
      return s;
    }

    StringMaker sm = new StringMaker(s.length());

    int pos = 0;
    while (true)
    {
      int x = s.indexOf(begin, pos);
      int y = s.indexOf(end, x + begin.length());

      if ((x == -1) || (y == -1)) {
        sm.append(s.substring(pos, s.length()));

        break;
      }

      sm.append(s.substring(pos, x));

      pos = y + end.length();
    }

    return sm.toString();
  }

  private static String trim(String s) {
    return trim(s, null);
  }

  private static String trim(String s, char c) {
    return trim(s, new char[] { c });
  }

  private static String trim(String s, char[] exceptions) {
    if (s == null) {
      return null;
    }

    char[] charArray = s.toCharArray();

    int len = charArray.length;

    int x = 0;
    int y = charArray.length;

    for (int i = 0; i < len; i++) {
      char c = charArray[i];

      if (!_isTrimable(c, exceptions)) break;
      x = i + 1;
    }

    for (int i = len - 1; i >= 0; i--) {
      char c = charArray[i];

      if (!_isTrimable(c, exceptions)) break;
      y = i;
    }

    if ((x != 0) || (y != len)) {
      return s.substring(x, y);
    }

    return s;
  }

  private static String trimLeading(String s)
  {
    return trimLeading(s, null);
  }

  private static String trimLeading(String s, char c) {
    return trimLeading(s, new char[] { c });
  }

  private static String trimLeading(String s, char[] exceptions) {
    if (s == null) {
      return null;
    }

    char[] charArray = s.toCharArray();

    int len = charArray.length;

    int x = 0;
    int y = charArray.length;

    for (int i = 0; i < len; i++) {
      char c = charArray[i];

      if (!_isTrimable(c, exceptions)) break;
      x = i + 1;
    }

    if ((x != 0) || (y != len)) {
      return s.substring(x, y);
    }

    return s;
  }

  private static String trimTrailing(String s)
  {
    return trimTrailing(s, null);
  }

  private static String trimTrailing(String s, char c) {
    return trimTrailing(s, new char[] { c });
  }

  private static String trimTrailing(String s, char[] exceptions) {
    if (s == null) {
      return null;
    }

    char[] charArray = s.toCharArray();

    int len = charArray.length;

    int x = 0;
    int y = charArray.length;

    for (int i = len - 1; i >= 0; i--) {
      char c = charArray[i];

      if (!_isTrimable(c, exceptions)) break;
      y = i;
    }

    if ((x != 0) || (y != len)) {
      return s.substring(x, y);
    }

    return s;
  }

  public static String upperCase(String s)
  {
    if (s == null) {
      return null;
    }

    return s.toUpperCase();
  }

  public static String upperCaseFirstLetter(String s)
  {
    char[] chars = s.toCharArray();

    if ((chars[0] >= 'a') && (chars[0] <= 'z')) {
      chars[0] = (char)(chars[0] - ' ');
    }

    return new String(chars);
  }

  private static String wrap(String text) {
    return wrap(text, 80, "\n");
  }

  private static String wrap(String text, int width, String lineSeparator) {
    if (text == null) {
      return null;
    }

    StringMaker sm = new StringMaker();
    try
    {
      BufferedReader br = new BufferedReader(new StringReader(text));

      String s = "";

      while ((s = br.readLine()) != null)
        if (s.length() == 0) {
          sm.append(lineSeparator);
        }
        else {
          String[] tokens = s.split(" ");
          boolean firstWord = true;
          int curLineLength = 0;

          for (int i = 0; i < tokens.length; i++) {
            if (!firstWord) {
              sm.append(" ");
              curLineLength++;
            }

            if (firstWord) {
              sm.append(lineSeparator);
            }

            sm.append(tokens[i]);

            curLineLength += tokens[i].length();

            if (curLineLength >= width) {
              firstWord = true;
              curLineLength = 0;
            }
            else {
              firstWord = false;
            }
          }
        }
    }
    catch (IOException ioe)
    {
      _log.error(ioe.getMessage());
    }

    return sm.toString();
  }

  private static boolean _isTrimable(char c, char[] exceptions) {
    if ((exceptions != null) && (exceptions.length > 0)) {
      for (int i = 0; i < exceptions.length; i++) {
        if (c == exceptions[i]) {
          return false;
        }
      }
    }

    return Character.isWhitespace(c);
  }
  
	/**
	 * 过滤特殊字符
	 * ~!！@$￥^……&*()（）[]{}|”"、# % \ ;:?：【】？『』 空格 tab
	 * @param str
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static String filterSpecialChar(String str) throws PatternSyntaxException {
		// 只允许字母和数字
		// String regEx = "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
//		String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		String regEx = "[`~!@#$%^&*()+=|{}':;',//[//]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		String res = m.replaceAll("").trim();
		//替换掉[] 『』
		res = res.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("『", "").replaceAll("』", "");
		//替换掉\和空格 tab "  	str=" 海豚浏览器 "，使用第一个替换不了，最后又添加了一个替换。
		res = res.replaceAll("\\\\", "").replaceAll("	", "").replaceAll(" ", "").replaceAll("\"", "").replaceAll(" ", "");
		return res;
		//old->1~!！@$￥^……&*()（）[]『』{}|:"“”？、path1endstartC:/盘C:\ProGram# % \ ;:? ""	“” 中文：path2end【】】【*adCVs*34_a _09_b5*[/435^*&城池()^$$&*).{}+.|.)%%*(*.中国}34{45[]12.fd'*&999下面是中文的字符￥……{}【】。，；’“‘”？英文问号?.apk
		//new->1path1endstartC盘CProGram中文path2endadCVs34_a_09_b5435城池....中国344512.fd999下面是中文的字符英文问号.apk
	}
	
	/**
	 * 把逗号间隔的字符串，去除空元素和重复元素
	 * @author maojd
	 * @param str 要去重的字符串。eg：",,,123, , , 456,123   , ,"
	 * @return 返回去重，去空后的结果。 eg: "123,456"
	 */
	public static String filterRepeatElement(String str){
		String strRes = "";
		if(StringUtils.isNotBlank(str)){
			strRes = str;
			strRes = strRes.replace(" ", "");
			strRes = strRes.replace("null", "");
			String [] strResArr = strRes.split(",");
			List<String> list = new ArrayList<String>();
			Collections.addAll(list, strResArr);
			
			//去重
			Set<String> set = new HashSet<String>();
			set.addAll(list);
			list = new ArrayList<String>(set);
			
			strRes = StringUtils.join(list, ",");
			if(strRes.startsWith(",")){
				strRes = strRes.replaceFirst(",", "");
			}
			if(strRes.endsWith(",")){
				strRes = strRes.substring(0, strRes.lastIndexOf(","));
			}
			strRes = strRes.replaceAll(",,", ",");
			
		}
		return strRes;
	}
	
	/**
	 * strArr 逗号分割的字符串元素,分割以后 是否包含 str元素
	 * @author maojd
	 * @param strArr 字符串数组逗号join。eg: "123,456"
	 * @param str	 要校验的字符从。 eg: "123  "
	 * @return
	 */
	public static boolean contains(String strArr, String str){
		boolean b = false;
		if( null != strArr && str != null){
			strArr = StringUtil.filterRepeatElement(strArr);
			str = str.trim();
			String [] strResArr = strArr.split(",");
			List<String> list = new ArrayList<String>();
			Collections.addAll(list, strResArr);
			if(list.contains(str)){
				b = true;
			}
		}
		return b;
	}
	
	/**
	 * 计算两个逗号间隔的字符串数据的交集。会忽略空格
	 * @param strArr1 eg: "1,334, 34, 21"
	 * @param strArr2 eg: "2 1,  334,   3"
	 * @return "21,334"
	 */
	public static String intersect(String strArr1, String strArr2){
		String res = "";
		if(null != strArr1 && null != strArr2){
			strArr1 = StringUtil.filterRepeatElement(strArr1);
			strArr2 = StringUtil.filterRepeatElement(strArr2);
			String [] arr1 = strArr1.split(",");
			String [] arr2 = strArr2.split(",");
			String [] arrRes = StringUtil.intersect(arr1, arr2);
			res = StringUtils.join(arrRes, ",");
		}
		return StringUtil.filterRepeatElement(res);
	}
	
	/**
	 * 计算两个字符串数组的差集。 会忽略空格
	 * @param strArr1 eg:123, 456,  789
	 * @param strArr2 eg:456,789,0
	 * @return 123,0
	 */
	public static String minus(String strArr1, String strArr2) {
		String res = "";
		if(null != strArr1 && null != strArr2){
			strArr1 = StringUtil.filterRepeatElement(strArr1);
			strArr2 = StringUtil.filterRepeatElement(strArr2);
			String [] arr1 = strArr1.split(",");
			String [] arr2 = strArr2.split(",");
			String [] arrRes = StringUtil.minus(arr1, arr2);
			res = StringUtils.join(arrRes, ",");
		}
		return StringUtil.filterRepeatElement(res);
	}
	
	/**
	 * 过滤掉delArr含有的元素。
	 * @param srcArr 1,2,3
	 * @param delArr 3,4,5
	 * @return		 1,2
	 */
	public static String delOtherArr(String srcArr, String delArr){
		srcArr = StringUtil.filterRepeatElement(srcArr);
		delArr = StringUtil.filterRepeatElement(delArr);
		String res = "";
		//如果原数组为空
		if(StringUtils.isNotBlank(srcArr) && StringUtils.isNotBlank(delArr)){
			//交集 3
			String ins = StringUtil.intersect(srcArr, delArr);
			//差集 ins = 1，2		  1,2,3 和 3
			res = StringUtil.minus(srcArr, ins);
		}else if(StringUtils.isNotBlank(srcArr) && StringUtils.isBlank(delArr)){
			res = srcArr;
		}else if(StringUtils.isBlank(srcArr)){
			//res = "";
		}
		return res;
	}
	
	/**
	 * 求两个字符串数组的并集，利用set的元素唯一性   。没有去空格
	 * @param arr1
	 * @param arr2
	 * @return
	 */
    public static String[] union(String[] arr1, String[] arr2) {   
        Set<String> set = new HashSet<String>();   
        for (String str : arr1) {   
            set.add(str);   
        }   
        for (String str : arr2) {   
            set.add(str);   
        }   
        String[] result = {};   
        return set.toArray(result);   
    }   
  
    /**
     * 求两个数组的交集   没有去空格
     * @param arr1
     * @param arr2
     * @return
     */
    public static String[] intersect(String[] arr1, String[] arr2) {   
        Map<String, Boolean> map = new HashMap<String, Boolean>();   
        LinkedList<String> list = new LinkedList<String>();   
        for (String str : arr1) {   
            if (!map.containsKey(str)) {   
                map.put(str, Boolean.FALSE);   
            }   
        }   
        for (String str : arr2) {   
            if (map.containsKey(str)) {   
                map.put(str, Boolean.TRUE);   
            }   
        }   
  
        for (Entry<String, Boolean> e : map.entrySet()) {   
            if (e.getValue().equals(Boolean.TRUE)) {   
                list.add(e.getKey());   
            }   
        }   
  
        String[] result = {};   
        return list.toArray(result);   
    }   
  
    /**
     * 求两个数组的差集. 没有去空格
     * @param arr1
     * @param arr2
     * @return
     */
    public static String[] minus(String[] arr1, String[] arr2) {   
        LinkedList<String> list = new LinkedList<String>();   
        LinkedList<String> history = new LinkedList<String>();   
        String[] longerArr = arr1;   
        String[] shorterArr = arr2;   
        //找出较长的数组来减较短的数组   
        if (arr1.length > arr2.length) {   
            longerArr = arr2;   
            shorterArr = arr1;   
        }   
        for (String str : longerArr) {   
            if (!list.contains(str)) {   
                list.add(str);   
            }   
        }   
        for (String str : shorterArr) {   
            if (list.contains(str)) {   
                history.add(str);   
                list.remove(str);   
            } else {   
                if (!history.contains(str)) {   
                    list.add(str);   
                }   
            }   
        }   
  
        String[] result = {};   
        return list.toArray(result);   
    }
    /**
     * 功能:逗号分隔加上单引号
     * @param str
     * @return
     */
    public static String spiltAddQuotes(String str){
    	StringBuilder strsb = new StringBuilder();
    	if(str.indexOf(",")!=-1){
    		String [] strArr = str.split(",");
    		for (int i = 0; i < strArr.length; i++) {
    			strsb.append("'").append(strArr[i]).append("'");
    			if(i !=  strArr.length -1){
    				strsb.append(",");
    			}
			}
    		return strsb.toString();
    	}else{
    		return str;
    	}
    }
    
    /**
     * 获取定长随机数。
     * @param strLength 随机数的长度 长度范围0-6,超过此范围返回null
     * @return
     */
	public static String getRandomFixLenth(int strLength) {
		if(strLength >0 && strLength <= 6){
			Random rm = new Random();
			// 获得随机数
			// Long pross = Long.valueOf((1 + rm.nextDouble()) * Math.pow(10,strLength));

			// 将获得的获得随机数转化为字符串
			// String fixLenthString = String.valueOf(pross);
			String fixLenthString = String.valueOf((1 + rm.nextDouble())
					* Math.pow(10, strLength));
			// 返回固定的长度的随机数
			return fixLenthString.substring(1, strLength + 1);
		}else{
			return null;
		}
		
	} 
    
	/**
	 * 把大写字母转化为 _小写字母
	 * @param str appName
	 * @return app_name
	 */
	public static String toRecordStr(String str){
		StringBuffer res = new StringBuffer();
		if (StringUtils.isNotBlank(str)) {
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if (Character.isUpperCase(c)) {
					res.append("_" + Character.toLowerCase(c));
				} else {
					res.append(c);
				}
			}
		}
		return res.toString();
	}
	
	/**
	 * 加密邮箱地址，将邮箱地址名称（@前）部分转义成保留前两位和最后一位中间用***代替
	 * @param mailStr
	 * @return encodeStr
	 * */
	public static String encodeMailAddress(String mailStr){
		try {
			 String mailExt = mailStr.substring(mailStr.indexOf("@"), mailStr.length());
	         String mailCode = mailStr.substring(0,mailStr.indexOf("@"));
	         if(mailCode.length()==1){
	       	  	mailCode = mailCode+"***";
	         }else if(mailCode.length()==2) {
	         	mailCode = mailCode.charAt(0)+"***"+mailCode.charAt(1);
	         }else {
	         	mailCode = mailCode.substring(0,2)+"***"+mailCode.charAt(mailCode.length()-1);
	         }
			return mailCode+mailExt;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



  public static String bytes2Hex(String str) throws UnsupportedEncodingException {
    if(str.indexOf("\\x") != -1){
      return StringUtils.replace(str, "\\x", "");
    }
    StringBuilder sb = new StringBuilder();
    for (byte b : str.getBytes("utf-8")) {
      sb.append(String.format("%02X", b));
    }

    return sb.toString();
  }

  public static String parseHexString(String s) throws UnsupportedEncodingException {
//    if(s.indexOf("\\x") == -1){
//      return s;
//    }

//    String rawHex = StringUtil.replace(s, "\\x", "");
    String rawHex = s;
    int len = rawHex.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(rawHex.charAt(i), 16) << 4)
              + Character.digit(rawHex.charAt(i+1), 16));
    }
    return new String(data, "utf-8");
  }

  //十六进制
  public static boolean isHexNumber(String str){
    boolean flag = false;
    for(int i=0;i<str.length();i++){
      char cc = str.charAt(i);
      if(cc=='0'||cc=='1'||cc=='2'||cc=='3'||cc=='4'||cc=='5'||cc=='6'||cc=='7'||cc=='8'||cc=='9'||cc=='A'||cc=='B'||cc=='C'||
              cc=='D'||cc=='E'||cc=='F'||cc=='a'||cc=='b'||cc=='c'||cc=='c'||cc=='d'||cc=='e'||cc=='f'){
        flag = true;
      }
    }
    return flag;
  }

  public static void main(String[] args) {
String l= "11233";
    	System.out.println(spiltAddQuotes(l));
	}
    
}