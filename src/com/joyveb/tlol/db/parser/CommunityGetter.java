package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.community.Community;
import com.joyveb.tlol.community.Communitys;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class CommunityGetter extends DataStruct {

	private HashMap<Long, Community> communitys = new HashMap<Long, Community>();

	@Override
	public final boolean readFromRs(final ResultSet resultSet) throws SQLException {
		while (resultSet.next()) {
			long id = resultSet.getLong(1);
			int itemid = resultSet.getInt(2);
			String cname = resultSet.getString(3);
			communitys.put(id, new Community(id, itemid, cname));
		}

		return true;
	}

	@Override
	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select nid, nitemid, scname from tbl_tianlong_community_"
				+ TianLongServer.srvId;
		return preSql;
	}

	public final void callback() {
		Communitys.INSTANCE.addAll(communitys);
	}
}
