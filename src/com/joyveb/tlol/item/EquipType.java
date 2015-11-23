package com.joyveb.tlol.item;

import com.joyveb.tlol.auction.AutionType;

/** 装备类型 */
public enum EquipType {
	/** 武器 */
	Weapon(0, "武器", Category.Weapon),
	/** 残刃 */
	Blade(1, "噬魂", Category.Jewelry),
	/** 戒指 */
	Ring(2, "戒指", Category.Jewelry),
	/** 项链 */
	Necklace(3, "项链", Category.Jewelry),
	/** 刺环 */
	Trinket(4, "刺环", Category.Jewelry),
	/** 头盔 */
	Helmet(5, "头盔", Category.Clothes),
	/** 披风 */
	Cloak(6, "披风", Category.Jewelry),
	/** 胸甲 */
	Cuirass(7, "战甲", Category.Clothes),
	/** 玉佩 */
	Wrists(8, "玉佩", Category.Jewelry),
	/** 腰带 */
	Belt(9, "腰带", Category.Clothes),
	/** 手套 */
	Gloves(10, "护手", Category.Clothes),
	/** 护腿 */
	Cuish(11, "护腿", Category.Clothes),
	/** 鞋子 */
	Shoes(12, "鞋子", Category.Clothes),
	/** 令牌 */
	Token(13, "令牌", Category.Jewelry);

	/** 穿戴位置 */
	private final byte wearIndex;
	
	/** 类型名称 */
	private final String typeName;

	/** 限定职业 */
	private final Category category;
	
	/**
	 * @param wear 穿戴位置
	 * @param typeName 类型名称
	 * @param category 装备归类
	 */
	private EquipType(final int wear, final String typeName, final Category category) {
		this.wearIndex = (byte) wear;
		this.typeName = typeName;
		this.category = category;
	}
	
	/** @return 穿戴位置 */
	public final byte getWearIndex() {
		return wearIndex;
	}

	/** @return 类型名称 */
	public String getTypeName() {
		return typeName;
	}

	/** @return 限定职业 */
	public boolean isExclusive() {
		return category != Category.Jewelry;
	}
	
	/**
	 * @return 是否可打孔
	 */
	public boolean canPunch() {
		return category != Category.Jewelry;
	}

	/**
	 * @return 是否是饰品
	 */
	public boolean isJewelry() {
		return category == Category.Jewelry;
	}
	
	/**
	 * 装备归类
	 * @author Sid
	 */
	private static enum Category {
		/**
		 * 武器类
		 */
		Weapon(AutionType.Weapon),
		/**
		 * 衣物类
		 */
		Clothes(AutionType.Clothes),
		/**
		 * 饰品类
		 */
		Jewelry(AutionType.EquipOther);
		
		/**
		 * 拍卖行归类
		 */
		final AutionType autionType;
		
		/**
		 * @param autionType 拍卖行归类
		 */
		private Category(final AutionType autionType) {
			this.autionType = autionType;
		}
	}

	/**
	 * @return 拍卖行归类码
	 */
	public int getAuctionCategory() {
		return 1 << category.autionType.ordinal();
	}
}
