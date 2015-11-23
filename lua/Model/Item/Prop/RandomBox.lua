
--开出随机物品的箱子
_RandomBox = _Prop:new()

function _RandomBox:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _RandomBox:canUse(jrole)
	return true
end

function _RandomBox:useItem(jrole, item)
	---------------------------------------------------------------------------
	--条件验证
	if _GetRoleLevel(jrole) < (self.level or 0) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "级别过低不能使用！")
		return
	end
	
	if Bag:remainSpace(jrole) == 0 then
		replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp, "包裹空间不足，该物品无法使用！")
		return
	end
	--条件验证
	---------------------------------------------------------------------------
	--计算开出的物品
	local awardItems = {}
	local rand_content = self.itempack:rand()
	awardItems[rand_content[1]] = 1
	--计算开出的物品
	---------------------------------------------------------------------------
	--检查能否添加到包裹
	if not Bag:checkAdd(jrole, { items = awardItems, }) then
		replyMessage(jrole, 4, MsgID.MsgID_Item_Do_Resp, "包裹空间不足，该物品无法使用！")
		return
	end
	--检查能否添加到包裹
	---------------------------------------------------------------------------
	--生成并返回使用物品提示
	local msg = { }
	for k, v in pairs(awardItems) do
		msg[#msg + 1] = ItemSet[k].name .. "×" .. v
		
		--log.item(jrole:getRoleid(), "[开箱获得][" .. ItemSet[k].name .. "][" .. v .. "个]")

		--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$7#$" .. ItemSet[k].name .. "#$" .. v .. "个#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	end
	
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name .. "，获得：/" .. table.concat(msg, "/"))
	--生成并返回使用物品提示
	---------------------------------------------------------------------------
	--同步数据
	prepareBody()
	
	fillBagDel(item:getUid())
	
	Bag:delItem(jrole, item:getUid(), 1)
	
	--log.item("[" .. getUTF8(jrole:getNick()) .. "][" .. jrole:getRoleid() .. "][使用物品][" .. self.name .. "][1个]")

	--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	
	log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
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
	
	if rand_content.bulletin then
		Broadcast:send("恭喜" .. _RoleGetNick(jrole) .. "获得物品【" .. ItemSet[rand_content[1]].name .. "】")
	end
end

function _RandomBox:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end
