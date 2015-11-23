package com.joyveb.tlol.bigOrSmall;

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
 * 赌场压大小 2011-1-17 LiJG（暂时无用）
 */
public final class BigOrSmallManager implements MinTickHandler {

	/** 单例类的常量 */
	private static BigOrSmallManager INSTANCE = new BigOrSmallManager();
	/** 每局时间 */
	private static final int TIME_PEROUND = 10;
	/** 系统当前的分钟数 */
	private int nowMinute = 0;
	/** 本局剩余时间 */
	private int restRoundTime;
	/** 上局开奖结果 */
	private String lastRoundResult;
	/** 押注的人员、金额、点数 */
	private TreeSet<BigOrSmallRole> betRole = new TreeSet<BigOrSmallRole>();
	/** 随机函数 */
	private Random random = new Random();
	/** 排序时使用 */
	private long betIndex;

	/**
	 * 构造方法
	 */
	private BigOrSmallManager() {

	}

	/**
	 * 获取单例类的常量
	 * 
	 * @return INSTANCE
	 */
	public static BigOrSmallManager getInstance() {
		return INSTANCE;
	}

	/**
	 * 将押注人员添加到vector
	 * 
	 * @param roleId
	 *            人物ID
	 * @param gold
	 *            押注金额
	 * @param betJoabSize
	 *            押注的项目（如，大小）
	 */
	public void addBetRole(final int roleId, final int gold,
			final int betSize) {
		this.betIndex++;
		BigOrSmallRole bigOrSmallRole = new BigOrSmallRole(betIndex, roleId,
				gold, betSize);
		betRole.add(bigOrSmallRole);
	}

	/**
	 * 开奖结果
	 */
	public void issuingAwards() {

		/** 随机结果 */
		int result = random.nextInt(2);
		String resultStr = (result == 0 ? "小" : "大");
		Log.info(Log.STDOUT, "本期押大小开奖结果为 ：" + resultStr);
		this.lastRoundResult = resultStr;
		this.betResult(result); // 根据结果发邮件

	}

	/**
	 * 给押大小的人发送邮件
	 * 
	 * @param lastRoundResult
	 *            开奖结果的枚举对象
	 */
	public void betResult(final int lastRoundResult) {

		Iterator<BigOrSmallRole> iterator = this.betRole.iterator();
		TreeSet<BigOrSmallRole> treeSet = new TreeSet<BigOrSmallRole>();
		String resultStrWin = (lastRoundResult == 0 ? "小" : "大");
		String resultStrLose = (lastRoundResult == 0 ? "大" : "小");
		while (iterator.hasNext()) {
			BigOrSmallRole bigOrSmallRole = iterator.next();
			if (lastRoundResult == bigOrSmallRole.getSize()) {
				treeSet.add(bigOrSmallRole);
				MailManager.getInstance().sendSysMail(
						bigOrSmallRole.getRoleId(), "押大小结果（赢）",
						"尊敬的玩家，本局押大小结果为：" + resultStrWin + "，您押注为：" + resultStrWin + "，恭喜您获得本局押大小的奖金，请注意查收您邮件中的金币。",
								bigOrSmallRole.getGold() * 2, null);
				try {
					Log.info(
							Log.BETBIGORSMALL,
							bigOrSmallRole.getRoleId()
									+ "#$36#$"
									+ new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss")
											.format(new Date()) + "#$"
									+ TianLongServer.srvId + "#$"
									+ bigOrSmallRole.getGold() + "#$" + 1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				MailManager.getInstance().sendSysMail(
						bigOrSmallRole.getRoleId(), "押大小结果（输）",
						"尊敬的玩家，本局押大小结果为：" + resultStrWin + "，您押注为：" + resultStrLose + "，很可惜您输掉了本局押大小的奖金。希望下一次好运降临到您的身上。", 0, null);
				try {
					Log.info(
							Log.BETBIGORSMALL,
							bigOrSmallRole.getRoleId()
									+ "#$36#$"
									+ new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss")
											.format(new Date()) + "#$"
									+ TianLongServer.srvId + "#$"
									+ bigOrSmallRole.getGold() + "#$" + 0);
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
	public void sendNotice(TreeSet<BigOrSmallRole> treeSet) {

		Iterator<BigOrSmallRole> iterator = treeSet.iterator(); // 中奖的Vector

		int i = 1;
		String str = "";// 公告内容
		while (iterator.hasNext() && i <= 3) {
			BigOrSmallRole bigOrSmallRole = iterator.next();
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
			str = str + "在本期押大小游戏中获得前三甲！可喜可贺！";
			Broadcast.send(str);
		}
		clearBetedPlayer();
	}

	/**
	 * 存储每个人押注的金额及ID(内部类) 2011-1-17 LiJG
	 */
	private class BigOrSmallRole implements Comparable<BigOrSmallRole> {

		/** 人物ID */
		private int roleId;
		/** 押注的金额 */
		private int gold;
		/** 押注大小（0为小，1为大） */
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
		public BigOrSmallRole(final long index, final int roleId,
				final int gold, final int size) {
			this.index = index;
			this.roleId = roleId;
			this.gold = gold;
			this.size = size;
		}

		@Override
		public int compareTo(BigOrSmallRole o) {
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
			if (obj == null || !(obj instanceof BigOrSmallRole))
				return false;

			return this.compareTo((BigOrSmallRole) obj) == 0;
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
