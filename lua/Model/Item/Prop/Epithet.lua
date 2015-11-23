
_Epithet = _Prop:new()

function _Epithet:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Epithet:canUse(jrole)
	return true
end

function _Epithet:useItem(jrole, item)
	---------------------------------------------------------------------------
	--返回使用物品提示
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name .. "，获得称号：/" .. self.epithet)
	--返回使用物品提示
	---------------------------------------------------------------------------
	--同步数据
	prepareBody()
	
	fillBagDel(item:getUid())
	
	Bag:delItem(jrole, item:getUid(), 1)
	
	--log.item("[" .. getUTF8(jrole:getNick()) .. "][" .. jrole:getRoleid() .. "][使用物品][" .. self.name .. "][1个]")

	--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	
	log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	_RoleSetEpithet(jrole, self.epithet)
	
	fillEpithet(jrole)
	
	putShort(0)
	
	sendMsg(jrole)
	--同步数据
	---------------------------------------------------------------------------
end

function _Epithet:getUseMenu()
	return	{
				{ 0, "使用", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end

_Epithet.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}

function _Epithet:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end
