package com.joyveb.tlol.cycles;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.LuaService;

/**
 * 老虎棒子鸡的javaFunction
 * 2011-2-14
 * LiJG
 */
public enum CyclesJavaFunc implements TLOLJavaFunction{
	
		
		
		/**
		 * Cyclesmanager:
		 * 
		 * @param 参数1：Cyclesmanager
		 */
		ChallengeAddCyclesMessage(new DefaultJavaFunc("_ChallengeAddCyclesMessage") {
			@Override
			public final int execute() throws LuaException {
				Challenge.getInstance().challengeAddCyclesMessage((int)this.getParam(2).getNumber(), (int)this.getParam(3).getNumber(), (int)this.getParam(4).getNumber());
				return 1;
			}	
		}),
		CyclesResults(new DefaultJavaFunc("_CyclesResults") {
			public final int execute() throws LuaException{
				Accept.getInstance().cyclesResults((int)this.getParam(2).getNumber(), (int)this.getParam(3).getNumber(), (int)this.getParam(4).getNumber());
				return 1;
			}
			
		}),
		/**
		 * Cyclesmanager:betCyclesMoney
		 * 
		 * @param 参数1：Cyclesmanager
		 */
		CyclesBetCyclesMoney(new DefaultJavaFunc("_CyclesBetCyclesMoney") {
			@Override
			public final int execute() throws LuaException {
				LuaService.push(Cyclesmanager.getInstance().betCyclesMoney((RoleBean)this.getParam(2).getObject()));
				return 1;
			}
		}),
		GetCyclesMessages(new DefaultJavaFunc("_GetCyclesMessages") {
			@Override
			public final int execute() throws LuaException {
				LuaService.push(Challenge.getInstance().getChallengeCyclesMessages());
				return 1;
			}
		}),	
		GetChallengeCyclesMessage(new DefaultJavaFunc("_GetChallengeCyclesMessage") {
			@Override
			public final int execute() throws LuaException {
				LuaService.push(Challenge.getInstance().getChallengeCyclesMessage((int)this.getParam(2).getNumber()));
				return 1;
			}
		}),	
		QueryChallenge(new DefaultJavaFunc("_QueryChallenge"){
			public final int execute() throws LuaException{
				LuaService.push(Accept.getInstance().queryChallenge((int)this.getParam(2).getNumber()));
				return 1;
			}
		}),
		GetInning(new DefaultJavaFunc("_GetInning"){
			public final int execute() throws LuaException{
				LuaService.push(((CyclesMessage)this.getParam(2).getObject()).getInning());
				return 1;
			}
		}),
		GetChallengeRoleid(new DefaultJavaFunc("_GetChallengeRoleid"){
			public final int execute() throws LuaException{
				LuaService.push(((CyclesMessage)this.getParam(2).getObject()).getChallengeRoleid());
				return 1;
			}
		}),
		GetName(new DefaultJavaFunc("_GetName"){
			public final int execute() throws LuaException{
				LuaService.push(((CyclesMessage)this.getParam(2).getObject()).getName());
				return 1;
			}
		}),
		GetNowTime(new DefaultJavaFunc("_GetNowTime"){
			public final int execute() throws LuaException{
				LuaService.push(((CyclesMessage)this.getParam(2).getObject()).getNowTime());
				return 1;
			}
		}),
		GetWin(new DefaultJavaFunc("_GetWin"){
			public final int execute() throws LuaException{
				LuaService.push(((CyclesMessage)this.getParam(2).getObject()).getWin());
				return 1;
			}
		}),
		GetGold(new DefaultJavaFunc("_GetGold"){
			public final int execute() throws LuaException{
				LuaService.push(((CyclesMessage)this.getParam(2).getObject()).getGold());
				return 1;
			}
		}),
		;
		/**
		 * 实现默认的可注册Java函数
		 */
		private final DefaultJavaFunc jf;

		/**
		 * @param jf 可注册Java函数
		 * @return 
		 */
		private CyclesJavaFunc(final DefaultJavaFunc jf) {
			this.jf = jf;
		}
		@Override
		public void register() {
			jf.register();
		}
}

