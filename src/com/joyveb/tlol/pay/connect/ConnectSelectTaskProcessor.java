package com.joyveb.tlol.pay.connect;

import com.joyveb.tlol.pay.domain.SelectState;
import com.joyveb.tlol.pay.service.GamePayChannelService;
import com.joyveb.tlol.pay.util.HttpRequest;


public class ConnectSelectTaskProcessor extends Thread {

	public static final long PERIOD_WAIT = 100;// 无任务时，周期�?�探测是否有新任务到�?

	public static final int REACTIVE_PERIOD_COUNT = 600;
	/**
	 * 管理任务
	 */
	ConnectTaskManager taskManager = null;
	/**
	 * 关机
	 */
	private boolean shutdown = false;
	/**
	 * 
	 * @param tm
	 */
	public ConnectSelectTaskProcessor(ConnectTaskManager tm) {
		taskManager = tm;
	}
	
	/**
	 * 线程�?
	 */
	public void run() {

		while (!shutdown) {
			long startTime = 0, spent = 0;
	
			try {	
				ConnectTask sqlTask = taskManager.getSelectNextTask();
				if (sqlTask != null) {
					// 取任务
					String flag= "true";
					try {
						String para = GamePayChannelService.getSelectAccount(sqlTask.getSelectYuanbao());
						String selectUrl = (ConnectTaskManager.getInstance().SELECTURL + para).replace("\n", "");
						try {
							String reqresult = HttpRequest.request(selectUrl);
							String req[] = reqresult.split(";");
								
							if(req[0].split(":")[1].equals("true")){
								
								sqlTask.setResult(reqresult);
								sqlTask.setSucess(true);
								SelectState selectState = new SelectState();
								selectState.setState(req[0].split(":")[1]);
								selectState.setNowAmt(Integer.valueOf(req[1].split(":")[1]));
								selectState.setPayConst(8);
								sqlTask.setSelectState(selectState);
								
								ConnectParser dbParser = sqlTask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onSelectBack(sqlTask);
								}
							}else if(req[0].split(":")[1].equals("false")){
							
								sqlTask.setResult(reqresult);
								sqlTask.setSucess(false);
								SelectState selectState = new SelectState();
								selectState.setState(req[0].split(":")[1]);
								selectState.setNowAmt(Integer.valueOf(req[1].split(":")[1]));
								selectState.setPayConst(8);
								sqlTask.setSelectState(selectState);
								
								ConnectParser dbParser = sqlTask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onSelectBack(sqlTask);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							ConnectTaskManager.getInstance().addSelectFailTask(sqlTask);
							flag="false";
						}						
					} catch (Exception sqle) {
						ConnectTaskManager.getInstance().addSelectFailTask(sqlTask);
						flag = "false";
					}finally{
						if("false".equals(flag)){
							System.out.println("**********#######%%%"+ConnectTaskManager.getInstance().getSelectQueue().size());
							taskManager.getSelectQueue().remove(sqlTask);
							System.out.println("**********#######%%%"+taskManager.getSelectQueue().size());
							
							System.out.println("**********#######%%%"+ConnectTaskManager.getInstance().getSelectFailQueue().size());
						}
					}

					// 计算时间
					spent = System.currentTimeMillis() - startTime;
				} else {
					if (spent < PERIOD_WAIT) {
						synchronized (this) {
							wait(PERIOD_WAIT);
						}
					}
				}
			} catch (Exception exception) {
				System.out.println("AffordTaskProcessor任务处理出错"+ exception);			
			}
		}
	}

	public void shutdown() {
		shutdown = true;
	}
}
