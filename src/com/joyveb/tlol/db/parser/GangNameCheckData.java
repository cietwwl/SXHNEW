package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class GangNameCheckData extends DataStruct {
	private boolean nameExist = false;

	private String name;

	public GangNameCheckData(final String name) {
		this.name = name;
	}

	@Override
	public final boolean readFromRs(final ResultSet rs) throws SQLException {
		if (rs.next() && rs.getInt(1) > 0)
			setNameExist(true);
		return true;
	}

	@Override
	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select count(0) from tbl_tianlong_gang_"
				+ TianLongServer.srvId + " where sname = ?";
		preSql.parameter.add(name);
		return preSql;
	}

	public final void setNameExist(final boolean nameExist) {
		this.nameExist = nameExist;
	}

	public final boolean isNameExist() {
		return nameExist;
	}

	public final String getName() {
		return name;
	}

	public final void setName(final String name) {
		this.name = name;
	}
}
