package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class FeeData extends DataStruct {
	
	 private List<FeeData> feeList = new ArrayList<FeeData>() ;
	 private int  version; 
	 private String feeName; 
	 private short feeKey; 
	 private String feeTip; 
	 private String feeRate; 
	 private String feeHelp; 
	 private String feeSign;
	 public byte feeMoneyNum;
	 public String feeMoneys;
	@Override
	public final boolean readFromRs(final ResultSet rs) throws SQLException {
		while (rs.next()) {
			
			FeeData f = new FeeData();
			f.setVersion(rs.getInt(1)) ;
			f.setFeeName( rs.getString(2));
			f.setFeeKey(rs.getShort(3));
			f.setFeeTip(rs.getString(4)); 
			f.setFeeRate(rs.getString(5));
			f.setFeeHelp(rs.getString(6));
			f.setFeeSign(rs.getString(7));
			f.setFeeMoneyNum(rs.getByte(8));
			f.setFeeMoneys(rs.getString(9));
			feeList.add(f);
		}

		return true;
	}
	
	
	@Override
	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select * from tbl_tianlong_fee";
		return preSql;
	}


	public List<FeeData> getFeeList() {
		return feeList;
	}


	public void setFeeList(List<FeeData> feeList) {
		this.feeList = feeList;
	}


	public int getVersion() {
		return version;
	}


	public void setVersion(int version) {
		this.version = version;
	}


	public String getFeeName() {
		return feeName;
	}


	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}


	public short getFeeKey() {
		return feeKey;
	}


	public void setFeeKey(short feeKey) {
		this.feeKey = feeKey;
	}


	public String getFeeTip() {
		return feeTip;
	}


	public void setFeeTip(String feeTip) {
		this.feeTip = feeTip;
	}


	public String getFeeRate() {
		return feeRate;
	}


	public void setFeeRate(String feeRate) {
		this.feeRate = feeRate;
	}


	public String getFeeHelp() {
		return feeHelp;
	}


	public void setFeeHelp(String feeHelp) {
		this.feeHelp = feeHelp;
	}


	public String getFeeSign() {
		return feeSign;
	}


	public void setFeeSign(String feeSign) {
		this.feeSign = feeSign;
	}


	public byte getFeeMoneyNum() {
		return feeMoneyNum;
	}


	public void setFeeMoneyNum(byte feeMoneyNum) {
		this.feeMoneyNum = feeMoneyNum;
	}


	public void setFeeMoneys(String feeMoneys) {
		this.feeMoneys = feeMoneys;
	}


	public String getFeeMoneys() {
		return feeMoneys;
	}





		
}
