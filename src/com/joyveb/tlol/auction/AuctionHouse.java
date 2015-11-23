package com.joyveb.tlol.auction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.Watchable;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.mail.MailManager;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.schedule.MinTickHandler;
import com.joyveb.tlol.server.ServerMessage;
import com.joyveb.tlol.util.Log;

/**
 * 拍卖行
 * 
 * @author Sid
 */
public enum AuctionHouse implements MinTickHandler, DataHandler, Watchable {
	/** 单例 */
	INSTANCE;

	/**
	 * 是否完成加载
	 */
	private volatile boolean loaded;

	/**
	 * 正在修改中的数量
	 */
	volatile int modifying;

	/**
	 * 存放在拍卖行的所有物品
	 */
	final TreeMap<Long, Auction> auctions = new TreeMap<Long, Auction>();

	/**
	 * 按照到期时间排列的拍卖号
	 */
	final ArrayList<Long> timeOutList = new ArrayList<Long>();

	final sortByAutionTimeout timeOutListSorter = new sortByAutionTimeout();

	/**
	 * 内部类 拍卖物品到时列表
	 * 
	 * @author zhongyuan
	 * 
	 */
	class sortByAutionTimeout implements Comparator<Long> {

		@Override
		public int compare(Long serial1, Long serial2) {
			return auctions.get(serial1).timeOut - auctions.get(serial2).timeOut;
		}

	}

	/**
	 * 拍品归类索引
	 */
	final EnumMap<AutionType, TreeSet<Long>> category = new EnumMap<AutionType, TreeSet<Long>>(AutionType.class);

	/**
	 * 构造函数
	 */
	private AuctionHouse() {
		for (AutionType autionType : AutionType.values())
			category.put(autionType, new TreeSet<Long>());
	}

	/**
	 * 加载所有拍卖记录
	 */
	public void loadAuction() {
		Log.info(Log.STDOUT, "AuctionHouse", "加载拍卖行");

		CommonParser.getInstance().postTask(DbConst.Auction_Get_All, this, new AuctionGetter(), true);
	}

	/**
	 * 添加拍卖
	 * 
	 * @param auction
	 *            一项拍卖
	 */
	public void addAuction(final Auction auction) {
		if (auction.buyoutPrice > 99999999 || auction.buyoutPrice <= 0 || auction.reservePrice > 99999999 || auction.reservePrice <= 0) {
			System.out.println("拍卖时有非法的数值");
			return;
		}
		auctions.put(auction.serial, auction);

		if (!timeOutList.add(auction.serial)) {
			Log.error(Log.STDOUT, new Exception("timeOutTreeSet.add 失败!!!"));
		}
		// Log.info(Log.STDOUT, "拍卖物品的序列号为: " + auction.serial);
		// Log.info(Log.STDOUT, "timeOutTreeSet.size: " + timeOutList.size());

		for (AutionType autionType : AutionType.values()) {
			if ((auction.categoryCode >> autionType.ordinal() & 1) == 1) {
				category.get(autionType).add(auction.serial);
			}
		}
	}

	/**
	 * @param auction
	 *            要移除的拍卖
	 */
	public void removeAuction(final Auction auction) {
		if (!timeOutList.remove(auction.serial)) {
			Log.info(Log.STDOUT, auction.detail());
			Log.error(Log.ERROR, new Exception("timeOutList.remove 调用失败!"));
		}
		for (AutionType autionType : AutionType.values()) {
			if ((auction.categoryCode >> autionType.ordinal() & 1) == 1) {
				category.get(autionType).remove(auction.serial);
			}
		}

		if (auctions.remove(auction.serial) == null) { // 必须在最后从map中删除
			Log.error(Log.ERROR, new Exception("auctions.remove 调用失败!"));
		}
	}

	@Override
	public void minTick(final int curMin) {
		Collections.sort(timeOutList, timeOutListSorter);
		Iterator<Long> it = timeOutList.iterator();
		while (it.hasNext()) {
			Auction auction = auctions.get(it.next());
			if (auction.modifying) {
				continue;
			}

			if (curMin < auction.timeOut) {
				return;
			}
			auction.modifying = true;
			modifying++;

			CommonParser.getInstance().postTask(DbConst.Auction_Delete, this, new AuctionDeleter(auction.serial));
		}

	}

