package com.joyveb.tlol.pay.connect;

import com.joyveb.tlol.pay.domain.GamePayPram;
import com.joyveb.tlol.pay.domain.GameAfford;
import com.joyveb.tlol.pay.domain.GameSubtract;
import com.joyveb.tlol.pay.domain.PayState;
import com.joyveb.tlol.pay.domain.AffordState;
import com.joyveb.tlol.pay.domain.SelectState;
import com.joyveb.tlol.pay.domain.SelectYuanbao;
import com.joyveb.tlol.pay.domain.SubtractState;

public class ConnectTask {

	/**
	 * 参数
	 */
	private ConnectParser parser;
	/**
	 * 处理回调
	 */
	private YuanBaoDataHandler dataHandler;
	private boolean sucess = false;
	/**
	 * 游戏支付参数
	 */
	private GamePayPram inputData;
	/**
	 * 游戏扣费参数
	 */
	private GameSubtract subtract;
	/**
	 * 支付结果
	 */
	private String result;
	/**
	 * 游戏支付状态(神州付)
	 */
	private PayState state;
	/**
	 * 游戏扣费状态
	 */
	private SubtractState subtractstate;
	/**
	 * 游戏充值参数
	 */
	private GameAfford afford;
	/**
	 * 游戏扣费状态
	 */
	private AffordState affordstate;

	/**
	 * 查询元宝参数
	 */
	private SelectYuanbao selectYuanbao;
	/**
	 * 查询元宝状态
	 */
	private SelectState selectState;

	public PayState getState() {
		return state;
	}

	public void setState(PayState state) {
		this.state = state;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	ConnectTask(ConnectParser parser, YuanBaoDataHandler dataHandler) {
		this.parser = parser;
		this.dataHandler = dataHandler;
	}

	public ConnectParser getParser() {
		return parser;
	}

	public void setParser(ConnectParser parser) {
		this.parser = parser;
	}

	public YuanBaoDataHandler getDataHandler() {
		return dataHandler;
	}

	public void setDataHandler(YuanBaoDataHandler dataHandler) {
		this.dataHandler = dataHandler;
	}

	public boolean isSucess() {
		return sucess;
	}

	public void setSucess(boolean sucess) {
		this.sucess = sucess;
	}

	public GamePayPram getInputData() {
		return inputData;
	}

	public void setInputData(GamePayPram inputData) {
		this.inputData = inputData;
	}

	public GameSubtract getSubtract() {
		return subtract;
	}

	public void setSubtract(GameSubtract subtract) {
		this.subtract = subtract;
	}

	public SubtractState getSubtractstate() {
		return subtractstate;
	}

	public void setSubtractstate(SubtractState subtractstate) {
		this.subtractstate = subtractstate;
	}

	public GameAfford getAfford() {
		return afford;
	}

	public void setAfford(GameAfford afford) {
		this.afford = afford;
	}

	public AffordState getAffordstate() {
		return affordstate;
	}

	public void setAffordstate(AffordState affordState) {
		this.affordstate = affordState;
	}

	public SelectYuanbao getSelectYuanbao() {
		return selectYuanbao;
	}

	public void setSelectYuanbao(SelectYuanbao selectYuanbao) {
		this.selectYuanbao = selectYuanbao;
	}

	public SelectState getSelectState() {
		return selectState;
	}

	public void setSelectState(SelectState selectState) {
		this.selectState = selectState;
	}

}
