package com.joyveb.tlol.betAccount;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.mail.MailManager;
import com.joyveb.tlol.schedule.Broadcast;
import com.joyveb.tlol.schedule.MinTickHandler;
import com.joyveb.tlol.util.Log;
import com.joyveb.tlol.pay.connect.ConnectCommonParser;
import com.joyveb.tlol.pay.domain.GameAfford;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCardService;

/**
 * 元宝赌数
 * 
 * @author SunHL
 * @下午04:05:42
 */

public final class BetAccountManager implements MinTickHandler {

	/** 单例类的常量 */
	private static BetAccountManager INSTANCE = new BetAccountManager();
	/** 每局时间 */
	private int time_peround = 5;
	/** 系统当前的分钟数 */
	private int nowMinute = 0;
	/** 本局剩余时间 */
	private int restRoundTime;
	/** 上局开奖结果 */
	private int lastRoundResult;
	/** 押注的人员、金额、点数 */
	private TreeSet<BetAccountRole> betRole = new TreeSet<BetAccountRole>();
	/** 随机函数 */
	private Random random = new Random();
	/** 排序时使用 */
	private int betIndex;
	/** 比率 */
	List<Integer> list = new ArrayList<Integer>();

	public List<Integer> getList() {
		return list;
	}

	public void setList(List<Integer> list) {
		this.list = list;
	}

	/**
	 * 构造方法
	 */
	private BetAccountManager() {
		list.add(150);
		list.add(50);
		list.add(25);
		list.add(15);
		list.add(10);
		list.add(7);
		list.add(6);
		list.add(5);
		list.add(5);
		list.add(6);
		list.add(7);
		list.add(10);
		list.add(15);
		list.add(25);
		list.add(50);
		list.add(150);
	}

	/**
	 * 获取单例类的常量 
	 * @return INSTANCE
	 */
	public static BetAccountManager getInstance() {
		return INSTANCE;
	}

	/**
	 * 将押注人员添加到vector
	 * 
	 * @param roleId
	 *            人物ID
	 * @param account
	 *            押注元宝数量
	 * @param betSize
	 *            押注点数（3至18）
	 */
	public void addBetRole(final RoleBean roleBean, final int account,
			final int betSize) {
		this.betIndex++;
		BetAccountRole betAccountRole = new BetAccountRole(betIndex, roleBean,
				roleBean.getRoleid(), roleBean.getUserid(),
				roleBean.getJoyid(), account, betSize);
		betRole.add(betAccountRole);
	}

	/**
	 * 开奖结果
	 */
	public void issuingAwards() {

		/** 随机结果 */
		int result1 = random.nextInt(6);
		int result2 = random.nextInt(6);
		int result3 = random.nextInt(6);
		int result = result1 + result2 + result3 + 3;
		Log.info(Log.STDOUT, "本期元宝赌数开奖结果为： " + result);
		this.lastRoundResult = result;
		this.betResult(lastRoundResult); // 根据结果发送邮件

	}

