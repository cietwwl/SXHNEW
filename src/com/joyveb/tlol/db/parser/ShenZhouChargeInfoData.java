package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class ShenZhouChargeInfoData extends DataStruct {

	private Vector<String> shenZhouRateInfo = new Vector<String>();

	@Override
	public final boolean readFromRs(final ResultSet rs) throws SQLException {
		while (rs.next()) {
			int payAmt = rs.getInt(1);
			int payParm = rs.getInt(2);
			shenZhouRateInfo.add(payAmt / 10 + "元 = " + payParm + "元宝");
		}
		return true;
	}

	@Override
	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select PAYAMT, PAYPRAM from TBL_PAY_SZ_PAYPRAM where paygameid = 'tlol' order by PAYAMT desc";
		return preSql;
	}

	public final Vector<String> getShenZhouRateInfo() {
		return shenZhouRateInfo;
	}
}
