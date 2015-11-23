package com.joyveb.tlol.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log {
	public static final byte STDOUT = 0;
	public static final byte OPERATOR = 1;
	public static final byte CHAT = 2;
	public static final byte PERFORMANCE = 3;
	public static final byte ITEM = 4;
	public static final byte NET = 5;
	public static final byte DATA = 6;
	public static final byte ERROR = 7;
	public static final byte CHATDETAIL = 8;
	public static final byte ROLE = 9;
	public static final byte PAY = 10;
	public static final byte SUBTRACT = 11;
	public static final byte BATTLE = 12;
	public static final byte BETBIGORSMALL = 13;
	public static final byte BETACCOUNT = 14;
	public static final byte GUESS = 15;
	public static final byte FATWA = 16;
	public static final byte MARRY = 17;
	public static final byte TRANS = 20;
	public static final byte HEGEMONY = 21;
	public static final byte EIGHTBUDDHA = 22;
	public static final byte MARTIALCHEATS = 23;

	

	private static final String[] LOGNAME = new String[] { "stdout",
		"operator", "chat", "performance", "item", "net", "data", "error",
		"chatDetail", "role","pay","subtract","battle","betBigOrSmall","betAccount","guess","fatwa","marry","","",
		"trans","hegemony", "eightbuddha", "martialcheats"};
	

	private static Logger[] logs;

	public static int THRESHOLD = 30; // 超过这个值将产生性能日志

	public static void resetTHRESHOLD(final int threshold) {
		if (threshold > 0 && threshold <= 1000) {
			THRESHOLD = threshold;
			info(STDOUT, "重置性能日志阀值为：" + threshold);
		} else
			info(STDOUT, "重置性能日志阀值失败：" + threshold);
	}

	public static void init() {
		logs = new Logger[LOGNAME.length];
		for (int i = 0; i < logs.length; i++)
			logs[i] = Logger.getLogger(LOGNAME[i]);
	}

	public final void setLoggerLevel(final Level level) {
		for (int i = 0; i < logs.length; i++)
			logs[i].setLevel(level);
	}

	public final void setLoggerLevel(final byte type, final Level level) {
		logs[type].setLevel(level);
	}

	/**
	 * debug
	 * @param type 
	 * @param msg 
	 */

	public static void debug(final byte type, final Object msg) {
		logs[type].debug(msg);
	}

	public static void debug(final byte type, final String func, final Object msg) {
		logs[type].debug("[" + func + "]" + msg);
	}

	public static void debug(final byte type, final Object msg, final Throwable t) {
		logs[type].debug(msg, t);
	}

	public static void debug(final byte type, final String func, final Object msg, final Throwable t) {
		logs[type].debug("[" + func + "]" + msg, t);
	}

	/**
	 * info
	 * @param type 
	 * @param msg 
	 */
	public static void info(final byte type, final Object msg) {
		logs[type].info(msg);
	}

	public static void info(final byte type, final String func, final Object msg) {
		logs[type].info("[" + func + "]" + msg);
	}

	public static void info(final byte type, final Object msg, final Throwable t) {
		logs[type].info(msg, t);
	}

	public static void info(final byte type, final String func, final Object msg, final Throwable t) {
		logs[type].info("[" + func + "]" + msg, t);
	}

	/**
	 * warning
	 * @param type 
	 * @param msg 
	 */

	public static void warn(final byte type, final Object msg) {
		logs[type].warn(msg);
	}

	public static void warn(final byte type, final String func, final Object msg) {
		logs[type].warn("[" + func + "]" + msg);
	}

	public static void warn(final byte type, final Object msg, final Throwable t) {
		logs[type].warn(msg, t);
	}

	public static void warn(final byte type, final String func, final Object msg, final Throwable t) {
		logs[type].warn("[" + func + "]" + msg, t);
	}

	/**
	 * error
	 * @param type 
	 * @param msg 
	 */
	public static void error(final byte type, final Object msg) {
		logs[ERROR].error(msg);
	}
	
	public static void error(final byte type, final Throwable t) {
		logs[ERROR].error("error",t);
	}

	public static void error(final byte type, final String func, final Object msg) {
		logs[ERROR].error("[" + func + "]" + msg);
	}

	public static void error(final byte type, final Object msg, final Throwable t) {
		logs[ERROR].error(msg, t);
	}

	public static void error(final byte type, final String func, final Throwable t) {
		logs[ERROR].error("[" + func + "]", t);
	}

	public static void error(final byte type, final String func, final Object msg, final Throwable t) {
		logs[ERROR].error("[" + func + "]" + msg, t);
	}

	/**
	 * fatal
	 * @param type 
	 * @param msg 
	 */
	public static void fatal(final byte type, final Object msg) {
		logs[ERROR].fatal(msg);
	}

	public static void fatal(final byte type, final String func, final Object msg) {
		logs[ERROR].fatal("[" + func + "]" + msg);
	}

	public static void fatal(final byte type, final Object msg, final Throwable t) {
		logs[ERROR].fatal(msg, t);
	}

	public static void fatal(final byte type, final String func, final Object msg, final Throwable t) {
		logs[ERROR].fatal("[" + func + "]" + msg, t);
	}

	/**
	 * performance special
	 * @param func 
	 * @param msg 
	 * @param startTime 
	 */
	public static void performance(final String func, final Object msg, final long startTime) {
		long timeElapse = System.currentTimeMillis() - startTime;
		if (timeElapse > THRESHOLD)
			logs[PERFORMANCE].info("[" + func + "]" + msg + timeElapse);
	}

	public static void performance(final Object msg, final long startTime) {
		performance("", msg, startTime);
	}

	public static void DBPerformance(final String func, final Object msg, final long startTime) {
		long timeElapse = System.currentTimeMillis() - startTime;
		if (timeElapse > 100)
			logs[PERFORMANCE].info("[" + func + "]" + "[ " + msg + " ] "
					+ timeElapse);
	}
}
