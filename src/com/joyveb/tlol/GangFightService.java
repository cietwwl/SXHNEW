package com.joyveb.tlol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.joyveb.tlol.gang.Gang;
import com.joyveb.tlol.gang.GangFight;
import com.joyveb.tlol.gang.GangService;
import com.joyveb.tlol.mail.MailManager;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.role.RoleCardService;
import com.joyveb.tlol.role.Vocation;
import com.joyveb.tlol.server.ServerMessage;
import com.joyveb.tlol.util.CompareListGangFight;

public final class GangFightService {
	/** 开始战斗标 ,false未开始过，true开始过了 */
	public static boolean start = false;
	public static boolean wordEnd = false;
	public static int round = 0;

	/** 帮战名单 */
	public static Map<Integer, List<GangFight>> gangFightMap = new HashMap<Integer, List<GangFight>>();
	/** 战斗的所有话语 */
	public static List<Map<Integer, List<String>>> fightWordLists = new ArrayList<Map<Integer, List<String>>>();

	/** 最后的WINNER 帮派的List<GangFight> */
	public static List<List<GangFight>> winList = new ArrayList<List<GangFight>>();

	/** 记录每轮都是哪几个帮， */
	public static List<List<Integer>> everyGangList = new ArrayList<List<Integer>>();

	public static Map<Integer, List<GangFight>> getGangFightList() {
		return gangFightMap;
	}

	public static void setGangFightList(Map<Integer, List<GangFight>> gangFightMap) {
		GangFightService.gangFightMap = gangFightMap;
	}

	public static boolean isStart() {
		return start;
	}

	public static void setStart(boolean start) {
		GangFightService.start = start;
	}

	public static void setWordEnd(boolean wordEnd) {
		GangFightService.wordEnd = wordEnd;
	}

	public static int getRound() {
		return round;
	}

	public static void setRound(int round) {
		GangFightService.round = round;
	}

