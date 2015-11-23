package com.joyveb.tlol.battle;

import java.util.ArrayList;
import java.util.List;

public class RoundData {

	/**(0,人，1.怪)*/
	private byte type; 

	private byte seatID;

	private int id;
	/**攻击的方法（0原地攻击1跳到怪物面前攻击2不攻击（逃跑失败））*/
	private byte fightMthod; 
	/**主角攻击动作动画组ID*/
	private short fGroupId;
	/**主角攻击动作效果动画ID*/
	private short fAnimationId;
	/**攻击效果的动画组ID*/
	private short eGroupId;
	/**攻击效果的动画id*/
	private short eAnimationId;
	/**被攻击角色效果动画组ID*/
	private short bGroupId;
	/**被攻击角色效果动画ID*/
	private short bAnimationId;

	private short detailLen;

	private byte[] detail;


	private List<OtherState> otherStateList = new ArrayList<OtherState>();


	private List<FightOne> fightOneList = new ArrayList<FightOne>();


	private short otherData;

	public final byte getType() {
		return type;
	}

	public final void setType(final byte type) {
		this.type = type;
	}

	public final byte getSeatID() {
		return seatID;
	}

	public final void setSeatID(final byte seatID) {
		this.seatID = seatID;
	}

	public final int getId() {
		return id;
	}

	public final void setId(final int id) {
		this.id = id;
	}

	public final byte getFightMthod() {
		return fightMthod;
	}

	public final void setFightMthod(final byte fightMthod) {
		this.fightMthod = fightMthod;
	}

	public final short getfGroupId() {
		return fGroupId;
	}

	public final void setfGroupId(final short fGroupId) {
		this.fGroupId = fGroupId;
	}

	public final short getfAnimationId() {
		return fAnimationId;
	}

	public final void setfAnimationId(final short fAnimationId) {
		this.fAnimationId = fAnimationId;
	}

	public final short geteGroupId() {
		return eGroupId;
	}

	public final void seteGroupId(final short eGroupId) {
		this.eGroupId = eGroupId;
	}

	public final short geteAnimationId() {
		return eAnimationId;
	}

	public final void seteAnimationId(final short eAnimationId) {
		this.eAnimationId = eAnimationId;
	}

	public final short getbGroupId() {
		return bGroupId;
	}

	public final void setbGroupId(final short bGroupId) {
		this.bGroupId = bGroupId;
	}

	public final short getbAnimationId() {
		return bAnimationId;
	}

	public final void setbAnimationId(final short bAnimationId) {
		this.bAnimationId = bAnimationId;
	}

	public final short getDetailLen() {
		return detailLen;
	}

	public final void setDetailLen(final short detailLen) {
		this.detailLen = detailLen;
	}

	public final byte[] getDetail() {
		return detail;
	}

	public final void setDetail(final byte[] detail) {
		this.detail = detail;
	}

	public final List<OtherState> getOtherStateList() {
		return otherStateList;
	}

	public final void setOtherStateList(final List<OtherState> otherStateList) {
		this.otherStateList = otherStateList;
	}

	public final List<FightOne> getFightOneList() {
		return fightOneList;
	}

	public final void setFightOneList(final List<FightOne> fightOneList) {
		this.fightOneList = fightOneList;
	}

	public final short getOtherData() {
		return otherData;
	}

	public final void setOtherData(final short otherData) {
		this.otherData = otherData;
	}

}

class OtherState {

	private short icon;

	private byte stateType;

	private byte stateMethod;

	private short stateValue;

	private short otherDataLength;
	public short getIcon() {
		return icon;
	}
	public void setIcon(final short icon) {
		this.icon = icon;
	}
	public byte getStateType() {
		return stateType;
	}
	public void setStateType(final byte stateType) {
		this.stateType = stateType;
	}
	public byte getStateMethod() {
		return stateMethod;
	}
	public void setStateMethod(final byte stateMethod) {
		this.stateMethod = stateMethod;
	}
	public short getStateValue() {
		return stateValue;
	}
	public void setStateValue(final short stateValue) {
		this.stateValue = stateValue;
	}
	public short getOtherDataLength() {
		return otherDataLength;
	}
	public void setOtherDataLength(final short otherDataLength) {
		this.otherDataLength = otherDataLength;
	}
	
}
