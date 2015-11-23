package com.joyveb.tlol.boss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.RedisMethod;
import com.joyveb.tlol.mail.MailManager;
import com.joyveb.tlol.role.Role;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.schedule.OnTheSeconedSchedule;
import com.joyveb.tlol.server.ServerMessage;
import com.joyveb.tlol.util.CompareListBoss;

public final class BossService {
	public static Boss boss = Boss.instance;
	public static TreeMap<Integer, Fighter> map = new TreeMap<Integer, Fighter>();
	public static ArrayList<Fighter> orderList = new ArrayList<Fighter>();
	public static int roleKiller = 0;// 最终击杀者的ID
	public static boolean flag = false;// true可以打,false不可以打

	public static List<Integer> noFreezeList = new ArrayList<Integer>();// 今日无冷却报名者
	public static List<Integer> bossHpRestList = new ArrayList<Integer>();// BOSS血量的剩余指标，播报完一次添进去个数，比如播报完9了，就不再报9了
	public static String word = "";
	public static List<Integer> firstWord = new ArrayList<Integer>();// 每个人首次打超过1万血，进行公告的控制list

	public static void beforeDo() {
		ServerMessage.sendGangWorldTalk(bossWord() + "BOSS等级为: " + boss.getLevel());
	}

	public static String bossWord() {

		String[] word = { "不管是谁都不能说强大的术士会输给一群蛮力的家伙！", "	疯狂！将你们带到我的面前,而我将以死亡终结你们！", "凡人！要明白你的处境，逃跑是无济于事的！！！", "你们这是找乐子？ 你们的打击我一点也感觉不到~！", "你们的死亡是我最好的礼物，灵魂将壮大我的力量！", "生命毫无意义，只有死亡才能让你了解生命的真谛！",
				"如果让我失望的话，就准备承受永恒的折磨吧！", "我的渴求是无止限的！", "总有一天，我的生命将抵达终点，而你，将加冕为王！", "你脸上疤痕~~还是不能提醒你，我是不可战胜的吗？", "你永远也站不到世界的最顶端，因为我站在那里~！", "	时间正在倒退，我的力量也正在强大~哈哈哈!", "	尝到失败的滋味了么？我想你会习惯的~！",
				"来吧~杀掉我，就能得到你梦寐以求的力量！", "	运气真好，刚从沉睡中苏醒，就有人来送死！", "能死在我的脚下，是上天给予他们的恩赐！", "你们是强有力的挑战者!看来我要稍微认真一点了!", "骄傲会把你送上绝路，来吧凡人，品尝我的愤怒吧！", "我该先灭掉谁呢？~~每一个都弱这么可怜。", "我已经等待很久啦，现在你们将面对灵魂的收割者！" };
		return word[(int) (Math.random() * 20)];
	}

