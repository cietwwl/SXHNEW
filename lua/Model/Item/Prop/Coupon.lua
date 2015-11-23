
--兑换券

_Coupon = _Prop:new()

function _Coupon:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Coupon:canUse(jrole)
	if (self.level or 0) > _GetRoleLevel(jrole) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "级别过低不能使用！")
		return
	end
	
	local dest = ItemSet[self.destTid]
	if not dest then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "兑换失败！")
		return
	end
	
	return true
end

function _Coupon:useItem(jrole, item)
	local dest = ItemSet[self.destTid]
	---------------------------------------------------------------------------
	--返回提示
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name)
	--返回提示
	---------------------------------------------------------------------------
	--兑换物品并同步消息
	prepareBody()
	
	fillNpcDialog(self.id, 0, 0, "一个【" .. self.name .. "】消失了，获得：/" .. dest.name .. "×1", 0)
	
	fillBagDel(self.id, 1)
	
	--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	Bag:delItem(jrole, self.id, 1)
	
	local itemsTbl = dest:creatJavaItem(num)
	for _, v in ipairs(itemsTbl) do
		fillBagAdd(v)
	end
	
	Bag:addJItem(jrole, itemsTbl)
	
	putShort(0)
	
	sendMsg(jrole)
	--兑换物品并同步消息
	---------------------------------------------------------------------------
end

function _Coupon:getUseMenu()
	if  ItemSet[self.destTid] then
		return	{
					{ 0, "使用", 0, },
					{ 4, "返回", 0, },
				}
	else
		return	{
					{ 4, "返回", 0, },
				}
	end
end

_Coupon.sellMenu = {
	{ 2, "返回", 0, },
}

function _Coupon:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end
