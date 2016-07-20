package com.xabber.android.ui.current;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;

public class CurrentSendMessage {
	public static void run(XMPPConnection connection, String lat, String lon)
			throws Exception {
		ProviderManager.getInstance().addIQProvider(CurrentlMessageIQ.ELEMENT,
				CurrentlMessageIQ.NAMESPACE, new CurrentIQProvider());

		XMPPConnection con = connection;
		PacketCollector collector = con
				.createPacketCollector(new PacketFilter() {
					public boolean accept(Packet p) {
						if (p instanceof CurrentlMessageIQ) {
							CurrentlMessageIQ m = (CurrentlMessageIQ) p;
							return true;
						}
						return false;
					}
				});

		CurrentlMessageIQ personalMessageIQ = new CurrentlMessageIQ();
		String fromuuid = connection.getUser();
		fromuuid = fromuuid.substring(0, fromuuid.indexOf("@"));
		personalMessageIQ.setType(IQ.Type.SET);
		personalMessageIQ.setFrom(connection.getUser());
		personalMessageIQ.setUid(fromuuid);
		personalMessageIQ.setLat(lat);
		personalMessageIQ.setLon(lon);
		con.sendPacket(personalMessageIQ);

		Packet packet = collector.nextResult(SmackConfiguration
				.getPacketReplyTimeout());

		collector.cancel();

		if (packet == null) {
			return;
		} else {
			return;
		}
	}
}
