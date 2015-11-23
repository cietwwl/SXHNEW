package com.joyveb.tlol.skill;

public class SkillDataOne {
	/**
	 * Icon 图标 U16 ID 技能号 U32 Index 排列的索引 U16 Type 类型：0主动1被动 U8 Num
	 * 作用人数（客户端选择的人数） U8 Little_Title_Length 精简标题长度(用于打斗选择) U16 Little_Title
	 * 精简标题（名称+等级） String Tile_Length 标题长度（用于列表显示，详细） U16 Tile 标题 String
	 * Other_Data_length 保留数据长 U16 Other_Data 保留数据 Data
	 */

	private short icon;
	private int id;
	private short index;
	private short type;
	private short Num;
	private byte effectObj;
	private int manaCost;
	private String littleTitle;
	private String title;

	// int lv;
	//
	// public SkillDataOne(int lv){
	// this.lv = lv;
	// }

	public final short getIcon() {
		return icon;
	}

	public final void setIcon(final short icon) {
		this.icon = icon;
	}

	public final int getId() {
		return id;
	}

	public final void setId(final int id) {
		this.id = id;
	}

	public final short getIndex() {
		return index;
	}

	public final void setIndex(final short index) {
		this.index = index;
	}

	public final short getType() {
		return type;
	}

	public final void setType(final short type) {
		this.type = type;
	}

	public final short getNum() {
		return Num;
	}

	public final void setNum(final short num) {
		Num = num;
	}

	public final String getLittleTitle() {
		return littleTitle;
	}

	public final void setLittleTitle(final String littleTitle) {
		this.littleTitle = littleTitle;
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(final String title) {
		this.title = title;
	}

	public final byte getEffectObj() {
		return effectObj;
	}

	public final void setEffectObj(final byte effectObj) {
		this.effectObj = effectObj;
	}

	public final int getManaCost() {
		return manaCost;
	}

	public final void setManaCost(final int manaCost) {
		this.manaCost = manaCost;
	}

	// public byte getEffectObj(){
	// return (byte)LuaService.callForInt(AgentProxy.L, "effectObj", id);
	// }
	//
	// public int getManaCost(){
	// return LuaService.callForInt(AgentProxy.L, "getManaCost", id, lv);
	// }
}
