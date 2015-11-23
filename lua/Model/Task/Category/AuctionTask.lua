
_AuctionTask = _Task:newInstance()

function _AuctionTask:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

-------------------------------------------------------------------------------
--在NPC对话列表处的显示
function _AuctionTask:shownInTalkList(jrole, npcid)
	putInt(self.id)
	putLong(0)
	putByte(5)
	putString(self.name)
	putInt(0)
	
	return true
end