package com.joyveb.tlol.net;

public interface NetListener {

	/*
	 * 当三次握手完成可以发送接收数据时调用
	 */
	void onAccept(NetHandler cliengHandler);
}
