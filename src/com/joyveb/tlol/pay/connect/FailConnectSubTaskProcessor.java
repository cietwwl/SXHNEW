package com.joyveb.tlol.pay.connect;


import com.joyveb.tlol.pay.domain.SubtractState;
import com.joyveb.tlol.pay.service.GamePayChannelService;
import com.joyveb.tlol.pay.util.Constants;
import com.joyveb.tlol.pay.util.HttpRequest;
import com.joyveb.tlol.util.Log;


public class FailConnectSubTaskProcessor extends Thread {

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
	public FailConnectSubTaskProcessor(ConnectTaskManager tm) {
		taskManager = tm;
	}
	/**
	 * 线程�?
	 */
	@SuppressWarnings("unused")
	public void run() {

		while (!shutdown) {
			long startTime = 0, spent = 0;
			try{
				ConnectTask failtask=taskManager.getSubNextFailTask();		
				if(failtask!=null){
					int i=0;
					while(i!=10){
					startTime = System.currentTimeMillis();
					try {             
						String para = GamePayChannelService.getGamePaySubTract(failtask.getSubtract());
						//String payurl=gpcs.queryByCode("sub")+para;
						String payurl=ConnectTaskManager.getInstance().SUBRUL+para;
							String reqresult=HttpRequest.request(payurl);
							String req[]=reqresult.split(";");
							if(req[0].split(":")[1].equals("true")){
								//BasicService.formatSubtractLog("subtract", reqresult);
								Log.info(Log.SUBTRACT,reqresult);
								failtask.setResult(reqresult);
								failtask.setSucess(true);
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
								failtask.setSubtractstate(sub);
								ConnectParser dbParser = failtask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onSubBack(failtask);
								}
								break;
							}else if(req[0].split(":")[1].equals("false")){
								//BasicService.formatSubtractLog("subtract", reqresult);
								Log.info(Log.SUBTRACT,reqresult);
								failtask.setResult(reqresult);
								failtask.setSucess(false);
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
								failtask.setSubtractstate(sub);
								ConnectParser dbParser = failtask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onSubBack(failtask);
								}
								if("3".equals(req[5].split(":")[1]) || "4".equals(req[5].split(":")[1]) || "5".equals(req[5].split(":")[1]) ){
								    String emailcontent="账户扣费失败用户信息:"+"失败用户id:"+req[1].split(":")[1]+";"+"失败用户joyid:"
								    +req[2].split(":")[1]+"失败用户扣费金额:"+req[3].split(":")[1]+"失败扣费情况描述:"+req[4].split(":")[1];
								    Constants.dataEmail("账户扣费失败用户信息", emailcontent,Constants.DATAMAIL);
								}
								break;
							}
						
					} catch (Exception sqle) {
						sqle.printStackTrace();
						i=i+1;//记录失败次数
						System.out.println("******failpaynum:"+i);
					}finally{
						if(10==i){
							//记录数据库,发邮件	
							    String emailcontent="账户扣费失败用户信息:"+"失败用户id:"+failtask.getSubtract().getUserid()+";"+"失败用户joyid:"
							    +failtask.getSubtract().getJoyid()+"失败用户扣费金额:"+failtask.getSubtract().getAmt();
							    Constants.dataEmail("账户扣费失败用户信息", emailcontent,Constants.DATAMAIL);
							    //BasicService.formatSubtractLog("subtract", emailcontent);
							    Log.info(Log.SUBTRACT,emailcontent);
								taskManager.getSubfailqueue().remove(failtask);
								System.out.println("请求错误超过10次"+"failSize:"+taskManager.getSubfailqueue().size());
                            }
					}
				}
				} else {
					if (spent < PERIOD_WAIT) {
						synchronized (this) {
							wait(PERIOD_WAIT);
							//System.out.println("等待接受中");
						}
					}
				}
			} catch (Exception exception) {
				System.out.println("TaskProcessor任务处理出错"+ exception);			
			}
		}
	}

	public void shutdown() {
		shutdown = true;
	}


}
