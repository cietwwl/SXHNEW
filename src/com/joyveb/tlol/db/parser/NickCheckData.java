package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class NickCheckData extends DataStruct {
	public boolean ifNameExist = false;

	public short zoneid;
	public String nick;

	public NickCheckData(final short zoneid, final String nick) {
		this.zoneid = zoneid;
		this.nick = nick;
	}

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
		preSql.parameter.add(nick);
		return preSql;
	}
}
