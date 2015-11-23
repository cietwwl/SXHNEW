package com.joyveb.tlol.guess;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.mail.MailManager;
import com.joyveb.tlol.schedule.Broadcast;
import com.joyveb.tlol.schedule.MinTickHandler;
import com.joyveb.tlol.util.Log;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCardService;

/**
 * @猜猜看
 * @author SunHL
 * @下午07:22:22
 */
public final class GuessManager implements MinTickHandler {

	/** 单例类的常量 */
	private static GuessManager INSTANCE = new GuessManager();
	/** 每局时间 */
	private static final int TIME_PEROUND = 5;
	/** 系统当前的分钟数 */
	private int nowMinute = 0;
	/** 本局剩余时间 */
	private int restRoundTime;
	/** 上局开奖结果 */
	private String lastRoundResult;
	/** 押注的人员、金额、点数 */
	private TreeSet<GuessRole> betRole = new TreeSet<GuessRole>();
	/** 随机函数 */
	private Random random = new Random();
	/** 排序时使用 */
	private long betIndex;

	/**
	 * 构造方法
	 */
	private GuessManager() {

	}

	/**
	 * 获取单例类的常量
	 * 
	 * @return INSTANCE
	 */
	public static GuessManager getInstance() {
		return INSTANCE;
	}

	/**
	 * 将押注人员添加到vector
	 * 
	 * @param roleId
	 *            人物ID
	 * @param gold
	 *            押注金额
	 * @param betSize
	 *            押注的项目
	 */
	public void addBetRole(final int roleId, final int gold, final int betSize) {
		this.betIndex++;
		GuessRole guessRole = new GuessRole(betIndex, roleId, gold, betSize);
		betRole.add(guessRole);
	}

	/**
	 * 开奖结果
	 */
	public void issuingAwards() {

		/** 随机结果 */
		int result1 = random.nextInt(6) + 1;
		int result2 = random.nextInt(6) + 1;
		int result3 = random.nextInt(6) + 1;
		int result = result1 + result2 + result3;
		String lastResult = "";
		String resultStr = "";
		if ((result1 == 1 && result2 == 1 && result3 == 1)
				|| (result1 == 2 && result2 == 2 && result3 == 2)
				|| (result1 == 3 && result2 == 3 && result3 == 3)
				|| (result1 == 4 && result2 == 4 && result3 == 4)
				|| (result1 == 5 && result2 == 5 && result3 == 5)
				|| (result1 == 6 && result2 == 6 && result3 == 6)) {
			lastResult = "5";
			resultStr = "豹子";
			this.lastRoundResult = result1 + "，" + result2 + "，" + result3
					+ "，" + resultStr;
			Log.info(Log.STDOUT, "本期猜猜看开奖结果为 ：" + this.lastRoundResult);
		} else {
			lastResult = lastResult + (result > 10 ? "1" : "2");
			lastResult = lastResult + (result % 2 == 0 ? "4" : "3");

			resultStr = resultStr + (result > 10 ? "大" : "小");
			resultStr = resultStr + "和" + (result % 2 == 0 ? "双" : "单");
			this.lastRoundResult = result1 + "，" + result2 + "，" + result3
					+ "，" + resultStr;
			Log.info(Log.STDOUT, "本期猜猜看开奖结果为 ：" + this.lastRoundResult);
		}
		this.betResult(lastResult); // 根据结果发邮件

	}

