package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class UIDData extends DataStruct {

	private long maxUID;

	@Override
	public final PreSql getPreSql_update() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "UPDATE TBL_TIANLONG_UID SET nuid_"
				+ TianLongServer.srvId + " =?";
		preSql.parameter.add(maxUID);
		return preSql;
	}

	@Override
	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select nuid_" + TianLongServer.srvId
				+ " from TBL_TIANLONG_UID";
		return preSql;
	}

	@Override
	public final boolean readFromRs(final ResultSet rs) throws SQLException {
		if (rs.next())
			maxUID = rs.getLong(1);
		return true;
	}

	public final long getMaxUID() {
		return maxUID;
	}

	public final void setMaxUID(final long maxUID) {
		this.maxUID = maxUID;
	}

}
