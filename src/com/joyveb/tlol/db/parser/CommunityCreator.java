package com.joyveb.tlol.db.parser;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.community.Community;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class CommunityCreator extends DataStruct {

	private long id;

	private int itemid;

	private String cname;

	public CommunityCreator(final Community community) {
		this.id = community.getId();
		this.itemid = community.getItemid();
		this.cname = community.getCname();
	}

	@Override
	public final PreSql getPreSql_insert() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "insert into tbl_tianlong_community_"
				+ TianLongServer.srvId
				+ "(nid, nitemid, scname) values (?, ?, ?)";
		preSql.parameter.add(id);
		preSql.parameter.add(itemid);
		preSql.parameter.add(cname);
		return preSql;
	}

}
