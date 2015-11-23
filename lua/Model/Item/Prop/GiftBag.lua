
--开出固定物品的箱子
_GiftBag = _Prop:new()

function _GiftBag:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _GiftBag:isDedicated()
	return self.vocation
end

function _GiftBag:canUse(jrole)
	return true
end

function _GiftBag:useItem(jrole, item)
	---------------------------------------------------------------------------
	--条件验证
	if (self.minLevel or 0) > _GetRoleLevel(jrole) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "级别过低不能使用！")
		return
	end
	--条件验证
	---------------------------------------------------------------------------
	--计算开出的物品
	local awardItems = {}
	if self.itempack then
		for k, v in pairs(self.itempack) do
			local release = ItemSet[k]--可能给的物品
			if release then
				if release.type == ItemConst.TaskProp then--不允许给任务物品！！
					log.error("箱子中不允许开出任务物品：" .. k)
				else
					if release:isDedicated() then
						if _ObjectEquals(release.vocation, _RoleGetVocation(jrole)) then
							awardItems[k] = v
						end
					else
						awardItems[k] = v
					end
				end
			end
		end
	end
	--计算开出的物品
	---------------------------------------------------------------------------
	--检查能否添加到包裹
	if not Bag:checkAdd(jrole, { items = awardItems, }) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "包裹空间不足，该物品无法使用！")
		return
	end
	--检查能否添加到包裹
	---------------------------------------------------------------------------
	--生成并返回使用物品提示
	local msg = { }
	if self.gold and self.gold > 0 then
		msg[#msg + 1] = "金钱：" .. getValueDescribe(self.gold)
	end
	
	for k, v in pairs(awardItems) do
		msg[#msg + 1] = ItemSet[k].name .. "×" .. v
	end
	
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name .. "，获得：/" .. table.concat(msg, "/"))
	--生成并返回使用物品提示
	---------------------------------------------------------------------------
	--同步数据
	prepareBody()
	
	fillBagDel(item:getUid())
	
	Bag:delItem(jrole, item:getUid(), 1)
	
	--log.item("[" .. getUTF8(jrole:getNick()) .. "][" .. jrole:getRoleid() .. "][使用物品][" .. self.name .. "][1个]")

	--log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	
	log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	
	if self.gold and self.gold > 0 then
		local gold = _RoleGetGold(jrole)
		_RoleSetGold(jrole, gold + self.gold)
		local difgold = self.gold

		log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$6#$本次获得所有物品#$获得物品所有个数#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$" .. difgold .. "")
		fillAttributes(jrole)
	end
	
	for k, v in pairs(awardItems) do
		local itemsTbl = ItemSet[k]:creatJavaItem(v)
		
		for _, v in ipairs(itemsTbl) do
			fillBagAdd(v)
		end
		
		Bag:addJItem(jrole, itemsTbl)
		
		--log.item("[" .. getUTF8(jrole:getNick()) .. "][" .. jrole:getRoleid() .. "][开箱获得][" .. ItemSet[k].name .. "][" .. v .. "个]")

		--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$7#$" .. ItemSet[k].name .. "#$" .. v .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)

        log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$7#$" .. ItemSet[k].name .. "#$" .. v .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(k).."#$".._Long2String(k))
	end
	
	putShort(0)
	
	sendMsg(jrole)
	--同步数据
	---------------------------------------------------------------------------
end

function _GiftBag:getUseMenu()
	return	{
				{ 0, "使用", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end

_GiftBag.sellMenu = {
	{ 2, "返回", 0, },
}

function _GiftBag:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end

