
_Skill = { }

function _Skill:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end