package com.joyveb.tlol.cycles;

public enum Win {
	/**胜利的ID*/
	Win(0),
	/**失败的ID*/
	Fail(1),
	/**平局的ID*/
	Draw(2)
	
	;
	
	
	/** 押注项目的ID*/
	private final int winId;
	/**
	 * 构造方法
	 * @param betId 押注项目的ID
	 */
	private Win(final int winId) {
		this.winId = winId;
	}
	/**
	 * 押注项目ID的get方法
	 * @return 押注项目的ID
	 */
	public int getWinId() {
		return winId;
	}
	@Override
	public String toString() {
		String result = null;
		switch(this){
		case Win: 
			result =  "胜利";
			break;
		case Fail: 
			result =  "失败";
			break;
		case Draw: 
			result =  "平局";
			break;
		default: 
			result = "无";
			break;	
		}
		return result;
		
	}
}
