package com.joyveb.tlol.charge;

public class KongZhongChargeChannelInfo {
	private short amount = 10;
	private short rate = 5;
	private String buyCode = "";
	private String order = "";
	private String tips = "";

	public final short getAmount() {
		return amount;
	}

	public final void setAmount(final short amount) {
		this.amount = amount;
	}

	public final short getRate() {
		return rate;
	}

	public final void setRate(final short rate) {
		this.rate = rate;
	}

	public final String getRateInfo() {
		return amount / 10 + "元 = " + rate + "元宝";
	}

	public final String getBuyCode() {
		return buyCode;
	}

	public final void setBuyCode(final String buyCode) {
		this.buyCode = buyCode;
	}

	public final String getOrder() {
		return order;
	}

	public final void setOrder(final String order) {
		this.order = order;
	}

	public final String getTips() {
		return tips;
	}

	public final void setTips(final String tips) {
		this.tips = tips;
	}
}
