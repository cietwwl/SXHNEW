
_WashSneakAttackNum = _Prop:new()

function _WashSneakAttackNum:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _WashSneakAttackNum:canUse(jrole)
	return true
end

function _WashSneakAttackNum:useItem(jrole, item)
	
	replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name .. "～")
	
	_SetSneakAttackNum(jrole,self.sneakAttackNum)

	prepareBody()
	
	fillBagDel(_ItemGetUid(item))
	
	log.item(_RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	Bag:delItem(jrole, _ItemGetUid(item), 1)
	
	fillAttributes(jrole)
	
	putShort(0)
	
	sendMsg(jrole)
end

function _WashSneakAttackNum:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end
