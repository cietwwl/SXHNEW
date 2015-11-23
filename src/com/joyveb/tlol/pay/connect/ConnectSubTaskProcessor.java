package com.joyveb.tlol.pay.connect;

import com.joyveb.tlol.pay.domain.SubtractState;
import com.joyveb.tlol.pay.service.GamePayChannelService;
import com.joyveb.tlol.pay.util.Constants;
import com.joyveb.tlol.pay.util.HttpRequest;
import com.joyveb.tlol.util.Log;


public class ConnectSubTaskProcessor extends Thread {

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
	public ConnectSubTaskProcessor(ConnectTaskManager tm) {
		taskManager = tm;
	}
	
	/**
	 * 线程�?
	 */
	public void run() {

		while (!shutdown) {
			long startTime = 0, spent = 0;
	
			try {	
				ConnectTask sqlTask = taskManager.getSubNextTask();
				if (sqlTask != null) {
					// 取任�?
					String flag="true";
					try {

						String para = GamePayChannelService.getGamePaySubTract(sqlTask.getSubtract());
						//String payurl=(gpcs.queryByCode("sub")+para).replace("\n", "");
						String payurl=(ConnectTaskManager.getInstance().SUBRUL+para).replace("\n", "");
						Log.info(Log.SUBTRACT,payurl);
						
						try {
							String reqresult=HttpRequest.request(payurl);
							String req[]=reqresult.split(";");
							if(req[0].split(":")[1].equals("true")){

								sqlTask.setResult(reqresult);
								sqlTask.setSucess(true);
								SubtractState sub=new SubtractState();
								sub.setState(req[0].split(":")[1]);
								sub.setUserid(req[1].split(":")[1]);
								sub.setJoyid(req[2].split(":")[1]);
								sub.setCost(Integer.parseInt(req[3].split(":")[1]));
								sub.setDes(req[4].split(":")[1]);
								sub.setCode(req[5].split(":")[1]);
								String privateData=req[6].split(":")[1];
								String[] privateInfo=privateData.split("_");
								sub.setItemid(Integer.parseInt(privateInfo[0]));
								sub.setItemNum(Integer.parseInt(privateInfo[1]));
								sub.setOriginalPrice(Integer.parseInt(privateInfo[2]));
								sub.setTaskId(Integer.parseInt(privateInfo[3]));
								sub.setSubConst(Integer.parseInt(privateInfo[4]));
								sub.setResAmt(Integer.parseInt(req[7].split(":")[1]));
								sqlTask.setSubtractstate(sub);
								ConnectParser dbParser = sqlTask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onSubBack(sqlTask);
								}
							}else if(req[0].split(":")[1].equals("false")){
							
								sqlTask.setResult(reqresult);
								sqlTask.setSucess(false);
								SubtractState sub=new SubtractState();
								sub.setState(req[0].split(":")[1]);
								sub.setUserid(req[1].split(":")[1]);
								sub.setJoyid(req[2].split(":")[1]);
								sub.setCost(Integer.parseInt(req[3].split(":")[1]));
								sub.setDes(req[4].split(":")[1]);
								sub.setCode(req[5].split(":")[1]);
								String privateData=req[6].split(":")[1];
								String[] privateInfo=privateData.split("_");
								sub.setItemid(Integer.parseInt(privateInfo[0]));
								sub.setItemNum(Integer.parseInt(privateInfo[1]));
								sub.setOriginalPrice(Integer.parseInt(privateInfo[2]));
								sub.setSubConst(Integer.parseInt(privateInfo[4]));
								sub.setResAmt(Integer.parseInt(req[7].split(":")[1]));
								sqlTask.setSubtractstate(sub);
								ConnectParser dbParser = sqlTask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onSubBack(sqlTask);
								}
								if("3".equals(req[5].split(":")[1]) || "4".equals(req[5].split(":")[1]) || "5".equals(req[5].split(":")[1]) ){
							    String emailcontent="账户扣费失败用户信息:"+"失败用户id:"+req[1].split(":")[1]+";"+"失败用户joyid:"
							    +req[2].split(":")[1]+"失败用户扣费金额:"+req[3].split(":")[1]+"失败扣费情况描述:"+req[4].split(":")[1];
							    Constants.dataEmail("账户扣费失败用户信息", emailcontent,Constants.DATAMAIL);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							ConnectTaskManager.getInstance().addSubFailTask(sqlTask);
							flag="false";
						}

						
					} catch (Exception sqle) {
						ConnectTaskManager.getInstance().addSubFailTask(sqlTask);
						flag="false";
					}finally{
						if("false".equals(flag)){
							System.out.println("**********#######%%%"+ConnectTaskManager.getInstance().getSubqueue().size());
							taskManager.getSubqueue().remove(sqlTask);
							System.out.println("**********#######%%%"+taskManager.getSubqueue().size());
							
							System.out.println("**********#######%%%"+ConnectTaskManager.getInstance().getSubfailqueue().size());
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
				System.out.println("SubTaskProcessor任务处理出错"+ exception);			
			}

		}
	}

	public void shutdown() {
		shutdown = true;
	}


}
