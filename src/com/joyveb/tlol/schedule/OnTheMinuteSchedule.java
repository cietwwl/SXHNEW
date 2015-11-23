package com.joyveb.tlol.schedule;

import java.util.ArrayList;

import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.auction.AuctionHouse;
import com.joyveb.tlol.betAccount.BetAccountManager;
import com.joyveb.tlol.gang.GangService;
import com.joyveb.tlol.guess.GuessManager;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.transfer.TransferManager;
import com.joyveb.tlol.util.Cardinality;
import com.joyveb.tlol.util.Log;

public enum OnTheMinuteSchedule implements ScheduleTask {
	INSTANCE;

	private int lastMinute = -1;

	@Override
	public void execute() {
		int curMinute = Cardinality.INSTANCE.getMinute();
		if (curMinute <= lastMinute)
			return;
		try {
			AuctionHouse.INSTANCE.minTick(curMinute);
			GangService.INSTANCE.minTick(curMinute);
			BetAccountManager.getInstance().minTick(curMinute);//元宝赌数
			GuessManager.getInstance().minTick(curMinute);//猜猜看
			TransferManager.getInstance().minTick(curMinute); // 转服
			OnlineService.minTick(curMinute);
			
			//----------每分钟减少一点邪恶值----------开始
			ArrayList<Integer> onlines = OnlineService.getAllOnlines();
			for(int roleid : onlines) {
				RoleBean role = OnlineService.getOnline(roleid);
				role.lessenEvil(1);
			}
			//----------每分钟减少一点邪恶值----------结束
		} catch (Exception e) {
			Log.error(Log.ERROR, e);
		}
		lastMinute = curMinute;
	}

	@Override
	public boolean isTimeOut() {
		return false;
	}

}
