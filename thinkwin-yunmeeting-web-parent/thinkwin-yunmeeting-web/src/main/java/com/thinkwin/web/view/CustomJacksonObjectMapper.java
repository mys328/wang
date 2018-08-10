package com.thinkwin.web.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.thinkwin.common.NullSerializer;

public class CustomJacksonObjectMapper extends ObjectMapper {
	public CustomJacksonObjectMapper() {
		super();

		DefaultSerializerProvider.Impl sp = new DefaultSerializerProvider.Impl();
		sp.setNullValueSerializer(new NullSerializer());
		this.setSerializerProvider(sp);
	}
}
