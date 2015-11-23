package com.joyveb.tlol.pay.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import com.joyveb.tlol.pay.domain.PayState;
import com.joyveb.tlol.pay.util.Constants;
import com.joyveb.tlol.util.Log;

public class CallbackProcessor extends Thread{

	
	private Socket socket;    
    
	private BufferedReader in;    
	
	private PrintWriter out;
	
	private ServerSocket ss;    
	
	private boolean shutdown = false;
	
	
	ConnectTaskManager taskManager = null;
	
	public CallbackProcessor(ConnectTaskManager taskManager) {
		
		this.taskManager = taskManager;
	}
	
	public void run(){
		String line=null;
		String result=null;
		try{
			ss=new ServerSocket(Integer.parseInt(ConnectTaskManager.getInstance().PAYPORT));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		while (!shutdown) {
			try{
			socket=ss.accept();
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));    
			out = new PrintWriter(socket.getOutputStream(),true);    

			line = in.readLine();    
			System.out.println("you input is :" + line);    
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
			ConcurrentHashMap<Integer,ConnectTask> tasks=taskManager.getWaittasks();
			if(line!=null && !"".equals(line)){
			String req[]=line.split(";");
			if(req[3].split(":")[1]!=null && !req[3].split(":")[1].equals("")){
				
				int order=Integer.parseInt(req[3].split(":")[1]);

				
				if(tasks.get(order)!=null && !"".equals(tasks.get(order))){
				
					if("success".equals(req[0].split(":")[1])){
					ConnectTask task=tasks.get(order);
					task.setResult(result);
					task.setSucess(true);
					PayState state=new PayState();
					state.setState("true");
					state.setUserid(req[1].split(":")[1]);
					state.setAmt(Integer.parseInt(req[2].split(":")[1]));
					state.setOrder(req[3].split(":")[1]);
					task.setState(state);
					//System.out.println("order:*********"+tasks.get(order));
					try{
					ConnectParser dbParser = task.getParser();
					// 回调
					if (dbParser != null) {
						dbParser.onBack(task);
					}
					tasks.remove(order);
					}catch(Exception e){
						e.printStackTrace();
						if (task.getParser()!= null) {
							task.getParser().onBack(task);
						}
						tasks.remove(order);
                    }
				}else if("failed".equals(req[0].split(":")[1])){
					ConnectTask task=tasks.get(order);
					task.setResult(result);
					task.setSucess(false);
					PayState state=new PayState();
					state.setState("false");
					state.setUserid(req[1].split(":")[1]);
					state.setAmt(Integer.parseInt(req[2].split(":")[1]));
					state.setOrder(req[3].split(":")[1]);
					state.setCode(1);
					task.setState(state);
					
					//System.out.println("order:*********"+tasks.get(order));
//					String emailContent="失败用户id:"+task.getInputData().getUserid()+";"+"joyid:"+task.getInputData().getJoyid()+";"+"充值金额:"+task.getInputData().getP_Amt()+";"+
//					"卡号:"+task.getInputData().getP_CardNo()+";"+"卡号密码:"+task.getInputData().getP_CardPwd()+";"+"充值卡金额:"+task.getInputData().getP_CardAmt()+";"+"订单号:"+order;
//					Constants.dataEmail("失败的游戏充值用户信息", emailContent,Constants.DATAMAIL);
					try{
					ConnectParser dbParser = task.getParser();
					// 回调
					if (dbParser != null) {
						dbParser.onBack(task);
					}
					tasks.remove(order);
					
					}catch(Exception e){
						e.printStackTrace();
                    	// 失败回调
						if (task.getParser()!= null) {
							task.getParser().onBack(task);
						}
						tasks.remove(order);
					}
				}
			}else{
				if("success".equals(req[0].split(":")[1])){
					
					String emailContent="失败回调用户id:"+req[1].split(":")[1]+";"+"充值金额:"+req[2].split(":")[1]+";"+"订单编号:"+req[3].split(":")[1];
					Constants.dataEmail("失败的游戏充值用户信息", emailContent,Constants.DATAMAIL);
				}
			}
			}
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
		taskManager.getWaittasks().clear();
	}
}
