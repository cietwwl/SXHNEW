package com.joyveb.tlol.db;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import com.joyveb.tlol.util.Log;

public class TaskProcessor extends Thread {

	public static final long PERIOD_WAIT = 100; // 无任务时，周期性探测是否有新任务到达

	public static final int REACTIVE_PERIOD_COUNT = 600;
	/**
	 * 管理器
	 */
	private TaskManager taskManager = null;
	/**
	 * 关机
	 */
	private volatile boolean shutdown = false;

	// private List<Clob> clobDataList = new ArrayList<Clob>();

	/**
	 * 
	 * @param tm 
	 */
	public TaskProcessor(final TaskManager tm) {
		taskManager = tm;
	}

	/**
	 * 线程体
	 */
	public final void run() {
		/**
		 * DB连接
		 */
		Connection con = null;
		PreparedStatement stat = null;
		ResultSet rs = null;
		while (!shutdown) {
			long startTime = 0, spent = 0;
			DbTask sqlTask = taskManager.getNextTask();
			if (sqlTask != null) {
				startTime = System.currentTimeMillis();
				// 取任务
				DbParser dbParser = sqlTask.getParser();
				PreSql psql = sqlTask.getSql();
				try {
					do {
						try {
							con = taskManager.getConnection();
						} catch (SQLException sQLException) {
							Log.error(Log.STDOUT, "TaskProcessor.run",
									sQLException);
							// BasicService.formatLogError("TaskProcessor.run",
							// sQLException);
						}
						if (con == null) {
							Thread.sleep(10);
							Log.error(Log.STDOUT, "TaskProcessor.run",
							"can not get db connection!!");
							// BasicService.formatLogError("TaskProcessor.run",
							// "can not get db connection!!");
						}
					} while (con == null);

					stat = con.prepareStatement(psql.sqlstr);
					fillParameter(stat, psql.parameter);
					// 根据SQL不同进行不同操作

					if (stat.execute()) { // 如果有记录，则设置记录集
						rs = stat.getResultSet();
					}

					sqlTask.setResultSet(rs);
					sqlTask.setSucess(true);

					// 回调
					if (dbParser != null) {
						dbParser.onBack(sqlTask);
					}
					stat.close();
				} catch (Exception sqle) {

					StringBuilder parms = new StringBuilder();
					for (int i = 0; i < psql.parameter.size(); i++) {
						parms.append(psql.parameter.get(i) + ", ");
					}

					Log.error(Log.ERROR, "TaskProcessor.run sql str : "
							+ psql.sqlstr + "sql params: " + parms);
					Log.error(Log.ERROR, "TaskProcessor.run sql params: "
							+ parms);
					Log.error(Log.ERROR, "TaskProcessor.run", sqle);
					Log.error(Log.ERROR, "TaskProcessor.run dbtask id: ",
							sqlTask.getEvent_id());

					sqlTask.setResultSet(null);
					sqlTask.setSucess(false);

					// 失败回调
					if (dbParser != null) {
						dbParser.onBack(sqlTask);
					}

				} finally {
					
					try {
						if (rs != null) {
							rs.close();
						} 
						if (stat != null) {
							stat.close();

						} 
						if (con != null) {
							con.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				// 计算时间
				spent = System.currentTimeMillis() - startTime;

				Log.DBPerformance("TaskProcessor.run", "DB sqlStr: ##"
						+ psql.sqlstr + "##", startTime);
			} else {
				if (spent < PERIOD_WAIT) {
					synchronized (this) {
						try {
							wait(PERIOD_WAIT);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

		}
		Log.info(Log.STDOUT, "TaskProcessor", "DB任务处理器已关闭:" + getName());
		// BasicService.formatLogInfo("TaskProcessor", "DB任务处理器已关闭:" +
		// getName());
	}

	public final void shutdown() {
		shutdown = true;
	}

	// private List<Clob> getCLOBParas(List<Object> para){
	// List<Clob> clobList = new ArrayList<Clob>();
	// for (Object o : para) {
	// if(o instanceof ClobStringData){
	// ClobStringData cds = (ClobStringData)o;
	// if(cds.getClobData() != null){
	// clobList.add(cds.getClobData());
	// }
	// }
	// }
	//
	// return clobList;
	// }

	/**
	 * 设置参数
	 * 
	 * @param pst 
	 * @param para 
	 * @throws SQLException
	 */
	private void fillParameter(final PreparedStatement pst, final List<Object> para)
			throws SQLException {
		int i = 1;
		for (Object o : para) {
			if (o instanceof Integer) {
				pst.setInt(i, ((Integer) o).intValue());
			} else if (o instanceof Short) {
				pst.setShort(i, ((Short) o).shortValue());
			} else if (o instanceof Boolean) {
				pst.setByte(i, (byte) (((Boolean) o) ? 1 : 0));
			} else if (o instanceof Byte) {
				pst.setByte(i, ((Byte) o).byteValue());
			} else if (o instanceof Long) {
				pst.setLong(i, ((Long) o).longValue());
			} else if (o instanceof String) {
				pst.setString(i, o.toString());
			} else if (o instanceof byte[]) {
				byte[] ba = (byte[]) o;
				ByteArrayInputStream bais = new ByteArrayInputStream(ba);
				pst.setBinaryStream(i, bais, ba.length);
			} else if (o instanceof Timestamp) {
				pst.setTimestamp(i, (Timestamp) o);
			} else if (o == null) {
				pst.setObject(i, null);
			}

			i++;
		}
	}
}
