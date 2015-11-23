package com.joyveb.tlol.auction;

import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.util.Cardinality;

/**
 * 拍卖
 * @author Sid
 */
public final class Auction {
	/**
	 * 序列化版本
	 */
	public static final int VERSION = 1;
	
	/**
	 * 每页显示上限
	 */
	public static final byte PER_PAGE = 30;
	
	/**
	 * 拍卖号
	 */
	final long serial;
	
	/**
	 * 拍品主人
	 */
	final int owner;
	
	/**
	 * 拍卖的物品
	 */
	final Item item;
	
	/**
	 * 跟踪类别
	 */
	final int categoryCode;
	
	/**
	 * 到期时间
	 */
	final int timeOut;
	
	/**
	 * 拍卖底价
	 */
	final int reservePrice;
	
	/**
	 * 一口价
	 */
	final int buyoutPrice;
	
	/**
	 * 当前出价
	 */
	int bidding;
	
	/**
	 * 最后出价玩家
	 */
	int roleid;
	
	/**
	 * 正在修改数据库
	 */
	boolean modifying;
	
	/**
	 * @param serial 拍卖号
	 * @param owner 拍品主人
	 * @param item 拍卖的物品
	 * @param timeOut 到期时间
	 * @param reservePrice 拍卖底价
	 * @param buyoutPrice 一口价
	 */
	public Auction(final long serial, final int owner, final Item item, 
			final int timeOut, final int reservePrice, final int buyoutPrice) {
		if(item == null || reservePrice > buyoutPrice)
			throw new IllegalArgumentException();
		
		this.serial = serial;
		this.owner = owner;
		this.item = item;
		this.timeOut = timeOut;
		this.reservePrice = reservePrice;
		this.buyoutPrice = buyoutPrice;
		
		this.categoryCode = item.getAuctionCategory() | (1  << AutionType.Total.ordinal());
		this.roleid = owner;
	}
	
	/**
	 * @param curMin 当前时间
	 * @return 此拍品的时间描述
	 */
	public String getTimeDes(final int curMin) {
		if(timeOut - curMin >= 12 * 60) 
			return " 剩余时间：非常长";
		else if(timeOut - curMin >= 8 * 60)
			return " 剩余时间：长";
		else if(timeOut - curMin >= 2 * 60)
			return " 剩余时间：短";
		else
			return " 剩余时间：非常短";
	}
	
	/**
	 * @return 是否有人竞拍
	 */
	public boolean hasBid() {
		return bidding != 0;
	}
	
	/**
	 * @return 下次竞拍金额
	 */
	public int nextBid() {
		if(bidding == 0)
			return this.reservePrice;
		
		int bid = bidding + (int) (reservePrice * 0.05);
		if(bid > buyoutPrice)
			return buyoutPrice;
		
		return bid;
	}
	
	/**
	 * @return 拍品
	 */
	public String getTitle() {
		return item.getName() + "×" + item.getStorage();
	}
	
	/**
	 * @return 简要介绍
	 */
	public String info() {
		StringBuilder builder = new StringBuilder();
		builder.append(" 拍卖号 ").append(serial).append(" 拍品主人：").append(owner).append(" 物品：").append(item.getName()).append(" 到期时间: ").append(this.timeOut).append(" 当前时间: ").append(Cardinality.INSTANCE.getMinute());
		
		builder.append(" 跟踪类别：");
		
		for(AutionType autionType : AutionType.values()) 
			if((categoryCode >> autionType.ordinal() & 1) == 1) 
				builder.append(autionType).append(" ");
		
		return builder.toString();
	}

	/**
	 * @return 拍卖详细
	 */
	public String detail() {
		StringBuilder builder = new StringBuilder();
		builder.append("\n拍卖号 ").append(serial).append(" 拍品主人 ").append(owner);
		builder.append("\n拍卖底价 ").append(reservePrice).append(" 一口价 ").append(buyoutPrice);
		builder.append("\n当前出价 ").append(bidding).append(" 出价玩家 ").append(roleid);
		
		int curMin = Cardinality.INSTANCE.getMinute();
		builder.append("\n到期时间 ").append(timeOut).append(" 剩余 ").append(timeOut - curMin).append(" 分钟  ")
				.append(getTimeDes(curMin));
		
		builder.append("\n拍买状态  modifying = ").append(modifying);
		
		builder.append("\n物品 tid = ").append(item.getTid()).append(" uid = ").append(item.getUid());
		builder.append("\n数量").append(item.getStorage()).append("\n");
		builder.append(item.getDescribe().replaceAll("/", "\n"));
		
		return builder.toString();
	}
}
