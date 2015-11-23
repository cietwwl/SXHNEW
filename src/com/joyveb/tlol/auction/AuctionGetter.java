package com.joyveb.tlol.auction;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.util.Log;

public class AuctionGetter extends DataStruct {

	@Override
	public final boolean readFromRs(final ResultSet resultSet) throws SQLException {
		while(resultSet.next()) {
			long id = resultSet.getLong(1);
			int owner = resultSet.getInt(2);
			int timeOut = resultSet.getInt(3);
			int reservePrice = resultSet.getInt(4);
			int buyoutPrice = resultSet.getInt(5);
			int bidding = resultSet.getInt(6);
			int roleid = resultSet.getInt(7);
			int tid = resultSet.getInt(8);
			long uid = resultSet.getLong(9);
			byte storage = resultSet.getByte(10);
			String itemStr = resultSet.getString(11);

			if(!LuaService.getBool(Item.LUA_CONTAINER, tid)) {
				CommonParser.getInstance().postTask(DbConst.Auction_Delete, null, new AuctionDeleter(id));
				Log.error(Log.ERROR, "拍卖的物品id已不存在！");
				Log.error(Log.ERROR, "owner = " + owner + " timeOut = " + timeOut + " reservePrice = " + reservePrice
						+ " buyoutPrice = " + buyoutPrice + " bidding = " + bidding + " roleid = " + roleid
						+ " tid = " + tid + " uid = " + uid + " storage = " + storage + " itemStr = 【" + itemStr + "】");
				continue;
			}
			
			Item item = null;
			try {
				item = Item.readItem(itemStr);
			}catch(Exception e) {
				Log.error(Log.ERROR, "AuctionGetter.readFromRs", "拍卖反序列化失败！id = " + id, e);
				continue;
			}

			Auction auction = new Auction(id, owner, item, timeOut, reservePrice, buyoutPrice);
			auction.bidding = bidding;
			auction.roleid = roleid;

			AuctionHouse.INSTANCE.addAuction(auction);
		}

		return true;
	}

	@Override
	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select nid, nowner, ntimeOut, nreservePrice, nbuyoutPrice, nbidding, "
				+ "nroleid, ntid, nuid, nstorage, sitemStr from tbl_tlol_auction_" + TianLongServer.srvId;
		return preSql;
	}

}
