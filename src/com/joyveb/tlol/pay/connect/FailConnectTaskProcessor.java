package com.joyveb.tlol.pay.connect;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import com.joyveb.tlol.pay.domain.PayState;
import com.joyveb.tlol.pay.service.GamePayChannelService;
import com.joyveb.tlol.pay.util.Constants;
import com.joyveb.tlol.pay.util.HttpRequest;
import com.joyveb.tlol.util.Log;

public class FailConnectTaskProcessor extends Thread {

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
	public FailConnectTaskProcessor(ConnectTaskManager tm) {
		taskManager = tm;
	}

	/**
	 * 线程�?
	 */
	@SuppressWarnings("unused")
	public void run() {

		while (!shutdown) {
			long startTime = 0, spent = 0;
			startTime = System.currentTimeMillis();
			try {
				ConnectTask failtask = taskManager.getNextFailTask();
				if (failtask != null) {

					int i = 0;
					while (i != 10) {

						try {
							String url = "";
							String para = GamePayChannelService.getGamePayPara(failtask.getInputData());
							if (failtask.getInputData().getP_Pf() != null && !"".equals(failtask.getInputData().getP_Pf())) {
								url = ConnectTaskManager.getInstance().ANDROIDURL;
							} else {
								url = ConnectTaskManager.getInstance().URL;
							}
							String payurl = url + para;
							String reqresult = HttpRequest.request(payurl);
							String req[] = reqresult.split(";");
							if (req[0].split(":")[1].equals("1")) {
								if (req[1].split(":")[1] != null && !req[1].split(":")[1].equals("")) {
									tasks.put(Integer.parseInt(req[1].split(":")[1]), failtask);
									taskManager.setWaittasks(tasks);
									break;
								}
							} else if (req[0].split(":")[1].equals("2")) {
								String result = "error:参数错误;userid:" + failtask.getInputData().getUserid() + ";" + "joyid" + failtask.getInputData().getJoyid() + ";" + "金额"
										+ failtask.getInputData().getP_Amt() + ";" + "卡号:" + failtask.getInputData().getP_CardNo() + ";" + "密码:" + failtask.getInputData().getP_CardPwd();
								Log.info(Log.PAY, result);
								failtask.setResult(result);
								failtask.setSucess(false);

								PayState state = new PayState();
								state.setState("false");
								state.setUserid(req[1].split(":")[1]);
								state.setAmt(Integer.parseInt(req[2].split(":")[1]));
								state.setOrder(req[3].split(":")[1]);
								state.setCode(1);
								failtask.setState(state);
								ConnectParser dbParser = failtask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onBack(failtask);
								}
								break;
							} else if (req[0].split(":")[1].equals("3")) {
								String result = "error:未提供回调地址;userid:" + failtask.getInputData().getUserid() + ";" + "joyid" + failtask.getInputData().getJoyid() + ";" + "金额"
										+ failtask.getInputData().getP_Amt() + ";" + "卡号:" + failtask.getInputData().getP_CardNo() + ";" + "密码:" + failtask.getInputData().getP_CardPwd();
								Log.info(Log.PAY, result);
								failtask.setResult(result);
								failtask.setSucess(false);

								PayState state = new PayState();
								state.setState("false");
								state.setUserid(req[1].split(":")[1]);
								state.setAmt(Integer.parseInt(req[2].split(":")[1]));
								state.setOrder(req[3].split(":")[1]);
								state.setCode(3);
								failtask.setState(state);
								ConnectParser dbParser = failtask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onBack(failtask);
								}
								break;
							} else if (req[0].split(":")[1].equals("4")) {
								String result = "error:重复提交;userid:" + failtask.getInputData().getUserid() + ";" + "joyid" + failtask.getInputData().getJoyid() + ";" + "金额"
										+ failtask.getInputData().getP_Amt() + ";" + "卡号:" + failtask.getInputData().getP_CardNo() + ";" + "密码:" + failtask.getInputData().getP_CardPwd();
								Log.info(Log.PAY, result);
								failtask.setResult(result);
								failtask.setSucess(false);
								PayState state = new PayState();
								state.setState("false");
								state.setUserid(req[1].split(":")[1]);
								state.setAmt(Integer.parseInt(req[2].split(":")[1]));
								state.setOrder(req[3].split(":")[1]);
								state.setCode(2);
								failtask.setState(state);
								ConnectParser dbParser = failtask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onBack(failtask);
								}
								break;
							} else if (req[0].split(":")[1].equals("0")) {
								i = i + 1; // 记录失败次数
							} else {
								i = i + 1; // 记录失败次数
							}

							// System.out.println("tasks"+taskManager.getWaittasks().size());

						} catch (Exception sqle) {
							sqle.printStackTrace();
							i = i + 1;// 记录失败次数
							// System.out.println("******i:"+i);
						} finally {
							if (10 == i) {
								// 记录数据库,发邮件
								String emailcontent = "账户充值失败用户信息:" + "失败用户id:" + failtask.getInputData().getUserid() + ";" + "失败用户joyid:" + failtask.getInputData().getJoyid() + "失败用户充值金额:"
										+ failtask.getInputData().getP_Amt() + ";" + "失败用户充值卡号:" + failtask.getInputData().getP_CardNo() + ";" + "失败用户充值卡密码:" + failtask.getInputData().getP_CardPwd()
										+ ";" + "充值卡金额:" + failtask.getInputData().getP_CardAmt();
								Constants.dataEmail("账户充值失败用户信息", emailcontent, Constants.DATAMAIL);
								// service.formatPayLog("pay", emailcontent);
								Log.info(Log.PAY, emailcontent);
								failtask.getInputData().setRequesttime(new Date());
								failtask.getInputData().setProcessState(0);
								// gpcs.insertGamePram(failtask.getInputData());

								PayState state = new PayState();
								state.setState("false");
								state.setUserid(String.valueOf(failtask.getInputData().getUserid()));
								state.setJoyid(failtask.getInputData().getJoyid());
								state.setAmt(failtask.getInputData().getP_Amt());
								state.setCode(3);
								failtask.setState(state);
								ConnectParser dbParser = failtask.getParser();
								// 回调
								if (dbParser != null) {
									dbParser.onBack(failtask);
								}

								// taskManager.getFailqueue().remove(failtask);
								System.out.println("请求错误超过10次" + "failSize:" + taskManager.getFailqueue().size());
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

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void shutdown() {
		shutdown = true;
	}

}
