package com.joyveb.tlol.marry;

import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.RoleBreakUpData;
import com.joyveb.tlol.util.Log;

public enum RoleBreakUPService implements DataHandler {
	INSTANCE;
	
	public void breakUp(int roleId) {
		Log.info(Log.STDOUT, "RoleBreakUp", "执行强制离婚");
		CommonParser.getInstance().postTask(DbConst.Role_Break_Up, this,
				new RoleBreakUpData(roleId));
	}

	@Override
	public void handle(DbConst eventID, boolean flag, DataStruct ds) {
		// TODO Auto-generated method stub
		
	}

}
