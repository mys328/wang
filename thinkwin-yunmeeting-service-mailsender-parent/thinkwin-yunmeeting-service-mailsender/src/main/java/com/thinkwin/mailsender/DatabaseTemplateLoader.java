package com.thinkwin.mailsender;

import com.thinkwin.config.service.ConfigService;
import freemarker.cache.TemplateLoader;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

@Component
public class DatabaseTemplateLoader implements TemplateLoader {

	@Autowired
	ConfigService configService;

	@Override
	public Object findTemplateSource(String name) throws IOException {
		byte[] decoded = Base64.decodeBase64(configService.get(name));
		return new String(decoded, "UTF-8");
	}

	@Override
	public long getLastModified(Object templateSource) {
		return -1;
	}

	@Override
	public Reader getReader(Object templateSource, String s) throws IOException {
		return new StringReader(((String) templateSource));
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {

	}
}
