package com.joyveb.tlol.db.parser;

import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class RoleMoneyData extends DataStruct {

	private int userId;
	private int money;

	public final PreSql getPreSql_update() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "update  tbl_tianlong_account t set NACCOUNT = ? where t.NID = ?";
		preSql.parameter.add(money);
		preSql.parameter.add(userId);
		return preSql;
	}

	public final int getUserId() {
		return userId;
	}

	public final void setUserId(final int userId) {
		this.userId = userId;
	}

	public final int getMoney() {
		return money;
	}

	public final void setMoney(final int money) {
		this.money = money;
	}

}
