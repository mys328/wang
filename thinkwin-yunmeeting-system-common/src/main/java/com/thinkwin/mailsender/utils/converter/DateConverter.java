package com.thinkwin.mailsender.utils.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

/**
 * 自定义的日期类型转换器
 * Time  : 16-8-24 下午1:13
 */
public class DateConverter implements Converter<String, Date> {

	/**
	 * 从字符串类型的日期转换成 Date 类型的日期
	 */
	public Date convert(String source) {

		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		try {
			return simpleDateFormat.parse(source);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
