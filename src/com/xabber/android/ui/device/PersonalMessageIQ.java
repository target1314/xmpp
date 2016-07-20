package com.xabber.android.ui.device;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

public class PersonalMessageIQ extends IQ {

	public static final String ELEMENT = "testiq";
	public static final String NAMESPACE = "com.hy.core.iq";
	private String uid;
	private String lat;
	private String lon;
	private String xml;

	public void saveToServer(Connection connection) throws XMPPException {
		setType(IQ.Type.SET);
		setFrom(connection.getUser());
		PacketCollector collector = connection
				.createPacketCollector(new PacketIDFilter(getPacketID()));
		connection.sendPacket(this);

		Packet response = collector.nextResult(SmackConfiguration
				.getPacketReplyTimeout());

		collector.cancel();
		if (response == null) {
			throw new XMPPException("No response from server on status set.");
		}
		if (response.getError() != null) {
			throw new XMPPException(response.getError());
		}
	}

	@Override
	public String getChildElementXML() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(ELEMENT).append(" xmlns=\"").append(NAMESPACE)
				.append("\">");
		sb.append("<uid>").append(getUid()).append("</uid>");
		sb.append("<lat>").append(getLat()).append("</lat>");
		sb.append("<lon>").append(getLon()).append("</lon>");
		sb.append("</").append(ELEMENT).append(">");
		return sb.toString();
	}

	public String getElementName() {
		return ELEMENT;
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
}