	public static boolean putGangProperty(int roleid, int leaderid) {
		try {
			RoleBean role = OnlineService.getOnline(roleid);

			if (role != null) {
				GangFight gangFight = new GangFight();
				gangFight.setRoleid(roleid);
				gangFight.setName(role.getName() + "(" + GangService.INSTANCE.getGang(role.getGangid()).getName() + ")");// 名字
				gangFight.setLeaderId(leaderid);// 帮主ID
				gangFight.setGangId((int) role.getGangid());// 帮派ID
				gangFight.setVocation(role.getVocation());// 职业
				gangFight.setLevel((int) role.getLevel());// 等级
				gangFight.setStrength(role.getStrength());// 力量
				gangFight.setAgility(role.getAgility());// 敏捷
				gangFight.setIntellect(role.getIntellect());// 智力
				gangFight.setVitality(role.getVitality());// 体质
				gangFight.setMinPAtk(role.getMinPAtk());// 最小物攻
				gangFight.setMaxPAtk(role.getMaxPAtk());// 最大物攻
				gangFight.setMinMAtk(role.getMinMAtk());// 最小法攻
				gangFight.setMaxMAtk(role.getMaxMAtk());// 最大法攻
				gangFight.setpDef(role.getpDef());// 物理防御
				gangFight.setmDef(role.getmDef());// 法术防御
				gangFight.setHit(role.getHit());// 命中
				gangFight.setEvade(role.getEvade());// 躲闪
				gangFight.setCrit(role.getCrit());// 致命
				gangFight.setAtkSpd(role.getAtkSpd());// 攻速
				gangFight.setMaxHp(role.getMaxHP());// 最大血
				gangFight.setMaxMp(role.getMaxMP());// 最大蓝
				gangFight.setHp(role.getMaxHP() + role.getVitality() * 8 + role.getIntellect() * 5);// 实际血
				gangFight.setNewMaxHp(gangFight.getHp());// 实际最大血
				// System.out.println(role.getName() + "的攻速是：" +
				// role.getAtkSpd() + ",  物攻： " + role.getMaxPAtk() + "， 法攻 ：" +
				// role.getMaxMAtk() + ",  血是 ：" + gangFight.getHp());
				gangFightMap.get(leaderid).add(gangFight);
			}
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * 开始帮战
	 * 
	 * */

	public static void start() {
		if (gangFightMap == null | gangFightMap.size() < 1) {
			start = true;
			return;
		}

		if (!start) {
			Collection<List<GangFight>> lists = gangFightMap.values();
			List<List<GangFight>> listAll = new ArrayList<List<GangFight>>();
			Iterator<List<GangFight>> it = lists.iterator();
			while (it.hasNext()) {
				List<GangFight> list = it.next();
				if (list.size() < 9) {
					MailManager.getInstance().send_GM_Mail(list.get(0).getRoleid(), "帮战退报名费", "报名人数不足9个，无法开战，请查收！", 100000, 0);
				} else {
					listAll.add(list);
				}
			}

			if (listAll.size() < 4) {
				for (int i = 0; i < listAll.size(); i++) {
					MailManager.getInstance().send_GM_Mail(listAll.get(i).get(0).getLeaderId(), "帮战退报名费", "报名数不足4个帮派，无法开战，请查收！", 100000, 0);
				}
				sendGangWorldTalk("今日参加帮战的帮派数量不足，无法开战！");
				gangFightMap.clear();
				start = true;

				return;
			}
			sendGangWorldTalk("请等待5分钟，帮战立即开始");
			List<List<GangFight>> rest = group(listAll);

			if (rest.size() == 2) {
				Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();

				List<GangFight> finalWinner = groupLittle(rest.get(0), rest.get(1), map);
				List<Integer> winnersGangIds = new ArrayList<Integer>();
				winnersGangIds.add(finalWinner.get(0).getGangId());
				everyGangList.add(winnersGangIds);
				fightWordLists.add(map);

				winList.add(finalWinner);
				rest.remove(finalWinner);
				winList.add(rest.get(0));
			}

			start = true;

		}
	}

	/**
	 * 全部帮分成两个两个的帮
	 * 
	 * */
	public static List<List<GangFight>> group(List<List<GangFight>> listAll) {
		Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();

		List<List<GangFight>> winners = new ArrayList<List<GangFight>>();

		// 把轮空的帮移除，同时付给luckyGang
		if (listAll.size() % 2 == 1) {
			Random random = new Random();
			int luckyNum = random.nextInt(listAll.size() - 1);
			winners.add(listAll.get(luckyNum));
			listAll.remove(luckyNum);

		}
		for (int i = 0; i < listAll.size(); i += 2) {
			winners.add(groupLittle(listAll.get(i), listAll.get(i + 1), map));
		}
		List<Integer> winnersGangIds = new ArrayList<Integer>();

		for (int i = 0; i < winners.size(); i++) {
			winnersGangIds.add(winners.get(i).get(0).getGangId());
		}
		everyGangList.add(winnersGangIds);
		fightWordLists.add(map);
		if (winners.size() < 3) {
			return winners;
		}
		return group(winners);

	}

	/** 两个大帮分成三组放入战斗中 */

	public static List<GangFight> groupLittle(List<GangFight> gangOne, List<GangFight> gangTwo, Map<Integer, List<String>> map) {
		List<String> word1 = fight(gangOne.subList(0, 3), gangTwo.subList(0, 3));
		List<String> word2 = fight(gangOne.subList(3, 6), gangTwo.subList(3, 6));
		List<String> word3 = fight(gangOne.subList(6, 9), gangTwo.subList(6, 9));

		List<String> wordAll1 = new ArrayList<String>();
		wordAll1.addAll(word1);
		wordAll1.addAll(word2);
		wordAll1.addAll(word3);

		List<String> wordAll2 = new ArrayList<String>();

		wordAll2.addAll(word1);
		wordAll2.addAll(word2);
		wordAll2.addAll(word3);

		int scoreOne = 0;
		int scoreTwo = 0;
		for (GangFight gangFight : gangOne) {
			// 打完一轮补满血
			gangFight.setHp(gangFight.getNewMaxHp());
			scoreOne = gangFight.getScore() + scoreOne;
		}

		for (GangFight gangFight : gangOne) {
			gangFight.setScore(0);
		}
		for (GangFight gangFight : gangTwo) {
			gangFight.setHp(gangFight.getNewMaxHp());
			scoreTwo = gangFight.getScore() + scoreTwo;
		}
		for (GangFight gangFight : gangTwo) {
			gangFight.setScore(0);
		}
		if (scoreOne > scoreTwo) {
			scoreOne = 0;
			scoreTwo = 0;
			// wordAll已经是本轮所有的话语，下面取得他们的帮派ID，存在MAP中，根据频道ID，再挨个发送
			wordAll1.add("本轮取得最终胜利的帮是  : " + GangService.INSTANCE.getGang(gangOne.get(0).getGangId()).getName());
			wordAll2.add("本轮取得最终胜利的帮是  : " + GangService.INSTANCE.getGang(gangOne.get(0).getGangId()).getName());
			map.put(gangOne.get(0).getGangId(), wordAll1);
			map.put(gangTwo.get(0).getGangId(), wordAll2);

			return gangOne;
		} else {
			scoreOne = 0;
			scoreTwo = 0;
			// wordAll已经是本轮所有的话语，下面取得他们的帮派ID，存在MAP中，根据频道ID，再挨个发送
			wordAll1.add("本轮取得最终胜利的帮是  : " + GangService.INSTANCE.getGang(gangTwo.get(0).getGangId()).getName());
			wordAll2.add("本轮取得最终胜利的帮是  : " + GangService.INSTANCE.getGang(gangTwo.get(0).getGangId()).getName());

			map.put(gangOne.get(0).getGangId(), wordAll1);
			map.put(gangTwo.get(0).getGangId(), wordAll2);
			return gangTwo;
		}
	}

	/**
	 * 
	 * 战斗
	 * 
	 * 这里面是两个小帮
	 * */
	public static List<String> fight(List<GangFight> gang1, List<GangFight> gang2) {
		// 一场战斗的话语
		List<String> msgList = new ArrayList<String>();
		List<GangFight> gangAll = new ArrayList<GangFight>();
		gangAll.addAll(gang1);
		gangAll.addAll(gang2);

		for (int j = 0;; j++) {

			// 对两帮的所有人进行出手顺序安排
			compars(gangAll);

			for (int i = 0; i < gangAll.size(); i++) {
				StringBuffer sb = new StringBuffer();

				GangFight biter = gangAll.get(i);
				int hp = 0;
				if (biter.getHp() > 0) {
					// 被打者
					GangFight bebiter = null;

					if (gang1.contains(biter)) {
						if (random(biter.getIntellect() * 1.0 / 1500)) {
							bebiter = choiceWeak(gang2);
						} else {
							bebiter = choice(gang2);
						}

						if (bebiter == null) {
							System.out.println("帮派  " + GangService.INSTANCE.getGang(biter.getGangId()).getName() + " 取得了小组胜利");
							msgList.add("帮派  " + GangService.INSTANCE.getGang(biter.getGangId()).getName() + " 取得了小组胜利");
							gangAll.get(i).setScore(biter.getScore() + 1);
							return msgList;
						}

						hp = fightCalculate(biter, bebiter, sb);

						bebiter.setHp(bebiter.getHp() - hp);

						System.out.println(sb.toString());
						if (bebiter.getHp() <= 0) {
							sb.append("," + (bebiter.getName() + "挂了"));
							System.out.println(bebiter.getName() + "挂了");
							
						}

					} else {
						if (random(biter.getIntellect() * 1.0 / 1000)) {
							bebiter = choiceWeak(gang1);
						} else {
							bebiter = choice(gang1);
						}
						if (bebiter == null) {
							System.out.println("帮派  " + GangService.INSTANCE.getGang(biter.getGangId()).getName() + " 取得了小组胜利");
							msgList.add("帮派  " + GangService.INSTANCE.getGang(biter.getGangId()).getName() + " 取得了小组胜利");
							gangAll.get(i).setScore(biter.getScore() + 1);
							return msgList;
						}
						hp = fightCalculate(biter, bebiter, sb);

						bebiter.setHp(bebiter.getHp() - hp);

						System.out.println(sb);
						if (bebiter.getHp() <= 0) {
							sb.append("," + (bebiter.getName() + "倒了"));
							System.out.println(bebiter.getName() + "倒了");
							
						}
					}
					msgList.add(sb.toString());
				} else {
					continue;
				}
			}
		}

	}

	/** 战斗计算 */
	public static int fightCalculate(GangFight biter, GangFight bebiter, StringBuffer msg) {
		int hp = 0;

		if (biter.getVocation() == Vocation.SHAQ) {
			int shangHai = biter.getMaxPAtk();
			if (biter.getHit() < bebiter.getEvade()) {
				if (random((bebiter.getEvade() - biter.getHit()) * 1.0 / 10000)) {
					msg.append(biter.getName() + "勇猛的辟下一刀，但被 " + bebiter.getName() + "机敏的躲过了");
					return hp;
				}
			}

			if (random(biter.getCrit() * 1.0 / 10000) || random(biter.getStrength() * 1.0 / 3000)) {
				shangHai = shangHai * 2;
				if (shangHai > bebiter.getpDef()) {
					hp = shangHai - bebiter.getpDef();
				} else {
					hp = 300;
				}
				msg.append(biter.getName() + "青筋绷起，面目狰狞，暴发神威，发出致命攻击，" + bebiter.getName() + "这一刀挨得不轻" + "，丢掉" + hp + "点血");

				return hp;
			}
			if (shangHai > bebiter.getpDef()) {
				hp = shangHai + 532 - bebiter.getpDef();// 增强侠客，攻击加532
			} else {
				hp = 300;
			}
			msg.append(biter.getName() + "一刀劈了 " + bebiter.getName() + hp + "血");
			return hp;
		} else if (biter.getVocation() == Vocation.Assassin) {
			int shangHai = biter.getMaxPAtk();
			if (random((bebiter.getEvade() - biter.getHit()) * 1.0 / 10000)) {
				msg.append(biter.getName() + "引箭向天，失去准头，玩大了， " + bebiter.getName() + "动都没动，纹丝无伤");
				return hp;
			}
			if (random(biter.getCrit() * 1.0 / 10000)) {
				shangHai = shangHai * 2;
				if (shangHai > bebiter.getpDef()) {
					hp = shangHai - bebiter.getpDef();
				} else {
					hp = 300;
				}
				msg.append(biter.getName() + "注入内力，搭弓射箭，箭上带着耀眼的雷电，" + bebiter.getName() + "被射成重伤" + "，丢掉" + hp + "点血");

				return hp;
			}

			if (shangHai > bebiter.getpDef()) {
				hp = shangHai - bebiter.getpDef();//
			} else {
				hp = 300;
			}
			msg.append(biter.getName() + "一箭射了 " + bebiter.getName() + hp + "点血");
			return hp;
		} else {

			int shangHai = biter.getMaxMAtk();
			if (random((bebiter.getEvade() - biter.getHit()) * 1.0 / 10000)) {
				msg.append(biter.getName() + "执扇默念符咒，突然一只蚊虫飞过叮在他（她）脸上，啾的发出的气劲打歪了 ，" + bebiter.getName() + "笑得前仰后合");
				return hp;
			}

			if (random(biter.getCrit() * 1.0 / 10000) || random(biter.getIntellect() * 1.0 / 3000)) {
				shangHai = shangHai * 2;
				if (shangHai > bebiter.getmDef()) {
					hp = shangHai - bebiter.getmDef();
				} else {
					hp = 300;
				}
				msg.append(biter.getName() + "摒心静气，孤注一掷，微风拂过，他（她）飘逸的长发飞舞，突然伸手一指，" + bebiter.getName() + "应声跪地，流血不止" + "，丢掉" + hp + "点血");

				return hp;
			}
			if (shangHai > bebiter.getmDef()) {
				hp = shangHai - bebiter.getmDef();
			} else {
				hp = 300;
			}
			msg.append(biter.getName() + "一指伤了 " + bebiter.getName() + hp + "点血");

			return hp;
		}
	}

	/** 在战斗的LIST中随机的选一个人,三个HP不为空就在三个随机，如果都为空返回NULL */
	public static GangFight choice(List<GangFight> list) {
		List<GangFight> list1 = new ArrayList<GangFight>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getHp() > 0) {
				list1.add(list.get(i));
			}
		}
		int man = 0;
		Random random = new Random();
		if (list1.size() == 3) {
			man = random.nextInt(2);
		} else if (list1.size() == 2) {
			man = random.nextInt(1);
		} else if (list1.size() == 1) {
			man = 0;
		} else {
			return null;
		}

		return (list1.get(man));
	}

