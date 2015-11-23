package com.joyveb.tlol.racing;

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
import com.joyveb.tlol.task.TaskState;
import com.joyveb.tlol.util.Log;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCardService;

/**
 * 赛马（暂时无用）
 * 
 * @author SunHL
 * @下午04:05:42
 */
public final class RacingManager implements MinTickHandler {

	/** 单例类的常量 */
	private static RacingManager instance = new RacingManager();
	/** 每局时间 */
	private static final int TIME_PEROUND = 10;
	/** 系统当前的分钟数 */
	private int nowMinute = 0;
	/** 本局剩余时间 */
	private int restRoundTime;
	/** 上局开奖结果 */
	private JoabSize lastRoundResult;
	/** 押大的总金额 */
	private double bigTotalMoney;
	/** 押小的总金额 */
	private double smallTotalMoney;
	/** 押大的人员及金额 */
	private TreeSet<BigOrSmallRoleId> betBigRole = new TreeSet<BigOrSmallRoleId>();
	/** 押小的人员及金额 */
	private TreeSet<BigOrSmallRoleId> betSmallRole = new TreeSet<BigOrSmallRoleId>();
	/** 随机函数 */
	private Random random = new Random();
	/** 排序时使用 */
	private int betIndex;
	/** 奖品代码 */
	private List<Integer> prizeCode = new ArrayList<Integer>();

	public List<Integer> getPrizeCode() {
		return prizeCode;
	}

	public void setPrizeCode(List<Integer> prizeCode) {
		this.prizeCode = prizeCode;
	}

	/** 奖品积分 */
	private List<Integer> prizeMark = new ArrayList<Integer>();

	public List<Integer> getPrizeMark() {
		return prizeMark;
	}

	public void setPrizeMark(List<Integer> prizeMark) {
		this.prizeMark = prizeMark;
	}

	/**
	 * 构造方法
	 */
	private RacingManager() {
		prizeCode.add(10134);
		prizeCode.add(10121);
		prizeCode.add(10100);
		prizeCode.add(10123);
		prizeCode.add(10154);
		prizeCode.add(10144);
		prizeCode.add(10134);
		prizeCode.add(10110);
		prizeMark.add(500);
		prizeMark.add(600);
		prizeMark.add(700);
		prizeMark.add(800);
		prizeMark.add(900);
		prizeMark.add(1000);
		prizeMark.add(1100);
		prizeMark.add(2500);
	}

	/**
	 * @return instance
	 */
	public static RacingManager getInstance() {
		return instance;
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
			final int betJoabSize) {
		this.betIndex++;
		BigOrSmallRoleId bigOrSmallRoleId = new BigOrSmallRoleId(betIndex,
				roleId, gold);
		if (betJoabSize == JoabSize.Big.getBetId()) {
			betBigRole.add(bigOrSmallRoleId);
			this.bigTotalMoney += gold;
		} else {
			betSmallRole.add(bigOrSmallRoleId);
			this.smallTotalMoney += gold;
		}
	}

	/**
	 * 开奖结果
	 */
	public void issuingAwards() {

		/** 随机结果 */
		int result = random.nextInt(2);

		Log.info(Log.STDOUT, "开始开奖：结果为 " + (result == 0 ? "小" : "大"));

		if (result == 0) {
			// 小中奖了
			this.lastRoundResult = JoabSize.Small;
			this.betResult(lastRoundResult);
		} else {
			// 大中奖了
			this.lastRoundResult = JoabSize.Big;
			this.betResult(lastRoundResult);
		}
	}

