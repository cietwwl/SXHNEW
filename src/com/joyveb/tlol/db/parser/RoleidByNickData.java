package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class RoleidByNickData extends DataStruct {
	private int roleid;
	private String nick;
	private boolean friend;

	public RoleidByNickData(final String nick, final boolean friend) {
		this.nick = nick;
	}

	@Override
	public final boolean readFromRs(final ResultSet rs) throws SQLException {
		if (rs.next())
			setRoleid(rs.getInt(1));

		return true;
	}

	@Override
	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select nid from tbl_tianlong_role" + "_"
				+ TianLongServer.srvId + " t where t.snick = ?";
		preSql.parameter.add(nick);
		return preSql;
	}

	public final void setRoleid(final int roleid) {
		this.roleid = roleid;
	}

	public final int getRoleid() {
		return roleid;
	}

	public final void setFriend(final boolean friend) {
		this.friend = friend;
	}

	public final boolean isFriend() {
		return friend;
	}
}
