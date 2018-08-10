package com.thinkwin.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class NullSerializer extends JsonSerializer<Object> {

	@Override
	public void serialize(Object o, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
		if(o instanceof String ){
			generator.writeString("");
		}
		else{
			generator.writeObject(null);
		}
	}
}
