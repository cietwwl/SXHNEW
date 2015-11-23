package com.joyveb.tlol.hegemony;

import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;

public class Hegemony implements Comparable<Hegemony> {
	private static int CLASSID = 0;
	private int selfId;
	private int points;
	private int ranking;
	private RoleBean Hegemony;
	private int acctLevel;
	private int resultPoints;

	public Hegemony() {
		this.selfId = ++CLASSID;
		this.points = 100;

	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public RoleBean getHegemony() {
		return Hegemony;
	}

	public void setHegemony(RoleBean hegemony) {
		Hegemony = hegemony;
	}

	public int getAcctLevel() {
		return acctLevel;
	}

	public void setAcctLevel(int acctLevel) {
		this.acctLevel = acctLevel;
	}

	public int getSelfId() {
		return selfId;
	}

	public int getResultPoints() {
		return resultPoints;
	}

	public void setResultPoints(int resultPoints) {
		this.resultPoints = resultPoints;
	}

	@Override
	public int compareTo(Hegemony o) {
		if (o != null) {
			return o.getPoints() - this.points;
		}
		return 0;
	}

	public void outOfBattle() {
		if (this.Hegemony.getBattle() != null) {
			MessageSend.body.clear();
			MessageSend.body.putInt(0);
			MessageSend.putString("此次战斗以平局记");
			MessageSend.putInt(0);
			MessageSend.putShort((short) 0);
			MessageSend.sendMsg(this.Hegemony, MsgID.MsgID_Fight_Closing);
		}

	}

}
