package com.joyveb.tlol.core;

import com.joyveb.tlol.net.IncomingMsg;

public interface PlayerAgent {

	/**
	 * 处理模块相关指令
	 * 
	 * @param msg 
	 */
	void processCommand(final IncomingMsg msg);
}