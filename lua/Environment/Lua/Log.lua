
log = { }

function log.item(...)
	Log:info(Log.ITEM, arg2string(...))
end

function log.error(...)
	Log:error(Log.ERROR, "Lua", "调用栈信息：")
	
	for i = 2, math.huge do
		local info = debug.getinfo(i, "Sl")
		if info then
			Log:error(Log.ERROR, "Lua", info.short_src .. "    line:" .. info.currentline)
		else
			break
		end
	end

	if #{...} > 0 then
		Log:error(Log.ERROR, "Lua", arg2string(...))
	end
end

function log.raw_error(err)
	Log:error(Log.ERROR, "Lua", "调用栈信息：")
	
	for i = 2, math.huge do
		local info = debug.getinfo(i, "Sl")
		if info then
			Log:error(Log.ERROR, "Lua", info.short_src .. "    line:" .. info.currentline)
		else
			break
		end
	end
	
	if err then
		Log:error(Log.ERROR, "Lua", err)
	end
end

function log.info(...)
	Log:info(Log.STDOUT, "Lua", arg2string(...))
end

function log.trans(...)
    Log:info(Log.TRANS,arg2string(...))
end

function log.eightBuddha(...)
   Log:info(Log.EIGHTBUDDHA, arg2string(...))
end

function log.hegemony(...)
   Log:info(Log.HEGEMONY, arg2string(...))
end

function log.martialcheats(...)
   Log:info(Log.MARTIALCHEATS, arg2string(...))
end

function log.raw_info(info)
	Log:warn(Log.ERROR, "Lua", info)
end

function log.warn(...)
	Log:warn(Log.STDOUT, "Lua", "警告！")
	Log:warn(Log.STDOUT, "Lua", "调用栈信息：")
	
	for i = 2, math.huge do
		local info = debug.getinfo(i, "Sl")
		if info then
			Log:warn(Log.ERROR, "Lua", info.short_src .. "    line:" .. info.currentline)
		else
			break
		end
	end
	
	if #{...} > 0 then
		Log:warn(Log.ERROR, "Lua", arg2string(...))
	end
end

debug.warn = log.warn

debug.traceback = log.error

traceback = log.error

local getlocal = debug.getlocal

function debug.getlocal(stack_level)
	for i = 1, math.huge do
		local name, value = getlocal(stack_level or 2, i)
		if not name then
			break
		end
		
		Log:error(Log.ERROR, "Lua", arg2string("局部变量 name = 【", name, "】 value = 【", (value or "nil"), "】"))
	end
end

local getupvalue = debug.getupvalue

function debug.getupvalue(stack_level)
	local info = debug.getinfo(stack_level or 2, 'f')
	if not info then
		return
	end
	local func = info.func
	
	for i = 1, math.huge do
		local name, value = getupvalue(func, i)
		if not name then
			break
		end
		
		Log:error(Log.ERROR, "Lua", arg2string("非局部的变量 name = 【", name, "】 value = 【", (value or "nil"), "】"))
	end
end
