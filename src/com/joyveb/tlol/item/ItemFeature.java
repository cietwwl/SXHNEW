package com.joyveb.tlol.item;

/**
 * 物品特征
 * @author Sid
 *
 */
public enum ItemFeature {

	/** 不可使用 */
	Unavailable("不可使用", false),
	/** 绑定 */
	Bind("拾取绑定", true),
	/** 不可丢弃 */
	NoDrop("不可丢弃", true),
	/** 战斗中可使用 */
	FightUse("战斗中可使用", false),
	/** 不可出售 */
	NoSell("不可出售", true),
	/** 不可邮寄 */
	NoMail("不可邮寄", true),
	/** 可强化 */
	Enhance("可强化", false),
	/**升星 */
	RisingStar("升星", false),
	/** 装备后绑定 */
	Mountable("装备后绑定", false),
	/**升星辅助道具，增加成功率 */
	RisingStarSuccessRate("升星辅助", false),
	/**
	 * 可合成
	 */
	Compose("可合成", false),
	/**
	 * 可分解
	 */
	Decomposable("可分解", false);

	
	/**
	 * 特征名
	 */
	private final String name;
	
	/**
	 * 是否显示
	 */
	private final boolean show;
	
	/**
	 * @param name 特征名
	 * @param show 是否显示
	 */
	private ItemFeature(final String name, final boolean show) {
		this.name = name;
		this.show = show;
	}

	/**
	 * @return 特征名
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return 是否显示
	 */
	public boolean isShow() {
		return show;
	}
}
