
_Trumpet = _Prop:new()

function _Trumpet:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Trumpet:getUseMenu()
	return	{
			{ 3, "丢弃", 0, },
			{ 4, "返回", 0, },
		}
end
			
_Trumpet.sellMenu = {
	{ 2, "返回", 0, },
}