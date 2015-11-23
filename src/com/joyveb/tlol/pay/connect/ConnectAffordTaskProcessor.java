package com.joyveb.tlol.pay.connect;

import com.joyveb.tlol.pay.domain.AffordState;
import com.joyveb.tlol.pay.service.GamePayChannelService;
import com.joyveb.tlol.pay.util.HttpRequest;


public class ConnectAffordTaskProcessor extends Thread {

	public static final long PERIOD_WAIT = 100;// 无任务时，周期�?�探测是否有新任务到�?

	public static final int REACTIVE_PERIOD_COUNT = 600;
	/**
	 * 管理�?
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
	public ConnectAffordTaskProcessor(ConnectTaskManager tm) {
		taskManager = tm;
	}
	
	/**
	 * 线程�?
	 */
	public void run() {

		while (!shutdown) {
			long startTime = 0, spent = 0;
	
			try {	
				ConnectTask sqlTask = taskManager.getAffordNextTask();
				if (sqlTask != null) {
					// 取任�?
					String flag="true";
					try {
						String para = GamePayChannelService.getGameAfford(sqlTask.getAfford());
						//String payurl=(gpcs.queryByCode("sub")+para).replace("\n", "");
						String payurl = (ConnectTaskManager.getInstance().PAYRUL+para).replace("\n", "");
						//Log.info(Log.BETACCOUNT,payurl);
						try {
							String reqresult=HttpRequest.request(payurl);
							String req[]=reqresult.split(";");
							if(req[0].split(":")[1].equals("true")){

								sqlTask.setResult(reqresult);
								sqlTask.setSucess(true);
								AffordState afford = new AffordState();
								afford.setState(req[0].split(":")[1]);
								
								String privateData=req[1].split(":")[1];
								String[] privateInfo=privateData.split("_");
								
								afford.setPayConst(Integer.parseInt(privateInfo[0]));
								afford.setResAmt(Integer.parseInt(req[2].split(":")[1]));
								afford.setCost(Integer.parseInt(privateInfo[2]));
								sqlTask.setAffordstate(afford);
								
								ConnectParser dbParser = sqlTask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onAffordBack(sqlTask);
								}
							}else if(req[0].split(":")[1].equals("false")){
							
								sqlTask.setResult(reqresult);
								sqlTask.setSucess(false);
								AffordState afford = new AffordState();
								afford.setState(req[0].split(":")[1]);
								String privateData=req[1].split(":")[1];
								String[] privateInfo=privateData.split("_");
								
								afford.setPayConst(Integer.parseInt(privateInfo[0]));
								afford.setResAmt(Integer.parseInt(privateInfo[1]));
								afford.setCost(Integer.parseInt(privateInfo[2]));
								sqlTask.setAffordstate(afford);
								ConnectParser dbParser = sqlTask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onAffordBack(sqlTask);
								}
//								if("3".equals(req[5].split(":")[1]) || "4".equals(req[5].split(":")[1]) || "5".equals(req[5].split(":")[1]) ){
//							    String emailcontent="神州付账户扣费失败用户信息:"+"失败用户id:"+req[1].split(":")[1]+";"+"失败用户joyid:"
//							    +req[2].split(":")[1]+"失败用户扣费金额:"+req[3].split(":")[1]+"失败扣费情况描述:"+req[4].split(":")[1];
//							    Constants.dataEmail("神州付账户扣费失败用户信息", emailcontent,Constants.DATAMAIL);
//								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							ConnectTaskManager.getInstance().addAffordFailTask(sqlTask);
							flag="false";
						}

						
					} catch (Exception sqle) {
						ConnectTaskManager.getInstance().addAffordFailTask(sqlTask);
						flag="false";
					}finally{
						if("false".equals(flag)){
							System.out.println("**********#######%%%"+ConnectTaskManager.getInstance().getAffordqueue().size());
							taskManager.getAffordqueue().remove(sqlTask);
							System.out.println("**********#######%%%"+taskManager.getAffordqueue().size());
							
							System.out.println("**********#######%%%"+ConnectTaskManager.getInstance().getAffordfailqueue().size());
						}
					}

					// 计算时间
					spent = System.currentTimeMillis() - startTime;
					//taskManager.addTaskSpent(spent);

				} else {
					if (spent < PERIOD_WAIT) {
						synchronized (this) {
							wait(PERIOD_WAIT);
							//System.out.println("等待接受中");
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
