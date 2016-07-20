package com.xabber.android.ui.device;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DeviceHandler extends DefaultHandler {

	private Personal_Center personal_Center;
	private List<Personal_Center> personal_CenterList;
	private String temp = "";
	private String localName = "";

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		personal_Center = new Personal_Center();
		personal_CenterList = new ArrayList<Personal_Center>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		this.localName = localName;
	}

	public List<Personal_Center> getParsedData() {
		return this.personal_CenterList;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		String data = new String(ch, start, length);
		if (localName.endsWith("uuid")) {
			temp = temp + data;
		} else if (localName.endsWith("uuname")) {
			temp = temp + data;
		} else if (localName.endsWith("coordinate")) {
			temp = temp + data;
		} else if (localName.endsWith("distance")) {
			temp = temp + data;
		}
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		if (localName.endsWith("uuid")) {
			personal_Center.setUid(temp.trim());
			temp = "";
		} else if (localName.endsWith("uuname")) {
			personal_Center.setUname(temp.trim());
			temp = "";
		} else if (localName.endsWith("coordinate")) {
			personal_Center.setCoordinate(temp.trim());
			temp = "";
		} else if (localName.endsWith("distance")) {
			personal_Center.setDistance(temp.trim());
			temp = "";
		} else if (localName.endsWith("watchbean")) {
			personal_CenterList.add(personal_Center);
			personal_Center = new Personal_Center();
		}
	}
}
