package com.joyveb.tlol.db;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.joyveb.tlol.util.Log;

/**
 * 
 * 数据库代理，负责处理parser过来的任务请求，无阻塞添加任务 通过传入的SqlTask进行操作，并把操作结果填入此SqlTask，并回调parser
 * 
 */
public final class TaskManager {

	/** 连接池配置文件 */
	private ArrayList<TaskProcessor> processors = new ArrayList<TaskProcessor>(); // 线程容器
	// DataSource dataSource;
	private BoneCP connectionPool;

	public static int dbTaskCount = 0;
	/**
	 * 实例
	 */
	private static TaskManager dbManager = new TaskManager();;
	/**
	 * 任务队列
	 */
	private ConcurrentLinkedQueue<DbTask> queue = new ConcurrentLinkedQueue<DbTask>();

	private ConcurrentLinkedQueue<DbTask> handledTask = new ConcurrentLinkedQueue<DbTask>();

	/**
	 * 私有构造
	 */
	private TaskManager() {
	}

	public void init() {
		try {
			Properties props = new Properties();
			InputStream in = new BufferedInputStream(new FileInputStream(
					"tlol.properties"));
			props.load(in);
			in.close();

			String oracleConnStr = props.getProperty("oracleConnStr");
			String dbuser = props.getProperty("dbuser");
			String dbpwd = props.getProperty("dbpwd");

			int partitionCount = Integer.parseInt(props
					.getProperty("connPoolParCount"));
			int maxConnCount = Integer.parseInt(props
					.getProperty("connPoolMaxConnPerPar"));
			int minConnCount = Integer.parseInt(props
					.getProperty("connPoolMinConnPerPar"));

			if (oracleConnStr != null && dbuser != null && dbpwd != null) {
				try {
					BoneCPConfig config = new BoneCPConfig(); // create a new
																// configuration
																// object
					config.setJdbcUrl(oracleConnStr); // set the JDBC url
					config.setUsername(dbuser); // set the username
					config.setPassword(dbpwd); // set the password
					config.setPartitionCount(partitionCount);
					config.setMinConnectionsPerPartition(minConnCount);
					config.setMaxConnectionsPerPartition(maxConnCount);
					connectionPool = new BoneCP(config);

					Log.info(
							Log.STDOUT,
							"BoneCP maxPerPar "
									+ config.getMaxConnectionsPerPartition()
									+ "minPerPar "
									+ config.getMinConnectionsPerPartition()
									+ "parCount " + config.getPartitionCount()
									+ "acquireIncrement "
									+ config.getAcquireIncrement());
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
			} else
				Log.error(Log.STDOUT, "TianLongServer",
						"Fail to get db properties");

			int TASK_THREAD_COUNT = 20;

			for (int i = 0; i < TASK_THREAD_COUNT; i++) {
				TaskProcessor task = new TaskProcessor(this);
				task.setName("DbTaskProcessor" + i);
				processors.add(task);
				task.start();
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}

	}

	/**
	 * 取得实例
	 * 
	 * @return 取得实例
	 */
	public static TaskManager getInstance() {
		return dbManager;
	}

	/**
	 * 添加任务
	 * 
	 * @param st 
	 * @return 是否添加任务成功
	 */
	public boolean addTask(final DbTask st) {
		dbTaskCount++;
		queue.offer(st);
		return true;
	}

	public void addHandledTask(final DbTask st) {
		handledTask.offer(st);
	}

	public void processHandledTask() {
		while (!handledTask.isEmpty()) {
			DbTask task = handledTask.poll();
			try {
				task.getDataHandler().handle(task.getEvent_id(),
						task.isSucess(), task.getInputData());
			} catch (Exception e) {
				Log.error(Log.ERROR, e);
			}
		}
	}

	/**
	 * 停机
	 */
	public void shutdown() {

		// 如果系统中还有数据尚未存完，则等候其存储完成
		while (!queue.isEmpty()) {
			try {
				Thread.sleep(500);
			} catch (Exception interruptedException) {
				interruptedException.printStackTrace();
			}
		}
		// 关掉所有线程
		for (TaskProcessor tp : processors) {
			tp.shutdown();
		}

		closePooledDB();
	}

	/**
	 * 取任务
	 * 
	 * @return 取任务
	 */
	public DbTask getNextTask() {
		if (dbTaskCount > 0)
			dbTaskCount = dbTaskCount - 1;
		return queue.poll();
	}

	/**
	 * 取得当前队列长度
	 * 
	 * @return 取得当前队列长度
	 */
	public boolean isQueueEmpty() {
		return queue.isEmpty();
	}

	/**
	 * 从池中分配连接
	 * 
	 * @return 从池中分配连接
	 * @throws SQLException
	 */
	public synchronized Connection getConnection() throws SQLException {
		return connectionPool.getConnection();
		// return dataSource.getConnection();
	}

	public long getThreadCount() {
		return processors.size();
	}

	public void closePooledDB() {
		connectionPool.shutdown();

		// try {
		// DataSources.destroy(dataSource);
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}
}