	/**
	 * @param role
	 *            传入角色对象
	 * @return 1 加入成功，2操作过快，3，BOSS已死 5,意外事件4(由于要传送剩余秒数，把此数设为大于100)，冷却时间
	 */
	public static int join(RoleBean role) {
		long time = System.currentTimeMillis();
		if (boss.getBossHp() <= 0) {
			return 3;
		}

		try {
			Fighter fighter;

			// 报名时判断有无此玩家，有就返回fighter，无就new一个,如果有但是state为false说明正在战斗，则报名不成功。您的操作过快
			if (map.containsKey(role.getRoleid())) {

				fighter = map.get(role.getRoleid());
				// 第一次出手是没有冷却时间的，所以把此方法放在这
				// 如果没在花费了元宝买去除冷却时间的LIST中，则判断是否在冷却时间内
				if (!noFreezeList.contains(fighter.getRoleId())) {
					long passTime = time - fighter.getLastTime();
					if (passTime < fighter.getFreeze()) {
						return 100 + 2 - (int) passTime / 1000;
					}
				}
				if (!fighter.isState()) {
					fighter.setState(true);
					fighter.setLastTime(time);
					fighter = dealHit(role, fighter);
					return 1;
				} else {
					return 2;
				}
			} else {
				fighter = new Fighter();
				fighter.setRoleId(role.getRoleid());
				fighter.setName(role.getNick());
				fighter.setState(true);
				fighter.setLastTime(time);
				fighter = dealHit(role, fighter);
				map.put(role.getRoleid(), fighter);
				System.out.println(fighter.getName() + " 已经报名成功了," + fighter.getHit() + "," + fighter.getTotalHit());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 5;
		}
		return 1;
	}

	/**
	 * 对角色的数值进行处理
	 * 
	 * @param role角色
	 * @param fighter攻击角色对象
	 * @return
	 */
	public static Fighter dealHit(RoleBean role, Fighter fighter) {
		// 定义一个随机数 0-500 ，攻击加上这个数是最终攻击
		// 如果这个攻击大于480，则有20/500 = 4%的概率产生1.5倍暴击
		// 对术士做出加强，暴击概率改成 80/500=16%暴击率
		int plus = (int) (Math.random() * 500);
		switch (role.getVocation()) {
		case SHAQ:
			int oldHit1 = role.getMaxPAtk();
			if (plus < 21) {
				oldHit1 = oldHit1 * 3 / 2;
			}

			fighter.setHit(oldHit1 + plus);
			break;
		case Warlock:
			int oldHit2 = role.getMaxMAtk();
			if (plus < 81) {
				oldHit2 = oldHit2 * 3 / 2;
			}

			fighter.setHit(oldHit2 + plus);
			break;
		case Assassin:
			int oldHit3 = role.getMaxPAtk();
			if (plus < 21) {
				oldHit3 = oldHit3 * 3 / 2;
			}
			fighter.setHit(oldHit3 + plus);
			break;
		default:
			break;
		}
		return fighter;
	}

	/**
	 * 战斗阶段
	 */
	public static void fight() {
		Fighter fighter;
		if (!map.isEmpty()) {
			Iterator<Entry<Integer, Fighter>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Fighter> entry = it.next();
				fighter = entry.getValue();
				if (fighter.isState()) {
					long singleHit;
					if( fighter.getHit()>=3000)
						singleHit = fighter.getHit() - 3000;
					else
						singleHit = 20;

					// boss防御先写 3000
					boss.minusHp(singleHit);
					if (boss.getBossHp() < 0) {
						boss.setBossHp(0);
					}
					fighter.setState(false);
					System.out.println("boss剩余血量---------: " + boss.getBossHp());

					int i = (int) (((boss.getBossMaxHp() - boss.getBossHp()) * 0.1 * 100) / boss.getBossMaxHp());
					if (!bossHpRestList.contains(i) && !(i > 9 || i < 1)) {
						word = "boss的剩余血量：";
						switch (i) {
						case 1:
							word = word + boss.getBossHp() + "，百分比" + "90%";

							break;
						case 2:
							word = word + boss.getBossHp() + "，百分比" + "80%";
							break;

						case 3:
							word = word + boss.getBossHp() + "，百分比" + "70%";
							break;

						case 4:
							word = word + boss.getBossHp() + "，百分比" + "60%";
							break;

						case 5:
							word = word + boss.getBossHp() + "，百分比" + "50%";
							break;

						case 6:
							word = word + boss.getBossHp() + "，百分比" + "40%";
							break;

						case 7:
							word = word + boss.getBossHp() + "，百分比" + "30%";
							break;

						case 8:
							word = word + boss.getBossHp() + "，百分比" + "20%";
							break;

						case 9:
							word = word + boss.getBossHp() + "，百分比" + "10%";
							break;

						default:
							break;
						}
					}

					ServerMessage.sendGangWorldTalk(word);
					word = "";
					bossHpRestList.add(i);
					// 把每次攻击加入总攻击，排榜用
					fighter.addTotalHit(singleHit);
					System.out.println("单次攻击 ： " + (singleHit));
					System.out.println("总攻击 ： " + fighter.getTotalHit());
					RoleBean role = OnlineService.getOnline(fighter.getRoleId());
					if (role != null) {
						ServerMessage.sendSysPrompt(role, "您对BOSS的伤害：" + (singleHit) + ",当前BOSS剩余血量：" + boss.getBossHp());
					}
					// 为了让更多的人在世界进行公告，每人首次打超过一万的血，进行公告
					if (!firstWord.contains(fighter.getRoleId()) && singleHit > 10000) {
						ServerMessage.sendGangWorldTalk(fighter.getName() + " 打出了" + singleHit + "的超高伤害");
						firstWord.add(fighter.getRoleId());
					}

					if (boss.getBossHp() <= boss.getBossMaxHp() * 0.05 && boss.getBossHp() > 0) {
						ServerMessage.sendGangWorldTalk("当前BOSS剩余血量: " + boss.getBossHp());
					}

					if (boss.getBossHp() <= 0) {
						roleKiller = fighter.getRoleId();
						String s = "世界BOSS已被击毁，战胜它的英雄是 " + fighter.getName() + " ,全服在线玩家获得《普天同庆》礼包。";
						// 给英雄发礼包，给在线玩家发礼包
						MailManager.getInstance().send_GM_Mail(roleKiller, "斩首英雄", "请查收！", 0, 130208);
						ArrayList<Integer> allRole = OnlineService.getAllOnlines();
						for (Integer integer : allRole) {
							MailManager.getInstance().send_GM_Mail(integer, "普天同庆礼包", "请查收！", 0, 5147);//暂无礼包id
						}

						ServerMessage.sendGangWorldTalk(s);
						// 排行榜上的发礼包
						sendPresent();
						// boss等级加1

						System.out.println("这里是战斗胜利后取得BOSS等级" + RedisMethod.instance().getBossLevel());
						RedisMethod.instance().incrBossLevel();
						System.out.println("这里是战斗胜利后取得BOSS等级&&&" + RedisMethod.instance().getBossLevel());

						return;
					}
				}
			}
		}
	}

