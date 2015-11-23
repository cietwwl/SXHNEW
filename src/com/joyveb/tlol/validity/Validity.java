package com.joyveb.tlol.validity;

/** 属性有效期接口 */
public interface Validity {

	/**
	 * 有效性枚举
	 * @author dell
	 *
	 */
	public enum State {
		/** 处于有效期 */
		Valid,
		/** 暂时失效 */
		MuteValid,
		/** 永久失效 */
		Invalid;
	}

	
	/**
	 * @param curMin 当前时间，分钟
	 * @return 是否有效
	 */
	State getState(int curMin);

}
