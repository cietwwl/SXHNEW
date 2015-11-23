_Barter = _Task:newInstance()

function _Barter:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Barter:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	
	if #self.commodity == 1 and self.choose then
		putByte(3)--进入包裹选择物品
	else
		putByte(1)--直接对话，自动扣物品
	end
	putString(self.name)
	putInt(0)
	return true
end

function _Barter:dialog(jrole, npcid, deep, uid, num)
	if deep == 1 then
		self:showTaskInfo(jrole, npcid, deep, uid, num)
	elseif deep == 2 then
		if #self.commodity == 1 and self.choose then
			self:convert(jrole, npcid, deep, uid, num)
		else
			self:trade(jrole)
		end
	else
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
	end
end

function _Barter:getJCommodity()
	local jmap = luajava.new(HashMap)
	
	for _, v in ipairs(self.commodity) do
		if ItemSet[v[1]] then
			jmap:put(v[1], v[2])
		end
	end
	
	return jmap
end

function _Barter:showTaskInfo(jrole, npcid, deep, uid, num)
	local describe
	
	if #self.commodity == 1 and self.choose then
		if not Bag:checkInPack(jrole, uid) then
			replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "操作失败！")
			return
		end
		
		local jItem, template, storage = Bag:getItemInPack(jrole, uid)
		if template.id ~= self.commodity[1][1] then
			describe = self.name .. "/该物品无法" .. self.opName .. "！/" .. self.describe
		end
	end
	
	describe = describe or self.name .. "/" .. self.describe
	
	prepareBody()
	
	putShort(0)
	
	fillNpcItemOP(	describe,
					0, 
					{ 1, },
					Bag:contains(jrole, self:getJCommodity()) and { {1 , self.opName, 0, }, {2 , "返回", 0, }, } or { {2 , "返回", 0, }, } )
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _Barter:creatJavaItemBatch(template_num_Tbl, jrole)
	local container = { }
	local prompts = { }
	
	for template, num in pairs(template_num_Tbl) do
		for _, item in ipairs(template:creatJavaItem(num, self.sign and jrole or nil)) do
			container[#container + 1] = item
		end
		prompts[#prompts + 1] = template.name .. "×" .. num
	end
	
	return container, prompts
end

function _Barter:convert(jrole, npcid, deep, uid, num)
	if not Bag:checkInPack(jrole, uid) then
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "操作失败！")
		return
	end
	
	local jItem, template, storage = Bag:getItemInPack(jrole, uid)
	if template.id ~= self.commodity[1][1] then
		replyMessage(jrole, 3, MsgID.MsgID_Talk_To_Npc_Resp, "该物品无法" .. self.opName .. "！")
		return
	end
	
	if storage < self.commodity[1][2] then
		replyMessage(jrole, 4, MsgID.MsgID_Talk_To_Npc_Resp, "操作失败！")
		return
	end
	
	local template_num_Tbl = self:getBarter(jrole)
	if not template_num_Tbl then
		return
	end
	
	local commodity = { }
	if not self:fee(jrole, commodity) then
		return
	end
	
	for _, v in ipairs(self.commodity) do
		if ItemSet[v[1]] then
			commodity[#commodity + 1] = "【" .. ItemSet[v[1]].name .. "×" .. v[2] .. "】"
			--log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$8#$" .. ItemSet[v[1]].name .."#$" .. v[2].."个#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
		end
	end
	
	local itemsTbl, prompts = self:creatJavaItemBatch(template_num_Tbl, jrole)
	
	for _, v in ipairs(itemsTbl) do
		local tid = _ItemGetTid(v)
		local uid = _ItemGetUid(v)
		local num=_ItemGetStorage(v)
		log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$8#$" .. ItemSet[tid].name .."#$" .. num.. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(tid).."#$".._Long2String(uid))
	end
	
	prepareBody()
	
	putShort(0)
	
	if #prompts > 0 then
		fillNpcDialog(self.id, 0, 0, table.concat(commodity, "，") .. "消失了，获得：/" .. table.concat(prompts), 0)
		
	else
		fillNpcDialog(self.id, 0, 0, self.opName .. "失败！/【" .. template.name .. "×1】消失了！", 0)
	end
	
	if self.commodity.exp or self.commodity.gold then
		fillAttributes(jrole)
	end
	
	fillBagDel(uid, self.commodity[1][2])
	
	Bag:delItem(jrole, uid, self.commodity[1][2])
	
	if pairs(template_num_Tbl) then
		for _, v in ipairs(itemsTbl) do
			fillBagAdd(v)
		end
		
		Bag:addJItem(jrole, itemsTbl)
	end
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _Barter:trade(jrole)
	local jcontain = Bag:contains(jrole, self:getJCommodity())
	if not jcontain then
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, self.opName .. "失败！")
		return
	end
	
	local template_num_Tbl = self:getBarter(jrole)
	if not template_num_Tbl then
		return
	end
	
	local commodity = { }
	if not self:fee(jrole, commodity) then
		return
	end
	
	for _, v in ipairs(self.commodity) do
		if ItemSet[v[1]] then
			commodity[#commodity + 1] = "【" .. ItemSet[v[1]].name .. "×" .. v[2] .. "】"

			--log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$8#$" .. ItemSet[v[1]].name .."#$" .. v[2].."#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
		end
	end
	
	local itemsTbl, prompts = self:creatJavaItemBatch(template_num_Tbl, jrole)
	
	for _, v in ipairs(itemsTbl) do
		local tid = _ItemGetTid(v)
		local uid = _ItemGetUid(v)
		local num=_ItemGetStorage(v)
		log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$8#$" .. ItemSet[tid].name .."#$" .. num .."个#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(tid).."#$".._Long2String(uid))
	end
	
	prepareBody()
	
	putShort(0)
	
	if #prompts > 0 then
		fillNpcDialog(self.id, 0, 0, table.concat(commodity, "，") .. "消失了，获得：/" .. table.concat(prompts), 0)
	else
		fillNpcDialog(self.id, 0, 0, self.opName .. "失败！/" .. table.concat(commodity, "，") .. "消失了！", 0)
	end
	
	if self.commodity.exp or self.commodity.gold then
		fillAttributes(jrole)
	end
	
	for k, v in jmapIter(jcontain) do
		fillBagDel(k, v)
		Bag:delItem(jrole, k, v)
	end
	
	if pairs(template_num_Tbl) then
		for _, v in ipairs(itemsTbl) do
			fillBagAdd(v)
		end
		
		Bag:addJItem(jrole, itemsTbl)
	end
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _Barter:fee(jrole, commodity)
	if self.commodity.exp then
		local exp = _RoleGetExp(jrole)
		if exp < self.commodity.exp then
			replyMessage(jrole, 5, MsgID.MsgID_Talk_To_Npc_Resp, "经验不足，" .. self.opName .. "失败！")
			return
		end
	end
	
	if self.commodity.gold then
		local gold = _RoleGetGold(jrole)
		if gold < self.commodity.gold then
			replyMessage(jrole, 6, MsgID.MsgID_Talk_To_Npc_Resp, "资金不足，" .. self.opName .. "失败！")
			return
		end
	end
	
	if self.commodity.exp then
		local exp = _RoleGetExp(jrole)
		_RoleSetExp(jrole, exp - self.commodity.exp)
		table.insert(commodity, self.commodity.exp .. "经验")
	end
	
	if self.commodity.gold then
		local gold = _RoleGetGold(jrole)
		_RoleSetGold(jrole, gold - self.commodity.gold)
		local currentgold = gold - self.commodity.gold
		local difgold = self.commodity.gold

		log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$9#$本次获得所有物品#$获得物品所有个数#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$" .. difgold .. "")

		table.insert(commodity, getValueDescribe(self.commodity.gold))
	end
	
	return true
end

function _Barter:getBarter(jrole)
	local template_num_Tbl = { }
	
	for i = 1, random.getn(self.barter.maxn) do
		local rand_value = self.barter:rand()
		if rand_value then
			local template = ItemSet[rand_value[math.random(1, #rand_value)]]
			if template then
				template_num_Tbl[template] = (template_num_Tbl[template] or 0) + 1
			end
		end
	end
	
	local items = { }
	for k, v in pairs(template_num_Tbl) do
		items[k.id] = v
	end
	
	
	if Bag:pureSpace(jrole, items) > Bag:remainSpace(jrole) or not Bag:checkAdd(jrole, { items = items, }) then
		replyMessage(jrole, 5, MsgID.MsgID_Talk_To_Npc_Resp, "包裹空间不足，无法" .. self.opName .. "！")
		return
	end
	
	return template_num_Tbl
end
