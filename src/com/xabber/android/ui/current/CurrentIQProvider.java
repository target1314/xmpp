package com.xabber.android.ui.current;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class CurrentIQProvider implements IQProvider {
	@Override
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		CurrentlMessageIQ result = new CurrentlMessageIQ();

		String payload = "" + parser.getText();

		StringBuilder sb = new StringBuilder();
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				if (depth > 0) {
					sb.append("</" + parser.getName() + ">");
				}
				break;
			case XmlPullParser.START_TAG:
				depth++;
				StringBuilder attrs = new StringBuilder();
				for (int i = 0; i < parser.getAttributeCount(); i++) {
					attrs.append(parser.getAttributeName(i) + "=\""
							+ parser.getAttributeValue(i) + "\" ");
				}
				sb.append("<" + parser.getName() + " " + attrs.toString() + ">");
				break;
			default:
				sb.append(parser.getText());
				break;
			}
		}
		payload = sb.toString();
		result.setXml(payload);
		result.setType(Type.RESULT);
		return result;
	}
}