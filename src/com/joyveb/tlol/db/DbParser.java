package com.joyveb.tlol.db;

import java.sql.ResultSet;

import com.joyveb.tlol.db.parser.DbConst;

/**
 * DB解释器，由各模块自已完成转换。
 * 
 * 主要实现两种： 1.数据对象DataStruct到SQL string 转换 2.从ResultSet到DataStruct的转换
 * 
 */
public abstract class DbParser {

	/**
	 * 把struct翻译为sql语句,由子类来实现
	 * 
	 * @param eventID
	 * @param data
	 * @return 翻译后sql语句
	 */
	public abstract PreSql struct2db(DbConst eventID, DataStruct data);

	/**
	 * 把resultset翻译为struct,由子类来实现
	 * 
	 * @param eventID
	 * @param rs
	 * @param ds
	 */
	public abstract void db2struct(DbConst eventID, ResultSet rs, DataStruct ds);

	/**
	 * 当TaskMan操作完成时回调此方法
	 * 
	 * @param sqlTask
	 */
	final void onBack(final DbTask sqlTask) {
		DataHandler handler = sqlTask.getDataHandler();
		if(handler != null) {
			// 把sqlTask中的输入数据结构传入，修改其数据作为返回
			db2struct(sqlTask.getEvent_id(), sqlTask.getResultSet(), sqlTask.getInputData());

			// 为了防止并发问题这里将任务返回给taskmanager等待主线程去处理
			// 这里可以考虑做下区分 分为可以并发的任务 以及 需要同步的任务 以提高效率
			if(!sqlTask.isAsync()) {
				TaskManager.getInstance().addHandledTask(sqlTask);
			}else {
				handler.handle(sqlTask.getEvent_id(), sqlTask.isSucess(), sqlTask.getInputData());
			}

		}
	}

	/**
	 * 由应用层传入parser的事务
	 * 
	 * @param eventID 事务ID
	 * @param handler
	 * @param data 此数据将在DB完成后，返回此数据给上层逻辑
	 * @return 返回此数据给上层逻辑
	 */
	public final boolean postTask(final DbConst eventID, final DataHandler handler, final DataStruct data) {
		PreSql sqlst = struct2db(eventID, data);
		DbTask sqlTask = new DbTask(eventID, this, sqlst, handler);
		sqlTask.setInputData(data);
		return TaskManager.getInstance().addTask(sqlTask);
	}

	/**
	 * 由应用层传入parser的事务
	 * 
	 * @param eventID 事务ID
	 * @param handler
	 * @param data 此数据将在DB完成后，返回此数据给上层逻辑
	 * @param isAsync 是否为异步任务
	 * @return 由应用层传入parser的事务
	 */
	public final boolean postTask(final DbConst eventID, final DataHandler handler, final DataStruct data,
			final boolean isAsync) {
		PreSql sqlst = struct2db(eventID, data);
		DbTask sqlTask = new DbTask(eventID, this, sqlst, handler);
		sqlTask.setAsync(isAsync);
		sqlTask.setInputData(data);
		return TaskManager.getInstance().addTask(sqlTask);
	}
}
