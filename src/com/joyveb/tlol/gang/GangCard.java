package com.joyveb.tlol.gang;

import com.joyveb.tlol.NameCard;

/**
 * 帮派卡片
 */
public class GangCard extends NameCard<GangCard> implements Cloneable {
	/** 帮派id */
	private long id;

	/** 总帮贡 */
	private int tribute;
	
	@Override
	public final GangCard clone() {
		try {
			return (GangCard) super.clone();
		} catch (CloneNotSupportedException e) {
			return new GangCard().copy(this);
		}
	}

	public final GangCard copy(final GangCard card) {
		return this.setId(card.id).setTribute(card.tribute).setName(card.name);
	}

	public final GangCard setName(final String name) {
		this.name = name;
		return this;
	}

	public final GangCard setId(final long id) {
		this.id = id;
		return this;
	}

	/**
	 * 获得帮派ID
	 * @return
	 */
	public final long getId() {
		return id;
	}

	public final GangCard setTribute(final int tribute) {
		this.tribute = tribute;
		return this;
	}

	/**
	 * 获得总帮贡
	 * @return
	 */
	public final int getTribute() {
		return tribute;
	}

}
