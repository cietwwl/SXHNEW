package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class UserInfoData extends DataStruct {

	private int userId;
	private String uname;
	private String passwd;
	private String tel;

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	@Override
	public final boolean readFromRs(final ResultSet rs) throws SQLException {
		if (rs.next()) {
			uname = rs.getString(1);
			passwd = rs.getString(2);
			tel = rs.getString(3);
		}
		return true;
	}

	@Override
	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "SELECT SNAME, SPASSWORD,STEL "
				+ "FROM TBL_TIANLONG_ACCOUNT " + "WHERE NID = ?";
		preSql.parameter.add(userId);
		return preSql;
	}

	public final String getUname() {
		return uname;
	}

	public final void setUname(final String uname) {
		this.uname = uname;
	}

	public final String getPasswd() {
		return passwd;
	}

	public final void setPasswd(final String passwd) {
		this.passwd = passwd;
	}

	public final int getUserId() {
		return userId;
	}

	public final void setUserId(final int userId) {
		this.userId = userId;
	}

}
