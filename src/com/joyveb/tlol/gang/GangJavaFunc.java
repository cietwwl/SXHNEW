package com.joyveb.tlol.gang;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.role.RoleBean;

/**
 * 帮派相关函数注册
 * @author Sid
 */
public enum GangJavaFunc implements TLOLJavaFunction {
	/**
	 * GangService.INSTANCE.getGang(long).getTribute()
	 * 查看帮派总贡献
	 * @param 参数1：long 帮派id
	 */
	GangGetTributeByRole(new DefaultJavaFunc("_GangGetTributeByRole") {
		@Override
		public int execute() throws LuaException {
			long gangid = ((RoleBean) this.getParam(2).getObject()).getGangid();
			if (GangService.INSTANCE.isGangLoaded(gangid))
				LuaService.push(GangService.INSTANCE.getGang(gangid).getTribute());
			else
				LuaService.push(0);
			return 1;
		}
	}),
	
	/**
	 * GangService.INSTANCE.getGang(RoleBean.getGangid()).updateTribute(RoleBean, int)
	 * 更新个人帮贡
	 * @param 参数1：RoleBean
	 * @param 参数2：int 个人贡献
	 */
	GangUpdateTribute(new DefaultJavaFunc("_GangUpdateTribute") {
		@Override
		public int execute() throws LuaException {
			RoleBean role = (RoleBean) this.getParam(2).getObject();
			Gang gang = GangService.INSTANCE.getGang(role.getGangid());
			if (gang != null)
				gang.updateTribute(role, (int) this.getParam(3).getNumber());

			return 0;
		}
	}),
	
	/**
	 * GangService.INSTANCE.isGangLoaded()
	 * 
	 * @param 参数1：long 帮派id
	 */
	IsGangLoaded(new DefaultJavaFunc("_IsGangLoaded") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(GangService.INSTANCE.isGangLoaded((long) this.getParam(2).getNumber()));
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
	private GangJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}
	
}
