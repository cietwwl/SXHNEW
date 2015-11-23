package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.joyveb.tlol.billboard.TopRated;
import com.joyveb.tlol.billboard.TopRatedCard;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.role.RoleCardService;

public class TopRoleCardData extends DataStruct {

	private PreSql preSql = new PreSql();

	private ArrayList<RoleCard> cards = new ArrayList<RoleCard>();

	public TopRoleCardData(final TopRatedCard topRated) {
		preSql.sqlstr = "select * from " + "(" + RoleCard.SQL
				+ " where nid != 1 and nid != 2 and " + topRated.getField()
				+ "> 0" +  "   and  sracingmark =0  "+" order by " + topRated.getField() + " desc) "
				+ "where rownum <= " + TopRated.max;
	}

	@Override
	public final boolean readFromRs(final ResultSet resultSet) throws SQLException {
		while (resultSet.next()) {
			cards.add(RoleCardService.INSTANCE.synchronousCard(RoleCard
					.readFromDB(resultSet)));
		}

		return true;
	}

	@Override
	public final PreSql getPreSql_query() {
		return preSql;
	}

	public final void setCards(final ArrayList<RoleCard> cards) {
		this.cards = cards;
	}

	public final ArrayList<RoleCard> getCards() {
		return cards;
	}

}
