package com.joyveb.tlol.pay.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.charge.WapPayAgent;
import com.joyveb.tlol.pay.domain.WapPayState;
import com.joyveb.tlol.pay.util.Constants;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.schedule.OneOffSchedule;
import com.joyveb.tlol.schedule.ScheduleManager;
import com.joyveb.tlol.util.Log;
public class WapCallbackProcessor extends Thread{

	
	private Socket socket;    
    
	private BufferedReader in;    
	
	private PrintWriter out;
	
	private ServerSocket ss;    
	
	private boolean shutdown = false;
	
	public ConnectTaskManager taskManager = null;
	

	public WapCallbackProcessor(ConnectTaskManager taskManager) {
		
		this.taskManager = taskManager;
	}
	
	@SuppressWarnings("unused")
	public void run(){
		String line=null;
		String result=null;
		try{
			ss=new ServerSocket(Integer.parseInt(ConnectTaskManager.getInstance().WAPPAYPORT));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		while (!shutdown) {
			try{
			socket=ss.accept();	
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));    
			out = new PrintWriter(socket.getOutputStream(),true);    

			line = in.readLine();    
            System.out.println("info***************"+line);
			Log.info(Log.PAY,line);
			out.close();    
			in.close();    
			socket.close();   
			result=line;
			
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("监听端口出现异常,请检查异常情况");
				String emailContent="监听端口出现异常,请检查异常情况";
				Constants.dataEmail("失败的游戏充值用户信息", emailContent,Constants.DATAMAIL);
			}
			try{
			if(line!=null && !"".equals(line)){
      			String req[]=line.split(";");//接受充值结果
				String paystate=req[0].split(":")[1]; //游戏支付状态
				int userid=Integer.parseInt(req[1].split(":")[1]); //用户userid
				int amt=Integer.parseInt(req[2].split(":")[1]); //用户支付金额
				final int roleid=Integer.parseInt(req[3].split(":")[1]); //用户角色id
				String wapPaycode=req[4].split(":")[1]; //用户wap支付状态
				String joyid=req[5].split(":")[1]; //游戏joyid
				String allmoney=req[6].split(":")[1]; //充值所有金额
				System.out.println(allmoney);
				String realmoney=req[7].split(":")[1]; //成功金额
				System.out.println(realmoney);
				final WapPayState wapState=new WapPayState();				
				if("success".equals(req[0].split(":")[1])){	
					wapState.setState("true");
					wapState.setUserid(userid);
					wapState.setPayamt(amt);
					wapState.setRoleid(roleid);
					wapState.setWappaycode(wapPaycode);
					wapState.setJoyid(joyid);
					wapState.setAllmoney(Integer.parseInt(allmoney));
					wapState.setRealmoney(Integer.parseInt(realmoney));
					if(Integer.parseInt(allmoney)!=Integer.parseInt(realmoney)){
						String emailContent="账户id:"+userid+";"+"充值金额:"+amt+";"+"角色id:"+roleid+";"+"joyid:"+joyid+";"+"所有充值金额:"+allmoney+";"+"充值成功金额:"+realmoney+";"+"状态码:"+wapPaycode;
						Constants.dataEmail("充值部分金额成功情况", emailContent,Constants.DATAMAIL);
					}
				}else if("failed".equals(req[0].split(":")[1])){
					wapState.setState("false");
					wapState.setUserid(userid);
					wapState.setPayamt(amt);
					wapState.setRoleid(roleid);
					wapState.setWappaycode(wapPaycode);
					wapState.setJoyid(joyid);
					wapState.setAllmoney(Integer.parseInt(allmoney));
					wapState.setRealmoney(Integer.parseInt(realmoney));
					if("timeout".equals(wapPaycode)){
						String emailContent="账户id"+userid+";"+"充值金额"+amt+";"+"角色id"+roleid+";"+"joyid"+joyid;
						Constants.dataEmail("可能产生投诉的游戏充值用户信息(超时一类的)", emailContent,Constants.DATAMAIL);
					}
				}
				
				ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {
					@Override
					public void execute() {
						RoleBean player=OnlineService.getOnline(roleid);
						if(player != null){
							WapPayAgent payagent=new WapPayAgent(player);
							payagent.wapOnlineHandle(wapState); //线上发送邮件
						}else{
							WapPayAgent payagent=new WapPayAgent();
							payagent.wapUnderlineHandle(wapState); //线下发送邮件
						}
					}
				});
			}
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("返回结果为空"+e);
			}
		}
		try {
			ss.close();
		} catch (IOException e) {		
			System.out.println("接受消息失败"+e);
		}
	}
	
	public void shutdown() {
		shutdown = true;
	}
}
