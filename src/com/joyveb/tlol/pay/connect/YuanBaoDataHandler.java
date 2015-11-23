package com.joyveb.tlol.pay.connect;

import com.joyveb.tlol.pay.domain.PayState;
import com.joyveb.tlol.pay.domain.AffordState;
import com.joyveb.tlol.pay.domain.SelectState;
import com.joyveb.tlol.pay.domain.SubtractState;


public interface YuanBaoDataHandler {
    
    /**
     * 处理充值的回调方法(神州付)
     */
    public void shenZhouCBHandle(boolean flag,String result,PayState state);
    
    /**
     * 处理消费的回调方法
     */
    public void yuanBaoConsumeCBHandle(boolean flag,String result,SubtractState state);
    
    /**
     * 处理充值的回调方法（元宝）
     */
    public void yuanBaoAffordCBHandle(boolean flag,String result,AffordState state);
    
    /**
     * 处理查询元宝的回调方法
     */
    public void yuanBaoSelectCBHandle(boolean flag,String result,SelectState state);
}
