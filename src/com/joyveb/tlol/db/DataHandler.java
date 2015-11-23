package com.joyveb.tlol.db;

import com.joyveb.tlol.db.parser.DbConst;

public interface DataHandler {

	/**
	 * 当返回时的业务逻处理器
	 * 
	 * @param eventID 
	 * @param flag 
	 * @param ds 
	 */
	void handle(DbConst eventID, boolean flag, DataStruct ds);
}
