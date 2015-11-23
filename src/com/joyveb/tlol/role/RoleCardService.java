package com.joyveb.tlol.role;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.parser.BatchGetRoleCard;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;

public enum RoleCardService {
	INSTANCE;

	private ConcurrentHashMap<Integer, RoleCard> cards = new ConcurrentHashMap<Integer, RoleCard>();
	private ConcurrentHashMap<String, RoleCard> nameCardMap = new ConcurrentHashMap<String, RoleCard>();

	public RoleCard getCard(final int roleid) {
		return cards.get(roleid);
	}

	public RoleCard getCard(final String roleName) {
		return roleName == null ? null : nameCardMap.get(roleName);
	}
	
	

	/**
	 * 将card存入管理器，因为内存中的card永远是最新的，使用putIfAbsent保证唯一性。<br>
	 * 需要注意的是这里可能会修改传入的card为管理器中现有的card
	 * @param card 
	 * @return 将card存入管理器
	 */
	public RoleCard synchronousCard(final RoleCard card) {
		// synchronized (cards) {
		// if(!cards.containsKey(card.getRoleid()))
		// cards.put(card.getRoleid(), card);
		// }
		cards.putIfAbsent(card.getRoleid(), card);
		nameCardMap.put(card.getName(), cards.get(card.getRoleid()));
		return cards.get(card.getRoleid());
	}

	/**
	 * 唯一可能调用这个函数的情况就是删除角色时，但是这是可选的。<br>
	 * 删除后可能仍被帮派新加载card，并继续存入，再次加载帮派的时候一定会被过滤。<br>
	 * 不会对其他逻辑造成影响。
	 * @param roleid 
	 */
	public void removeCard(final int roleid) {
		RoleCard card = cards.remove(roleid);
		if (card != null)
			nameCardMap.remove(card.getName());
	}

	public RoleCard synchronous(final RoleBean source) {
		RoleCard roleCard = cards.get(source.getRoleid());
		if(!source.getName().equals(roleCard.getName())) {
			nameCardMap.remove(roleCard.getName());
			nameCardMap.put(source.getName(), roleCard);
		}
			
		return roleCard.setName(source.getName()).setLevel(source.getLevel())
				.setGold(source.getGold()).setCharm(source.getCharm())
				.setExp(source.getEXP()).setMark(source.getMark())
				.setEvil(source.getEvil()).setHonor(source.getHonor());
	}

	/**
	 * 批量加载RoleCard
	 * @param dataHandler 数据库回调
	 * @param roleidLists 多个角色id列表
	 */
	public void loadCard(final DataHandler dataHandler, final List<Integer>... roleidLists) {
		String sql = makeSql(roleidLists);
		if (sql == null)
			return;

		CommonParser.getInstance().postTask(DbConst.BatchGetRoleCard,
				dataHandler, new BatchGetRoleCard(makeSql(roleidLists)));
	}

	private String makeSql(final List<Integer>... roleidLists) {
		StringBuilder builder = null;
		boolean first = true;
		boolean added = false;

		for (List<Integer> roleids : roleidLists) {
			for (int roleid : roleids) {
				if (!cards.containsKey(roleid)) {
					if (builder == null) {
						builder = new StringBuilder();
						builder.append(RoleCard.SQL + " where");
					}

					if (first) {
						builder.append(" nid = " + roleid);
						first = false;
					} else
						builder.append(" or nid = " + roleid);

					added = true;
				}
			}
		}

		return added ? builder.toString() : null;
	}

	public void cleanDiscarded(final Iterable<Integer> iterable) {
		Iterator<Integer> it = iterable.iterator();

		while (it.hasNext()) {
			int roleid = it.next();
			if (!RoleCardService.INSTANCE.hasCard(roleid))
				it.remove();
		}
	}

	public boolean hasCard(final int roleid) {
		return cards.containsKey(roleid);
	}

	public boolean hasCard(final String roleName) {
		return roleName == null ? false : nameCardMap.containsKey(roleName);
	}

}
