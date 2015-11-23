package com.joyveb.tlol.pay.connect;

import com.joyveb.tlol.pay.domain.PayState;
import com.joyveb.tlol.pay.domain.AffordState;
import com.joyveb.tlol.pay.domain.SelectState;
import com.joyveb.tlol.pay.domain.SubtractState;
import com.joyveb.tlol.util.Log;

public class ConnectDataHandlerImpl implements YuanBaoDataHandler {
	
	public void shenZhouCBHandle(boolean flag, String result, PayState state) {

		Log.info(Log.PAY,result);
		System.out.println(result);

	}
	public void yuanBaoConsumeCBHandle(boolean flag, String result, SubtractState state) {

		Log.info(Log.PAY,"###" + result);
		System.out.println(result);
	}
	
	public void yuanBaoAffordCBHandle(boolean flag, String result, AffordState state) {

		Log.info(Log.PAY,"###" + result);
		System.out.println(result);
	}
	
	public void yuanBaoSelectCBHandle(boolean flag, String result,
			SelectState state) {
		Log.info(Log.PAY,"###" + result);
		System.out.println(result);		
	}

}
