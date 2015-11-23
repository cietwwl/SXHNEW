package com.joyveb.tlol.cycles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.mail.MailManager;
import com.joyveb.tlol.schedule.MinTickHandler;

public class Challenge implements MinTickHandler{
	/**单例类的常量*/
	private static Challenge instance = new Challenge();
	private int inning;
	private Challenge(){
		
	}
	
	/**
	 * @return instance
	 */
	public static Challenge getInstance() {
		return instance;
	}
	private static TreeSet<CyclesMessage> challengeTreeSet = new TreeSet<CyclesMessage>();
	private ArrayList<CyclesMessage> cyclesMessageArrayList = new ArrayList<CyclesMessage>();
	
	
	public void challengeAddCyclesMessage(int challengeRoleid,int gold,int cyclesId){
		String name = OnlineService.getOnline(challengeRoleid).getNick();
		this.inning ++;
		CyclesMessage cyclesMessage = new CyclesMessage(inning,challengeRoleid, name, gold, cyclesId);
		challengeTreeSet.add(cyclesMessage);
	}
	
	public TreeSet<CyclesMessage> getChallengeTreeSet() {
		return challengeTreeSet;
	}
	/**
	 * 返回发起挑战人员最早的5个人
	 * @return
	 */
	public ArrayList<CyclesMessage> getChallengeCyclesMessages() {
		cyclesMessageArrayList.clear();
		if(challengeTreeSet != null){
			Iterator<CyclesMessage> cyclesMessageIterator = challengeTreeSet.iterator();
			ArrayList<CyclesMessage> cyclesMessageArrayListTemp = new ArrayList<CyclesMessage>();
			while(cyclesMessageIterator.hasNext() ) {
				CyclesMessage cyclesMessage = cyclesMessageIterator.next();
				cyclesMessageArrayListTemp.add(cyclesMessage);
			}
			for(int index = cyclesMessageArrayListTemp.size() - 1 ; index >= cyclesMessageArrayListTemp.size() - 10 && index >= 0; index--){
				CyclesMessage cyclesMessage = cyclesMessageArrayListTemp.get(index);
				cyclesMessageArrayList.add(cyclesMessage);
			}
		}
		return cyclesMessageArrayList;
	}
	
	public CyclesMessage getChallengeCyclesMessage(int inning){
		CyclesMessage cyclesMessage = new CyclesMessage();
		Iterator<CyclesMessage> cyclesMessageIterator = challengeTreeSet.iterator();
		while(cyclesMessageIterator.hasNext()){
			CyclesMessage cyclesMessageTemp = cyclesMessageIterator.next();
			if(cyclesMessageTemp.getInning() == inning){
				cyclesMessage = cyclesMessageTemp;
			}
		}
		return cyclesMessage;
	}

	/**
	 * 删除发起挑战的玩家的信息
	 * @param inning 总局数，即ID
	 */
	public void deleteCycelsMessage(CyclesMessage cyclesMessage){
		challengeTreeSet.remove(cyclesMessage);
	}
	/**
	 * 删除所有发起挑战人的信息
	 */
	public void challengedeleteAllCycelsMessage(){
		challengeTreeSet.clear();
	}
	/**
	 * 退钱
	 */
	public void moneyBack(){
		Iterator<CyclesMessage> cyclesMessageIterator = challengeTreeSet.iterator();
		while(cyclesMessageIterator.hasNext()) {
			CyclesMessage cyclesMessage = cyclesMessageIterator.next();
			MailManager.getInstance().sendSysMail(cyclesMessage.getChallengeRoleid(), "老虎棒子鸡退钱", "尊敬的玩家，老虎棒子鸡游戏的金币已退回" , cyclesMessage.getGold() , null);
			//it.remove();
		}
	}
	@Override
	public void minTick(final int curMin) {
		Iterator<CyclesMessage> cyclesMessageIterator = challengeTreeSet.iterator();
		while(cyclesMessageIterator.hasNext()) {
			CyclesMessage cyclesMessage = cyclesMessageIterator.next();
			if(curMin >= cyclesMessage.getNowTime()){
				MailManager.getInstance().sendSysMail(cyclesMessage.getChallengeRoleid(), "老虎棒子鸡退钱", "尊敬的玩家，老虎棒子鸡游戏时间已到，金币已退回" , cyclesMessage.getGold() , null);
				//it.remove();
				challengeTreeSet.remove(cyclesMessage);
			}
		}
	}
	/**
	 * 关闭服务器时的判断
	 */
	public void shutdown(){
		this.moneyBack();
		this.challengedeleteAllCycelsMessage();
	}
}
