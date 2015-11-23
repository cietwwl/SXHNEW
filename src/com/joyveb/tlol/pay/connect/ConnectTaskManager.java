package com.joyveb.tlol.pay.connect;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.joyveb.tlol.pay.util.Constants;
import com.joyveb.tlol.util.Log;

/**
 * 
 * 
 * 
 */
public class ConnectTaskManager {

	/** 连接池配置文�? */
	ArrayList<ConnectTaskProcessor> processors = new ArrayList<ConnectTaskProcessor>();// 接受线程容器
	ArrayList<CallbackProcessor> callbackprocessors = new ArrayList<CallbackProcessor>();// 接受信息线程容器(神州付)
	ArrayList<WapCallbackProcessor> wapcallbackprocessors = new ArrayList<WapCallbackProcessor>();// 接受信息线程容器(wap)
	ArrayList<FailConnectTaskProcessor> failprocessors = new ArrayList<FailConnectTaskProcessor>();// 失败信息线程容器
	ArrayList<ConnectSubTaskProcessor> subprocessors = new ArrayList<ConnectSubTaskProcessor>();// 接受线程容器
	ArrayList<FailConnectSubTaskProcessor> failsubprocessors = new ArrayList<FailConnectSubTaskProcessor>();// 接受线程容器
	ArrayList<ConnectAffordTaskProcessor> payprocessors = new ArrayList<ConnectAffordTaskProcessor>();// 接受线程容器（元宝）
	ArrayList<FailConnectAffordTaskProcessor> failpayprocessors = new ArrayList<FailConnectAffordTaskProcessor>();// 接受线程容器（元宝）
	
	ArrayList<ConnectSelectTaskProcessor> selectProcessors = new ArrayList<ConnectSelectTaskProcessor>();// 接受线程容器（查询元宝）
	ArrayList<FailConnectSelectTaskProcessor> failSelectProcessors = new ArrayList<FailConnectSelectTaskProcessor>();// 接受线程容器（查询元宝）
	
	private long debug_taskCount = 0;// 总任务数
	private long fail_taskCount = 0;// 总任务数

	private long subdebug_taskCount = 0;// 总任务数
	private long subfail_taskCount = 0;// 总任务数

	private long afforddebug_taskCount = 0;// 总任务数（元宝）
	private long affordfail_taskCount = 0;// 总任务数（元宝）
	
	private long selectDebug_taskCount = 0;// 总任务数（查询元宝）
	private long selectFail_taskCount = 0;// 总任务数（查询元宝）

	private long debug_taskSpentSum = 0;
	/**
	 * 实例
	 */
	private static ConnectTaskManager dbManager = null;
	/**
	 * 充值任务队列
	 */
	private ConcurrentLinkedQueue<ConnectTask> queue = new ConcurrentLinkedQueue<ConnectTask>();

	
	/**
	 * 失败任务队列
	 */
	private ConcurrentLinkedQueue<ConnectTask> failqueue = new ConcurrentLinkedQueue<ConnectTask>();
	/**
	 * 需要处理的任务队列(神州付)
	 */
	private ConcurrentLinkedQueue<ConnectTask> handledTask = new ConcurrentLinkedQueue<ConnectTask>();
	/**
	 * 充值任务
	 */
	private ConcurrentHashMap<Integer, ConnectTask> waittasks = new ConcurrentHashMap<Integer, ConnectTask>();

	
	/**
	 * 扣费任务队列
	 */
	private ConcurrentLinkedQueue<ConnectTask> subqueue = new ConcurrentLinkedQueue<ConnectTask>();
	/**
	 * 失败扣费任务队列
	 */
	private ConcurrentLinkedQueue<ConnectTask> subfailqueue = new ConcurrentLinkedQueue<ConnectTask>();
	/**
	 * 待处理的扣费任务队列
	 */
	private ConcurrentLinkedQueue<ConnectTask> subhandledTask = new ConcurrentLinkedQueue<ConnectTask>();

	
	/**
	 * 充值（元宝）任务队列
	 */
	private ConcurrentLinkedQueue<ConnectTask> affordqueue = new ConcurrentLinkedQueue<ConnectTask>();
	/**
	 * 失败充值（元宝）任务队列
	 */
	private ConcurrentLinkedQueue<ConnectTask> affordfailqueue = new ConcurrentLinkedQueue<ConnectTask>();
	/**
	 * 待处理的充值（元宝）任务队列
	 */
	private ConcurrentLinkedQueue<ConnectTask> affordhandledTask = new ConcurrentLinkedQueue<ConnectTask>();
	
	
	/**
	 * 查询元宝任务队列
	 */
	private ConcurrentLinkedQueue<ConnectTask> selectQueue = new ConcurrentLinkedQueue<ConnectTask>();
	/**
	 * 失败查询元宝任务队列
	 */
	private ConcurrentLinkedQueue<ConnectTask> selectFailQueue = new ConcurrentLinkedQueue<ConnectTask>();
	/**
	 * 待处理的查询元宝任务队列
	 */
	private ConcurrentLinkedQueue<ConnectTask> selectHandledTask = new ConcurrentLinkedQueue<ConnectTask>();

	
	public String URL = null;// 支付地址
	
