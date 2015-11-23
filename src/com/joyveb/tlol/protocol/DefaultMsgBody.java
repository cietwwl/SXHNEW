package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.joyveb.tlol.util.Log;

public class DefaultMsgBody extends MsgBody {

	private String protocol;

	private ArrayList<Object> data = new ArrayList<Object>();

	public DefaultMsgBody(final String protocol) {
		this.protocol = protocol;
	}

	protected final boolean defaultReadBody(final ByteBuffer body) {
		data.clear();
		body.position(0);

		for (int i = 0; i < protocol.length(); i++) {
			switch (protocol.charAt(i)) {
			case ' ':
				break;
			case '1':
				if (body.remaining() < 1) {
					Log.error(Log.ERROR, "中断于第" + i + "个数据");
					return false;
				}
				data.add(Byte.valueOf(body.get()));
				break;
			case '2':
				if (body.remaining() < 2) {
					Log.error(Log.ERROR, "中断于第" + i + "个数据");
					return false;
				}
				data.add(Short.valueOf(body.getShort()));
				break;
			case '4':
				if (body.remaining() < 4) {
					Log.error(Log.ERROR, "中断于第" + i + "个数据");
					return false;
				}
				data.add(Integer.valueOf(body.getInt()));
				break;
			case '8':
				if (body.remaining() < 8) {
					Log.error(Log.ERROR, "中断于第" + i + "个数据");
					return false;
				}
				data.add(Long.valueOf(body.getLong()));
				break;
			case 's':
			case 'S':
				if (body.remaining() < 2) {
					Log.error(Log.ERROR, "中断于第" + i + "个数据");
					return false;
				}
				
				short strLen = body.getShort();
				if (strLen <= 0 || body.remaining() < strLen) {
					Log.error(Log.ERROR, "中断于第" + i + "个数据");
					return false;
				}

				data.add(getStrByLen(body, strLen));
				
				break;
			default:
				Log.error(Log.STDOUT, "defaultReadBody", "unhandled msgid! : " + protocol.charAt(i));
				break;
			}
		}

		return true;
	}

	@Override
	public final boolean readBody(final ByteBuffer body) {
		return defaultReadBody(body);
	}

	public final byte getByte(final int index) {
		return (Byte) data.get(index);
	}

	public final short getShort(final int index) {
		return (Short) data.get(index);
	}

	public final int getInt(final int index) {
		return (Integer) data.get(index);
	}

	public final long getLong(final int index) {
		return (Long) data.get(index);
	}

	public final String getString(final int index) {
		return (String) data.get(index);
	}

}
