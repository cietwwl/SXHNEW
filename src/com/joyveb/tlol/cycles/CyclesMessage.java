package com.joyveb.tlol.cycles;

import com.joyveb.tlol.util.Cardinality;

public class CyclesMessage implements Comparable<CyclesMessage>{
	/**总局数*/
	private int inning;
	/**发起挑战人物的ID*/
	private int challengeRoleid;
	/**应战人物ID*/
	private int acceptRoleid;
	/**金币*/
	private int gold;
	/**本房间时间*/
	private int nowTime;
	/**每局时间*/
	private static final int TIME_PEROUND = 30;
	/**是否胜利（0，胜利；1，失败;2,平局）*/
	private String win;
	/**玩家昵称*/
	private String name;
	
	
	/**押注的选择*/
	private Cycles cycles;
	
	
	CyclesMessage(){
		
	}
	/**
	 * 添加发起挑战的TreeSet
	 * @param inning
	 * @param challengeRoleid
	 * @param gold
	 * @param cyclesId
	 */
	CyclesMessage(int inning, int challengeRoleid, String name, int gold, int cyclesId){
		int systemTime = Cardinality.INSTANCE.getMinute();
		this.inning = inning;
		this.challengeRoleid = challengeRoleid;
		this.gold = gold;
		this.nowTime = TIME_PEROUND+systemTime;
		this.cycles =  Cycles.values()[cyclesId];
		this.name = name;
	}
	/**
	 * 发起挑战记录表
	 * @param inning
	 * @param challengeRoleid
	 * @param name
	 * @param gold
	 * @param win
	 */
	public CyclesMessage(int inning, int challengeRoleid, String name, int gold, String win){
		this.inning = inning;
		this.challengeRoleid = challengeRoleid;
		this.gold = gold;
		this.win = win;
		this.name = name;
	}
	/**
	 * 应战记录记录存储
	 * @param inning
	 * @param challengeRoleid
	 * @param acceptRoleid
	 * @param gold
	 * @param win
	 */
	public CyclesMessage(int inning, int challengeRoleid, int acceptRoleid, String name, int gold, String win){
		this.inning = inning;
		this.challengeRoleid = challengeRoleid;
		this.acceptRoleid = acceptRoleid;
		this.gold = gold;
		this.win = win;
		this.name = name;
	}
	
	public int getInning() {
		return inning;
	}
	public int getChallengeRoleid() {
		return challengeRoleid;
	}
	public int getAcceptRoleid() {
		return acceptRoleid;
	}
	public int getGold() {
		return gold;
	}
	public int getNowTime() {
		return nowTime;
	}
	public Cycles getCycles() {
		return cycles;
	}
	public String getWin() {
		return win;
	}
	public String getName() {
		return name;
	}
	@Override
	public int compareTo(CyclesMessage o) {
		return this.inning > o.getInning() ? -1 : (this.inning == o.getInning() ? 0 : 1);
	}
	
}
