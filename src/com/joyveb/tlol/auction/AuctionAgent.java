package com.joyveb.tlol.auction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.SubModules;
import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.item.UniqueItem;
import com.joyveb.tlol.mail.MailManager;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.DefaultMsgBody;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.server.ServerMessage;
import com.joyveb.tlol.store.CommonPack;
import com.joyveb.tlol.util.Cardinality;
import com.joyveb.tlol.util.Log;
import com.joyveb.tlol.util.UID;

/**
 * 拍卖行消息代理
 * 
 * @author Sid
 */
public class AuctionAgent extends AgentProxy implements DataHandler {

	/**
	 * 构造函数
	 * 
	 * @param player
	 *            角色
	 */
	public AuctionAgent(final RoleBean player) {
		this.player = player;
	}

	@Override
	public void processCommand(final IncomingMsg msg) {
		switch (MsgID.getInstance(msg.getHeader().getMsgID())) {
		case Auction_Req_List:
			if (MsgID.Auction_Req_List.readBody(msg.getBody()))
				auctionReqList();
			else
				replyMessage(player, 1, MsgID.Auction_Req_List_Resp, "错误的请求！");

			break;
		case Auction_Req_NextPage:
			if (MsgID.Auction_Req_NextPage.readBody(msg.getBody()))
				auctionReqNextPage();
			else
				replyMessage(player, 1, MsgID.Auction_Req_List_Resp, "错误的请求！");

			break;
		case Auction_Bidding_Confirm:
			if (MsgID.Auction_Bidding_Confirm.readBody(msg.getBody()))
				auctionBiddingConfirm();
			else
				replyMessage(player, 1, MsgID.Auction_Bidding_Confirm_Resp, "错误的请求！");

			break;
		case Auction_Bidding:
			if (MsgID.Auction_Bidding.readBody(msg.getBody()))
				auctionBidding();
			else
				replyMessage(player, 1, MsgID.Auction_Bidding_Resp, "错误的请求！");

			break;
		case Auction_Sale_Confirm:
			if (MsgID.Auction_Sale_Confirm.readBody(msg.getBody()))
				auctionSaleConfirm();
			else
				replyMessage(player, 1, MsgID.Auction_Sale_Confirm_Resp, "错误的请求！");

			break;
		case Auction_Item_Sale:
			if (MsgID.Auction_Item_Sale.readBody(msg.getBody()))
				auctionItemSale();
			else
				replyMessage(player, 1, MsgID.Auction_Item_Sale_Resp, "错误的请求！");

			break;
		default:
			Log.info(Log.STDOUT, "为处理的协议！");
			break;
		}

	}

	/**
	 * 请求某类别列表
	 */
	public void auctionReqList() {
		sendSinglePage(MsgID.Auction_Req_List.getMsgBody().getByte(1), 1);
	}

	/**
	 * 请求下一页
	 */
	public void auctionReqNextPage() {
		DefaultMsgBody defaultMsgBody = MsgID.Auction_Req_NextPage.getMsgBody();
		byte type = defaultMsgBody.getByte(1);
		short curqPage = defaultMsgBody.getShort(2);
		byte direction = defaultMsgBody.getByte(3);

		boolean backward = direction == 0;

		TreeSet<Long> auctionIds = AuctionHouse.INSTANCE.category.get(AutionType.values()[type]);

		int page = curqPage + (backward ? -1 : 1);
		int totalPage = (int) Math.ceil((double) auctionIds.size() / Auction.PER_PAGE);

		if (page < 1)
			page = totalPage;
		else if (page > totalPage)
			page = backward ? totalPage - 1 : 1;

		sendSinglePage(type, page);
	}

	/**
	 * 发送单独的页面
	 * 
	 * @param type
	 *            页面类型
	 * @param page
	 *            页码
	 */
	private void sendSinglePage(final byte type, final int page) {
		TreeSet<Long> auctionIds = AuctionHouse.INSTANCE.category.get(AutionType.values()[type]);
		int start = (page - 1) * Auction.PER_PAGE + 1;
		int end = start + Auction.PER_PAGE > auctionIds.size() ? auctionIds.size() : start + Auction.PER_PAGE;

		prepareBody();
		putShort((short) 0);

		putByte(type);
		putShort((short) page);// 当前页

		int totalPage = (short) (Math.ceil((double) auctionIds.size() / Auction.PER_PAGE));
		putShort((short) (totalPage == 0 ? 1 : totalPage));// 总页数

		putByte((byte) (end - start + 1));// 本页数量

		if (end > 0) {
			int curMin = Cardinality.INSTANCE.getMinute();

			TreeSetIntervalIterator<Long> iterator = new TreeSetIntervalIterator<Long>(auctionIds, start, end);
			while (iterator.hasNext()) {
				Long auctionId = iterator.next();

				Auction auction = AuctionHouse.INSTANCE.auctions.get(auctionId);

				putString(auction.getTitle() + " 竞拍价：" + LuaService.call4String("getValueDescribe", auction.nextBid()) + " 一口价：" + LuaService.call4String("getValueDescribe", auction.buyoutPrice)
						+ auction.getTimeDes(curMin));

				putInt(auction.item.getColor());

				putLong(auctionId);
			}
		}

		sendMsg(player, MsgID.Auction_Req_List_Resp);
	}