	/**
	 * 根据结果发送邮件
	 * 
	 * @param lastRoundResult
	 *            开奖结果
	 */
	public void betResult(final int lastRoundResult) {

		Iterator<BetAccountRole> iterator = betRole.iterator();
		TreeSet<BetAccountRole> treeSet = new TreeSet<BetAccountRole>();
		while (iterator.hasNext()) {
			BetAccountRole betAccountRole = iterator.next();
			
			if (betAccountRole.getBetSize() == lastRoundResult) {// 玩家赢
				treeSet.add(betAccountRole);
				int returnAccount = betAccountRole.getAccount()
						* list.get(betAccountRole.getBetSize() - 3);// 返还玩家的元宝数量
				
				GameAfford gameAfford = new GameAfford(betAccountRole.getRoleBean(),
						betAccountRole.getUserId(), betAccountRole.getJoyId(),
						returnAccount, betAccountRole.getRoleBean().getMoney(),
						7);
				Boolean boolean1 = ConnectCommonParser.getInstance().postAffordTask(betAccountRole
						.getRoleBean().getYuanBaoOp(), gameAfford);
				MailManager.getInstance().sendSysMail(
						betAccountRole.getRoleId(),
						"元宝赌数结果（赢）",
						"本次股子得总和为" + lastRoundResult + "，您选择的为"
								+ betAccountRole.getBetSize()
								+ "，系统已将您获得的元宝返还到了商城中，请注意查收。", 0, null);
				try {
					Log.info(
							Log.BETACCOUNT,
							betAccountRole.getUserId()
									+ "#$"
									+ betAccountRole.getRoleId()
									+ "#$47#$"
									+ new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss")
											.format(new Date()) + "#$"
									+ TianLongServer.srvId + "#$"
									+ betAccountRole.getBetSize() + "#$"
									+ betAccountRole.getAccount() + "#$"
									+ betAccountRole.getAccount()
									* list.get(betAccountRole.getBetSize() - 3)
									+ "#$" + 1 + "#$" + (boolean1 ? 1 : 0));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {// 玩家输
				MailManager.getInstance().sendSysMail(
						betAccountRole.roleId,
						"元宝赌数结果（输）",
						"本次股子的总和为" + lastRoundResult + "，您选择的为"
								+ betAccountRole.getBetSize() + "，没有中奖，请继续努力。",
						0, null);
				try {
					Log.info(
							Log.BETACCOUNT,
							betAccountRole.getUserId()
									+ "#$"
									+ betAccountRole.getRoleId()
									+ "#$47#$"
									+ new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss")
											.format(new Date()) + "#$"
									+ TianLongServer.srvId + "#$"
									+ betAccountRole.getBetSize() + "#$"
									+ betAccountRole.getAccount() + "#$"
									+ betAccountRole.getAccount() * 0 + "#$"
									+ 0 + "#$0");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		sendNotice(treeSet);// 发送系统公告
	}

	/**
	 * 发送公告
	 * 
	 * @param lastRoundResult
	 *            开奖结果
	 * @param treeSet
	 *            中奖名单
	 */
	public void sendNotice(TreeSet<BetAccountRole> treeSet) {
		Iterator<BetAccountRole> iterator = treeSet.iterator(); // 中奖的Vector

		// 公告
		int i = 1;
		String str = "";// 公告内容
		while (iterator.hasNext() && i <= 3) {
			BetAccountRole betAccountRole = iterator.next();
			if (betAccountRole != null) {
				if (RoleCardService.INSTANCE
						.getCard(betAccountRole.getRoleId()) != null) {
					str = str
							+ RoleCardService.INSTANCE.getCard(
									betAccountRole.getRoleId()).getName() + "，";
				}
			}
			i++;
		}
		if (str != null && !str.equals("")) {
			str = str + "在本期元宝赌数游戏中获得前三甲！可喜可贺！";
			Broadcast.send(str);
		}
		clearBetedPlayer();
	}

	/**
	 * 存储每个人押注的元宝数量及ID(内部类)
	 * 
	 * @author SunHL
	 * @下午02:59:09
	 */
	private class BetAccountRole implements Comparable<BetAccountRole> {

		private RoleBean roleBean;
		/** 人物ID */
		private int roleId;
		/** 用户ID */
		private int userId;
		/** joyID */
		private String joyId;
		/** 押注的元宝数量 */
		private int account;
		/** 押注的点数 */
		private int betSize;

		private int index;

		public RoleBean getRoleBean() {
			return roleBean;
		}

		/** 获得人物ID */
		public int getRoleId() {
			return roleId;
		}

		/** 获得用户ID */
		public int getUserId() {
			return userId;
		}

		/** 获得joyID */
		public String getJoyId() {
			return joyId;
		}

		/** 获得押注元宝数量 */
		public int getAccount() {
			return account;
		}

		/** 获得押注点数 */
		public int getBetSize() {
			return betSize;
		}

		public int getIndex() {
			return index;
		}

		/**
		 * 构造方法
		 * 
		 * @param index
		 *            ID
		 * @param roleId
		 *            人物ID
		 * @param account
		 *            押注金额
		 * @param betSize
		 *            押注点数
		 */
		public BetAccountRole(final int index, final RoleBean roleBean,
				final int roleId, final int userId, final String joyId,
				final int account, final int betSize) {
			this.index = index;
			this.roleBean = roleBean;
			this.roleId = roleId;
			this.userId = userId;
			this.account = account;
			this.betSize = betSize;
			this.joyId = joyId;
		}

		@Override
		public int compareTo(BetAccountRole o) {
			if (this.account > o.getAccount()) {
				return -1;
			} else if (this.account == o.getAccount()) {
				return this.index > o.getIndex() ? -1 : (this.index == o
						.getIndex()) ? 0 : 1;
			} else {
				return 1;
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof BetAccountRole))
				return false;

			return this.compareTo((BetAccountRole) obj) == 0;
		}

	}

	@Override
	public void minTick(final int curMin) {
		if (curMin >= nowMinute) {
			this.restRoundTime = this.time_peround;
			this.nowMinute = curMin + this.time_peround;
			this.issuingAwards();
		} else {
			restRoundTime--;
		}
		this.time_peround = 10;
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
	 * @return int 上局中奖结果
	 */
	public int getLastRoundResult() {
		return lastRoundResult;
	}

	/**
	 * 根据押注点数获取比率
	 * 
	 * @param key
	 * @return
	 */
	public int getOddsByBet(int key) {

		return (int) list.get(key - 1);
	}

	/**
	 * 关闭服务器时的判断
	 */
	public void shutdown() {
		this.issuingAwards();
	}

}
