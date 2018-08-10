package com.thinkwin.mailsender.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.thinkwin.mailsender.DatabaseTemplateLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 邮件内容模板生产工厂类
 * 
 * @Description:
 * @project mailUtil
 */
public class TemplateFactory {
	// 日志记录对象
	private static Logger log = LoggerFactory.getLogger(TemplateFactory.class);
	// 模板文件路径
	private static String templatePath = "/";
	// 模板文件内容编码
	private static final String ENCODING = "utf-8";
	// 模板生成配置
	private static Configuration conf = new Configuration();
	// 邮件模板缓存池
	private static Map<String, Template> tempMap = new HashMap<String, Template>();
	static {
		// 设置模板文件路径
//		conf.setClassForTemplateLoading(TemplateFactory.class, templatePath);
		conf.setTemplateLoader(new DatabaseTemplateLoader());
	}

	/**
	 * 通过模板文件名称获取指定模板
	 * 
	 * @param name
	 *            模板文件名称
	 * @return Template 模板对象
	 * @throws IOException
	 * @Description:
	 * @return Template
	 */
	private static Template getTemplateByName(String name) throws IOException {
		if (tempMap.containsKey(name)) {
			//log.debug("the template is already exist in the map,template name :"+ name);
			// 缓存中有该模板直接返回
			return tempMap.get(name);
		}
		// 缓存中没有该模板时，生成新模板并放入缓存中
		Template temp = conf.getTemplate(name);
		if(temp == null){
			log.info("vvvvvvvvvv发送邮件的模板文件没有发现："+name);
		}
		tempMap.put(name, temp);
		log.debug("the template is not found  in the map,template name :"+ name);
		return temp;
	}
	/**
	 * 通过字符串获取freeMarker 模板对象
	 * 
	 * @param name
	 *            模板文件名称
	 * @return Template 模板对象
	 * @throws IOException
	 * @Description:
	 * @return Template
	 */
	private static Template getTemplateByStringContent(String ftlContent) throws IOException {
		Template template=null;
        StringTemplateLoader stringLoader = new StringTemplateLoader();  
        if(StringUtils.isBlank(ftlContent)){
        	return null;
        }
        String templateContent=ftlContent;
        stringLoader.putTemplate("myTemplate",templateContent);  
        conf.setTemplateLoader(stringLoader);  
        try { 
            template = conf.getTemplate("myTemplate","utf-8");
        } catch (IOException e) {  
            e.printStackTrace();
        }
        return template;
	}

	/**
	 * 根据指定模板将内容输出到控制台
	 * 
	 * @param name
	 *            模板文件名称
	 * @param map
	 *            与模板内容转换对象
	 * @Description:
	 * @return void
	 */
	public static void outputToConsole(String name, Map<String, String> map) {
		try {
			// 通过Template可以将模板文件输出到相应的流
			Template temp = getTemplateByName(name);
			temp.setEncoding(ENCODING);
			temp.process(map, new PrintWriter(System.out));
		} catch (TemplateException e) {
			log.info(e.toString(), e);
		} catch (IOException e) {
			log.info(e.toString(), e);
		}
	}

	/**
	 * 根据指定模板将内容直接输出到文件
	 * 
	 * @param name
	 *            模板文件名称
	 * @param map
	 *            与模板内容转换对象
	 * @param outFile
	 *            输出的文件绝对路径
	 * @Description:
	 * @return void
	 */
	public static void outputToFile(String name, Map<String, String> map,
			String outFile) {
		FileWriter out = null;
		try {
			out = new FileWriter(new File(outFile));
			Template temp = getTemplateByName(name);
			temp.setEncoding(ENCODING);
			temp.process(map, out);
		} catch (IOException e) {
			log.info(e.toString(), e);
		} catch (TemplateException e) {
			log.info(e.toString(), e);
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				log.info(e.toString(), e);
			}
		}
	}

	/**
	 * 
	 * @param name
	 *            模板文件的名称
	 * @param map
	 *            与模板内容转换对象
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 * @Description:
	 * @return String
	 */
	public static String generateHtmlFromFtl(String name,
			Map<String, String> map) throws IOException, TemplateException {
		Writer out = new StringWriter(2048);
		Template temp = getTemplateByName(name);
		temp.setEncoding(ENCODING);
		temp.process(map, out);
		return out.toString();
	}
	
	/**
	 * 功能:以字符串方式生成freeMarker模板对象
	 * @param ftlContent
	 *            模板字符串内容
	 * @param map
	 *            与模板内容转换对象
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 * @Description:
	 * @return String
	 */
	public static String generateHtmlFromFtlContent(String ftlContent,
			Map<String, String> map) throws IOException, TemplateException {
		Writer out = new StringWriter(2048);
		Template temp = getTemplateByStringContent(ftlContent);
		temp.setEncoding(ENCODING);
		temp.process(map, out);
		return out.toString();
	}
}