	public String ANDROIDURL = null; //android支付地址

	public String SUBRUL = null;// 扣费地址

	public String PAYPORT = null;// 支付端口

	public String WAPPAYPORT = null;// wap支付端口

	public String PAYRUL = null;// 游戏获得元宝端口
	
	public static String GAMEID;//游戏id，用作标识游戏充值
	
	private String P_BNum=null;//商户编号
	
	public String SELECTURL = null;//查询用户元宝数量端口

	public String getP_BNum() {
		return P_BNum;
	}

	public void setP_BNum(String num) {
		P_BNum = num;
	}

	public String getURL() {
		return URL;
	}
	
	public String getANDROIDURL() {
		return ANDROIDURL;
	}

	public String getSUBRUL() {
		return SUBRUL;
	}

	public String getPAYPORT() {
		return PAYPORT;
	}

	public String getWAPPAYPORT() {
		return WAPPAYPORT;
	}

	public String getPAYRUL() {
		return PAYRUL;
	}
	
	public String getSELECTURL() {
		return SELECTURL;
	}

	/**
	 * 私有构造方法
	 */
	private ConnectTaskManager() {
	}

	
	public ConnectTaskManager init() {
		try {

			ConnectTaskProcessor task = new ConnectTaskProcessor(this); // 处理充值任务
			task.setName("PayTaskProcessor");
			processors.add(task);
			task.start();
			return task.taskManager;

		} catch (Exception ioe) {
			ioe.printStackTrace();
			System.out.println("初始化充值任务失败");
			return getInstance();
		}
	}
	public void initFailTasks(ConnectTaskManager taskManager) {
		try {

			FailConnectTaskProcessor task = new FailConnectTaskProcessor(
					taskManager); // 处理失败充值任务
			task.setName("failPayTaskProcessor");
			failprocessors.add(task);
			task.start();

		} catch (Exception ioe) {
			ioe.printStackTrace();
			System.out.println("初始化失败任务失败");
		}
	}

	
	public void initCallBack(ConnectTaskManager taskManager) {
		try {
			CallbackProcessor callback = new CallbackProcessor(taskManager); // 处理充值返回消息
			callback.setName("CallBackProcessor");
			callbackprocessors.add(callback);
			callback.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("初始化监听任务失败");
		}
	}
	public void initWapCallBack(ConnectTaskManager taskManager) {
		try {
			WapCallbackProcessor wapcallback = new WapCallbackProcessor(
					taskManager); // 处理充值返回消息
			wapcallback.setName("CallBackProcessor");
			wapcallbackprocessors.add(wapcallback);
			wapcallback.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("初始化监听任务失败(wap)");
		}
	}

	
	public ConnectTaskManager subinit() {
		try {

			ConnectSubTaskProcessor subtask = new ConnectSubTaskProcessor(this); // 处理充值任务
			subtask.setName("SubTaskProcessor");
			subprocessors.add(subtask);
			subtask.start();
			return subtask.taskManager;

		} catch (Exception ioe) {
			ioe.printStackTrace();
			System.out.println("初始化扣费任务失败");
			return getInstance();

		}
	}
	public void initSubFailTasks(ConnectTaskManager taskManager) {
		try {

			FailConnectSubTaskProcessor subfailtask = new FailConnectSubTaskProcessor(
					taskManager); // 处理失败充值任务
			subfailtask.setName("failSubTaskProcessor");
			failsubprocessors.add(subfailtask);
			subfailtask.start();

		} catch (Exception ioe) {
			ioe.printStackTrace();
			System.out.println("初始化扣费失败任务失败");
		}
	}

	
	public ConnectTaskManager addinit() {
		try {

			ConnectAffordTaskProcessor addtask = new ConnectAffordTaskProcessor(
					this); // 处理充值（元宝）任务
			addtask.setName("PayTaskProcessor");
			payprocessors.add(addtask);
			addtask.start();
			return addtask.taskManager;

		} catch (Exception ioe) {
			ioe.printStackTrace();
			System.out.println("初始化充值（元宝）任务失败");
			return getInstance();

		}
	}
	public void initAddFailTasks(ConnectTaskManager taskManager) {
		try {

			FailConnectAffordTaskProcessor addfailtask = new FailConnectAffordTaskProcessor(
					taskManager); // 处理失败充值（元宝）任务
			addfailtask.setName("failPayTaskProcessor");
			failpayprocessors.add(addfailtask);
			addfailtask.start();

		} catch (Exception ioe) {
			ioe.printStackTrace();
			System.out.println("初始化充值（元宝）失败任务失败");
		}
	}
	
	
	
