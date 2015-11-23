package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.billboard.TopRated;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.gang.GangCard;

public class TopTributeGangData extends DataStruct {

	private ArrayList<GangCard> cards = new ArrayList<GangCard>();

	@Override
	public final boolean readFromRs(final ResultSet resultSet) throws SQLException {
		while (resultSet.next()) {
			cards.add(new GangCard().setId(resultSet.getLong(1))
					.setName(resultSet.getString(2))
					.setTribute(resultSet.getInt(3)));
		}

		return true;
	}

	@Override
	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select * from" + "(select nid, sname, ntribute "
				+ "from tbl_tianlong_role" + "_" + TianLongServer.srvId
				+ " where by ntribute desc) " + "where rownum <= "
				+ TopRated.max;
		return preSql;
	}

	public final void setCards(final ArrayList<GangCard> cards) {
		this.cards = cards;
	}

	public final ArrayList<GangCard> getCards() {
		return cards;
	}
}
