package com.joyveb.tlol.map;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;

/**
 * 地图相关函数注册
 * @author Sid
 */
public enum MapJavaFunc implements TLOLJavaFunction {
	/**
	 * Coords.getMap(), Coords.getX(), Coords.getY()
	 * 
	 * @param 参数1：Coords
	 */
	CoordsGetAttr(new DefaultJavaFunc("_CoordsGetAttr") {
		@Override
		public int execute() throws LuaException {
			Coords coords = (Coords) this.getParam(2).getObject();
			LuaService.push(coords.getMap());
			LuaService.push(coords.getX());
			LuaService.push(coords.getY());
			return 3;
		}
	}),
	
	/**
	 * Coords.setMap(short).setX(int).setY(int)
	 * 
	 * @param 参数1：Coords
	 * @param 参数2：short map
	 * @param 参数3：int x
	 * @param 参数4：int y
	 */
	CoordsSetAttr(new DefaultJavaFunc("_CoordsSetAttr") {
		@Override
		public int execute() throws LuaException {
			((Coords) this.getParam(2).getObject())
				.setMap((short) this.getParam(3).getNumber())
				.setX((int) this.getParam(4).getNumber())
				.setY((int) this.getParam(5).getNumber());
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
	private MapJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}
	
}
