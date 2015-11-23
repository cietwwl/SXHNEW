package com.joyveb.tlol;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.joyveb.tlol.GameLoader.SP;
import com.joyveb.tlol.util.Log;

public class Conf {

	private String luaPath;
	private int port;
	private String luaResource;
	private String sp;
	private String srvId = null;
	private String serverIP = null;
	private String serverName = null;
	private String gameResPath = null;
	private boolean local;
	private int mx4jport;
	private String mx4jhost;
	private String url;

	private static Conf instance = new Conf();

	private Conf() {

	}

	public static Conf instance() {
		return instance;
	}

	public boolean init() {
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream("tlol.properties"));
			props.load(in);

			port = Integer.parseInt(props.getProperty("port"));
			luaPath = props.getProperty("luaPath");
			luaResource = props.getProperty("luaResource");
			sp = props.getProperty("sp");
			srvId = props.getProperty("srvid");
			serverIP = props.getProperty("serverip");
			local = Byte.parseByte(props.getProperty("isloacl")) == 0;
			gameResPath = props.getProperty("gameRes");

			serverName = new String(props.getProperty("srvName").getBytes("ISO-8859-1"), "UTF-8");
			mx4jhost = props.getProperty("mx4jhost");
			mx4jport = Integer.parseInt(props.getProperty("mx4jport"));
			url = props.getProperty("url");
			return true;
		} catch (Exception e) {
			Log.error(Log.ERROR, "init configration file: tlol.properties failed! ");
			Log.error(Log.ERROR, "TIANLONGBABU start failed !!");
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

	public String getLuaPath() {
		return luaPath;
	}

	public int getPort() {
		return port;
	}

	public String getLuaResource() {
		return luaResource;
	}

	public String getSp() {
		return sp;
	}

	public String getSrvId() {
		return srvId;
	}

	public String getServerIP() {
		return serverIP;
	}

	public String getServerName() {
		return serverName;
	}

	public String getGameResPath() {
		return gameResPath;
	}

	public boolean isLocal() {
		return local;
	}

	public int getMx4jport() {
		return mx4jport;
	}

	public String getMx4jhost() {
		return mx4jhost;
	}

	public String getUrl() {
		return url;
	}

}
