package com.joyveb.tlol.db.parser;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class GangDeleter extends DataStruct {

	private PreSql preSql = new PreSql();

	private long gangid;

	public GangDeleter(final long gangid) {
		this.gangid = gangid;

		preSql.sqlstr = "delete from tbl_tianlong_gang_" + TianLongServer.srvId
				+ " where nid = ?";
		preSql.parameter.add(gangid);
	}
	/**
	 * @return 返回delete语句
	 */
	public final PreSql getPreSql_delete() {
		return preSql;
	}

	public final void setGangid(final long gangid) {
		this.gangid = gangid;
	}

	public final long getGangid() {
		return gangid;
	}

}
