package com.thinkwin.common.utils.redis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.Validate;

/**
 * 字符串压缩工具
 * 
 * @author liusp
 * @created 2015-12-08 上午8:21:57
 */

public class CompressUtil {

	private static int cachesize =4096*5;
	//private static Inflater decompresser = new Inflater();
//	private static Deflater compresser = new Deflater();

	public static String compress(String input) {
		Validate.notBlank(input);
		byte[] bytes = compressBytes(input.getBytes());
		return new Base64().encodeAsString(bytes);
	}
	
	public static String uncompress(String input) {
		Validate.notBlank(input);
		Base64 b64 = new Base64();
		byte[] bytes = decompressBytes(b64.decode(input));
		return new String(bytes);
	}

	public static byte[] decompressBytes(byte input[]) {
		byte output[] = new byte[0];
		
		Inflater decompresser = new Inflater();
		decompresser.reset();
		
		ByteArrayOutputStream o = new ByteArrayOutputStream(input.length);
		try {
			byte[] buf = new byte[cachesize];
			decompresser.setInput(input);
			int got;
			while (!decompresser.finished()) {
				got = decompresser.inflate(buf);
				o.write(buf, 0, got);
			}
			output = o.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		  
			decompresser.end();  
	    }  
	      
		return output;
	}
	
	
	
	public static byte[] compressBytes(byte[] inputs){

		Deflater compresser = new Deflater();
		ByteArrayOutputStream stream = new ByteArrayOutputStream(inputs.length);
		try {
			compresser.reset();
			compresser.setInput(inputs);
			compresser.finish();
			byte outputs[] = new byte[0];
			byte[] bytes = new byte[cachesize];
			int value = 0;
			while(!compresser.finished()){
				value = compresser.deflate(bytes);
				stream.write(bytes,0, value);
			}
			outputs = stream.toByteArray();
			return outputs;
        } catch (Exception e) {
                e.printStackTrace();
        }finally {
			try{
				stream.close();
				compresser.end();
			}catch (Exception e){
				e.printStackTrace();
			}

		}
		return null;
	}
	
	public static void main(String[] args) {
		String str = "eJyrysjPS6/ITMzPKM2sQmIXp6UUp6WmFaUXpVaWFJWkl6empaakFaWCERimpo6qH1U/qn7wqAcA/fuYEQ==";
		String str1 = "zhongxiaohuizhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfeezhongxiaohuisfdsfefrgreytrtgwefedfrefrefrfrfrfee";
		String com = compress(str1);
		
		System.out.println(com);
		String decom = uncompress(com);
		System.out.println(decom);
		
	}

}
