package com.joyveb.tlol;

import java.io.File;
import java.io.IOException;

import com.joyveb.tlol.auction.AuctionHouse;
import com.joyveb.tlol.betAccount.BetAccountManager;
import com.joyveb.tlol.bigOrSmall.BigOrSmallManager;
import com.joyveb.tlol.charge.ShenZhouChargeInfo;
import com.joyveb.tlol.cycles.Challenge;
import com.joyveb.tlol.db.TaskManager;
import com.joyveb.tlol.gang.GangService;
import com.joyveb.tlol.guess.GuessManager;
import com.joyveb.tlol.net.Reactor;
import com.joyveb.tlol.pay.connect.ConnectTaskManager;
import com.joyveb.tlol.res.ResourceManager;
import com.joyveb.tlol.uid.UIDManager;
import com.joyveb.tlol.util.Log;

public class TianLongServer {
	
	private Reactor reactor = null;
	private ProcessCenter processor = null;
	public static String srvId = null;
	public static String serverIP = null;
	public static String serverName = null;
	public static String gameResPath = null;
	public static boolean local;

	public TianLongServer(final int port, final String luaPath, final String luaResource) {
		Log.info(Log.STDOUT, "TianLongServer: tianLongPort: " + port);

		TaskManager.getInstance().init();
		UIDManager.getInstance().init();
		ShenZhouChargeInfo.getInstance().init();
//		KongZhongChargeInfo.getInstance().init();
		GangService.INSTANCE.loadGang();
		ConnectTaskManager.getInstance().startAllMoneyService();
		ResourceManager.initResource();

		LuaService.start(new File(luaPath), new File(luaResource));

		if (port != 0) {
			try {
				processor = new ProcessCenter();
				reactor = new Reactor(port, processor);
				reactor.start();
				processor.start();
			} catch (IOException e) {
				Log.info(Log.STDOUT, "TIANLONGBABU start failed !!");
				Log.info(Log.STDOUT, e);
			}
		}
		
		Log.info(Log.STDOUT, "TIANLONGBABU started!!");
	}

	/** 
	 * 停止服务器运行 
	 * @throws InterruptedException 
	 */
	public final void stopTianLongServer() throws InterruptedException {
		if (reactor != null)
			reactor.shutdown();
		Log.info(Log.STDOUT, "network closed !");

		BigOrSmallManager.getInstance().shutdown();//押大小游戏强制开奖
		Challenge.getInstance().shutdown();//老虎棒子鸡游戏强制退钱
		GuessManager.getInstance().shutdown();//猜猜看游戏强制开奖
		BetAccountManager.getInstance().shutdown();//元宝赌数游戏强制开奖
		GangFightService.shutdown();//停服帮战就退钱不开
		if (Conf.instance().getSp().equals("android")) {
			OnlineService.writeCodeList();// 停服写激活码
		}
		if(processor != null) {
			processor.stopRun();
			processor = null;
		}
		//帮派回写
		GangService.INSTANCE.minTick(Integer.MAX_VALUE);
		//回写删除追杀
		OnlineService.writeBackDelFatwa();
		//回写添加追杀
		OnlineService.writeBackInsertFatwa();
		OnlineService.writeBackDB();
		//回写强制离婚
		OnlineService.writeBackMarryDB();
		//回写强制解除师傅
		OnlineService.writeBackMasterDB();
		//回写强制解除徒弟
		OnlineService.writeBackApprenticeDB();
		
		AuctionHouse.INSTANCE.waitDB();
		TaskManager.getInstance().shutdown();
		ConnectTaskManager.getInstance().shutdown();

		LuaService.close();
		
		Thread.sleep(5000);
		
		Log.info(Log.STDOUT, "server stoped !");
		reactor = null;
	}
}