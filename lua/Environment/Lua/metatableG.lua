
local mt = getmetatable( _G )
mt = mt or { }
mt.__index = function (_, n)
	debug.warn("访问未定义的全局变量" .. n)
end
setmetatable( _G, mt )