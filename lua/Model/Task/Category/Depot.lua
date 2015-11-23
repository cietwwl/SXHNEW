
_Depot = _Task:newInstance()

function _Depot:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Depot:shownInTalkList(jrole)
	putInt(self.id)
	putLong(_GetDepotCount(jrole))
	putByte(4)
	putString(self.name)
	putInt(0)
	
	return true
end
