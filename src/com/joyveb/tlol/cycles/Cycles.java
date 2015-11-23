package com.joyveb.tlol.cycles;

public enum Cycles {
	/**老虎的ID*/
	Tiger(0),
	/**棒子的ID*/
	Stick(1),
	/**鸡的ID*/
	Chicken(2)
	
	;
	
	
	/** 押注项目的ID*/
	private final int cyclesId;
	/**
	 * 构造方法
	 * @param betId 押注项目的ID
	 */
	private Cycles(final int cyclesId) {
		this.cyclesId = cyclesId;
	}
	/**
	 * 押注项目ID的get方法
	 * @return 押注项目的ID
	 */
	public int getCyclesId() {
		return cyclesId;
	}
	@Override
	public String toString() {
		String result = null;
		switch(this){
		case Tiger: 
			result =  "老虎";
			break;
		case Stick: 
			result =  "棒子";
			break;
		case Chicken: 
			result =  "鸡";
			break;
		default: 
			result = "无";
			break;	
		}
		return result;
		
	}
}
