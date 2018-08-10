package com.thinkwin.ssm.generator;

import org.apache.ibatis.io.Resources;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Time  : 16-8-23 上午11:09
 */
public class MybatisGenerator {

	// 加载 log4j 配置文件
	static {
		try {
			String resource = "log4j.properties";
			InputStream inputStream = Resources.getResourceAsStream(resource);
			PropertyConfigurator.configure(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void generator() throws Exception {
		List<String> warnings = new ArrayList<String>();
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(MybatisGenerator.class.getResourceAsStream("/config/generator/generatorConfig.xml"));
		DefaultShellCallback callback = new DefaultShellCallback(true);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		myBatisGenerator.generate(null);
	}
	/*@Test
	public void generator() throws Exception {
		List<String> warnings = new ArrayList<String>();
		File configFile = new File(String.valueOf(MybatisGenerator.class.getResourceAsStream("/config/generator/generatorConfig.xml")));
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(configFile);
		DefaultShellCallback callback = new DefaultShellCallback(true);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		myBatisGenerator.generate(null);
	}*/
	
	public static void main(String[] args) {
		MybatisGenerator gen = new MybatisGenerator();
		try {
			gen.generator();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
