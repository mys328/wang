package com.thinkwin.document.service;

import com.thinkwin.document.DocumentService;
import com.thinkwin.fileupload.service.FileUploadService;
import org.jodconverter.OfficeDocumentConverter;
import org.jodconverter.office.ExternalOfficeManagerBuilder;
import org.jodconverter.office.OfficeConnectionProtocol;
import org.jodconverter.office.OfficeException;
import org.jodconverter.office.OfficeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

@Service
public class PDFConvertService implements DocumentService {

	@Value("${server.port}")
	private Integer port = 8100;

	private static OfficeDocumentConverter converter = null;

	@PostConstruct
	public void startService() {
		ExternalOfficeManagerBuilder builder = new ExternalOfficeManagerBuilder();
		builder.setConnectionProtocol(OfficeConnectionProtocol.SOCKET);
		builder.setPortNumber(port);
		OfficeManager officeManager= builder.build();
		converter = new OfficeDocumentConverter(officeManager);
	}

	@Override
	public boolean convert(String inputFilePath, String outputFilePath) {
		File input = new File(inputFilePath);
		File output = new File(outputFilePath);
		try {
			converter.convert(input, output);
			return true;
		} catch (OfficeException e) {
			e.printStackTrace();
		}
		return false;
	}
}