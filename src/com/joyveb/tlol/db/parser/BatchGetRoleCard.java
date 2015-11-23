package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.role.RoleCardService;

public class BatchGetRoleCard extends DataStruct {

	private HashMap<Integer, RoleCard> cards = new HashMap<Integer, RoleCard>();

	PreSql preSql;

	public BatchGetRoleCard(final PreSql preSql, final Object... sceneInfo) {
		this.preSql = preSql;
		this.setSceneInfo(sceneInfo);
	}

	public BatchGetRoleCard(final String sql, final Object... sceneInfo) {
		preSql = new PreSql();
		preSql.sqlstr = sql;
		this.setSceneInfo(sceneInfo);
	}

	public BatchGetRoleCard(final ArrayList<Integer> roleids,
			final Object... sceneInfo) {
		preSql = new PreSql();

		StringBuilder builder = new StringBuilder();
		builder.append(RoleCard.SQL);
		builder.append(" where");

		for (int i = 0; i < roleids.size(); ++i)
			if (i == roleids.size() - 1) {
				builder.append(" nid = ?");
				preSql.parameter.add(roleids.get(i));
			} else {
				builder.append(" nid = ? or");
				preSql.parameter.add(roleids.get(i));
			}
		preSql.sqlstr = builder.toString();
		this.setSceneInfo(sceneInfo);
	}

	@Override
	public final boolean readFromRs(final ResultSet resultSet)
			throws SQLException {
		while (resultSet.next()) {
			RoleCard card = new RoleCard().setRoleid(resultSet.getInt(1))
					.setName(resultSet.getString(2))
					.setLevel(resultSet.getShort(3))
					.setExp(resultSet.getInt(4)).setGold(resultSet.getInt(5))
					.setCharm(resultSet.getInt(6)).setMark(resultSet.getInt(7))
					.setEvil(resultSet.getInt(8)).setHonor(resultSet.getInt(9));

			RoleCardService.INSTANCE.synchronousCard(card);
			cards.put(card.getRoleid(), card);
		}

		return true;
	}

	@Override
	public final PreSql getPreSql_query() {
		return preSql;
	}

	public final HashMap<Integer, RoleCard> getCards() {
		return cards;
	}

	public final boolean hasGot() {
		return cards.isEmpty();
	}

}
