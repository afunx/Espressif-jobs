package com.afunx.xml.model.parser;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.afunx.xml.model.SoftApXmlModel;

public class PullSoftApXmlParser implements SoftApXmlParser {

	@Override
	public List<SoftApXmlModel> parse(InputStream is) throws Exception {

		List<SoftApXmlModel> softaps = null;
		SoftApXmlModel softap = null;

		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "UTF-8");

		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				softaps = new ArrayList<SoftApXmlModel>();
				break;
			case XmlPullParser.START_TAG:
				if (parser.getName().equals(NODE_OBJECT_KEY)) {
					softap = new SoftApXmlModel();
				} else if (parser.getName().equals(NODE_ATTR_KEY_SSID)) {
					eventType = parser.next();
					softap.setSsid(parser.getText());
				}  else if (parser.getName().equals(NODE_ATTR_KEY_PASSWORD)) {
					eventType = parser.next();
					softap.setPassword(parser.getText());
				}   else if (parser.getName().equals(NODE_ATTR_KEY_CIPHER_TYPE)) {
					eventType = parser.next();
					softap.setCipherType(Integer.parseInt(parser.getText()));
				}
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals(NODE_OBJECT_KEY)) {
					softaps.add(softap);
					softap = null;
				}
				break;
			}
			eventType = parser.next();
		}

		return softaps;
	}

	@Override
	public String serialize(List<SoftApXmlModel> softaps) throws Exception {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);
		serializer.startTag("", NODE_OBJECTS_KEY);
		for (SoftApXmlModel softap : softaps) {
			serializer.startTag("", NODE_OBJECT_KEY);
			
			serializer.startTag("", NODE_ATTR_KEY_SSID);
			serializer.text(softap.getSsid());
			serializer.endTag("", NODE_ATTR_KEY_SSID);
			
			serializer.startTag("", NODE_ATTR_KEY_PASSWORD);
			serializer.text(softap.getPassword());
			serializer.endTag("", NODE_ATTR_KEY_PASSWORD);
			
			serializer.startTag("", NODE_ATTR_KEY_CIPHER_TYPE);
			serializer.text(softap.getCipherType()+"");
			serializer.endTag("", NODE_ATTR_KEY_CIPHER_TYPE);
			
			serializer.endTag("", NODE_OBJECT_KEY);
		}
		
		serializer.endTag("", NODE_OBJECTS_KEY);
		serializer.endDocument();
		
		String unformattedXml = writer.toString();
		return XmlFormatter.format(unformattedXml);
	}

}
