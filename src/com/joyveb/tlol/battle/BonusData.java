package com.joyveb.tlol.battle;

import java.util.ArrayList;
import java.util.List;

import com.joyveb.tlol.item.Item;

class BonusData {

	private List<ModifyData> modifyDataList = new ArrayList<ModifyData>();
	private List<ModifyItemData> itemDataList = new ArrayList<ModifyItemData>();
	private String taskprop = null;

	public List<ModifyData> getModifyDataList() {
		return modifyDataList;
	}

	public void setModifyDataList(final List<ModifyData> modifyDataList) {
		this.modifyDataList = modifyDataList;
	}

	public List<ModifyItemData> getItemDataList() {
		return itemDataList;
	}

	public void setItemDataList(final List<ModifyItemData> itemDataList) {
		this.itemDataList = itemDataList;
	}

	public String getTaskprop() {
		return taskprop;
	}

	public void setTaskprop(final String taskprop) {
		this.taskprop = taskprop;
	}
}

class ModifyItemData {

	/** 0增加 1减少 **/
	private byte modifyAct;
	private Item item;

	public byte getModifyAct() {
		return modifyAct;
	}

	public void setModifyAct(final byte modifyAct) {
		this.modifyAct = modifyAct;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(final Item item) {
		this.item = item;
	}

	ModifyItemData(final byte modifyAct, final Item item) {
		this.modifyAct = modifyAct;
		this.item = item;
	}
}

class ModifyData {
	/**奖励或惩罚的类型（0.经验,1金币）*/
	private byte modifyType;

	/** 0增加 1减少 **/
	private byte modifyAct;

	private int modifyValue;

	public byte getModifyType() {
		return modifyType;
	}

	public void setModifyType(final byte modifyType) {
		this.modifyType = modifyType;
	}

	public byte getModifyAct() {
		return modifyAct;
	}

	public void setModifyAct(final byte modifyAct) {
		this.modifyAct = modifyAct;
	}

	public int getModifyValue() {
		return modifyValue;
	}

	public void setModifyValue(final int modifyValue) {
		this.modifyValue = modifyValue;
	}

	ModifyData(final int modifyTpye) {
		modifyType = (byte) modifyTpye;
	}
}
