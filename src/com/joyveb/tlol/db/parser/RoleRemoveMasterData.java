package com.joyveb.tlol.db.parser;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class RoleRemoveMasterData extends DataStruct {
	
	private String appIdString;
	private int masterId;
	
	public RoleRemoveMasterData(String appIdString,int masterId){
		this.appIdString = appIdString;
		this.masterId = masterId;
	}

	@Override
	public final PreSql getPreSql_update() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "update tbl_tianlong_role" + "_" + TianLongServer.srvId
				+ " " + "set APPDELMASTER = ? " + "where nid = ?";
		preSql.parameter.add(appIdString);
		preSql.parameter.add(this.masterId);
		return preSql;
	}

}
