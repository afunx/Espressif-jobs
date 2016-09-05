package com.afunx.xml.model.parser;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.afunx.xml.model.SoftApXmlModel;

public class DomSoftApXmlParser implements SoftApXmlParser {

	@Override
	public List<SoftApXmlModel> parse(InputStream is) throws Exception {
		List<SoftApXmlModel> softaps = new ArrayList<SoftApXmlModel>();
		// get DocumentBuilderFactory instance
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// get DocumentBuilder instance from DocumentBuilderFactory
		DocumentBuilder builder = factory.newDocumentBuilder();
		// parse inputstream
		Document doc = builder.parse(is);
		Element rootElement = doc.getDocumentElement();
		NodeList items = rootElement.getElementsByTagName(NODE_OBJECT_KEY);
		for (int i = 0; i < items.getLength(); ++i) {
			SoftApXmlModel softap = new SoftApXmlModel();
			Node item = items.item(i);
			NodeList properties = item.getChildNodes();
			for (int j = 0; j<properties.getLength();++j){
				Node property = properties.item(j);
				String nodeName = property.getNodeName();
				if(nodeName.equals(NODE_ATTR_KEY_SSID)){
					String nodeValue = property.getFirstChild().getNodeValue();
					softap.setSsid(nodeValue);
				} else if(nodeName.equals(NODE_ATTR_KEY_PASSWORD)){
					String nodeValue = property.getFirstChild().getNodeValue();
					softap.setPassword(nodeValue);
				} else if(nodeName.equals(NODE_ATTR_KEY_CIPHER_TYPE)) {
					String nodeValue = property.getFirstChild().getNodeValue();
					softap.setCipherType(Integer.parseInt(nodeValue));
				}
			}
			softaps.add(softap);
		}
		return softaps;
	}

	@Override
	public String serialize(List<SoftApXmlModel> softaps) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder = factory.newDocumentBuilder();  
        Document doc = builder.newDocument();
        
        Element rootElement = doc.createElement(NODE_OBJECTS_KEY);
        
        for (SoftApXmlModel softap : softaps) {
        	Element softapElement = doc.createElement(NODE_OBJECT_KEY);
        	
        	Element ssidElement = doc.createElement(NODE_ATTR_KEY_SSID);
        	ssidElement.setTextContent(softap.getSsid());
        	softapElement.appendChild(ssidElement);
        	
        	Element passwordElement = doc.createElement(NODE_ATTR_KEY_PASSWORD);
        	ssidElement.setTextContent(softap.getPassword());
        	softapElement.appendChild(passwordElement);
        	
        	Element cipherTypeElement = doc.createElement(NODE_ATTR_KEY_CIPHER_TYPE);
        	ssidElement.setTextContent(softap.getCipherType()+"");
        	softapElement.appendChild(cipherTypeElement);
        	
        	rootElement.appendChild(softapElement);
        }
        
        doc.appendChild(rootElement);
        
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        
        StringWriter writer = new StringWriter();
        Source source = new DOMSource(doc);
        Result result = new StreamResult(writer);
        transformer.transform(source, result);
        
        String unformattedXml = writer.toString();
		return XmlFormatter.format(unformattedXml);
	}

}
