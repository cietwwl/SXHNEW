package com.joyveb.tlol.item;

import java.awt.Color;

/**
 * 装备品质
 * @author Sid
 */
public enum EquipQuality {
	/** 白装 */
	White(Color.BLACK),
	/** 绿装 */
	Green(0x3c7f06),
	/** 蓝装 */
	Blue(0x215fe6),
	/** 紫装 */
	Purple(0xA020F0),
	/** 黄金装 */
	Gold(0xee5d15),
	/**红色*/
	Red(0xff0000);
	
	/** 颜色编码 */
	private final int colorCode;
	
	/** @param color 颜色 */
	private EquipQuality(final Color color) {
		this.colorCode = color.getRGB() & 0x00FFFFFF;
	}
	
	/** @param colorCode 颜色编码 */
	private EquipQuality(final int colorCode) {
		this.colorCode = colorCode;
	}

	/** @return 颜色编码 */
	public int getColorCode() {
		return colorCode;
	}
	
}
