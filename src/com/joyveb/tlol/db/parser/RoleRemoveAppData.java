package com.joyveb.tlol.db.parser;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class RoleRemoveAppData extends DataStruct {
	
	private int roleId;
	
	public RoleRemoveAppData(int roleId){
		this.roleId = roleId;
	}

	@Override
	public final PreSql getPreSql_update() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "update tbl_tianlong_role" + "_" + TianLongServer.srvId
				+ " " + "set MASTER = ? " + "where nid = ?";
		preSql.parameter.add("");
		preSql.parameter.add(this.roleId);
		return preSql;
	}

}
