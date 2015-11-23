package com.joyveb.tlol.pay.connect;

import com.joyveb.tlol.pay.domain.AffordState;
import com.joyveb.tlol.pay.service.GamePayChannelService;
import com.joyveb.tlol.pay.util.Constants;
import com.joyveb.tlol.pay.util.HttpRequest;

public class FailConnectAffordTaskProcessor extends Thread {

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
	public FailConnectAffordTaskProcessor(ConnectTaskManager tm) {
		taskManager = tm;
	}

	/**
	 * 线程�?
	 */
	@SuppressWarnings("unused")
	public void run() {

		while (!shutdown) {
			long startTime = 0, spent = 0;
			try {
				ConnectTask failtask = taskManager.getAffordNextFailTask();
				if (failtask != null) {
					int i = 0;
					while (i != 10) {
						System.out.println("failtask" + failtask);
						startTime = System.currentTimeMillis();
						try {
							String para = GamePayChannelService
									.getGameAfford(failtask.getAfford());

							String payurl = ConnectTaskManager.getInstance().PAYRUL
									+ para;
							System.out.println(payurl);
							String reqresult = HttpRequest.request(payurl);
							String req[] = reqresult.split(";");
							if (req[0].split(":")[1].equals("true")) {

								//Log.info(Log.BETACCOUNT, reqresult);
								failtask.setResult(reqresult);
								failtask.setSucess(true);
								AffordState afford = new AffordState();
								afford.setState(req[0].split(":")[1]);
								String privateData=req[1].split(":")[1];
								String[] privateInfo=privateData.split("_");
								
								afford.setPayConst(Integer.parseInt(privateInfo[0]));
								afford.setResAmt(Integer.parseInt(req[2].split(":")[1]));
								afford.setCost(Integer.parseInt(privateInfo[2]));
								failtask.setAffordstate(afford);
								ConnectParser dbParser = failtask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onAffordBack(failtask);
								}
								break;
							} else if (req[0].split(":")[1].equals("false")) {
								//Log.info(Log.BETACCOUNT, reqresult);
								failtask.setResult(reqresult);
								failtask.setSucess(false);
								AffordState afford = new AffordState();
								afford.setState(req[0].split(":")[1]);
								String privateData=req[1].split(":")[1];
								String[] privateInfo=privateData.split("_");
								
								afford.setPayConst(Integer.parseInt(privateInfo[0]));
								afford.setResAmt(Integer.parseInt(privateInfo[1]));
								afford.setCost(Integer.parseInt(privateInfo[2]));
								failtask.setAffordstate(afford);
								ConnectParser dbParser = failtask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onAffordBack(failtask);
								}
								break;
							}

						} catch (Exception sqle) {
							sqle.printStackTrace();
							i = i + 1;// 记录失败次数
							System.out.println("******failpaynum:" + i);
						} finally {
							if (10 == i) {
								// 记录数据库,发邮件
								String emailcontent = "账户扣费失败用户信息:"
										+ "失败用户id:"
										+ failtask.getAfford().getUserid()
										+ ";" + "失败用户joyid:"
										+ failtask.getAfford().getJoyid()
										+ "失败用户扣费金额:"
										+ failtask.getAfford().getAmt();
								Constants.dataEmail("账户扣费失败用户信息",
										emailcontent, Constants.DATAMAIL);
								// BasicService.formatSubtractLog("subtract",
								// emailcontent);
								//Log.info(Log.BETACCOUNT, emailcontent);
								taskManager.getAffordfailqueue().remove(
										failtask);
								System.out.println("请求错误超过10次"
										+ "failSize:"
										+ taskManager.getAffordfailqueue()
												.size());
							}
						}
					}
				} else {
					if (spent < PERIOD_WAIT) {
						synchronized (this) {
							wait(PERIOD_WAIT);
							// System.out.println("等待接受中");
						}
					}
				}
			} catch (Exception exception) {
				System.out.println("TaskProcessor任务处理出错" + exception);
			}
		}
	}

	public void shutdown() {
		shutdown = true;
	}

}
