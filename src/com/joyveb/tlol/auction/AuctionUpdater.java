package com.joyveb.tlol.auction;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class AuctionUpdater extends DataStruct {
	
	private PreSql preSql = new PreSql();

	private final long auctionId;
	private final int bid;
	
	public AuctionUpdater(final Auction auction, int bid) {
		this.auctionId = auction.serial;
		this.bid = bid;
		
		preSql.sqlstr = "update tbl_tlol_auction_" + TianLongServer.srvId
				+ " t set nbidding = ?, nroleid = ? where t.nid = ?";
		preSql.parameter.add(auction.bidding);
		preSql.parameter.add(auction.roleid);
		preSql.parameter.add(auction.serial);
	}

	public final PreSql getPreSql_update() {
		return preSql;
	}

	public long getAuctionId() {
		return auctionId;
	}

	public int getBid() {
		return bid;
	}

}
