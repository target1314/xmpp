package com.xabber.android.data.message.phrase;

import java.io.File;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import android.content.Context;
import android.content.Intent;

import com.xabber.android.utils.Constant;
import com.xabber.android.utils.ImageResizer;

public class MessageFileLisense implements FileTransferListener {

	private File file;
	private String uid;
	private Context context;

	public MessageFileLisense(Context context) {
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void fileTransferRequest(FileTransferRequest prequest) {
		// TODO Auto-generated method stub
		uid = prequest.getRequestor().split("/")[0];
		if (prequest.getFileName().contains(".jpg")
				|| prequest.getFileName().contains(".png")
				|| prequest.getFileName().contains(".gif")
				|| prequest.getFileName().contains(".jpeg")) {
			String content = ImageResizer.getImagePath(context,
					prequest.getFileName());
			file = new File(content);
			IncomingFileTransfer infiletransfer = prequest.accept();
			try {
				infiletransfer.recieveFile(file);
				new fileImageThread(infiletransfer, content).start();
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (prequest.getFileName().contains(".amr")) {
			String content = ImageResizer.getVoicePath(context,
					prequest.getFileName());
			file = new File(content);
			IncomingFileTransfer infiletransfer = prequest.accept();
			try {
				infiletransfer.recieveFile(file);
				new fileVoiceThread(infiletransfer, content).start();
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return;
		}
	}

	class fileImageThread extends Thread {
		private IncomingFileTransfer accept;
		private String content;

		public fileImageThread(IncomingFileTransfer accept, String content) {
			this.accept = accept;
			this.content = content;
		}

		public void run() {
			boolean order = accept.isDone();
			double progress = 0;
			while (!order) {
				if (accept.getStatus().equals(Status.error)) {
					System.out.println("error" + accept.getError());
					order = true;
				} else {
					progress = accept.getProgress();
					progress *= 100;
					System.out.println("status=" + accept.getStatus());
					System.out.println("progress=" + progress + "%");
					if (progress == 100.0) {
						Intent intent = new Intent(Constant.UNIQUE_STRING);
						intent.putExtra("senduid", uid);
						intent.putExtra("content", content);
						context.sendBroadcast(intent);
						order = true;
					}
				}
			}
		}
	}

	class fileVoiceThread extends Thread {
		private IncomingFileTransfer accept;
		private String content;

		public fileVoiceThread(IncomingFileTransfer accept, String content) {
			this.accept = accept;
			this.content = content;
		}

		public void run() {
			boolean order = accept.isDone();
			while (!order) {
				if (accept.getStatus().equals(Status.error)) {
					System.out.println("error" + accept.getError());
					order = true;
				} else {
					double progress = accept.getProgress();
					progress *= 100;
					System.out.println("status=" + accept.getStatus());
					System.out.println("progress=" + progress + "%");
					if (progress == 100.0) {
						Intent intent = new Intent(Constant.UNIQUE_STRING);
						intent.putExtra("senduid", uid);
						intent.putExtra("content", content);
						context.sendBroadcast(intent);
						order = true;
					}
				}
			}
		}
	}
}