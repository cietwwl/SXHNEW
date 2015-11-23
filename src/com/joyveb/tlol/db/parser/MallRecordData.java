package com.joyveb.tlol.db.parser;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.item.Item;

public class MallRecordData extends DataStruct {

	private int userId;
	private String joyId;
	private int roleId;
	private Item item = null;
	private int cost;

	@Override
	public final PreSql getPreSql_insert() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "INSERT INTO TBL_TIANLONG_BUYRECORD(ID, NACCOUNTID, SJOYID, NROLEID, SPROPNAME, NPROPNUM, DBUYTIME, NBUYMONEY, SSERVERID,SPROPUID,SPROPTID) "
				+ "VALUES(SEQ_TIANLONG_BUYRECORD.nextval, ?, ?, ?, ?, ?, SYSDATE, ?, ?, ?, ?)";

		preSql.parameter.add(userId);
		preSql.parameter.add(joyId);
		preSql.parameter.add(roleId);
		preSql.parameter.add(item.getName());
		preSql.parameter.add(item.getStorage());
		preSql.parameter.add(cost);
		preSql.parameter.add(TianLongServer.srvId);
		preSql.parameter.add(item.getUid());
		preSql.parameter.add(item.getTid());
		return preSql;
	}

	public MallRecordData(final int userId, final String joyId, final int roleId, final int cost,
			final Item item) {
		this.userId = userId;
		this.joyId = joyId;
		this.roleId = roleId;
		this.cost = cost;
		this.item = item;
	}

	public final int getUserId() {
		return userId;
	}

	public final void setUserId(final int userId) {
		this.userId = userId;
	}

	public final String getJoyId() {
		return joyId;
	}

	public final void setJoyId(final String joyId) {
		this.joyId = joyId;
	}

	public final Item getItem() {
		return item;
	}

	public final void setItem(final Item item) {
		this.item = item;
	}

	public final int getCost() {
		return cost;
	}

	public final void setCost(final int cost) {
		this.cost = cost;
	}

	public final int getRoleId() {
		return roleId;
	}

	public final void setRoleId(final int roleId) {
		this.roleId = roleId;
	}

}