	/**
	 * 给押大小的人发送邮件
	 * 
	 * @param lastRoundResult
	 *            开奖结果的枚举对象
	 */
	public void betResult(final JoabSize lastRoundResult) {
		Iterator<BigOrSmallRoleId> buZhongJiang = null; // 不中奖的Vector
		Iterator<BigOrSmallRoleId> zhongJiang = null; // 中奖的Vector
		if (lastRoundResult.getBetId() == 0) {
			zhongJiang = betSmallRole.iterator();
			buZhongJiang = betBigRole.iterator();
		} else {
			buZhongJiang = betSmallRole.iterator();
			zhongJiang = betBigRole.iterator();
		}
		// 给中奖的人发送邮件
		while (zhongJiang.hasNext()) {
			BigOrSmallRoleId roleInfo = zhongJiang.next();
			MailManager.getInstance().sendSysMail(roleInfo.roleId, "押大小结果",
					"尊敬的玩家，恭喜您获得本局押大小的奖金，请注意查收您邮件中的金币。", roleInfo.gold * 2,
					null);
			if (roleInfo.gold == 0) {
				try {
					Log.info(
							Log.ITEM,
							0
									+ "#$"
									+ roleInfo.roleId
									+ "#$35#$"
									+ ""
									+ "#$"
									+ 0
									+ "#$"
									+ new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss")
											.format(new Date()) + "#$"
									+ TianLongServer.srvId + "#$"
									+ roleInfo.gold + "#$" + 0 + "#$" + 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				Log.info(
						Log.BETBIGORSMALL,
						roleInfo.roleId
								+ "#$36#$"
								+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
										.format(new Date()) + "#$"
								+ TianLongServer.srvId + "#$" + roleInfo.gold
								+ "#$" + 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 给不中奖的人发送邮件
		while (buZhongJiang.hasNext()) {
			BigOrSmallRoleId roleInfo = buZhongJiang.next();
			MailManager.getInstance().sendSysMail(roleInfo.roleId, "押大小结果",
					"尊敬的玩家，很可惜您输掉了本局押大小的奖金。希望下一次好运降临到您的身上。", 0, null);
			// it.remove();
			try {
				Log.info(
						Log.BETBIGORSMALL,
						roleInfo.roleId
								+ "#$36#$"
								+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
										.format(new Date()) + "#$"
								+ TianLongServer.srvId + "#$" + roleInfo.gold
								+ "#$" + 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 发送系统公告
		sendNotice(lastRoundResult);
	}

	/**
	 * 发送公告
	 * 
	 * @param lastRoundResult
	 *            开奖结果
	 */
	public void sendNotice(final JoabSize lastRoundResult) {
		Iterator<BigOrSmallRoleId> zhongJiang = null; // 中奖的Vector
		if (lastRoundResult.getBetId() == 0) {
			zhongJiang = betSmallRole.iterator();
		} else {
			zhongJiang = betBigRole.iterator();
		}
		// 公告
		int i = 1;
		String str = "";// 公告内容
		while (zhongJiang.hasNext() && i <= 3) {
			BigOrSmallRoleId roleInfo = zhongJiang.next();
			if (roleInfo != null) {
				if (RoleCardService.INSTANCE.getCard(roleInfo.roleId) != null) {
					str = str
							+ RoleCardService.INSTANCE.getCard(roleInfo.roleId)
									.getName() + "，";
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
	 * 枚举押注的项目(如，大小)（枚举） 2011-1-17 LiJG
	 */
	public enum JoabSize {
		/** 押小的ID */
		Small(0),
		/** 押大的ID */
		Big(1)

		;

		/** 押注项目的ID */
		private final int betId;

		/**
		 * 构造方法
		 * 
		 * @param betId
		 *            押注项目的ID
		 */
		private JoabSize(final int betId) {
			this.betId = betId;
		}

		/**
		 * 押注项目ID的get方法
		 * 
		 * @return 押注项目的ID
		 */
		public int getBetId() {
			return betId;
		}

		@Override
		public String toString() {
			String result = null;
			switch (this) {
			case Small:
				result = "小";
				break;
			case Big:
				result = "大";
				break;
			default:
				result = "无";
				break;
			}
			return result;

		}

	}

	/**
	 * 存储每个人押注的金额及ID(内部类) 2011-1-17 LiJG
	 */
	private class BigOrSmallRoleId implements Comparable<BigOrSmallRoleId> {
		/** 人物ID */
		private int roleId;
		/** 每个人押注的金额 */
		private int gold;
		private int index;

		@SuppressWarnings("unused")
		public int getRoleId() {
			return roleId;
		}

		public int getGold() {
			return gold;
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
		 * @param gold
		 *            押注金额
		 */
		public BigOrSmallRoleId(final int index, final int roleId,
				final int gold) {
			this.index = index;
			this.roleId = roleId;
			this.gold = gold;
		}

		@Override
		public int compareTo(BigOrSmallRoleId o) {
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
			if (obj == null || !(obj instanceof BigOrSmallRoleId))
				return false;

			return this.compareTo((BigOrSmallRoleId) obj) == 0;
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

	public void minTick(final int curMin, final RoleBean roleBean) {
		
		TaskState taskState = roleBean.getTasks().getTaskState(1040011);
		if (taskState != null) {
			
			ArrayList<Integer> extra = taskState.getExtra();

			int j = 4;
			if (extra.size() < j) {
				for (int index = extra.size(); index < j; index++)
					extra.add(0);
			}
			if (extra.get(j - 1) == 1) {
				System.out.println("测试到这里啦！33333333333333333333333");
				int i = 2;
				if (extra.size() < i) {
					for (int index = extra.size(); index < i; index++)
						extra.add(0);
				}
				int x = extra.get(i - 1) - curMin;
				if (x <= 0) {
					roleBean.setRacingMessage("开奖啦！");
					/** 随机结果 */
					int result = random.nextInt(4)+1;
					System.out.println("开奖啦！" + result);
					if (extra.size() < 4) {
						for (int index = extra.size(); index < 4; index++)
							extra.add(0);
					}
					extra.set(3, 0);
					
					if (extra.size() < 5) {
						for (int index = extra.size(); index < 5; index++)
							extra.add(0);
					}
					int betNum = extra.get(4);
					if(betNum==result){
						System.out.println("恭喜您，赢了！");
						roleBean.setRacingMessage("恭喜您，赢了！");
						racingSetMarks(roleBean,racingGetMarks(roleBean)+500);
						
						if (extra.size() < 8) {
							for (int index = extra.size(); index < 8; index++)
								extra.add(0);
						}
						extra.set(7, Integer.parseInt("" + extra.get(7) + "1"));
						
					}else{
						System.out.println("很遗憾，您输了！");
						roleBean.setRacingMessage("很遗憾，您输了！");
						
						if (extra.size() < 8) {
							for (int index = extra.size(); index < 8; index++)
								extra.add(0);
						}
						extra.set(7, Integer.parseInt("" + extra.get(7) + "0"));
					}	
					extra.set(4, 0);
					
					if (extra.size() < 6) {
						for (int index = extra.size(); index < 6; index++)
							extra.add(0);
					}
					extra.set(5, extra.get(5)+1);
						
					if (extra.size() < 7) {
						for (int index = extra.size(); index < 7; index++)
							extra.add(0);
					}
					extra.set(6, Integer.parseInt("" + extra.get(6) + betNum));
					
					
					
					
				} else if (x <= 1) {
					roleBean.setRacingMessage("还有1分钟！");
					System.out.println("还有1分钟！");
				} else if (x <= 2) {
					roleBean.setRacingMessage("还有2分钟！");
					System.out.println("还有2分钟！");
				} else {
					roleBean.setRacingMessage("还有3分钟！");
					System.out.println("还有3分钟！");
				}
				
				
				
			}
		}
	}

	/**
	 * 开奖后 清空vector
	 */
	private void clearBetedPlayer() {
		betBigRole.clear();
		betSmallRole.clear();
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
	 * 取得赛马剩余积分
	 * 
	 * @param role
	 *            角色对象
	 * @return String 剩余积分
	 */
	public int racingGetMarks(final RoleBean role) {
		return role.getRacingMarks();
	}

	/**
	 * 设置赛马剩余积分
	 * 
	 * @param role
	 *            角色对象
	 * @return String 剩余积分
	 */
	public void racingSetMarks(final RoleBean role, final int marks) {
		role.setRacingMarks(marks);
	}

	/**
	 * 根据index获取奖品编号
	 * 
	 * @param index
	 * @return integer 奖品编号
	 */
	public int racingPrizeCode(int index) {
		return this.getPrizeCode().get(index - 1);
	}
	
	public String racingHistoryMessage(int num,String message,String resultMess){
		String messageStr = "";
		char[] messageChar = message.toCharArray();
		char[] ResultChar = resultMess.toCharArray();
		//boolean x = num>=15?true:false;
		System.out.println("没有问题！");
//		if(x){
//			System.out.println("大于15次！");
//			for(int i = num-15; i < num ; i++){
//				messageStr = messageStr + "您今日的第" + (i+1) + "场大马" +  messageChar[i] + (Integer.parseInt(String.valueOf(ResultChar[i]))==1?"获胜":"失败");
//			}
//		}else{
			System.out.println("小于15次！");
			for(int i = 0; i < num ; i++){
				messageStr = messageStr + "您今日的第" + (i+1) + "场大马" +  messageChar[i] + (Integer.parseInt(String.valueOf(ResultChar[i]))==1?"获胜":"失败");
			}
//		}
		
		System.out.println(messageStr);
		
		return messageStr;
	}
	

	/**
	 * 根据index获取奖品积分
	 * 
	 * @param index
	 * @return integer 奖品积分
	 */
	public int racingDeductMarks(int index) {
		return this.getPrizeMark().get(index - 1);
	}

	/**
	 * 关闭服务器时的判断
	 */
	public void shutdown() {
		this.issuingAwards();
	}

}
