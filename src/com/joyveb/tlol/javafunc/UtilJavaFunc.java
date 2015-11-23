package com.joyveb.tlol.javafunc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;

/**
 * 待注册的迭代器相关函数
 * @author Sid
 */
public enum UtilJavaFunc implements TLOLJavaFunction {
	/** 获取迭代器 */
	GetIterator(new DefaultJavaFunc("_GetIterator") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Collection<?>) this.getParam(2).getObject()).iterator());
			return 1;
		}
	}),
	
	/** 迭代器hasNext */
	IterHasNext(new DefaultJavaFunc("_IterHasNext") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Iterator<?>) this.getParam(2).getObject()).hasNext());
			return 1;
		}
	}),
	
	/** 迭代器next */
	IterGetNext(new DefaultJavaFunc("_IterGetNext") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Iterator<?>) this.getParam(2).getObject()).next());
			return 1;
		}
	}),
	
	/** 迭代器remove */
	IterRemove(new DefaultJavaFunc("_IterRemove") {
		@Override
		public int execute() throws LuaException {
			((Iterator<?>) this.getParam(2).getObject()).remove();
			return 0;
		}
	}),
	
	/**
	 * Map.Entry.getKey
	 * 
	 * @param 参数1：Map.Entry
	 */
	EntryGetKey(new DefaultJavaFunc("_EntryGetKey") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Entry<?, ?>) this.getParam(2).getObject()).getKey());
			return 1;
		}
	}),

	/**
	 * Map.Entry.getValue
	 * 
	 * @param 参数1：Map.Entry
	 */
	EntryGetValue(new DefaultJavaFunc("_EntryGetValue") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Entry<?, ?>) this.getParam(2).getObject()).getValue());
			return 1;
		}
	}),

	/**
	 * Map.clear
	 * 
	 * @param 参数1：Map
	 */
	ClearMap(new DefaultJavaFunc("_ClearMap") {
		@Override
		public int execute() throws LuaException {
			((Map<?, ?>) this.getParam(2).getObject()).clear();
			return 0;
		}
	}),

	/**
	 * Map.entrySet
	 * 
	 * @param 参数1：Map
	 */
	MapGetEntrySet(new DefaultJavaFunc("_MapGetEntrySet") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Map<?, ?>) this.getParam(2).getObject()).entrySet());
			return 1;
		}
	}),
	
	/**
	 * Map.put
	 * 
	 * @param 参数1：Map
	 * @param 参数2：key
	 * @param 参数1：value
	 */
	MapPutKeyValue(new DefaultJavaFunc("_MapPutKeyValue") {
		@SuppressWarnings("unchecked")
		@Override
		public int execute() throws LuaException {
			((Map<Object, Object>) this.getParam(2).getObject()).put(
					this.getParam(3).getObject(), this.getParam(4).getObject());
			return 0;
		}
	}),
	
	/**
	 * Collection.clear
	 * 
	 * @param 参数1：Collection
	 */
	ClearCollection(new DefaultJavaFunc("_ClearCollection") {
		@Override
		public int execute() throws LuaException {
			((Collection<?>) this.getParam(2).getObject()).clear();
			return 0;
		}
	}),
	
	/**
	 * Collection.size()
	 * 
	 * @param 参数1：Collection
	 */
	CollectionSize(new DefaultJavaFunc("_CollectionSize") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Collection<?>) this.getParam(2).getObject()).size());
			return 1;
		}
	}),
	
	/**
	 * List.add()
	 * 
	 * @param 参数1：Collection
	 */
	ListAdd(new DefaultJavaFunc("_ListAdd") {
		@SuppressWarnings("unchecked")
		@Override
		public int execute() throws LuaException {
			((List<Object>) this.getParam(2).getObject()).add(this.getParam(3).getObject());
			return 0;
		}
	}),
	
	/**
	 * List.get()
	 * 
	 * @param 参数1：Collection
	 */
	ListGet(new DefaultJavaFunc("_ListGet") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((List<?>) this.getParam(2).getObject()).get((int) this.getParam(3).getNumber()));
			return 1;
		}
	}),
	
	/**
	 * ArrayList.trimToSize()
	 * 
	 * @param 参数1：ArrayList
	 */
	AListTrimToSize(new DefaultJavaFunc("_AListTrimToSize") {
		@Override
		public int execute() throws LuaException {
			((ArrayList<?>) this.getParam(2).getObject()).trimToSize();
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
	private UtilJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}

}
