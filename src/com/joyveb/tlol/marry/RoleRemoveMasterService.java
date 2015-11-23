package com.joyveb.tlol.marry;

import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.RoleBreakUpData;
import com.joyveb.tlol.db.parser.RoleRemoveMasterData;
import com.joyveb.tlol.util.Log;

public enum RoleRemoveMasterService implements DataHandler {
	INSTANCE;
	
	public void removeMaster(int roleId, String str) {
		Log.info(Log.STDOUT, "RoleRemoveMaster", "执行强制解除师傅");
		CommonParser.getInstance().postTask(DbConst.Role_Remove_Master, this,
				new RoleRemoveMasterData(str,roleId));
	}

	@Override
	public void handle(DbConst eventID, boolean flag, DataStruct ds) {
		// TODO Auto-generated method stub
		
	}

}
