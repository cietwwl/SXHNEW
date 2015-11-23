package com.joyveb.tlol.schedule;

/**
 * 时间间隔通知回调接口
 */
public interface MinTickHandler {

	/** 通报当前时间 
	 * @param  curMin 
	 */
	void minTick(int curMin);

}