	/** 在战斗的LIST中受智力因素影响，触发则选血最少的，如果都为空返回NULL */
	public static GangFight choiceWeak(List<GangFight> list) {
		List<GangFight> list1 = new ArrayList<GangFight>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getHp() > 0) {
				list1.add(list.get(i));
			}
		}

		int man = 0;
		if (list1.size() == 3) {
			if (list1.get(0).getHp() < list1.get(1).getHp() && list1.get(0).getHp() < list1.get(2).getHp()) {
				man = 0;
			} else if (list1.get(1).getHp() < list1.get(0).getHp() && list1.get(1).getHp() < list1.get(2).getHp()) {
				man = 1;
			} else if (list1.get(2).getHp() < list1.get(0).getHp() && list1.get(2).getHp() < list1.get(1).getHp()) {
				man = 2;
			} else {
				man = 0;
			}

		} else if (list1.size() == 2) {
			man = list1.get(0).getHp() > list1.get(1).getHp() ? 0 : 1;
		} else if (list1.size() == 1) {
			man = 0;
		} else {
			return null;
		}

		return (list1.get(man));
	}

	/** 根据攻速属性排序 */
	@SuppressWarnings("unchecked")
	public static void compars(List<GangFight> list) {
		CompareListGangFight sortList = new CompareListGangFight();
		Collections.sort(list, sortList);
	}

	/** 发放奖励 */
	public static void send() {
		if (winList != null && winList.size() > 1) {
			int num = 0;
			try {
				Collection<List<GangFight>> lists = gangFightMap.values();
				Iterator<List<GangFight>> it = lists.iterator();
				while (it.hasNext()) {
					List<GangFight> list = it.next();

					if (list.size() >= 9) {
						num++;
						for (int i = 0; i < list.size(); i++) {
							try {
								MailManager.getInstance().send_GM_Mail(list.get(i).getRoleid(), "帮派战争参与奖励", "恭喜你！", 0, 31124);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {

				Gang gang1 = GangService.INSTANCE.getGang(winList.get(0).get(0).getGangId());
				for (int i = 0; i < winList.get(0).size(); i++) {
					MailManager.getInstance().send_GM_Mail(winList.get(0).get(i).getRoleid(), "帮派战争冠军奖励", "恭喜你，你们帮派在此次战斗中取得了最终的胜利，请向帮主领取赏金！", 0, 8036);
					gang1.updateOffLineTribute(winList.get(0).get(i).getRoleid(), 5);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {

				Gang gang2 = GangService.INSTANCE.getGang(winList.get(1).get(0).getGangId());

				for (int i = 0; i < winList.get(1).size(); i++) {
					MailManager.getInstance().send_GM_Mail(winList.get(1).get(i).getRoleid(), "帮派战争亚军奖励", "恭喜你，你们帮派在此次战斗中取得第二名的好成绩，请向帮主领取赏金 ！", 0, 8035);
					gang2.updateOffLineTribute(winList.get(1).get(i).getRoleid(), 3);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {

				MailManager.getInstance().send_GM_Mail(winList.get(0).get(0).getLeaderId(), "最强帮主", "恭喜你！", (int) (num * 80000 * 0.6), 0);
				MailManager.getInstance().send_GM_Mail(winList.get(1).get(0).getLeaderId(), "第二强帮主", "恭喜你！", (int) (num * 80000 * 0.4), 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			winList.clear();
		}
	}

	public static void beforeTalk() {
		try {

			if (gangFightMap == null | gangFightMap.size() < 4) {
				start = true;
				return;
			}

			List<String> listGangNames = new ArrayList<String>();
			Collection<List<GangFight>> lists = gangFightMap.values();
			Iterator<List<GangFight>> it = lists.iterator();
			while (it.hasNext()) {
				List<GangFight> list = it.next();
				listGangNames.add(GangService.INSTANCE.getGang(list.get(0).getGangId()).getName());
			}
			if (listGangNames != null && listGangNames.size() >= 4) {
				int num = 0;
				if (listGangNames.size() % 4 == 0) {
					num = listGangNames.size() / 4;

					for (int i = 0; i < num; i++) {
						StringBuffer str = new StringBuffer();
						str.append("参加帮战的帮派有　：");

						for (int j = 0; j < 4; j++) {

							str.append(listGangNames.get(i * 4 + j) + " ");
						}
						sendGangWorldTalk(str.toString());
					}

				} else {
					num = (listGangNames.size() / 4) + 1;

					for (int i = 0; i < num; i++) {
						StringBuffer str = new StringBuffer();
						str.append("参加帮战的帮派有　：");

						if (i == num - 1) {
							for (int j = 0; j < listGangNames.size() % 4; j++) {

								str.append(listGangNames.get((listGangNames.size() / 4) * 4 + j) + " ");
							}
							sendGangWorldTalk(str.toString());
						} else {

							for (int j = 0; j < 4; j++) {

								str.append(listGangNames.get(i * 4 + j) + " ");
							}
							sendGangWorldTalk(str.toString());
						}
					}
				}
				sendGangWorldTalk("帮战正式开始！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 播报战斗 */
	public static void talk() {
		System.out.println("进来喊话");
		if (fightWordLists.size() <= 0) {
			wordEnd = true;
			send();
			gangFightMap.clear();
			return;
		}

		for (int j = 0; j < fightWordLists.size(); j++) {

			if (fightWordLists.get(j).isEmpty()) {
				fightWordLists.remove(j);
				round = round + 1;
				ServerMessage.sendBulletin("第" + round + "轮帮战结束");
				String winGangsStr = "";
				if (everyGangList.get(0).size() > 1) {
					for (int i = 0; i < everyGangList.get(0).size(); i++) {
						winGangsStr = GangService.INSTANCE.getGang(everyGangList.get(0).get(i)).getName() + "  " + winGangsStr;
					}
					sendGangWorldTalk("晋级下一轮的帮派有 ： " + winGangsStr);
					everyGangList.remove(0);
				} else {

					sendGangWorldTalk("帮派战争最终的冠军帮派为   ：   " + GangService.INSTANCE.getGang(everyGangList.get(0).get(0)).getName() + " ，他们已经近乎无敌了，谁还敢来挑战？？？");
				}
			}

			if (fightWordLists.size() > 0) {
				Set<Integer> set = fightWordLists.get(j).keySet();
				for (Iterator<Integer> it = set.iterator(); it.hasNext();) {
					int key = it.next();

					if (fightWordLists.size() == 1 && fightWordLists.get(j).size() == 2) {
						fightWordLists.get(j).remove(key);
						return;
					}

					List<String> list2 = fightWordLists.get(j).get(key);
					if (list2.size() <= 0) {
						fightWordLists.get(j).remove(key);
						return;
					}
					if (fightWordLists.size() == 1 && fightWordLists.get(j).size() == 1) {
						sendGangWorldTalk(list2.get(0));
					} else {
						try {
							sendGangTalk(key, list2.get(0));
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("帮派" + key + "已解散");
						}
					}
					System.out.println("key" + key + " 话 ： " + list2.get(0));
					list2.remove(0);
				}
			}
			return;
		}
	}

	public static boolean ifHasGang(RoleBean role) {
		if (role.getGangid() == 0) {
			return false;
		}
		return true;
	}

	public static boolean ifIsInTheGang(RoleBean role, int leaderId) {

		Gang gang = GangService.INSTANCE.getGang(role.getGangid());
		if (gang.getLeader() == leaderId) {
			return true;
		}
		return false;
	}

	public static boolean ifGangFightEnough(int leaderId) {
		if (gangFightMap.get(leaderId).size() >= 9) {
			return true;
		}
		return false;
	}

	/** 根据角色ID 取任何一个人的角色名字 */
	public static String getOffLineName(int roleid) {
		if (!RoleCardService.INSTANCE.hasCard(roleid)) {
			return "";
		} else {
			RoleCard card = RoleCardService.INSTANCE.getCard(roleid);
			return card.getName();
		}
	}

	/** 根据角色ID 取帮主帮派 */
	public static String getGangFightLeaderName(int roleid) {
		return getOffLineName(roleid);

	}

	/**
	 * 取得帮派等级
	 * 
	 * */
	public static int getGangLevel(RoleBean role) {
		return GangService.INSTANCE.getGang(role.getGangid()).getLevel();
	}

	/**
	 * 取得帮派帮主
	 * 
	 * */
	public static int getGangLeader(RoleBean role) {
		return GangService.INSTANCE.getGang(role.getGangid()).getLeader();
	}

	/**
	 * 是否报过名
	 * 
	 * */
	public static boolean getIfHas(RoleBean role) {
		Collection<List<GangFight>> lists = gangFightMap.values();
		Iterator<List<GangFight>> it = lists.iterator();
		while (it.hasNext()) {
			List<GangFight> list = it.next();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getRoleid() == role.getRoleid()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * 发送帮派公告，报战果
	 * 
	 * */
	public static void sendGangTalk(int gangId, String contentStr) {
		ServerMessage.sendGangTalk(gangId, contentStr);
	}

	/**
	 * 
	 * 发送最后一场帮派公告
	 * 
	 * */
	public static void sendGangWorldTalk(String contentStr) {

		ServerMessage.sendGangWorldTalk(contentStr);
	}

	/**
	 * 
	 * 随机概率 true是发生了，false没发生
	 * 
	 * */
	public static boolean random(double gailv) {
		Random rd = new Random();
		if (gailv * 100000 > rd.nextInt(1000000)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * 停服退钱不开
	 * 
	 * */
	public static void shutdown() {
		if (gangFightMap == null | gangFightMap.size() < 1) {
			start = true;
			return;
		}

		for (int leaderid : gangFightMap.keySet()) {
			MailManager.getInstance().send_GM_Mail(leaderid, "帮战退报名费", "今日停服，无法开战，请查收！", 100000, 0);
		}
		start = true;
		wordEnd = true;
	}

}