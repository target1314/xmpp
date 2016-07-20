package com.xabber.android.receiver;

import org.jivesoftware.smackx.filetransfer.FileTransferManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xabber.android.data.connection.ConnectionThread;
import com.xabber.android.data.message.phrase.MessageFileLisense;

public class GroupReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		FileTransferManager fileTransferManager = new FileTransferManager(
				ConnectionThread.getXMPPConnection());
		fileTransferManager.addFileTransferListener(new MessageFileLisense(
				context));
	}

}
