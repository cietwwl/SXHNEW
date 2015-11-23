package com.joyveb.tlol.pay.connect;


import com.joyveb.tlol.pay.domain.GamePayPram;
import com.joyveb.tlol.pay.domain.GameAfford;
import com.joyveb.tlol.pay.domain.GameSubtract;
import com.joyveb.tlol.pay.domain.SelectYuanbao;

/**
 * DB解释器，由各模块自已完成转换�?
 * 
 * 
 */
public abstract class ConnectParser {


    /**
     * 当充值操作完成时回调此方法
     * @param eventID
     * @param flag
     * @param rs
     */
    final void onBack(ConnectTask sqlTask) {
        YuanBaoDataHandler handler = sqlTask.getDataHandler();
        if (handler != null) {
            //为了防止并发问题这里将任务返回给taskmanager等待主线程去处理
            ConnectTaskManager.getInstance().addHandledTask(sqlTask);
            //handler.handle(sqlTask.isSucess(), sqlTask.getResult(),sqlTask.getState());
        }
    }

    /**
     * 由服务器传入支付平台，进行充值任务添加
     * @param eventID  事务ID
     * @param flag     
     */
    public boolean postTask(YuanBaoDataHandler handler, GamePayPram pram) {
        ConnectTask sqlTask = new ConnectTask(this,handler);
        sqlTask.setInputData(pram);
        return ConnectTaskManager.getInstance().addTask(sqlTask);
    }
    
    /**
     * 当扣费操作完成时回调此方法
     * @param eventID
     * @param flag
     * @param rs
     */
    final void onSubBack(ConnectTask sqlTask) {
        YuanBaoDataHandler handler = sqlTask.getDataHandler();
        if (handler != null) {
        
            //为了防止并发问题这里将任务返回给taskmanager等待主线程去处理
            ConnectTaskManager.getInstance().addSubHandledTask(sqlTask);
            //handler.subHandle(sqlTask.isSucess(), sqlTask.getResult(),sqlTask.getSubtractstate());
        }
    }

    /**
     * 由服务器传入支付平台,进行扣费任务添加
     * @param eventID  事务ID
     * @param flag     
     */
    public boolean postSubTask(YuanBaoDataHandler handler, GameSubtract subtract) {
        ConnectTask sqlTask = new ConnectTask(this,handler);
        sqlTask.setSubtract(subtract);
        return ConnectTaskManager.getInstance().addSubTask(sqlTask);
    }
    
    /**
     * 当充值（元宝）操作完成时回调此方法
     * @param sqlTask
     */
    final void onAffordBack(ConnectTask sqlTask) {
        YuanBaoDataHandler handler = sqlTask.getDataHandler();
        if (handler != null) {
        
            //为了防止并发问题这里将任务返回给taskmanager等待主线程去处理
            ConnectTaskManager.getInstance().addAffordHandledTask(sqlTask);
            //handler.subHandle(sqlTask.isSucess(), sqlTask.getResult(),sqlTask.getSubtractstate());
        }
    }

    /**
     * 由服务器传入支付平台,进行充值（元宝）任务添加
     * @param handler
     * @param afford
     * @return
     */
    public boolean postAffordTask(YuanBaoDataHandler handler, GameAfford afford) {
        ConnectTask sqlTask = new ConnectTask(this,handler);
        sqlTask.setAfford(afford);
        return ConnectTaskManager.getInstance().addAffordTask(sqlTask);
    }
    
    
    
    
    
    
    
    
    
    /**
     * 当查询元宝操作完成时回调此方法
     * @param sqlTask
     */
    final void onSelectBack(ConnectTask sqlTask) {
        YuanBaoDataHandler handler = sqlTask.getDataHandler();
        if (handler != null) {
        
            //为了防止并发问题这里将任务返回给taskmanager等待主线程去处理
            ConnectTaskManager.getInstance().addSelectHandledTask(sqlTask);
        }
    }

    /**
     * 由服务器传入支付平台,进行查询元宝任务添加
     * @param handler
     * @param selectYuanbao
     * @return
     */
    public boolean postSelectTask(YuanBaoDataHandler handler, SelectYuanbao selectYuanbao) {
        ConnectTask sqlTask = new ConnectTask(this,handler);
        sqlTask.setSelectYuanbao(selectYuanbao);
        return ConnectTaskManager.getInstance().addSelectTask(sqlTask);
    }
    
}
