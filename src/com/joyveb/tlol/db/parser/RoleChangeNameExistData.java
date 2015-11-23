package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class RoleChangeNameExistData extends DataStruct {

	private String newName;
	private boolean ifNameExist = false;

	@Override
	public final boolean readFromRs(final ResultSet rs) throws SQLException {
		if (rs.next() && rs.getInt(1) > 0)
			ifNameExist = true;
		return true;
	}

	@Override
	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select count(0) from tbl_tianlong_role" + "_"
				+ TianLongServer.srvId
				+ " t where t.snick = ?";
		preSql.parameter.add(newName);
		return preSql;
	}

	public final String getNewName() {
		return newName;
	}

	public final void setNewName(final String newName) {
		this.newName = newName;
	}

	public final boolean isIfNameExist() {
		return ifNameExist;
	}

	
}