	/**
	 * 给猜猜看的人发送邮件
	 * 
	 * @param lastRoundResult
	 * 
	 */
	public void betResult(final String lastResult) {

		Iterator<GuessRole> iterator = this.betRole.iterator();
		TreeSet<GuessRole> treeSet = new TreeSet<GuessRole>();
		while (iterator.hasNext()) {
			GuessRole guessRole = iterator.next();
			if (lastResult.contains(String.valueOf(guessRole.getSize()))) {
				treeSet.add(guessRole);
				if (lastResult.equals("5")) {
					MailManager.getInstance().sendSysMail(
							guessRole.getRoleId(),
							"猜猜看结果（赢）",
							"尊敬的玩家，本期猜猜看开奖结果为：" + this.lastRoundResult
									+ "，您押注为：豹子，恭喜您获得本局猜猜看的奖金，请注意查收您邮件中的金币。",
							guessRole.getGold() * 18, null);
				} else {
					MailManager
							.getInstance()
							.sendSysMail(
									guessRole.getRoleId(),
									"猜猜看结果（赢）",
									"尊敬的玩家，本期猜猜看开奖结果为："
											+ this.lastRoundResult
											+ "，您押注为："
											+ (guessRole.size == 1 ? "大"
													: (guessRole.size == 2 ? "小"
															: (guessRole.size == 3 ? "单"
																	: "双")))
											+ "，恭喜您获得本局猜猜看的奖金，请注意查收您邮件中的金币。",
									guessRole.getGold() * 2, null);
					;
				}
				try {
					Log.info(
							Log.GUESS,
							guessRole.getRoleId()
									+ "#$37#$"
									+ new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss")
											.format(new Date()) + "#$"
									+ TianLongServer.srvId + "#$"
									+ guessRole.getGold() + "#$" + 1 + "#$"
									+ guessRole.getSize() + "#$" + lastResult);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				MailManager
						.getInstance()
						.sendSysMail(
								guessRole.getRoleId(),
								"猜猜看结果（输）",
								"尊敬的玩家，本期猜猜看开奖结果为："
										+ this.lastRoundResult
										+ "，您押注为："
										+ (guessRole.size == 1 ? "大"
												: (guessRole.size == 2 ? "小"
														: (guessRole.size == 3 ? "单"
																: (guessRole.size == 4 ? "双"
																		: "豹子"))))
										+ "，很可惜您输掉了本局猜猜看的奖金。希望下一次好运降临到您的身上。",
								0, null);
				try {
					Log.info(
							Log.GUESS,
							guessRole.getRoleId()
									+ "#$37#$"
									+ new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss")
											.format(new Date()) + "#$"
									+ TianLongServer.srvId + "#$"
									+ guessRole.getGold() + "#$" + 0 + "#$"
									+ guessRole.getSize() + "#$" + lastResult);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// 发送系统公告
		sendNotice(treeSet);
	}

	/**
	 * 发送公告
	 * 
	 * @param lastRoundResult
	 *            开奖结果
	 */
	public void sendNotice(TreeSet<GuessRole> treeSet) {

		Iterator<GuessRole> iterator = treeSet.iterator(); // 中奖的Vector

		int i = 1;
		String str = "";// 公告内容
		while (iterator.hasNext() && i <= 3) {
			GuessRole bigOrSmallRole = iterator.next();
			if (bigOrSmallRole != null) {
				if (RoleCardService.INSTANCE
						.getCard(bigOrSmallRole.getRoleId()) != null) {
					str = str
							+ RoleCardService.INSTANCE.getCard(
									bigOrSmallRole.getRoleId()).getName() + "，";
				}
			}
			i++;
		}
		if (str != null && !str.equals("")) {
			str = str + "在本期猜猜看游戏中获得前三甲！可喜可贺！";
			Broadcast.send(str);
		}
		clearBetedPlayer();
	}

	/**
	 * 存储每个人ID、押注的金额以及押注(内部类)
	 */
	private class GuessRole implements Comparable<GuessRole> {

		/** 人物ID */
		private int roleId;
		/** 押注的金额 */
		private int gold;
		/** 押注（1为大，2为小，3为单，4为双，5为豹子） */
		private int size;

		private long index;

		public int getRoleId() {
			return roleId;
		}

		public int getGold() {
			return gold;
		}

		public int getSize() {
			return size;
		}

		public long getIndex() {
			return index;
		}

		/**
		 * 构造方法
		 * 
		 * @param index
		 *            ID
		 * @param roleId
		 *            人物ID
		 * @param gold
		 *            押注金额
		 */
		public GuessRole(final long index, final int roleId, final int gold,
				final int size) {
			this.index = index;
			this.roleId = roleId;
			this.gold = gold;
			this.size = size;
		}

		@Override
		public int compareTo(GuessRole o) {
			if (this.gold > o.getGold()) {
				return -1;
			} else if (this.gold == o.getGold()) {
				return this.index > o.getIndex() ? -1 : (this.index == o
						.getIndex()) ? 0 : 1;
			} else {
				return 1;
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof GuessRole))
				return false;

			return this.compareTo((GuessRole) obj) == 0;
		}

	}

	@Override
	public void minTick(final int curMin) {
		if (curMin >= nowMinute) {
			this.restRoundTime = TIME_PEROUND;
			this.nowMinute = curMin + TIME_PEROUND;
			this.issuingAwards();
		} else {
			restRoundTime--;

		}
	}

	/**
	 * 开奖后 清空vector
	 */
	private void clearBetedPlayer() {
		betRole.clear();
	}

	/**
	 * 本局剩余时间
	 * 
	 * @return String 本局剩余时间
	 */
	public String getRemainTime() {
		return restRoundTime + "分钟";

	}

	/**
	 * 上局中奖结果
	 * 
	 * @return String 上局中奖结果
	 */
	public String getLastRoundResult() {
		return lastRoundResult.toString();
	}

	/**
	 * 可押注金额
	 * 
	 * @param role
	 *            角色对象
	 * @return String 可押注的金额（单位：银）
	 */
	public String betMoney(final RoleBean role) {
		int roleGold = role.getGold() / 100;
		return roleGold + "银";
	}

	/**
	 * 关闭服务器时的判断
	 */
	public void shutdown() {
		this.issuingAwards();
	}

}
