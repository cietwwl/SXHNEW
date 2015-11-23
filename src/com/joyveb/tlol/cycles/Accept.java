package com.joyveb.tlol.cycles;

import java.util.Iterator;
import java.util.TreeSet;

import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.mail.MailManager;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCardService;

public class Accept {
	
	/**单例类的常量*/
	private static Accept instance = new Accept();
	/**手续费*/
	private static final double COMMISSION = 0.1; 
	private Accept(){
		
	}
	/**
	 * @return instance
	 */
	public static Accept getInstance() {
		return instance;
	}
	
	@SuppressWarnings("static-access")
	public void cyclesResults(int inning,int acceptRoleid,int cyclesId){
		TreeSet<CyclesMessage> challengeTreeSet = Challenge.getInstance().getChallengeTreeSet();
		Iterator<CyclesMessage> challengeIterator = challengeTreeSet.iterator();
		while(challengeIterator.hasNext()) {
			CyclesMessage cyclesMessage = challengeIterator.next();
			
			if(cyclesMessage.getInning() == inning){
				
				Cycles acceptCycles =  Cycles.values()[cyclesId];
				Cycles challengeCycles = cyclesMessage.getCycles();
				int challengeRoleid = cyclesMessage.getChallengeRoleid();//发起挑战者得ID
				int gold = cyclesMessage.getGold();//本局金币
				int winGold = (int)(gold * (2-this.COMMISSION));//赢家赢得金币
				//挑战双方存记录时使用的获得方式
				Win challengeWin = null;
				Win acceptWin = null;
				//获得挑战双方的人物信息
				RoleBean acceptRole = OnlineService.getOnline(acceptRoleid);
				RoleBean challengeRole = OnlineService.getOnline(challengeRoleid);
				String challengeName = "";
				String acceptName = "";
				if(RoleCardService.INSTANCE.getCard(challengeRoleid) !=null){
					challengeName = RoleCardService.INSTANCE.getCard(challengeRoleid).getName();//给发起挑战的人
				}
				if(RoleCardService.INSTANCE.getCard(acceptRoleid) != null){
					acceptName = RoleCardService.INSTANCE.getCard(acceptRoleid).getName();//给发起应战的人加应战记录
				}
				
				
				
				//挑战输的情况
				if((challengeCycles.getCyclesId() == 0 && acceptCycles.getCyclesId() == 1) || (challengeCycles.getCyclesId() == 1 && acceptCycles.getCyclesId() == 2) || (challengeCycles.getCyclesId() == 2 && acceptCycles.getCyclesId() == 0)){
					//发送金币
					MailManager.getInstance().sendSysMail(acceptRoleid, "老虎棒子鸡结果", "尊敬的玩家，恭喜您获得本局老虎棒子鸡的奖金，请注意查收您邮件中的金币。" , winGold, null);
					MailManager.getInstance().sendSysMail(challengeRoleid, "老虎棒子鸡结果", "尊敬的玩家，很可惜您输掉了本局老虎棒子鸡的奖金。希望下一次好运降临到您的身上。", 0, null);
					challengeWin = Win.values()[1]; 
					acceptWin = Win.values()[0];
					//增加记录
					if(challengeRole != null){
						challengeRole.addChallengeRecord(inning, challengeRoleid, acceptRoleid, acceptName, 0, challengeWin.toString());
					}
					if(acceptRole != null){
						acceptRole.addAcceptRecord(inning, challengeRoleid, acceptRoleid, challengeName, winGold, acceptWin.toString());
					}
				}
				//应战输的情况
				if((challengeCycles.getCyclesId() == 0 && acceptCycles.getCyclesId() == 2) || (challengeCycles.getCyclesId() == 1 && acceptCycles.getCyclesId() == 0) || (challengeCycles.getCyclesId() == 2 && acceptCycles.getCyclesId() == 1)){
					MailManager.getInstance().sendSysMail(challengeRoleid, "老虎棒子鸡结果", "尊敬的玩家，恭喜您获得本局老虎棒子鸡的奖金，请注意查收您邮件中的金币。" , winGold, null);
					MailManager.getInstance().sendSysMail(acceptRoleid, "老虎棒子鸡结果", "尊敬的玩家，很可惜您输掉了本局老虎棒子鸡的奖金。希望下一次好运降临到您的身上。", 0, null);
					challengeWin = Win.values()[0]; 
					acceptWin = Win.values()[1];
					//增加记录
					if(challengeRole != null){
						challengeRole.addChallengeRecord(inning, challengeRoleid, acceptRoleid, acceptName, winGold, challengeWin.toString());
					}
					if(acceptRole != null){
						acceptRole.addAcceptRecord(inning, challengeRoleid, acceptRoleid, challengeName, 0, acceptWin.toString());
					}
				}
				//平局情况
				if(challengeCycles.getCyclesId() == acceptCycles.getCyclesId()){
					MailManager.getInstance().sendSysMail(challengeRoleid, "老虎棒子鸡结果", "尊敬的玩家，恭喜您获得本局老虎棒子鸡的奖金，结果为平局，请注意查收您邮件中的金币。" , gold, null);
					MailManager.getInstance().sendSysMail(acceptRoleid, "老虎棒子鸡结果", "尊敬的玩家，恭喜您获得本局老虎棒子鸡的奖金，结果为平局,请注意查收您邮件中的金币。" , gold, null);
					challengeWin = Win.values()[2]; 
					acceptWin = Win.values()[2];
					//增加记录
					if(challengeRole != null){
						challengeRole.addChallengeRecord(inning, challengeRoleid, acceptRoleid, acceptName, gold, challengeWin.toString());
					}
					if(acceptRole != null){
						acceptRole.addAcceptRecord(inning, challengeRoleid, acceptRoleid, challengeName, gold, acceptWin.toString());
					}
				}
				//删除发起挑战的Vector
				challengeIterator.remove();
			}
		}
	}
	/**
	 * 查询该局数的挑战房间是否存在
	 * @param inning
	 * @return
	 */
	public boolean queryChallenge(int inning){
		boolean isExist = false;
		TreeSet<CyclesMessage> challengeTreeSet = Challenge.getInstance().getChallengeTreeSet();
		Iterator<CyclesMessage> cyclesMessageIterator = challengeTreeSet.iterator();
		while(cyclesMessageIterator.hasNext()) {
			CyclesMessage cyclesMessage = cyclesMessageIterator.next();
			if(cyclesMessage.getInning() == inning){
				isExist = true;
			}
		}
		return isExist;
	}

}
