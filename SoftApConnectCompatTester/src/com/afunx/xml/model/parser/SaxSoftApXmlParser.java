package com.afunx.xml.model.parser;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.afunx.xml.model.SoftApXmlModel;

public class SaxSoftApXmlParser implements SoftApXmlParser {

	@Override
	public List<SoftApXmlModel> parse(InputStream is) throws Exception {
		// get SAXParserFactory instance
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// get SAXParser instance from SAXParserFactory
		SAXParser parser = factory.newSAXParser();
		// implement custom handler
		MyHandler handler = new MyHandler();
		// parse xml inputstream by custom handler
		parser.parse(is, handler);
		return handler.getSoftAps();
	}

	@Override
	public String serialize(List<SoftApXmlModel> softaps) throws Exception {
		// get SAXTransformerFactory instance
		SAXTransformerFactory factory = (SAXTransformerFactory) TransformerFactory.newInstance();
		// get TransformerHandler instance from SAXTransformerFactory
		TransformerHandler handler = factory.newTransformerHandler();
		// get Transformer instance from TransformerHandler
		Transformer transformer = handler.getTransformer();
		// set output coding
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		// set add indent
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        // set ignore xml declaration
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        
        StringWriter writer = new StringWriter();  
        Result result = new StreamResult(writer);  
        handler.setResult(result);
        
        // uri for name space, don't set null
        String uri = "";
        // local name for name space, don't set null
        String localName = "";
        
        handler.startDocument();
        handler.startElement(uri, localName, NODE_OBJECTS_KEY, null);
        
        char[] ch = null;
        for (SoftApXmlModel softap: softaps) {
        	
        	// sofap start
        	handler.startElement(uri, localName, NODE_OBJECT_KEY, null);
        	
        	// ssid
        	handler.startElement(uri, localName, NODE_ATTR_KEY_SSID, null);
        	ch = String.valueOf(softap.getSsid()).toCharArray();
        	handler.characters(ch, 0, ch.length);
        	handler.endElement(uri, localName, NODE_ATTR_KEY_SSID);
        	
        	// password
        	handler.startElement(uri, localName, NODE_ATTR_KEY_PASSWORD, null);
        	ch = String.valueOf(softap.getPassword()).toCharArray();
        	handler.characters(ch, 0, ch.length);
        	handler.endElement(uri, localName, NODE_ATTR_KEY_PASSWORD);
        	
        	// cipher-type
        	handler.startElement(uri, localName, NODE_ATTR_KEY_CIPHER_TYPE, null);
        	ch = String.valueOf(softap.getCipherType()).toCharArray();
        	handler.characters(ch, 0, ch.length);
        	handler.endElement(uri, localName, NODE_ATTR_KEY_CIPHER_TYPE);
        	
        	// detail
        	handler.startElement(uri, localName, NODE_ATTR_KEY_DETAIL, null);
        	ch = String.valueOf(softap.getDetail()).toCharArray();
        	handler.characters(ch, 0, ch.length);
        	handler.endElement(uri, localName, NODE_ATTR_KEY_DETAIL);
        	
        	// selected
        	handler.startElement(uri, localName, NODE_ATTR_KEY_SELECTED, null);
        	ch = String.valueOf(softap.getIsSelected()).toCharArray();
        	handler.characters(ch, 0, ch.length);
        	handler.endElement(uri, localName, NODE_ATTR_KEY_SELECTED);
        	
        	// sofap end
        	handler.endElement(uri, localName, NODE_OBJECT_KEY);
        }
        
        handler.endElement(uri, localName, NODE_OBJECTS_KEY);
        handler.endDocument();
        
		String unformattedXml = writer.toString();
		return XmlFormatter.format(unformattedXml);
	}

	private class MyHandler extends DefaultHandler {
		private List<SoftApXmlModel> mSoftAps;
		private SoftApXmlModel mSoftAp;
		private StringBuilder builder;

		public List<SoftApXmlModel> getSoftAps() {
			return mSoftAps;
		}

		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
			mSoftAps = new ArrayList<SoftApXmlModel>();
			builder = new StringBuilder();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, qName, attributes);
			if (localName.equals(NODE_OBJECT_KEY)) {
				mSoftAp = new SoftApXmlModel();
			}
			builder.setLength(0);
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			super.characters(ch, start, length);
			builder.append(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			super.endElement(uri, localName, qName);
			if (localName.equals(NODE_OBJECT_KEY)) {
				mSoftAps.add(mSoftAp);
			} else if (localName.equals(NODE_ATTR_KEY_SSID)) {
				mSoftAp.setSsid(builder.toString());
			} else if (localName.equals(NODE_ATTR_KEY_PASSWORD)) {
				mSoftAp.setPassword(builder.toString());
			} else if (localName.equals(NODE_ATTR_KEY_CIPHER_TYPE)) {
				mSoftAp.setCipherType(Integer.parseInt(builder.toString()));
			} else if (localName.equals(NODE_ATTR_KEY_DETAIL)) {
				mSoftAp.setDetail(builder.toString());
			} else if (localName.equals(NODE_ATTR_KEY_SELECTED)) {
				mSoftAp.setIsSelected(Boolean.parseBoolean(builder.toString()));
			}
		}
	}
}
