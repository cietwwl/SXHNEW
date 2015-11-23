package com.joyveb.tlol.pay.connect;


import com.joyveb.tlol.pay.domain.SelectState;
import com.joyveb.tlol.pay.service.GamePayChannelService;
import com.joyveb.tlol.pay.util.HttpRequest;

public class FailConnectSelectTaskProcessor extends Thread {

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
	public FailConnectSelectTaskProcessor(ConnectTaskManager tm) {
		taskManager = tm;
	}

	/**
	 * 线程�?
	 */
	public void run() {

		while (!shutdown) {
			long spent = 0;
//			long startTime = 0;
			try {
				ConnectTask failtask = taskManager.getSelectNextFailTask();
				if (failtask != null) {
					int i = 0;
					while (i != 10) {
						System.out.println("failtask" + failtask);
//						startTime = System.currentTimeMillis();
						try {

							String para = GamePayChannelService.getSelectAccount(failtask.getSelectYuanbao());
							String selectUrl = (ConnectTaskManager.getInstance().SELECTURL + para).replace("\n", "");

							String reqresult = HttpRequest.request(selectUrl);
							String req[] = reqresult.split(";");
							
							if (req[0].split(":")[1].equals("true")) {

								failtask.setResult(reqresult);
								failtask.setSucess(true);
								
								SelectState selectState = new SelectState();
								selectState.setState(req[0].split(":")[1]);
								selectState.setNowAmt(Integer.valueOf(req[1].split(":")[1]));
								selectState.setPayConst(8);
								failtask.setSelectState(selectState);
								ConnectParser dbParser = failtask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onSelectBack(failtask);
								}
								break;
							} else if (req[0].split(":")[1].equals("false")) {
								
								failtask.setResult(reqresult);
								failtask.setSucess(false);
								SelectState selectState = new SelectState();
								selectState.setState(req[0].split(":")[1]);
								selectState.setNowAmt(Integer.valueOf(req[1].split(":")[1]));
								selectState.setPayConst(8);
								failtask.setSelectState(selectState);
								ConnectParser dbParser = failtask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onSelectBack(failtask);
								}
								break;
							}
						} catch (Exception sqle) {
							sqle.printStackTrace();
							i = i + 1;// 记录失败次数
							System.out.println("******failpaynum:" + i);
						} finally {
							if (10 == i) {
//								// 记录数据库,发邮件
//								String emailcontent = "神州付账户扣费失败用户信息:"
//										+ "失败用户id:"
//										+ failtask.getAfford().getUserid()
//										+ ";" + "失败用户joyid:"
//										+ failtask.getAfford().getJoyid()
//										+ "失败用户扣费金额:"
//										+ failtask.getAfford().getAmt();
//								Constants.dataEmail("神州付账户扣费失败用户信息",
//										emailcontent, Constants.DATAMAIL);
							
								taskManager.getSelectFailQueue().remove(failtask);
								System.out.println("请求错误超过10次"+ "failSize:"+ taskManager.getSelectFailQueue().size());
							}
						}
					}
				} else {
					if (spent < PERIOD_WAIT) {
						synchronized (this) {
							wait(PERIOD_WAIT);
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
