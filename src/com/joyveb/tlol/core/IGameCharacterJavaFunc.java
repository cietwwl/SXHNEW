package com.joyveb.tlol.core;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;

/**
 * IGameCharacter相关函数注册
 * @author Sid
 */
public enum IGameCharacterJavaFunc implements TLOLJavaFunction {
	/**
	 * IGameCharacter.decreaseHP(int)
	 * 
	 * @param 参数1：IGameCharacter
	 * @param 参数2：int
	 */
	GameCharacterDecreaseHP(new DefaultJavaFunc("_GameCharacterDecreaseHP") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.decreaseHP((int) this.getParam(3).getNumber()));
			return 1;
		}
	}),
	
	GameCharacterGetCrit(new DefaultJavaFunc("_GameCharacterGetCrit") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject()).getCrit());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.fixValueAfterBuff(byte, int)
	 * 
	 * @param 参数1：IGameCharacter
	 * @param 参数1：byte
	 * @param 参数1：int
	 */
	GameCharacterFixValueAfterBuff(new DefaultJavaFunc("_GameCharacterFixValueAfterBuff") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.fixValueAfterBuff((byte) this.getParam(3).getNumber(),
							(int) this.getParam(4).getNumber()));
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getAnime()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetAnime(new DefaultJavaFunc("_GameCharacterGetAnime") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.getAnime());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getAnimeGroup()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetAnimeGroup(new DefaultJavaFunc("_GameCharacterGetAnimeGroup") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.getAnimeGroup());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getEvade()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetEvade(new DefaultJavaFunc("_GameCharacterGetEvade") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.getEvade());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getHit()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetHit(new DefaultJavaFunc("_GameCharacterGetHit") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.getHit());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getHP()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetHP(new DefaultJavaFunc("_GameCharacterGetHP") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject()).getHP());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.fixValueAfterBuffgetId()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetId(new DefaultJavaFunc("_GameCharacterGetId") {
		@Override
		public int execute() throws LuaException {
			LuaService
			.push(((IGameCharacter) this.getParam(2).getObject()).getId());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getLevel()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetLevel(new DefaultJavaFunc("_GameCharacterGetLevel") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.getLevel());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getMaxHP()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetMaxHP(new DefaultJavaFunc("_GameCharacterGetMaxHP") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject()).getMaxHP());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getMaxMAtk()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetMaxMAtk(new DefaultJavaFunc("_GameCharacterGetMaxMAtk") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.getMaxMAtk());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getMaxMP()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetMaxMP(new DefaultJavaFunc("_GameCharacterGetMaxMP") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.getMaxMP());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getMaxPAtk()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetMaxPAtk(new DefaultJavaFunc("_GameCharacterGetMaxPAtk") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.getMaxPAtk());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getmDef()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetmDef(new DefaultJavaFunc("_GameCharacterGetmDef") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.getmDef());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getMinMAtk()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetMinMAtk(new DefaultJavaFunc("_GameCharacterGetMinMAtk") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.getMinMAtk());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getMinPAtk()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetMinPAtk(new DefaultJavaFunc("_GameCharacterGetMinPAtk") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.getMinPAtk());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getMP()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetMP(new DefaultJavaFunc("_GameCharacterGetMP") {
		@Override
		public int execute() throws LuaException {
			LuaService
			.push(((IGameCharacter) this.getParam(2).getObject()).getMP());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getpDef()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetpDef(new DefaultJavaFunc("_GameCharacterGetpDef") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.getpDef());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getSeatId()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetSeatId(new DefaultJavaFunc("_GameCharacterGetSeatId") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.getSeatId());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getType()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetType(new DefaultJavaFunc("_GameCharacterGetType") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.getType());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.getVocation()
	 * 
	 * @param 参数1：IGameCharacter
	 */
	GameCharacterGetVocation(new DefaultJavaFunc("_GameCharacterGetVocation") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.getVocation());
			return 1;
		}
	}),
	
	/**
	 * IGameCharacter.hasBuff(byte)
	 * 
	 * @param 参数1：IGameCharacter
	 * @param 参数2：byte
	 */
	GameCharacterHasBuff(new DefaultJavaFunc("_GameCharacterHasBuff") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((IGameCharacter) this.getParam(2).getObject())
					.hasBuff((byte) this.getParam(3).getNumber()));
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
	private IGameCharacterJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}
	
}
