package com.joyveb.tlol.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用于游戏逻辑在持久层和对象数据间进行转换
 */
public abstract class DataStruct {

	public static enum CommandType {
		SELECT, INSERT, UPDATE, DELETE,

		UNKNOW;
	}

	public CommandType type = CommandType.UNKNOW;

	private Object[] sceneInfo;

	/**
	 * 从ResultSet中取得数据
	 * 
	 * @param rs 
	 * @throws java.sql.SQLException \
	 * @return 是否成功从ResultSet中取得数据
	 */
	public boolean readFromRs(final ResultSet rs) throws SQLException {
		return false;
	}

	/**
	 * 生成insert语句
	 * 
	 * @return 生成insert语句
	 */
	public PreSql getPreSql_insert() {
		return null;
	}

	/**
	 * 生成delete语句
	 * 
	 * @return 生成delete语句
	 */
	public PreSql getPreSql_delete() {
		return null;
	}

	/**
	 * 生成query sql语句
	 * 
	 * @return 生成query sql语句
	 */
	public PreSql getPreSql_query() {
		return null;
	}

	/**
	 * 生成update sql语句
	 * 
	 * @return 生成update sql语句
	 */
	public PreSql getPreSql_update() {
		return null;
	}

	public final void setSceneInfo(final Object[] sceneInfo) {
		this.sceneInfo = sceneInfo;
	}

	public final Object[] getSceneInfo() {
		return sceneInfo;
	}

}
