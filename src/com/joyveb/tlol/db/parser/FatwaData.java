package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.fatwa.Fatwa;
import com.joyveb.tlol.fatwa.FatwaTable;

public class FatwaData extends DataStruct {
	private int promulgatorId;
	public String promulgatorName ;
	private int roleIdByFatwa;
	private long timeOut;
	
	public FatwaData(int promulgatorId,String promulgatorName, int roleIdByFatwa,long timeOut){
		this.promulgatorId = promulgatorId;
		this.promulgatorName = promulgatorName;
		this.roleIdByFatwa = roleIdByFatwa;
		this.timeOut = timeOut;
	}
	
	public FatwaData(){};
	
	/**
	 * @function 追杀令添加
	 * @author LuoSR
	 * @return
	 * @date 2011-12-21
	 */
	@Override
	public PreSql getPreSql_insert() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "insert into tbl_tianlong_fatwa"
				+ "_"
				+ TianLongServer.srvId
				+ "(fatwaId, promulgatorId, promulgatorName, roleIdByFatwa, timeOut ) "
				+ "values (seq_tianlong_fatwa.nextval, ?, ?, ?, ?)";
		preSql.parameter.add(promulgatorId);
		preSql.parameter.add(promulgatorName);
		preSql.parameter.add(roleIdByFatwa);
		preSql.parameter.add(timeOut);
		return preSql;
	}

	/**
	 * @function 删除追杀令
	 * @author LuoSR
	 * @return
	 * @date 2011-12-21
	 */
	public PreSql getPreSql_delete() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "delete from tbl_tianlong_fatwa"
				+ "_"
				+ TianLongServer.srvId;
		return preSql;
	}
	
	/**
	 * @function 查询追杀令信息
	 * @author LuoSR
	 * @return
	 * @date 2011-12-21
	 */
	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select promulgatorId, promulgatorName, roleIdByFatwa, timeOut from tbl_tianlong_fatwa" 
				+ "_"
				+ TianLongServer.srvId;
		return preSql;
	}
	
	/**
	 * @function 从ResultSet中取得数据
	 * @author LuoSR
	 * @param rs
	 * @return 
	 * @throws SQLException
	 * @date 2011-12-21
	 */
	@Override
	public final boolean readFromRs(final ResultSet rs) throws SQLException {		
		while (rs != null && rs.next()) {
			setPromulgatorId(rs.getInt(1));
			setPromulgatorName(rs.getString(2));
			setRoleIdByFatwa(rs.getInt(3));
			setTimeOut(rs.getLong(4));
		}
		 FatwaTable.INSTANCE.getFatwas().put(getRoleIdByFatwa(), this.toFatwa());
		return true;
	}
	
	public Fatwa toFatwa(){
		Fatwa  fatwa = new Fatwa();
		fatwa.setPromulgatorId(this.getPromulgatorId());
		fatwa.setPromulgatorName(this.getPromulgatorName());
		fatwa.setRoleIdByFatwa(this.getRoleIdByFatwa());
		fatwa.setTimeOut(this.getTimeOut());
		return fatwa;
	}

	public int getPromulgatorId() {
		return promulgatorId;
	}

	public void setPromulgatorId(int promulgatorId) {
		this.promulgatorId = promulgatorId;
	}

	public int getRoleIdByFatwa() {
		return roleIdByFatwa;
	}

	public void setRoleIdByFatwa(int roleIdByFatwa) {
		this.roleIdByFatwa = roleIdByFatwa;
	}

	public long getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}

	public String getPromulgatorName() {
		return promulgatorName;
	}

	public void setPromulgatorName(String promulgatorName) {
		this.promulgatorName = promulgatorName;
	}
	
}
