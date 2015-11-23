
luaprint = print

local tbl2string

function tbl2string(source, dest, deep)
	deep = deep or 4
	for k, v in pairs(source) do
		if type(v) == "table" then
			dest[#dest + 1] = "\n" .. string.rep(" ", deep) .."[" .. k .. " = table：\n"
			tbl2string(v, dest, deep + 4)
			dest[#dest + 1] = "\n" .. string.rep(" ", deep) .."]\n"
		elseif type(v) == "function" then
			dest[#dest + 1] = string.rep(" ", deep) .. "[" .. k .. " = function]"
		elseif type(v) == "userdata" then
			dest[#dest + 1] = string.rep(" ", deep) .. _ObjectToString(v)
		else
			dest[#dest + 1] = string.rep(" ", deep) .. "[" .. k .. " = " .. tostring(v) .. "]"
		end
	end
end

function arg2string(...)
	local outTbl = { }
	
	for _, v in ipairs{...} do
		if type(v) == "table" then
			outTbl[#outTbl + 1] = "\n[\n"
			tbl2string(v, outTbl)
			outTbl[#outTbl + 1] = "\n]\n"
		elseif type(v) == "function" then
			outTbl[#outTbl + 1] = "function"
		elseif type(v) == "userdata" then
			outTbl[#outTbl + 1] = _ObjectToString(v)
		else
			outTbl[#outTbl + 1] = tostring(v)
		end
	end
	
	return table.concat(outTbl, " ")
end

function print(...)
	Log:info(Log.STDOUT, "Lua", arg2string(...))
end
