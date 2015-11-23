package com.joyveb.tlol.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class GZIP {
	private static CharBuffer chBuffer = CharBuffer.allocate(10240);

	private GZIP() {
		
	}
	public static byte[] zip(final String cource) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream gzipOutput = new DataOutputStream(
					new BufferedOutputStream(new GZIPOutputStream(baos)));
			gzipOutput.writeBytes(cource);
			gzipOutput.flush();
			gzipOutput.close();

			return baos.toByteArray();
		} catch (IOException e) {
			Log.error(Log.ERROR, e);
		}

		return null;
	}

	public static String unzip(final byte[] zip) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new GZIPInputStream(new ByteArrayInputStream(zip))));

			chBuffer.clear();
			reader.read(chBuffer);

			reader.close();

			chBuffer.flip();

			return chBuffer.toString();
		} catch (IOException e) {
			Log.error(Log.ERROR, e);
		}

		return null;
	}

}
