package com.joyveb.tlol.charge;

import java.util.Vector;

import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.ShenZhouChargeInfoData;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.schedule.OneOffSchedule;
import com.joyveb.tlol.schedule.ScheduleManager;
import com.joyveb.tlol.util.Log;

public class ShenZhouChargeInfo extends MessageSend implements DataHandler {

	public String shenZhouChargeTip = "1、天龙支持多种充值卡充值。神州行卡，联通卡，电信卡，Q币卡，盛大卡，骏网一卡通，网易卡，搜狐卡，完美卡，易宝e卡通，征途卡，久游卡。请选择适合您的充值方式。/2、天龙给予玩家更多优惠，充的越多，赠的越多。充值1～49元，每1元兑换10元宝；充值50～99元，每1元兑换11元宝；充值100元以上，每1元兑换12元宝。/3、提示您充值的请求提交以后，请返回游戏界面耐心等待，结果将以邮件形式发送至您邮箱，请注意查收。/4、充值过程中，如遇到问题，请联系客服，电话400-650-8380；QQ1614494278。";
	private boolean isInitFinish = false;

	private Vector<String> shenZhouRateInfo;

	private static ShenZhouChargeInfo instance = new ShenZhouChargeInfo();

	private ShenZhouChargeInfo() {

	}

	public static ShenZhouChargeInfo getInstance() {
		return instance;
	}

	public void init() {
		shenZhouRateInfo = new Vector<String>();
		shenZhouRateInfo.add("1～49元，每1元兑换10元宝");
		shenZhouRateInfo.add("50～99元，每1元兑换11元宝");
		shenZhouRateInfo.add("100元以上，每1元兑换12元宝");
//		ShenZhouChargeInfoData shenZhouChargeInfoData = new ShenZhouChargeInfoData();
//		CommonParser.getInstance().postTask(DbConst.ShenZhouChargeInfo, this,
//				shenZhouChargeInfoData, true);
//
//		int waitCount = 0;
//		while (!isInitFinish) {
//			try {
//				Thread.sleep(1000);
//				waitCount++;
//				if (waitCount > 10)
//					Log.error(Log.STDOUT,
//							"can not init ShenZhou Charge info!!!");
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		Log.info(Log.STDOUT, "init ShenZhouChargeInfo finished!!");
	}

	@Override
	public void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
//		if (flag) {
//			switch (eventID) {
//			case ShenZhouChargeInfo:
//				ShenZhouChargeInfoData szcd = (ShenZhouChargeInfoData) ds;
//				shenZhouRateInfo = szcd.getShenZhouRateInfo();
//				isInitFinish = true;
//				break;
//			default:
//				Log.error(Log.STDOUT, "handle", "unhandled msgid! : "
//						+ flag);
//				break;
//			}
//		} else {
//			Log.error(Log.STDOUT,
//					"can not init ShenZhou Charge info from db!!!");
//		}
	}

	public void sendChargeInfo(final RoleBean role) {
		putShort((short) 701);
		putByte((byte)shenZhouRateInfo.size());
		for(String info : shenZhouRateInfo) {
			putString(info);
		}
		putString(shenZhouChargeTip);
	}

	public void reloadChargeInfo() {
		ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {
			@Override
			public void execute() {
				ShenZhouChargeInfoData shenZhouChargeInfoData = new ShenZhouChargeInfoData();
				CommonParser.getInstance().postTask(DbConst.ShenZhouChargeInfo,
						ShenZhouChargeInfo.getInstance(),
						shenZhouChargeInfoData);
			}
		});
	}

}