	/**
	 * 竞拍确认
	 */
	private void auctionBiddingConfirm() {
		Long auctionId = MsgID.Auction_Bidding_Confirm.getMsgBody().getLong(1);

		Auction auction = AuctionHouse.INSTANCE.auctions.get(auctionId);

		if (auction == null) {
			replyMessage(player, 2, MsgID.Auction_Bidding_Confirm_Resp, "该物品已下架！");
			return;
		}

		prepareBody();
		putShort((short) 0);

		putString("数量：" + Math.abs(auction.item.getStorage()) + "/" + "竞拍价：" + LuaService.call4String("getValueDescribe", auction.nextBid()) + "/" + "一口价："
				+ LuaService.call4String("getValueDescribe", auction.buyoutPrice) + "/" + auction.item.getDescribe());

		putInt(auction.nextBid());
		putInt(auction.buyoutPrice);

		sendMsg(player, MsgID.Auction_Bidding_Confirm_Resp);
	}

	/**
	 * 竞拍
	 */
	private void auctionBidding() {
		Long auctionId = MsgID.Auction_Bidding.getMsgBody().getLong(1);
		int bid = MsgID.Auction_Bidding.getMsgBody().getInt(2);

		Auction auction = AuctionHouse.INSTANCE.auctions.get(auctionId);

		if(bid>99999999|| player.getGold()>99999999|| player.getGold()<0||bid<=0){
			replyMessage(player, 3, MsgID.Auction_Sale_Confirm_Resp, "非法数值！");
			return;
		}
		
		
		if (bid > player.getGold())
			replyMessage(player, 2, MsgID.Auction_Bidding_Resp, "资金不足，无法竞拍！");
		else if (auction == null)
			replyMessage(player, 3, MsgID.Auction_Bidding_Resp, "该拍卖已结束！");
		else if (!auction.modifying && (bid == auction.nextBid() || bid == auction.buyoutPrice)) {
			player.setGold(player.getGold() - bid);// 扣钱
			try {

				// 扣除金币
				Log.info(Log.ITEM, player.getUserid() + "#$" + player.getRoleid() + "#$45#$" + auction.item.getName() + "#$" + Math.abs(auction.item.getStorage()) + "#$"
						+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + TianLongServer.srvId + "#$" + bid + "#$" + auction.item.getTid() + "#$" + auction.item.getUid());
			} catch (Exception e) {
				e.printStackTrace();
			}
			auction.modifying = true;
			AuctionHouse.INSTANCE.modifying++;

			prepareBody();
			SubModules.fillAttributes(player);
			putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);

			if (bid == auction.buyoutPrice)// 一口价
				CommonParser.getInstance().postTask(DbConst.Auction_Delete, this, new AuctionDeleter(auctionId));
			else
				CommonParser.getInstance().postTask(DbConst.Auction_Update, this, new AuctionUpdater(auction, bid));
		} else {
			replyMessage(player, 4, MsgID.Auction_Bidding_Resp, "该拍品价格已被其他玩家刷新，竞拍失败，请重新竞拍。");
		}
	}

	/**
	 * 拍卖确认
	 */
	public void auctionSaleConfirm() {
		DefaultMsgBody defaultMsgBody = MsgID.Auction_Sale_Confirm.getMsgBody();
		long uid = defaultMsgBody.getLong(1);
		byte num = defaultMsgBody.getByte(2);
		int reservePrice = defaultMsgBody.getInt(3);
		int buyoutPrice = defaultMsgBody.getInt(4);
		byte halfDays = defaultMsgBody.getByte(5);

		CommonPack bag = player.getStore().getBag();
		Item item = bag.getItem(uid);
		if(reservePrice>99999999||buyoutPrice>99999999||reservePrice<=0||buyoutPrice<=0){
			replyMessage(player, 3, MsgID.Auction_Sale_Confirm_Resp, "非法数值！");
			return;
		}
		
		if (item == null || Math.abs(item.getStorage()) < num || num < 0 || num > 20 || reservePrice > buyoutPrice || halfDays < 1 || halfDays > 4) {
			replyMessage(player, 1, MsgID.Auction_Sale_Confirm_Resp, "无效的请求！");
			return;
		}

		if (item.isBind()) {
			replyMessage(player, 2, MsgID.Auction_Sale_Confirm_Resp, "无法拍卖绑定的物品！");
			return;
		}

		if (item instanceof UniqueItem && ((UniqueItem) item).monitored()) {
			replyMessage(player, 3, MsgID.Auction_Sale_Confirm_Resp, "限时装备无法拍卖！");
			return;
		}
	
		
		int fee = item.getAuctionFee(halfDays);
		if (player.getGold() < fee)
			replyMessage(player, 4, MsgID.Auction_Sale_Confirm_Resp, "资金不足，无法拍卖该物品！/需要收取手续费" + LuaService.call4String("getValueDescribe", fee) + "！");
		else
			replyMessage(player, 0, MsgID.Auction_Sale_Confirm_Resp, "拍卖该物品需要收取手续费" + LuaService.call4String("getValueDescribe", fee) + "，是否继续？");
	}

	/**
	 * 拍卖物品
	 */
	private void auctionItemSale() {
		DefaultMsgBody defaultMsgBody = MsgID.Auction_Item_Sale.getMsgBody();
		long uid = defaultMsgBody.getLong(1);
		byte num = defaultMsgBody.getByte(2);
		int reservePrice = defaultMsgBody.getInt(3);
		int buyoutPrice = defaultMsgBody.getInt(4);
		byte halfDays = defaultMsgBody.getByte(5);

		CommonPack bag = player.getStore().getBag();
		Item itemCheck = bag.getItem(uid);
		
		if(reservePrice>99999999||buyoutPrice>99999999||reservePrice<=0||buyoutPrice<=0){
			replyMessage(player, 3, MsgID.Auction_Sale_Confirm_Resp, "非法数值！");
			return;
		}
		
		
		if (itemCheck == null || Math.abs(itemCheck.getStorage()) < num || num < 0 || num > 20 || reservePrice > buyoutPrice || halfDays < 1 || halfDays > 4) {
			replyMessage(player, 1, MsgID.Auction_Item_Sale_Resp, "无效的请求！");
			return;
		}

		if (itemCheck.isBind()) {
			replyMessage(player, 2, MsgID.Auction_Item_Sale_Resp, "无法拍卖绑定的物品！");
			return;
		}

		int fee = itemCheck.getAuctionFee(halfDays);
		if (player.getGold() < fee) {
			replyMessage(player, 3, MsgID.Auction_Item_Sale_Resp, "资金不足，无法拍卖该物品！/需要收取手续费" + LuaService.call4String("getValueDescribe", fee));
			return;
		}
		
		

		player.setGold(player.getGold() - fee);

		Item item = bag.pickItem(uid, num, true);

		prepareBody();
		SubModules.fillAttributes(player);
		LuaService.callLuaFunction("fillBagDel", uid, num);
		putShort((short) 0);
		sendMsg(player, MsgID.MsgID_Special_Train);

		Auction auction = new Auction(UID.next(), player.getRoleid(), item, Cardinality.INSTANCE.getMinute() + halfDays * 12 * 60, reservePrice, buyoutPrice);

		replyMessage(player, 0, MsgID.Auction_Item_Sale_Resp, "请求已提交，收取手续费" + LuaService.call4String("getValueDescribe", fee) + "！");

		AuctionHouse.INSTANCE.modifying++;
		CommonParser.getInstance().postTask(DbConst.Auction_Creat, this, new AuctionCreator(auction));
	}

	@Override
	public void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
		switch (eventID) {
		case Auction_Creat:
			startAuction((AuctionCreator) ds);
			break;
		case Auction_Delete:
			buyout((AuctionDeleter) ds);
			break;
		case Auction_Update:
			bidding((AuctionUpdater) ds);
			break;
		default:
			break;
		}
	}

	/**
	 * 完成拍卖
	 * 
	 * @param auctionDeleter
	 *            负责删除数据库中对应记录的对象
	 */
	public void buyout(final AuctionDeleter auctionDeleter) {
		Auction auction = AuctionHouse.INSTANCE.auctions.get(auctionDeleter.getAuctionId());
		if (auction.hasBid()) {// 之前有人出价，发送邮件提示失败并寄回金钱
			MailManager.getInstance().sendSysMail(auction.roleid, "系统邮件：竞拍失败", "已有人出价" + LuaService.call4String("getValueDescribe", auction.buyoutPrice) + "超过您，竞拍物品【" + auction.getTitle() + "】失败。",
					auction.bidding, null);
			try {

				// 退回金币
				Log.info(Log.ITEM, 0 + "#$" + auction.roleid + "#$44#$" + "" + "#$" + 0 + "#$" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + TianLongServer.srvId + "#$"
						+ auction.bidding + "#$" + 0 + "#$" + 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		replyMessage(player, 0, MsgID.Auction_Bidding_Resp, "物品【" + auction.getTitle() + "】竞拍成功！");

		MailManager.getInstance().sendSysMail(player.getRoleid(), "系统邮件：竞拍成功", "物品【" + auction.getTitle() + "】竞拍成功！消耗资金" + LuaService.call4String("getValueDescribe", auction.buyoutPrice), 0,
				auction.item);

		int fee = (int) (auction.buyoutPrice * 0.05);
		ServerMessage.sendSysPrompt(OnlineService.getOnline(auction.owner), "物品【" + auction.getTitle() + "】拍卖成功！");
		MailManager.getInstance().sendSysMail(auction.owner, "系统邮件：拍卖成功",
				"物品【" + auction.getTitle() + "】拍卖成功！/收取手续费：" + LuaService.call4String("getValueDescribe", fee) + "/获得资金" + LuaService.call4String("getValueDescribe", auction.buyoutPrice - fee),
				auction.buyoutPrice - fee, null);
		try {
			// 得到物品
			Log.info(Log.ITEM, player.getUserid() + "#$" + player.getRoleid() + "#$41#$" + auction.item.getName() + "#$" + auction.item.getStorage() + "#$"
					+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + TianLongServer.srvId + "#$" + "0" + "#$" + auction.item.getTid() + "#$" + auction.item.getUid());
			// 得到金币
			Log.info(Log.ITEM, 0 + "#$" + auction.owner + "#$42#$" + "" + "#$" + 0 + "#$" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + TianLongServer.srvId + "#$"
					+ (auction.buyoutPrice - fee) + "#$" + 0 + "#$" + 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		AuctionHouse.INSTANCE.removeAuction(auction);
		AuctionHouse.INSTANCE.modifying--;
	}

	/**
	 * 参与竞拍
	 * 
	 * @param auctionUpdater
	 *            负责更新数据库中对应记录的对象
	 */
	public void bidding(final AuctionUpdater auctionUpdater) {
		Auction auction = AuctionHouse.INSTANCE.auctions.get(auctionUpdater.getAuctionId());
		if (auction.hasBid()) {// 之前有人出价，发送邮件提示失败并寄回金钱
			MailManager.getInstance().sendSysMail(auction.roleid, "系统邮件：竞拍失败",
					"已有人出价" + LuaService.call4String("getValueDescribe", auctionUpdater.getBid()) + "超过您，竞拍物品【" + auction.getTitle() + "】失败。", auction.bidding, null);
			try {

				// 拍卖失败，返回金币
				Log.info(Log.ITEM,
						0 + "#$" + auction.roleid + "#$46#$" + auction.item.getName() + "#$" + auction.item.getStorage() + "#$" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$"
								+ TianLongServer.srvId + "#$" + auction.bidding + "#$" + auction.item.getTid() + "#$" + auction.item.getUid());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		auction.roleid = player.getRoleid();
		auction.bidding = auctionUpdater.getBid();
		auction.modifying = false;
		AuctionHouse.INSTANCE.modifying--;

		replyMessage(player, 0, MsgID.Auction_Bidding_Resp, "您成功竞拍了物品【" + auction.getTitle() + "】");
	}

	/**
	 * 开始拍卖
	 * 
	 * @param auctionCreator
	 *            负责创建拍卖记录的对象
	 */
	private void startAuction(final AuctionCreator auctionCreator) {
		Auction auction = auctionCreator.getAuction();
		AuctionHouse.INSTANCE.addAuction(auction);

		AuctionHouse.INSTANCE.modifying--;

		ServerMessage.sendSysPrompt(player, "物品【" + auction.getTitle() + "】的拍卖已开始～");
		// 卖家失去物品
		Log.info(Log.ITEM, 0 + "#$" + auction.owner + "#$43#$" + auction.item.getName() + "#$" + auction.item.getStorage() + "#$" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
				+ "#$" + TianLongServer.srvId + "#$" + "" + "#$" + auction.item.getTid() + "#$" + auction.item.getUid());
	}

}
