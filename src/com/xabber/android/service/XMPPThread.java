package com.xabber.android.service;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;

import com.xabber.android.data.connection.ConnectionThread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class XMPPThread implements Runnable {

	private Handler handler = null;
	private static final String TAG = "XMPPThread";
	private static final boolean RECIEVE_FLAG = true;

	public XMPPThread(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		handleMessage();
	}

	public void handleMessage() {
		PacketFilter filter = new PacketTypeFilter(
				org.jivesoftware.smack.packet.Message.class);
		PacketListener myListener = new PacketListener() {
			@SuppressWarnings("deprecation")
			public void processPacket(final Packet packet) {
				if (RECIEVE_FLAG) {
					System.out.println("Activity----processPacket"
							+ packet.toXML());
					final org.jivesoftware.smack.packet.Message mes = (org.jivesoftware.smack.packet.Message) packet;
					System.out.println("来自：" + mes.getFrom() + "  消息内容："
							+ mes.getBody());
					Message msg = handler.obtainMessage(0, packet);
					handler.sendMessage(msg);
				}
			}
		};
		ConnectionThread.getXMPPConnection().addPacketListener(myListener,
				filter);
	}

}
