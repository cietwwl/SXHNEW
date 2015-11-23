package com.joyveb.tlol.auction;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class AuctionDeleter extends DataStruct {

	private PreSql preSql = new PreSql();

	private long auctionId;

	public AuctionDeleter(final long auctionId) {
		this.auctionId = auctionId;

		preSql.sqlstr = "delete from tbl_tlol_auction_" + TianLongServer.srvId + " where nid = ?";
		preSql.parameter.add(auctionId);
	}

	public final PreSql getPreSql_delete() {
		return preSql;
	}

	public void setAuctionId(long auctionId) {
		this.auctionId = auctionId;
	}

	public long getAuctionId() {
		return auctionId;
	}
	
}
