package com.afunx.xml.model.parser;

import java.io.InputStream;
import java.util.List;

import com.afunx.xml.model.SoftApXmlModel;

public interface SoftApXmlParser {
	
	public List<SoftApXmlModel> parse(InputStream is) throws Exception;

	public String serialize(List<SoftApXmlModel> softaps) throws Exception;
	
	public final String NODE_OBJECTS_KEY = "softaps";
	
	public final String NODE_OBJECT_KEY = "softap";
	
	public final String NODE_ATTR_KEY_SSID = "ssid";
	
	public final String NODE_ATTR_KEY_PASSWORD = "password";
	
	public final String NODE_ATTR_KEY_CIPHER_TYPE = "cipher-type";
}
