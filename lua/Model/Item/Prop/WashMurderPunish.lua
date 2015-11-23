
_WashMurderPunish = _Prop:new()

function _WashMurderPunish:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _WashMurderPunish:canUse(jrole)
	return true
end

function _WashMurderPunish:useItem(jrole, item)
	if _RoleGetNameCatalog(jrole) ~= JRole.NAME_RED then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "只有红名方可使用此道具！")
		return
	end
	
	replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name .. "～")
	
	_SetEvil(jrole)

	prepareBody()
	
	fillBagDel(_ItemGetUid(item))
	
	--log.item(_RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	
	log.item(_RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	Bag:delItem(jrole, _ItemGetUid(item), 1)
	
	fillAttributes(jrole)
	
	putShort(0)
	
	sendMsg(jrole)
end

function _WashMurderPunish:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end
