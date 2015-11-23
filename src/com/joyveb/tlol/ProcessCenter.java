package com.joyveb.tlol;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.joyveb.tlol.auction.AuctionHouse;
import com.joyveb.tlol.billboard.TopRatedService;
import com.joyveb.tlol.community.Communitys;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.db.TaskManager;
import com.joyveb.tlol.fatwa.FatwaService;
import com.joyveb.tlol.fee.FeeService;
import com.joyveb.tlol.mailnotice.MailNoticeService;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.net.NetHandler;
import com.joyveb.tlol.net.NetListener;
import com.joyveb.tlol.pay.connect.ConnectTaskManager;
import com.joyveb.tlol.protocol.MsgCatIDs;
import com.joyveb.tlol.protocol.MsgHeader;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.schedule.ScheduleManager;
import com.joyveb.tlol.user.UserBean;
import com.joyveb.tlol.util.Log;

/** 消息处理中心 */
public class ProcessCenter implements NetListener {
	/**
	 * 主循环时间单元：纳秒
	 */
	public static final int PERIOD = 100 * 1000 * 1000;

	/**
	 * 主逻辑线程
	 */
	private Processor processor = new Processor();

	/**
	 * @param luaPath 脚本代码路径
	 * @param resourcepath 资源脚本路径
	 */
	public ProcessCenter() {
		BulletinService.INSTANCE.loadBulletin();
		Communitys.INSTANCE.loadCommunity();
		TopRatedService.INSTANCE.loadTopRated();
		AuctionHouse.INSTANCE.loadAuction();
		MailNoticeService.INSTANCE.loadMailNotice();//加载邮件共公告
		LoginBulletinService.INSTANCE.loadLoginBulletin();//加载上线提示公告
		FeeService.INSTANCE.loadFee();//加载计费
		//加载追杀令
		FatwaService.INSTANCE.loadFatwa();
		do {
			try {
				Thread.sleep(1000);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}while(!TopRatedService.INSTANCE.isLoaded() || !Communitys.INSTANCE.isLoaded()
				|| !AuctionHouse.INSTANCE.isLoaded());
	}

	/** 停止运行消息处理服务 */
	public final void stopRun() {
		processor.process = false;
		try {
			processor.join();
		}catch(InterruptedException e) {
			e.printStackTrace();
		}finally {
			processor.interrupt();
		}
	}
	
	public void start() {
		processor.start();
	}

	/** 消息处理线程 */
	private static class Processor extends Thread {
		/**
		 * 运行标志
		 */
		private volatile boolean process = true;

		/**
		 * 构造函数
		 */
		public Processor() {
			this.setName("Main loop");
		}

		/**
		 * 处理在线角色消息
		 */
		private void onlinePlayerHandler() {
			ArrayList<Integer> onlines = OnlineService.getAllOnlines();
			for(int roleid : onlines) {
				RoleBean role = OnlineService.getOnline(roleid);
				if(role == null)
					continue;

				// 每个循环roleBean对象应该做的事情
				role.tick();

				// 处理收发消息
				if(role.isConnected())
					processComingMsg(role);
				else {
					// 掉线
					if(role.getBattleAgent().getBattle() == null || role.getBattle().isDeadBattle()) {
						if(!role.isWriteBacking()) {
							if(role.getBattle() != null) {
								role.getBattle().close();
							}
							role.getRoleAgent().userDisconnect();
						}
					}else {
						// role.getBattle().removeFighter(role);
						if(!role.getBattleAgent().getBattle().isChose(role)) {
							role.getBattleAgent().getBattle().offLinePlayerAI(role);
							if(role.getBattle().chooseOver())
								role.getBattle().fightStart();
						}
					}
				}
			}
		}

		/**
		 * 处理未登录玩家
		 */
		private void unloginPlayerHandler() {
			for(int i = 0; i < OnlineService.getUnLogUsers().size(); i++) {
				UserBean user = OnlineService.getUnLogUsers().elementAt(i);
				if(user.getNetHandler() != null && user.getNetHandler().getState() == NetHandler.STATE_NORMAL)
					processUnlogUserComingMsg(user);
				else
					// 掉线
					user.getUserAgent().unloginUserDisconnect();
			}
		}

		@Override
		public void run() {
			while(process) {
				try {
					ScheduleManager.INSTANCE.execute();

					long timeElapse = System.nanoTime();

					TaskManager.getInstance().processHandledTask();

					ConnectTaskManager.getInstance().processHandledTask(); // 充值处理

					ConnectTaskManager.getInstance().processSubHandledTask(); // 扣费处理
					
					ConnectTaskManager.getInstance().processPayHandledTask(); // 充值（元宝）处理

					ConnectTaskManager.getInstance().processSelectHandledTask(); // 查询处理
					
					onlinePlayerHandler();

					unloginPlayerHandler();

					timeElapse = System.nanoTime() - timeElapse;
					if(timeElapse > PERIOD)
						Log.info(Log.PERFORMANCE, "Processor.run", timeElapse / 1000000); // 循环时间长于PERIOD可能存在性能问题
					else if(timeElapse >= 0)
						TimeUnit.MILLISECONDS.sleep((PERIOD - timeElapse) / 1000000);
				}catch(Exception e) {
					Log.error(Log.STDOUT, "Processor.run", e);
				}
			}
		}

		/**
		 * 处理user状态的消息
		 * 
		 * @param user 消息发送者
		 */
		private void processUnlogUserComingMsg(final UserBean user) {
			IncomingMsg message = user.getNetHandler().receive(0);

			if(message != null) {
				message.getBody().position(0);
				int bodylen = message.getBody().getInt();
				message.getBody().position(0);

				MsgHeader msgHeader = message.getHeader();
				if(!msgCheck(msgHeader, bodylen) || msgHeader.getProtocolID() != 1000) {
					Log.error(Log.STDOUT, "processUnlogUserComingMsg", msgHeader);
					return;
				}

				switch(MsgCatIDs.getInstance(msgHeader.getMsgType())) {
				case CAT_ROLE:
					user.getUserAgent().processCommand(message);
					break;
				case CAT_HEARTBEAT:
					user.getUserAgent().processCommand(message);
					break;
				default:
					Log.error(Log.STDOUT, "processUnlogUserComingMsg",
							"msgHeader.getMsgType() = " + msgHeader.getMsgType() + " msgHeader.getMsgID() = "
									+ msgHeader.getMsgID());
					break;
				}
			}
		}

		/**
		 * 处理收到的消息
		 * 
		 * @param role 消息来源
		 */
		private void processComingMsg(final RoleBean role) {
			IncomingMsg message = role.getNetHandler().receive(role.getRoleid());

			if(message == null)
				return;

			message.getBody().position(0);
			int bodylen = message.getBody().getInt();
			message.getBody().position(0);

			MsgHeader msgHeader = message.getHeader();
			if(!msgCheck(msgHeader, bodylen) || msgHeader.getProtocolID() != 1000) {
				Log.error(Log.STDOUT, "processComingMsg", msgHeader);
				return;
			}

			MsgID msg = MsgID.getInstance(msgHeader.getMsgID());
			if(msg == null) {
				Log.error(Log.STDOUT, "收到不存在的消息id：" + msgHeader.getMsgID());
				return;
			}

			MsgCatIDs msgCatIDs = MsgCatIDs.getInstance(msgHeader.getMsgType());

			long startTime = System.currentTimeMillis();
			
			AgentProxy agentProxy = role.getAgent(msgCatIDs);
			if(agentProxy != null)
				agentProxy.processCommand(message);
			
			Log.performance(MsgID.getInstance(msgHeader.getMsgID()).name(),
					"[" + role.getUserid() + "]" + "[" + role.getRoleid() + "]" + "[" + role.getNick() + "]", startTime);
		}

		/**
		 * 消息验证
		 * 
		 * @param msgHeader 消息头
		 * @param bodylen 消息长度
		 * @return 消息验证是否成功
		 */
		private boolean msgCheck(final MsgHeader msgHeader, final int bodylen) {
			return msgHeader.getMsgCheck() == msgHeader.getMsgID() + bodylen - 3;
		}
	}

	@Override
	public final void onAccept(final NetHandler clientHandler) {
		OnlineService.addUnLoginUser(new UserBean(clientHandler));
	}
	
}
