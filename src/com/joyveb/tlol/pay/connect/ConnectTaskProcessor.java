package com.joyveb.tlol.pay.connect;

import java.util.concurrent.ConcurrentHashMap;

import com.joyveb.tlol.pay.domain.PayState;
import com.joyveb.tlol.pay.service.GamePayChannelService;
import com.joyveb.tlol.pay.util.HttpRequest;
import com.joyveb.tlol.util.Log;

public class ConnectTaskProcessor extends Thread {

	public static final long PERIOD_WAIT = 100;// 无任务时，周期�?�探测是否有新任务到�?

	public static final int REACTIVE_PERIOD_COUNT = 600;
	/**
	 * 管理�?
	 */
	ConnectTaskManager taskManager = null;

	ConcurrentHashMap<Integer, ConnectTask> tasks = new ConcurrentHashMap<Integer, ConnectTask>();

	/**
	 * 关机
	 */
	private boolean shutdown = false;

	/**
	 * 
	 * @param tm
	 */
	public ConnectTaskProcessor(ConnectTaskManager tm) {
		taskManager = tm;
	}

	/**
	 * 线程�?
	 */
	public void run() {

		while (!shutdown) {
			long startTime = 0, spent = 0;

			try {
				ConnectTask sqlTask = taskManager.getNextTask();
				if (sqlTask != null) {
					// 取任�?
					String flag = "true";
					try {
						String url = "";
						String para = GamePayChannelService.getGamePayPara(sqlTask.getInputData());
						if (sqlTask.getInputData().getP_Pf() != null && !"".equals(sqlTask.getInputData().getP_Pf())) {
							url = ConnectTaskManager.getInstance().ANDROIDURL;
						} else {
							url = ConnectTaskManager.getInstance().URL;
						}
						String payurl = (url + para).replace("\n", "");
						System.out.println("支付请求地址:" + payurl);
						try {
							String reqresult = HttpRequest.request(payurl);

							String req[] = reqresult.split(";");
							if (req[0].split(":")[1].equals("1")) {
								if (req[1].split(":")[1] != null && !req[1].split(":")[1].equals("")) {
									tasks.put(Integer.parseInt(req[1].split(":")[1]), sqlTask);
									taskManager.setWaittasks(tasks);
								}
								//
							} else if (req[0].split(":")[1].equals("2")) {
								String result = "error:参数错误;userid:" + sqlTask.getInputData().getUserid() + ";" + "joyid" + sqlTask.getInputData().getJoyid() + ";" + "金额"
										+ sqlTask.getInputData().getP_Amt() + ";" + "卡号:" + sqlTask.getInputData().getP_CardNo() + ";" + "密码:" + sqlTask.getInputData().getP_CardPwd();
								// BasicService.formatPayLog("pay", result);
								Log.info(Log.PAY, result);
								sqlTask.setResult(result);
								sqlTask.setSucess(false);

								PayState state = new PayState();
								state.setState("false");
								state.setUserid(String.valueOf(sqlTask.getInputData().getUserid()));
								state.setJoyid(sqlTask.getInputData().getJoyid());
								state.setAmt(sqlTask.getInputData().getP_Amt());
								state.setCode(1);// 错误代码
								sqlTask.setState(state);

								ConnectParser dbParser = sqlTask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onBack(sqlTask);
								}
							} else if (req[0].split(":")[1].equals("3")) {
								String result = "error:未提供回调地址;userid:" + sqlTask.getInputData().getUserid() + ";" + "joyid" + sqlTask.getInputData().getJoyid() + ";" + "金额"
										+ sqlTask.getInputData().getP_Amt() + ";" + "卡号:" + sqlTask.getInputData().getP_CardNo() + ";" + "密码:" + sqlTask.getInputData().getP_CardPwd();
								// BasicService.formatPayLog("pay", result);
								Log.info(Log.PAY, result);
								sqlTask.setResult(result);
								sqlTask.setSucess(false);

								PayState state = new PayState();
								state.setState("false");
								state.setUserid(String.valueOf(sqlTask.getInputData().getUserid()));
								state.setJoyid(sqlTask.getInputData().getJoyid());
								state.setAmt(sqlTask.getInputData().getP_Amt());
								state.setCode(3);// 程序出现错误
								sqlTask.setState(state);
								ConnectParser dbParser = sqlTask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onBack(sqlTask);
								}
							} else if (req[0].split(":")[1].equals("4")) {
								String result = "error:重复提交;userid:" + sqlTask.getInputData().getUserid() + ";" + "joyid" + sqlTask.getInputData().getJoyid() + ";" + "金额"
										+ sqlTask.getInputData().getP_Amt() + ";" + "卡号:" + sqlTask.getInputData().getP_CardNo() + ";" + "密码:" + sqlTask.getInputData().getP_CardPwd();
								// BasicService.formatPayLog("pay", result);
								Log.info(Log.PAY, result);
								sqlTask.setResult(result);
								sqlTask.setSucess(false);

								PayState state = new PayState();
								state.setState("false");
								state.setUserid(String.valueOf(sqlTask.getInputData().getUserid()));
								state.setJoyid(sqlTask.getInputData().getJoyid());
								state.setAmt(sqlTask.getInputData().getP_Amt());
								state.setCode(2);// 重复提交
								sqlTask.setState(state);
								ConnectParser dbParser = sqlTask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onBack(sqlTask);
								}
							} else if (req[0].split(":")[1].equals("0")) {
								ConnectTaskManager.getInstance().addFailTask(sqlTask);
								flag = "false";
							} else {
								ConnectTaskManager.getInstance().addFailTask(sqlTask);
								flag = "false";
							}
						} catch (Exception e) {
							e.printStackTrace();
							ConnectTaskManager.getInstance().addFailTask(sqlTask);
							flag = "false";
						}
						// System.out.println("tasks"+taskManager.getWaittasks().size());

					} catch (Exception sqle) {
						sqle.printStackTrace();
						ConnectTaskManager.getInstance().addFailTask(sqlTask);
						flag = "false";
					} finally {
						if ("false".equals(flag)) {
							System.out.println("**********#######%%%" + ConnectTaskManager.getInstance().getQueue().size());
							// taskManager.getQueue().remove(sqlTask);
							System.out.println("**********#######%%%" + taskManager.getQueue().size());

							System.out.println("**********#######%%%" + ConnectTaskManager.getInstance().getFailqueue().size());
						}
					}

					// 计算时间
					spent = System.currentTimeMillis() - startTime;
					// taskManager.addTaskSpent(spent);

				} else {
					if (spent < PERIOD_WAIT) {
						synchronized (this) {
							wait(PERIOD_WAIT);
							// System.out.println("等待接受中");
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				System.out.println("TaskProcessor任务处理出错" + exception);
			}

		}
	}

	public void shutdown() {
		shutdown = true;
	}

}
