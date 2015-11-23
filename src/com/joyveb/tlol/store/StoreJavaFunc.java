package com.joyveb.tlol.store;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.item.EquipType;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.role.RoleBean;

/**
 * 物品存储相关函数注册
 * 
 * @author Sid
 */
public enum StoreJavaFunc implements TLOLJavaFunction {
	/**
	 * Pack.getItem()
	 * 
	 * @param 参数2：Pack
	 * @param 参数3：long
	 */
	PackGetItem(new DefaultJavaFunc("_PackGetItem") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Pack) this.getParam(2).getObject()).getItem((long) this.getParam(3).getNumber()));
			return 1;
		}
	}),

	/**
	 * Pack.getCapacity()
	 * 
	 * @param 参数1：Pack
	 */
	PackGetCapacity(new DefaultJavaFunc("_PackGetCapacity") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Pack) this.getParam(2).getObject()).getCapacity());
			return 1;
		}
	}),

	/**
	 * RoleBean.getStore().getDepotCount()
	 * 
	 * @param 参数1：RoleBean
	 */
	GetDepotCount(new DefaultJavaFunc("_GetDepotCount") {
		@Override
		public int execute() throws LuaException {
			this.L.pushNumber(((RoleBean) this.getParam(2).getObject()).getStore().getDepotCount());
			return 1;
		}
	}),

	/**
	 * Pack.getPackItems()
	 * 
	 * @param 参数1：Pack
	 */
	PackGetItemsAll(new DefaultJavaFunc("_PackGetItemsAll") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Pack) this.getParam(2).getObject()).getPackItems());
			return 1;
		}
	}),

	/**
	 * RoleBean.getStore().getBag().getPackItems().clear()
	 * 
	 * @param 参数1：RoleBean
	 */
	BagClear(new DefaultJavaFunc("_BagClear") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).getStore().getBag().getPackItems().clear();
			return 0;
		}
	}),

	/**
	 * InusePack.getInuseItem()
	 * 
	 * @param 参数1：InusePack
	 * @param 参数2：byte 穿戴位置
	 */
	InusePackGetItem(new DefaultJavaFunc("_InusePackGetItem") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((InusePack) this.getParam(2).getObject()).getInuseItem((EquipType) this.getParam(3)
					.getObject()));
			return 1;
		}
	}),

	/**
	 * Pack.pickItem(long, int, boolean)
	 */
	PickItem(new DefaultJavaFunc("_PackPickItem") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Pack) this.getParam(2).getObject()).pickItem((long) this.getParam(3).getNumber(),
					(int) this.getParam(4).getNumber(), this.getParam(4).getBoolean()));
			return 1;
		}
	}),

	/**
	 * Pack.addItem()
	 * 
	 * @param 参数1：Pack
	 * @param 参数2：Item
	 */
	PackAddItem(new DefaultJavaFunc("_PackAddItem") {
		@Override
		public int execute() throws LuaException {
			((Pack) this.getParam(2).getObject()).addItem((Item) this.getParam(3).getObject(), true);
			return 0;
		}
	});

	/**
	 * 实现默认的可注册Java函数
	 */
	private final DefaultJavaFunc jf;

	/**
	 * @param jf 可注册Java函数
	 */
	private StoreJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}

}
