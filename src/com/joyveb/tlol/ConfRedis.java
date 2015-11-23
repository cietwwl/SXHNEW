package com.joyveb.tlol;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.joyveb.tlol.util.Log;

public class ConfRedis {

	private String host;
	private int port;
	private String passwd;
	private int maxActive;
	private int maxIdle;
	private int maxWait;

	public static String TAG_SESSION = "session";

	private static ConfRedis instance = new ConfRedis();

	private ConfRedis() {

	}

	public String getKey(int userId) {
		return TAG_SESSION + ":" + userId;
	}

	public static ConfRedis instance() {
		return instance;
	}

	public boolean init() {
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = new BufferedInputStream(
					new FileInputStream("redis.properties"));
			props.load(in);
			host = props.getProperty("host");
			port = Integer.parseInt(props.getProperty("port"));
			passwd = props.getProperty("passwd");
			maxActive = Integer.parseInt(props.getProperty("maxActive"));
			maxIdle = Integer.parseInt(props.getProperty("maxIdle"));
			maxWait = Integer.parseInt(props.getProperty("maxWait"));
			return true;
		} catch (Exception e) {
			Log.error(Log.ERROR,
					"init configration file: redis.properties failed! ");
			Log.error(Log.ERROR, "TIANLONGBABU_REDIS start failed !!");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the maxActive
	 */
	public int getMaxActive() {
		return maxActive;
	}

	/**
	 * @return the maxIdle
	 */
	public int getMaxIdle() {
		return maxIdle;
	}

	/**
	 * @return the maxWait
	 */
	public int getMaxWait() {
		return maxWait;
	}

	/**
	 * @return the passwd
	 */
	public String getPasswd() {
		return passwd;
	}

}