	@Override
	public void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
		switch (eventID) {
		case Auction_Get_All:
			loaded = true;
			Log.info(Log.STDOUT, "AuctionHouse", "加载拍卖行完成");
			break;
		case Auction_Delete:
			timeOut((AuctionDeleter) ds);
			break;
		default:
			break;
		}

	}

	@Override
	public void watch() {
		Log.info(Log.STDOUT, "总拍卖量：" + auctions.size());

		for (AutionType autionType : AutionType.values()) {
			Log.info(Log.STDOUT, autionType.name());
			for (Long auctionId : category.get(autionType))
				Log.info(Log.STDOUT, auctions.get(auctionId).info());
		}
	}

	/**
	 * @param auctionDeleter
	 *            拍卖到时负责删除拍卖记录的对象
	 */
	public void timeOut(final AuctionDeleter auctionDeleter) {
		long auctionId = auctionDeleter.getAuctionId();
		Auction auction = auctions.get(auctionId);
		removeAuction(auction);
		modifying--;

		if (auction.hasBid()) { // 有人参与竞拍
			ServerMessage.sendSysPrompt(OnlineService.getOnline(auction.roleid), "物品【" + auction.getTitle() + "】竞拍成功！");

			MailManager.getInstance()
					.sendSysMail(auction.roleid, "系统邮件：竞拍成功", "物品【" + auction.getTitle() + "】竞拍成功！消耗资金" + LuaService.call4String("getValueDescribe", auction.bidding), 0, auction.item);

			int fee = (int) (auction.bidding * 0.05);
			ServerMessage.sendSysPrompt(OnlineService.getOnline(auction.owner), "物品【" + auction.getTitle() + "】拍卖成功！");
			MailManager.getInstance().sendSysMail(auction.owner, "系统邮件：拍卖成功",
					"物品【" + auction.getTitle() + "】拍卖成功！/收取手续费：" + LuaService.call4String("getValueDescribe", fee) + "/获得资金" + LuaService.call4String("getValueDescribe", auction.bidding - fee),
					auction.bidding - fee, null);
			try {
				// 卖家得到金币
				Log.info(Log.ITEM, 0 + "#$" + auction.owner + "#$40#$" + "" + "#$" + 0 + "#$" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + TianLongServer.srvId + "#$"
						+ (auction.bidding - fee) + "#$" + 0 + "#$" + 0);
				// 买家得到物品
				Log.info(Log.ITEM,
						0 + "#$" + auction.roleid + "#$39#$" + auction.item.getName() + "#$" + auction.item.getStorage() + "#$" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$"
								+ TianLongServer.srvId + "#$" + "0" + "#$" + auction.item.getTid() + "#$" + auction.item.getUid());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			ServerMessage.sendSysPrompt(OnlineService.getOnline(auction.owner), "物品【" + auction.getTitle() + "】拍卖失败！");
			MailManager.getInstance().sendSysMail(auction.owner, "系统邮件：拍卖失败", "物品【" + auction.getTitle() + "】拍卖失败！", 0, auction.item);
			try {
				Log.info(Log.ITEM,
						0 + "#$" + auction.owner + "#$38#$" + auction.item.getName() + "#$" + auction.item.getStorage() + "#$" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$"
								+ TianLongServer.srvId + "#$" + "0" + "#$" + auction.item.getTid() + "#$" + auction.item.getUid());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @return 是否加载完成
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * 等待数据库回写完成
	 */
	public void waitDB() {
		while (modifying > 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return 存放在拍卖行的所有物品
	 */
	public TreeMap<Long, Auction> getAuctions() {
		return auctions;
	}

	/**
	 * 判断拍卖中有没有某人的物品
	 * 
	 * @param role
	 * @return 0没有 1 有
	 */
	public int ifHaveAuction(RoleBean role) {
		Iterator<Auction> it = auctions.values().iterator();
		while (it.hasNext()) {
			Auction a = it.next();
			if (a.owner == role.getRoleid()) {
				return 1;
			}
		}
		return 0;
	}
}