	public static void bossNotDead() {

		String s = "世界BOSS未被击毁，时间已到。";
		ServerMessage.sendGangWorldTalk(s);
		// 清空map
		map.clear();
		// boss等级减1
		if (boss.getLevel() > 1) {
			RedisMethod.instance().decrBossLevel();
		}

	}

	/**
	 * 前3名发奖
	 */
	public static void sendPresent() {
		orderList = sortMap();
		StringBuffer sb0 = new StringBuffer();
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		StringBuffer sb3 = new StringBuffer();

		if (!map.isEmpty()) {
			sb0.append("本次世界BOSS战已结束,取得BOSS伤害的前3名： ");

			for (int i = 0; i < orderList.size(); i++) {
				if (i == 0) {
					MailManager.getInstance().send_GM_Mail_Num(orderList.get(i).getRoleId(), "世界BOSS奖励", "请查收！", 0, 130205,
							Boss.instance.getLevel() % 10 > 0 ? Boss.instance.getLevel() / 10 + 1 : Boss.instance.getLevel() / 10);
					sb1.append(" 第1名为:  ");
					sb1.append(orderList.get(i).getName());
					sb1.append(",总伤害：" + orderList.get(i).getTotalHit());
				}
				if (i == 1) {
					MailManager.getInstance().send_GM_Mail_Num(orderList.get(i).getRoleId(), "世界BOSS奖励", "请查收！", 0, 130206,
							Boss.instance.getLevel() % 10 > 0 ? Boss.instance.getLevel() / 10 + 1 : Boss.instance.getLevel() / 10);
					sb2.append(" 第2名为: ");
					sb2.append(orderList.get(i).getName());
					sb2.append(",总伤害：" + orderList.get(i).getTotalHit());
				}
				if (i == 2) {
					MailManager.getInstance().send_GM_Mail_Num(orderList.get(i).getRoleId(), "世界BOSS奖励", "请查收！", 0, 130207,
							Boss.instance.getLevel() % 10 > 0 ? Boss.instance.getLevel() / 10 + 1 : Boss.instance.getLevel() / 10);
					sb3.append(" 第3名为:  ");
					sb3.append(orderList.get(i).getName());
					sb3.append(",总伤害：" + orderList.get(i).getTotalHit());
				}
			}
		}
		if (!(sb1.toString().equals("") && sb2.toString().equals("") && sb3.toString().equals(""))) {
			ServerMessage.sendGangWorldTalk(sb0.toString());
		}
		if (!sb1.toString().equals("")) {
			ServerMessage.sendGangWorldTalk(sb1.toString());
		}
		if (!sb2.toString().equals("")) {
			ServerMessage.sendGangWorldTalk(sb2.toString());
		}
		if (!sb3.toString().equals("")) {
			ServerMessage.sendGangWorldTalk(sb3.toString());
		}
		if (!(sb1.toString().equals("") && sb2.toString().equals("") && sb3.toString().equals(""))) {
			ServerMessage.sendGangWorldTalk("他们将获得《战将礼包》");
		}
		orderList.clear();
		setFlag(false);
		map.clear();
	}
	
	
	public static void changeFlag(){
		noFreezeList.clear();
		bossHpRestList.clear();
		firstWord.clear();
		orderList.clear();
		Boss.instance.setBasicHp(1000000l);
		OnTheSeconedSchedule.	change();
	}

	/**
	 * @return 排过序的Map
	 * 
	 */
	public static ArrayList<Fighter> sortMap() {

		for (Fighter f : map.values()) {
			orderList.add(f);
		}
		CompareListBoss sortList = new CompareListBoss();
		Collections.sort(orderList, sortList);
		return orderList;

	}

	/**
	 * @return the noFreezeList
	 */
	public static List<Integer> getNoFreezeList() {
		return noFreezeList;
	}

	/**
	 * @param noFreezeList
	 *            the noFreezeList to set
	 */
	public static void setNoFreezeList(List<Integer> noFreezeList) {
		BossService.noFreezeList = noFreezeList;
	}

	public static boolean addNoFreezeList(Role role) {

		return noFreezeList.add(role.getRoleid());
	}

	public static boolean getIfNoFreezeList(Role role) {

		return noFreezeList.contains(role.getRoleid());
	}

	/**
	 * @return the roleKiller
	 */
	public static int getRoleKiller() {
		return roleKiller;
	}

	/**
	 * @param roleKiller
	 *            the roleKiller to set
	 */
	public static void setRoleKiller(int roleKiller) {
		BossService.roleKiller = roleKiller;
	}

	/**
	 * @return the flag
	 */
	public static boolean isFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public static void setFlag(boolean flag) {
		BossService.flag = flag;
	}
}
