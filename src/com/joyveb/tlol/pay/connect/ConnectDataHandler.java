package com.joyveb.tlol.pay.connect;

import com.joyveb.tlol.pay.domain.PayState;
import com.joyveb.tlol.pay.domain.SubtractState;


public interface ConnectDataHandler {
    
    /**
     * ������ʱ��处理充值的回调方法(神州付)���ߴ�����
     * @param eventID
     * @param flag
     * @param ds
     */
    public void handle(boolean flag,String result,PayState state);
    
    /**
     * 处理消费的回调方法
     * @param flag
     * @param result
     * @param state
     */
    public void subHandle(boolean flag,String result,SubtractState state);
}
