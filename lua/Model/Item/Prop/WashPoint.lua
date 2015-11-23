
_WashPoint = _Prop:new()

function _WashPoint:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

Wash = {
	Strength = 1,
	Agility = 2,
	Intellect = 3,
	Vitality = 4,
	All = 5,
}

function _WashPoint:canUse(jrole)
	return true
end

function _WashPoint:useItem(jrole, item)
	local role_level = _GetRoleLevel(jrole)
	
	if self.wash == Wash.All then
		local can_wash
		for _, func_get in ipairs(RoleAttributes.point.get) do
			if func_get(jrole) > role_level then
				can_wash = true
				break
			end
		end
		
		if not can_wash then
			replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "没有可清洗的属性点！")
			return
		end
		
		for _, func_set in ipairs(RoleAttributes.point.set) do
			func_set(jrole, role_level)
		end
	elseif RoleAttributes.point.get[self.wash](jrole) > role_level then 
		RoleAttributes.point.set[self.wash](jrole, role_level)
	else 
		replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp, "该属性点无法被清洗，已达到下限！")
		return
	end
	
	replyMessage(jrole, 4, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name .. "～")
	
	prepareBody()
	
	fillBagDel(item:getUid())
	
	--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	
	log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	
	Bag:delItem(jrole, item:getUid(), 1)
	
	fillAttributes(jrole)
	
	fillAttributesDes(jrole)
	
	putShort(0)
	
	sendMsg(jrole)
end

function _WashPoint:getUseMenu()
	return	{
				{ 0, "使用", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end
		
_WashPoint.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}

function _WashPoint:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end
