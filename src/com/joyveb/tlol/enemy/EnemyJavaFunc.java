package com.joyveb.tlol.enemy;
import java.util.ArrayList;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.role.RoleCardService;

/**
 * 任务操作注册函数
 */
public enum EnemyJavaFunc implements TLOLJavaFunction {
	
	/**
	 * 获得仇人ID列表
	 * 
	 * @param 参数1：RoleBean
	 */
	GetEnemyIdList(new DefaultJavaFunc("_getEnemyIdList") {
		@Override
		public int execute() throws LuaException {
			ArrayList<Integer> enemyList = ((RoleBean) this.getParam(2).getObject()).getEnemys();
			LuaService.push(enemyList);
			return 1;
		}
	}),
	
	/**
	 * 通过ID获得仇人的名字
	 * 
	 * @param 参数1：id
	 */
	GetEnemyNameById(new DefaultJavaFunc("_getEnemyNameById") {
		@Override
		public int execute() throws LuaException {
			if (RoleCardService.INSTANCE.hasCard((int)this.getParam(2).getNumber())){
				RoleCard card = RoleCardService.INSTANCE.getCard((int)this.getParam(2).getNumber());
				LuaService.push(card.getName());
			}
			return 1;
		}
	}),
	
	IfHasEnemys(new DefaultJavaFunc("_ifHasEnemys") {
		@Override
		public int execute() throws LuaException {
			boolean flag  = ((RoleBean) this.getParam(2).getObject()).ifHasEnemys();
			LuaService.push(flag);
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
	private EnemyJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}

}
