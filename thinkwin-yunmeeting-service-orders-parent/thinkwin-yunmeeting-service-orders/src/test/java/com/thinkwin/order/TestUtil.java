package com.thinkwin.order;

import com.thinkwin.common.utils.StringUtil;
import com.thinkwin.common.utils.TimeUtil;
import com.thinkwin.orders.util.OrderVoUtil;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class TestUtil {

	@Test
	public void testTruncate() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date a = new Date();
		Date b = DateUtils.addYears(a, 1);
		Date startTime = DateUtils.truncate(a, Calendar.DAY_OF_MONTH);
//		Date c = DateUtils.addYears(startTime, orderVo.getServiceTerm());
		Date start = formatter.parse("2018-01-28");
		Date end = DateUtils.addMonths(start, 1);

		long daysLeft = TimeUtil.getDateDiff(startTime, DateUtils.addMonths(startTime, 3), TimeUnit.DAYS);
		System.out.print(daysLeft);
	}

	public static String bytes2hex01(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X", b));
		}

		return sb.toString();
	}

	public static String methodV1_(byte[] bytes)
	{
		try{
			StringBuilder sb = new StringBuilder();
			for (byte b : bytes) {
				sb.append(String.format("%02X", b));
			}

			return sb.toString();
		}catch (Exception e){

		}
		return "";
	}

	public static String methodV2(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X", b));
		}

		return sb.toString();
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

	@Test
	public void testHexToString() throws UnsupportedEncodingException {
		String hex = bytes2hex01("九峰 苍翠九峰 苍翠九峰 苍翠九峰 苍翠\uD83D\uDEEB".getBytes("utf-8"));

		hex = "\\xE4\\xB9\\x9D\\xE5\\xB3\\xB0\\xE8\\x8B\\x8D\\xE7\\xBF\\xA0\\xF0\\x9F\\x9B\\xAB";

//		hex = "九峰苍翠";

		String a = StringUtil.parseHexString(StringUtil.bytes2Hex(hex));

		System.out.println(a);

		Assert.assertTrue(a != null);
	}

	@Test
	public void testLong(){
		long a = 0;
		Assert.assertTrue(a == 0);
		Assert.assertTrue(Long.compare(a, 0) == 0);
	}

	@Test
	public void testDoubleForamt(){
		String a = OrderVoUtil.formatAmount(0.23D);
		Assert.assertTrue("0.23".equals(a));

		String b = OrderVoUtil.formatAmount(149.995D);
		Assert.assertTrue("150.00".equals(b));
	}

	@Test
	public void testDate(){
		Date start = new Date();
		Date end = DateUtils.addHours(start, 23);
		end = DateUtils.truncate(end, Calendar.DAY_OF_MONTH);
		System.out.println(TimeUtil.getDateDiff(start, end, TimeUnit.DAYS));
	}

	@Test
	public void testDecimal(){
		BigDecimal fullYear = new BigDecimal("365");
		BigDecimal orderPrice = new BigDecimal("6600").setScale(6, RoundingMode.HALF_EVEN);
		orderPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		orderPrice = orderPrice.divide(fullYear, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(364));

		orderPrice = orderPrice.setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal x = new BigDecimal("3789.00");
		System.out.println(x.compareTo(orderPrice) == 0);
		System.out.println(x);
		System.out.println(orderPrice);
	}

}
