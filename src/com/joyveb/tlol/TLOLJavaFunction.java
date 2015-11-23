package com.joyveb.tlol;

/**
 * TLOL可注册 Java函数
 * @author dell
 *
 */
public interface TLOLJavaFunction {
	/**
	 * 向Lua中注册此函数
	 */
	void register();
}
