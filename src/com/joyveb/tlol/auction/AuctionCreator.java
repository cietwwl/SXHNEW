package com.joyveb.tlol.auction;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class AuctionCreator extends DataStruct {
	
	private PreSql preSql = new PreSql();

	private final Auction auction;
	
	public AuctionCreator(final Auction auction) {
		this.auction = auction;
		preSql.sqlstr = "insert into tbl_tlol_auction_" + TianLongServer.srvId
				+ " (nid, nowner, ntimeOut, nreservePrice, nbuyoutPrice, nbidding, "
				+ "nroleid, ntid, nuid, nstorage, sitemStr) "
				+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		preSql.parameter.add(auction.serial);
		preSql.parameter.add(auction.owner);
		preSql.parameter.add(auction.timeOut);
		preSql.parameter.add(auction.reservePrice);
		preSql.parameter.add(auction.buyoutPrice);
		preSql.parameter.add(auction.bidding);
		preSql.parameter.add(auction.roleid);
		preSql.parameter.add(auction.item.getTid());
		preSql.parameter.add(auction.item.getUid());
		preSql.parameter.add(auction.item.getStorage());
		preSql.parameter.add("#" + Auction.VERSION + auction.item.serialize());
	}
	
	/**
	 * @return 返回insert语句
	 */
	public final PreSql getPreSql_insert() {
		return preSql;
	}

	public Auction getAuction() {
		return auction;
	}

}
