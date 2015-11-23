
_Grocery = _Prop:new()

function _Grocery:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Grocery:getUseMenu()
	return	{
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end
					
_Grocery.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}

_Scapegoat = _Grocery:new()
function _Scapegoat:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

_EquipRefine = _Grocery:new()
function _EquipRefine:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _EquipRefine:getAuctionCategory()
	return 2 ^ _EnumOrdinal(AutionType.Grocery) + 2 ^ _EnumOrdinal(AutionType.ItemRefine)
end
