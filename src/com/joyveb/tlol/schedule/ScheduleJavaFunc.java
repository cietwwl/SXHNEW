package com.joyveb.tlol.schedule;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.util.Cardinality;

/**
 * 计划任务注册函数
 * @author Sid
 */
public enum ScheduleJavaFunc implements TLOLJavaFunction {
	/**
	 * Cardinality.INSTANCE.getDay()
	 */
	GetDay(new DefaultJavaFunc("_GetDay") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(Cardinality.INSTANCE.getDay());
			return 1;
		}
	}),
	
	/**
	 * Cardinality.INSTANCE.getMinute()
	 */
	GetMinute(new DefaultJavaFunc("_GetMinute") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(Cardinality.INSTANCE.getMinute());
			return 1;
		}
	}),
	
	/**
	 * Cardinality.INSTANCE.getSecond()
	 */
	GetSecond(new DefaultJavaFunc("_GetSecond") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(Cardinality.INSTANCE.getSecond());
			return 1;
		}
	}),
	
	/**
	 * Cardinality.INSTANCE.getNextFreshMin()
	 */
	GetNextFreshMin(new DefaultJavaFunc("_GetNextFreshMin") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(Cardinality.INSTANCE.getNextFreshMin((int) this.getParam(2).getNumber()));
			return 1;
		}
	}),
	
	/**
	 * Cardinality.INSTANCE.getMinute()
	 */
	OfferSchedule(new DefaultJavaFunc("_OfferSchedule") {
		@Override
		public int execute() throws LuaException {
			ScheduleManager.INSTANCE.offerTask((ScheduleTask) this.getParam(2).getObject());
			return 0;
		}
	}),
	
	/**
	 * Cardinality.INSTANCE.getHour()
	 */
	GetHour(new DefaultJavaFunc("_GetHour") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(Cardinality.INSTANCE.getHour());
			return 1;
		}
	});

	/**
	 * 实现默认的可注册Java函数
	 */
	private final DefaultJavaFunc jf;

	/**
	 * @param jf 可注册Java函数
	 */
	private ScheduleJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}
	
}