	public ConnectTaskManager selectinit() {
		try {

			ConnectSelectTaskProcessor addtask = new ConnectSelectTaskProcessor(
					this); // 处理查询元宝任务
			addtask.setName("SelectTaskProcessor");
			selectProcessors.add(addtask);
			addtask.start();
			return addtask.taskManager;

		} catch (Exception ioe) {
			ioe.printStackTrace();
			System.out.println("初始化查询元宝任务失败");
			return getInstance();

		}
	}
	public void initSelectFailTasks(ConnectTaskManager taskManager) {
		try {

			FailConnectSelectTaskProcessor addfailtask = new FailConnectSelectTaskProcessor(
					taskManager); // 处理失败查询元宝任务
			addfailtask.setName("FailSelectTaskProcessor");
			failSelectProcessors.add(addfailtask);
			addfailtask.start();

		} catch (Exception ioe) {
			ioe.printStackTrace();
			System.out.println("初始化查询元宝失败任务失败");
		}
	}

	
	
	public void startAllMoneyService() {
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					"payserver.properties"));
			props.load(in);
			in.close();
			URL = props.getProperty("szf");
			ANDROIDURL = props.getProperty("androidReq");
			SUBRUL = props.getProperty("sub");
			PAYRUL = props.getProperty("add");
			SELECTURL = props.getProperty("select");
			PAYPORT = props.getProperty("payPort");
			WAPPAYPORT = props.getProperty("wapPayPort");
			GAMEID=props.getProperty("gameid");
			P_BNum=props.getProperty("p_bnum");
			Constants.initErrorCode();
			ConnectTaskManager manager = ConnectTaskManager.getInstance().init(); // 初始化任务列表
			ConnectTaskManager.getInstance().initWapCallBack(manager);// 初始化监听
			ConnectTaskManager.getInstance().initCallBack(manager);// 初始化监听
			ConnectTaskManager.getInstance().initFailTasks(manager); // 初始化失败任务列表
			ConnectTaskManager manager1 = ConnectTaskManager.getInstance().subinit();// 初始化扣费任务列表
			ConnectTaskManager.getInstance().initSubFailTasks(manager1);// 初始化扣费失败任务列表
			ConnectTaskManager manager2 = ConnectTaskManager.getInstance().addinit();// 初始化充值（元宝）任务列表
			ConnectTaskManager.getInstance().initAddFailTasks(manager2);// 初始化充值（元宝）失败任务列表
				
			ConnectTaskManager manager3 = ConnectTaskManager.getInstance().selectinit();// 初始化查询元宝任务列表
			ConnectTaskManager.getInstance().initSelectFailTasks(manager3);// 初始化查询元宝失败任务列表
		} catch (Exception e) {
			System.out
					.println("can not find configration file: payserver.properties");
			System.out.println("PayServer start failed !!");
			System.out.println("系统强制退出");
			Thread.yield();
			return;
		}
	}

	/**
	 * 取得实例
	 * 
	 * @return
	 */
	public synchronized static ConnectTaskManager getInstance() {
		if (dbManager == null) {
			dbManager = new ConnectTaskManager();
		}
		return dbManager;
	}

	/**
	 * 添加任务(神州付)
	 * 
	 * @param st
	 */
	public boolean addTask(ConnectTask st) {
		queue.add(st);
		debug_taskCount++;
		return true;
	}

	public boolean addFailTask(ConnectTask st) {
		failqueue.add(st);
		fail_taskCount++;
		return true;
	}

	public void addHandledTask(ConnectTask st) {
		handledTask.add(st);
	}

	/**
	 * 添加任务
	 * 
	 * @param st
	 */
	public boolean addSubTask(ConnectTask st) {
		subqueue.add(st);
		subdebug_taskCount++;
		return true;
	}

	public boolean addSubFailTask(ConnectTask st) {
		subfailqueue.add(st);
		subfail_taskCount++;
		return true;
	}

	public void addSubHandledTask(ConnectTask st) {
		subhandledTask.add(st);
	}

	/**
	 * 添加任务（元宝）
	 * 
	 * @param st
	 */
	public boolean addAffordTask(ConnectTask st) {
		affordqueue.add(st);
		afforddebug_taskCount++;
		return true;
	}

	public boolean addAffordFailTask(ConnectTask st) {
		affordfailqueue.add(st);
		affordfail_taskCount++;
		return true;
	}

	public void addAffordHandledTask(ConnectTask st) {
		affordhandledTask.add(st);
	}
	
	
	
	/**
	 * 添加任务（查询元宝）
	 * 
	 * @param st
	 */
	public boolean addSelectTask(ConnectTask st) {
		selectQueue.add(st);
		selectDebug_taskCount++;
		return true;
	}

	public boolean addSelectFailTask(ConnectTask st) {
		selectFailQueue.add(st);
		selectFail_taskCount++;
		return true;
	}

	public void addSelectHandledTask(ConnectTask st) {
		selectHandledTask.add(st);
	}
	
	
	

	/**
	 * 处理充值任务(神州付)
	 */
	public void processHandledTask() {
		Iterator<ConnectTask> it = handledTask.iterator();

		while (it.hasNext()) {
			ConnectTask handledtask = it.next();
			try {
				handledtask.getDataHandler().shenZhouCBHandle(
						handledtask.isSucess(), handledtask.getResult(),
						handledtask.getState());
			} catch (Exception e) {
				Log.error(Log.ERROR, "processSubHandledTask", e);
			}
			it.remove();
		}
	}

	/**
	 * 处理扣费任务
	 */
	public void processSubHandledTask() {
		Iterator<ConnectTask> it = subhandledTask.iterator();

		while (it.hasNext()) {
			ConnectTask handledTask = it.next();
			try {
				handledTask.getDataHandler().yuanBaoConsumeCBHandle(
						handledTask.isSucess(), handledTask.getResult(),
						handledTask.getSubtractstate());
			} catch (Exception e) {
				Log.error(Log.ERROR, "processSubHandledTask", e);
			}

			it.remove();
		}
	}
	
	/**
	 * 处理充值任务（游戏元宝）
	 */
	public void processPayHandledTask() {
		Iterator<ConnectTask> it = affordhandledTask.iterator();

		while (it.hasNext()) {
			ConnectTask handledTask = it.next();
			try {
				handledTask.getDataHandler().yuanBaoAffordCBHandle(
						handledTask.isSucess(), handledTask.getResult(),
						handledTask.getAffordstate());
			} catch (Exception e) {
				Log.error(Log.ERROR, "processPayHandledTask", e);
			}

			it.remove();
		}
	}
	
	/**
	 * 处理查询元宝
	 */
	public void processSelectHandledTask() {
		Iterator<ConnectTask> it = selectHandledTask.iterator();

		while (it.hasNext()) {
			ConnectTask handledTask = it.next();
			try {
				handledTask.getDataHandler().yuanBaoSelectCBHandle(
						handledTask.isSucess(), handledTask.getResult(),
						handledTask.getSelectState());
			} catch (Exception e) {
				Log.error(Log.ERROR, "processSelectHandledTask", e);
			}

			it.remove();
		}
	}

	/**
	 * 停机
	 */
	public void shutdown() {
		System.out.println("任务队列" + getQueueSize());

		System.out.println("错误队列" + getFailQueueSize());

		System.out.println("扣费队列" + getSubQueueSize());

		System.out.println("扣费失败队列" + getSubFailQueueSize());
		// 如果系统中还有数据尚未存完，则等候其存储完成
		while (getQueueSize() > 0 || getFailQueueSize() > 0
				|| getSubQueueSize() > 0 || getSubFailQueueSize() > 0) {
			try {
				Thread.sleep(500);
			} catch (Exception interruptedException) {
				interruptedException.printStackTrace();
			}
		}
		System.out.println("充值（元宝）队列" + getAffordQueueSize());

		System.out.println("充值（元宝）失败队列" + getAffordFailQueueSize());
		// 如果系统中还有数据尚未存完，则等候其存储完成
		while (getQueueSize() > 0 || getFailQueueSize() > 0
				|| getAffordQueueSize() > 0 || getAffordFailQueueSize() > 0) {
			try {
				Thread.sleep(500);
			} catch (Exception interruptedException) {
				interruptedException.printStackTrace();
			}
		}

		System.out.println("任务队列" + getQueueSize());

		System.out.println("错误队列" + getFailQueueSize());

		System.out.println("扣费队列" + getSubQueueSize());

		System.out.println("扣费失败队列" + getSubFailQueueSize());

		System.out.println("充值（元宝）队列" + getAffordQueueSize());

		System.out.println("充值（元宝）失败队列" + getAffordFailQueueSize());
		
		System.out.println("查询队列" + getSelectQueueSize());

		System.out.println("查询失败队列" + getSelectFailQueueSize());
		// 关掉所有线程
		for (ConnectTaskProcessor tp : processors) {
			tp.shutdown();
		}
		System.out.println("任务队列成功关闭");
		for (FailConnectTaskProcessor fp : failprocessors) {
			fp.shutdown();
		}
		System.out.println("失败任务队列成功关闭");
		for (CallbackProcessor cp : callbackprocessors) {
			cp.shutdown();
		}
		System.out.println("监听任务队列成功关闭");
		for (WapCallbackProcessor wcp : wapcallbackprocessors) {
			wcp.shutdown();
		}
		System.out.println("监听任务队列成功关闭(wap)");
		for (ConnectSubTaskProcessor sp : subprocessors) {
			sp.shutdown();
		}
		System.out.println("扣费任务队列成功关闭");
		for (FailConnectSubTaskProcessor fstp : failsubprocessors) {
			fstp.shutdown();
		}
		System.out.println("失败任务队列成功关闭");

		for (ConnectAffordTaskProcessor sp : payprocessors) {
			sp.shutdown();
		}
		System.out.println("充值（元宝）任务队列成功关闭");
		for (FailConnectAffordTaskProcessor fstp : failpayprocessors) {
			fstp.shutdown();
		}
		System.out.println("失败充值（元宝）任务队列成功关闭");
		
		for (ConnectSelectTaskProcessor sp : selectProcessors) {
			sp.shutdown();
		}
		System.out.println("查询任务队列成功关闭");
		for (FailConnectSelectTaskProcessor fstp : failSelectProcessors) {
			fstp.shutdown();
		}
		System.out.println("失败查询任务队列成功关闭");
	}

	/**
	 * 取任务
	 * 
	 * @return
	 */
	synchronized public ConnectTask getNextTask() {
		if (queue.size() > 0) {
			return queue.poll();
		}
		return null;
	}

	/**
	 * 取失败任务
	 * 
	 * @return
	 */
	synchronized public ConnectTask getNextFailTask() {
		if (failqueue.size() > 0) {
			return failqueue.poll();
		}
		return null;
	}

	/**
	 * 取得当前队列长度
	 * 
	 * @return
	 */
	public int getQueueSize() {
		return queue.size();
	}

	/**
	 * 取得当前队列长度
	 * 
	 * @return
	 */
	public int getFailQueueSize() {
		return failqueue.size();
	}

	/**
	 * 取扣费队列任务
	 * 
	 * @return
	 */
	synchronized public ConnectTask getSubNextTask() {
		if (subqueue.size() > 0) {
			return subqueue.poll();
		}
		return null;
	}

	/**
	 * 取失败扣费任务
	 * 
	 * @return
	 */
	synchronized public ConnectTask getSubNextFailTask() {
		if (subfailqueue.size() > 0) {
			return subfailqueue.poll();
		}
		return null;
	}

	/**
	 * 取得当前扣费队列长度
	 * 
	 * @return
	 */
	public int getSubFailQueueSize() {
		return subfailqueue.size();
	}

	/**
	 * 取得当前扣费队列长度
	 * 
	 * @return
	 */
	public int getSubQueueSize() {
		return subqueue.size();
	}

	
	
	
	/**
	 * 取充值（元宝）队列任务
	 * 
	 * @return
	 */
	synchronized public ConnectTask getAffordNextTask() {
		if (affordqueue.size() > 0) {
			return affordqueue.poll();
		}
		return null;
	}

	/**
	 * 取失败充值（元宝）任务
	 * 
	 * @return
	 */
	synchronized public ConnectTask getAffordNextFailTask() {
		if (affordfailqueue.size() > 0) {
			return affordfailqueue.poll();
		}
		return null;
	}

	/**
	 * 取得当前充值（元宝）队列长度
	 * 
	 * @return
	 */
	public int getAffordFailQueueSize() {
		return affordfailqueue.size();
	}

	/**
	 * 取得当前充值（元宝）队列长度
	 * 
	 * @return
	 */
	public int getAffordQueueSize() {
		return affordqueue.size();
	}
	
	
	
	
	
	
	
	
	/**
	 * 取查询元宝队列任务
	 * 
	 * @return
	 */
	synchronized public ConnectTask getSelectNextTask() {
		if (selectQueue.size() > 0) {
			return selectQueue.poll();
		}
		return null;
	}

	/**
	 * 取失败查询元宝任务
	 * 
	 * @return
	 */
	synchronized public ConnectTask getSelectNextFailTask() {
		if (selectFailQueue.size() > 0) {
			return selectFailQueue.poll();
		}
		return null;
	}

	/**
	 * 取得当前查询元宝队列长度
	 * 
	 * @return
	 */
	public int getSelectFailQueueSize() {
		return selectFailQueue.size();
	}

	/**
	 * 取得当前查询元宝队列长度
	 * 
	 * @return
	 */
	public int getSelectQueueSize() {
		return selectQueue.size();
	}
	
	
	
	
	

	/**
	 * 
	 * @return
	 */
	public long getDebug_taskCount() {
		return debug_taskCount;
	}
	/**
	 * 
	 * @return
	 */
	public long getDebug_taskSpentSum() {
		return debug_taskSpentSum;
	}

	public long getThreadCount() {
		return processors.size();
	}

	public ConcurrentHashMap<Integer, ConnectTask> getWaittasks() {
		return waittasks;
	}

	public void setWaittasks(ConcurrentHashMap<Integer, ConnectTask> waittasks) {
		this.waittasks = waittasks;
	}

	public ConcurrentLinkedQueue<ConnectTask> getFailqueue() {
		return failqueue;
	}

	public void setFailqueue(ConcurrentLinkedQueue<ConnectTask> failqueue) {
		this.failqueue = failqueue;
	}

	public ConcurrentLinkedQueue<ConnectTask> getQueue() {
		return queue;
	}

	public void setQueue(ConcurrentLinkedQueue<ConnectTask> queue) {
		this.queue = queue;
	}

	public ConcurrentLinkedQueue<ConnectTask> getSubqueue() {
		return subqueue;
	}

	public void setSubqueue(ConcurrentLinkedQueue<ConnectTask> subqueue) {
		this.subqueue = subqueue;
	}

	public ConcurrentLinkedQueue<ConnectTask> getSubfailqueue() {
		return subfailqueue;
	}

	public void setSubfailqueue(ConcurrentLinkedQueue<ConnectTask> subfailqueue) {
		this.subfailqueue = subfailqueue;
	}

	public ConcurrentLinkedQueue<ConnectTask> getSubhandledTask() {
		return subhandledTask;
	}

	public void setSubhandledTask(
			ConcurrentLinkedQueue<ConnectTask> subhandledTask) {
		this.subhandledTask = subhandledTask;
	}

	public long getSubdebug_taskCount() {
		return subdebug_taskCount;
	}

	public long getSubfail_taskCount() {
		return subfail_taskCount;
	}

	public long getAfforddebug_taskCount() {
		return afforddebug_taskCount;
	}

	public long getAffordfail_taskCount() {
		return affordfail_taskCount;
	}

	public long getSelectDebug_taskCount() {
		return selectDebug_taskCount;
	}

	public long getSelectFail_taskCount() {
		return selectFail_taskCount;
	}

	public ConcurrentLinkedQueue<ConnectTask> getAffordqueue() {
		return affordqueue;
	}

	public void setAffordqueue(ConcurrentLinkedQueue<ConnectTask> affordqueue) {
		this.affordqueue = affordqueue;
	}

	public ConcurrentLinkedQueue<ConnectTask> getAffordfailqueue() {
		return affordfailqueue;
	}

	public void setAffordfailqueue(
			ConcurrentLinkedQueue<ConnectTask> affordfailqueue) {
		this.affordfailqueue = affordfailqueue;
	}

	public ConcurrentLinkedQueue<ConnectTask> getAffordhandledTask() {
		return affordhandledTask;
	}

	public void setAffordhandledTask(
			ConcurrentLinkedQueue<ConnectTask> affordhandledTask) {
		this.affordhandledTask = affordhandledTask;
	}

	public ConcurrentLinkedQueue<ConnectTask> getSelectFailQueue() {
		return selectFailQueue;
	}

	public void setSelectFailQueue(
			ConcurrentLinkedQueue<ConnectTask> selectFailQueue) {
		this.selectFailQueue = selectFailQueue;
	}

	public ConcurrentLinkedQueue<ConnectTask> getSelectHandledTask() {
		return selectHandledTask;
	}

	public void setSelectHandledTask(
			ConcurrentLinkedQueue<ConnectTask> selectHandledTask) {
		this.selectHandledTask = selectHandledTask;
	}

	public ConcurrentLinkedQueue<ConnectTask> getSelectQueue() {
		return selectQueue;
	}

	public void setSelectQueue(ConcurrentLinkedQueue<ConnectTask> selectQueue) {
		this.selectQueue = selectQueue;
	}
	
}
