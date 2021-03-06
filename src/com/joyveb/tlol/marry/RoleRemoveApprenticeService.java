package com.joyveb.tlol.marry;

import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.RoleBreakUpData;
import com.joyveb.tlol.db.parser.RoleRemoveAppData;
import com.joyveb.tlol.util.Log;

public enum RoleRemoveApprenticeService implements DataHandler {
	INSTANCE;
	
	public void removeApp(int roleId) {
		Log.info(Log.STDOUT, "RemoveApprentice", "执行强制解除徒弟");
		CommonParser.getInstance().postTask(DbConst.Role_Remove_App, this,
				new RoleRemoveAppData(roleId));
	}

	@Override
	public void handle(DbConst eventID, boolean flag, DataStruct ds) {
		// TODO Auto-generated method stub
		
	}

}
