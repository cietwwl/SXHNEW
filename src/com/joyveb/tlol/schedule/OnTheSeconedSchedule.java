package com.joyveb.tlol.schedule;

import java.util.Calendar;
import java.util.Date;

import com.joyveb.tlol.GangFightService;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.RedisMethod;
import com.joyveb.tlol.boss.Boss;
import com.joyveb.tlol.boss.BossService;
import com.joyveb.tlol.util.Cardinality;

public enum OnTheSeconedSchedule implements ScheduleTask {
	INSTANCE;
	int hour = 11;

	private int lastSecond = -1;
	private static long limitTime = 0;
	private static boolean isFirstDo = false;// 本天是不是第一次执行喊话
	static long bosslimitTime = 0;
	int bossHour = 21;
	static boolean isFirstBoss = false;

	public static int flaggggggg = 0;

	public static void change() {

		isFirstBoss = false;
		bosslimitTime = 0;
	}

	@Override
	public void execute() {

		int curSecond = Cardinality.INSTANCE.getSecond();

		if (curSecond == lastSecond)
			return;
		OnlineService.seconedTick(curSecond);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());

		// 帮派战斗
		if ((c.get(Calendar.HOUR_OF_DAY) == 11 && (c.get(Calendar.MINUTE) == 55))) {
			GangFightService.start();
		}

		// 帮派战斗喊话过程
		if (c.get(Calendar.HOUR_OF_DAY) > hour && !GangFightService.wordEnd) {
			if (!isFirstDo) {
				limitTime = System.currentTimeMillis();
				isFirstDo = true;
				GangFightService.beforeTalk();

			}
			long nowTime = System.currentTimeMillis();
			if (nowTime - limitTime >= 5000 && limitTime != 0 && nowTime - limitTime <= 24 * 60 * 60 * 1000) {
				GangFightService.talk();
				limitTime = nowTime;
			}
		}

		// 标记改变
		if ((c.get(Calendar.HOUR_OF_DAY) == 1 && (c.get(Calendar.MINUTE) == 1))) {
			OnlineService.setStart(true);
			GangFightService.setStart(false);
			GangFightService.setWordEnd(false);
			GangFightService.setRound(0);
			GangFightService.gangFightMap.clear();
			GangFightService.everyGangList.clear();
			GangFightService.fightWordLists.clear();
			isFirstDo = false;
			limitTime = 0;
			isFirstBoss = false;
			bosslimitTime = 0;
		}

		// 标记改变//||(c.get(Calendar.HOUR_OF_DAY) == 22&& (c.get(Calendar.MINUTE) == 1))
		if ((c.get(Calendar.HOUR_OF_DAY) == 11&& (c.get(Calendar.MINUTE) == 1))||(c.get(Calendar.HOUR_OF_DAY) == 23&& (c.get(Calendar.MINUTE) == 1))) {
			BossService.changeFlag();
		}
		if (c.get(Calendar.HOUR_OF_DAY) == 20 && (c.get(Calendar.MINUTE) == 45)) {
			OnlineService.settleHegemony();
		}

		// /-------------------------------------------------------世界BOSS战--------------------------
//(c.get(Calendar.HOUR_OF_DAY) == 21) ||
		// 击杀BOSS开始
		if ( (c.get(Calendar.HOUR_OF_DAY) == 22) ||(c.get(Calendar.HOUR_OF_DAY) == 10)) {

			if (!isFirstBoss) {
				System.out.println("这就开始了&……&*……%……&……&￥%￥");
				// 在这里把BOSS的血量赋值
				int level = RedisMethod.instance().getBossLevel();
				Boss.instance.setLevel(level);
				long hp = Boss.instance.getLevel() * Boss.instance.getBasicHp();
				Boss.instance.setBossMaxHp(hp);
				Boss.instance.setBossHp(hp);
				BossService.setFlag(true);
				bosslimitTime = System.currentTimeMillis();
				isFirstBoss = true;
				BossService.beforeDo();
				return;
			}
			if (BossService.isFlag()) {
				long nowTime = System.currentTimeMillis();
				if (nowTime - bosslimitTime >= 1000 && bosslimitTime != 0 && nowTime - bosslimitTime <= 24 * 60 * 60 * 1000) {
					BossService.fight();
					bosslimitTime = nowTime;
				}
			}
		}

		 //击杀BOSS结束 BOSS未死
		if ((c.get(Calendar.HOUR_OF_DAY) == 23) && (c.get(Calendar.MINUTE) == 1)) {
			if (Boss.instance.getBossHp() > 0 && !BossService.map.isEmpty()) {
				BossService.bossNotDead();
			}
		}
		if ((c.get(Calendar.HOUR_OF_DAY) == 11) && (c.get(Calendar.MINUTE) == 1)) {
			if (Boss.instance.getBossHp() > 0 && !BossService.map.isEmpty()) {
				BossService.bossNotDead();
			}
		}
		// -----------------------------------------------世界BOSS------------------------------------------------------------

		lastSecond = curSecond;

	}

	@Override
	public boolean isTimeOut() {
		return false;
	}

}
