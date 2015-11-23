package com.joyveb.tlol;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Properties;

import javax.management.JMException;

import redis.clients.jedis.JedisPoolConfig;

import com.joyveb.tlol.redis.Redis;
import com.joyveb.tlol.util.Log;

public final class GameLoader {
	// static
	// {
	// try { Class.forName( "oracle.jdbc.driver.OracleDriver" ); }
	// catch (Exception e) { e.printStackTrace(); }
	// }
	//
	/**
	 * 无参构造方法
	 */
	private GameLoader() {
	}

	public enum SP {
		sky, shenxunhe, kuyu,
	}

	/**
	 * 入口方法--main
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		Log.init();

		// =============加载redis=====================
		JedisPoolConfig config = new JedisPoolConfig();
		ConfRedis.instance().init();
		String redisHost = ConfRedis.instance().getHost();
		System.out.println("redisHost:" + redisHost);

		int redisPort = ConfRedis.instance().getPort();
		System.out.println("redisPort:" + redisPort);

		String redisPwd = ConfRedis.instance().getPasswd();
		System.out.println("redisPwd:" + redisPwd);

		int maxActive = ConfRedis.instance().getMaxActive();
		System.out.println("maxActive:" + maxActive);

		int maxIdle = ConfRedis.instance().getMaxIdle();
		System.out.println("maxIdle:" + maxIdle);

		int maxWait = ConfRedis.instance().getMaxWait();
		System.out.println("maxWait:" + maxWait);

		config.setMaxActive(maxActive);
		config.setMaxIdle(maxIdle);
		config.setMaxWait(maxWait);
		config.setTestOnBorrow(true);

		// Redis.instance().init(config, redisHost, redisPort);
		Redis.instance().init(config, redisHost, redisPort, redisPwd);
		// =============加载redis=====================

		Properties props = new Properties();
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream("tlol.properties"));
			props.load(in);
		} catch (Exception e) {
			Log.error(Log.ERROR,
					"can not find configration file: tlol.properties");
			Log.error(Log.ERROR, "TIANLONGBABU start failed !!");
			return;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		int port = Integer.parseInt(props.getProperty("port"));
		String luaPath = props.getProperty("luaPath");
		String luaResource = props.getProperty("luaResource");
		TianLongServer.srvId = props.getProperty("srvid");
		TianLongServer.serverIP = props.getProperty("serverip");
		TianLongServer.local = Byte.parseByte(props.getProperty("isloacl")) == 0;
		TianLongServer.gameResPath = props.getProperty("gameRes");
		try {
			TianLongServer.serverName = new String(props.getProperty("srvName")
					.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		if (TianLongServer.srvId == null || TianLongServer.serverIP == null
				|| port == 0) {
			Log.error(Log.ERROR, "TIANLONGBABU start failed !!");
			return;
		}

		if (!Conf.instance().init())
			Log.error(Log.ERROR, "TIANLONGBABU start failed !!");

		TianLongDriver TLOLManager = new TianLongDriver();
		HttpAdaptor adaptor = new HttpAdaptor(TLOLManager,
				props.getProperty("mx4jhost"), Integer.parseInt(props
						.getProperty("mx4jport")));

		try {
			adaptor.start(); // 适配器启动
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (JMException e) {
			e.printStackTrace();
		}

		try {
			// 启动游戏
			TLOLManager.startService(port, luaPath, luaResource);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// =========读取激活码======
		if (Conf.instance().getSp().equals("android")) {
			File file = new File("code.txt");
			String tempstring = "";
			try {
				FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader reader = new BufferedReader(isr);

				while ((tempstring = reader.readLine()) != null) {

					OnlineService.getCodeList().add(tempstring);
					System.out.println(tempstring);
				}
				reader.close();
				isr.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// =========读取激活码======
	}

}
