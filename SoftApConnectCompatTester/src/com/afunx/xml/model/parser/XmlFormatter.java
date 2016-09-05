package com.afunx.xml.model.parser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XmlFormatter {

	public static String format(String unformattedXml) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new StringReader(unformattedXml));
		String formatedXml = null;
		XMLWriter writer = null;
		if (document != null) {
			try {
				StringWriter stringWriter = new StringWriter();
				OutputFormat format = new OutputFormat("    ", true);
				writer = new XMLWriter(stringWriter, format);
				writer.write(document);
				writer.flush();
				formatedXml = stringWriter.getBuffer().toString();
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return formatedXml;
	}
}
