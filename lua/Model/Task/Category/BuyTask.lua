
_BuyTask = _Task:newInstance()

_BuyTask.buyMenu = {
	{ 1, "购买", 0, },
	{ 2, "返回", 0, },
}

function _BuyTask:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _BuyTask:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	putByte(1)
	putString(self.name)
	putInt(0)
	return true
end

function _BuyTask:dialog(jrole, npcid, deep, uid, num)

	if deep == 1 or uid == 0 then--自动调整deep为1
		self:listItems(jrole, npcid, deep, uid, num)
	elseif deep == 2 then
		self:showItemInfo(jrole, npcid, deep, uid, num)
	elseif deep == 3 then
		self:buyItem(jrole, npcid, deep, uid, num)
	else
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
	end
end

function _BuyTask:listItems(jrole, npcid, deep, uid, num)
	prepareBody()
	
	putShort(0)
	
	putShort(300)
	
	placeholder('b')
	
	local fill = 0
	
	for _, v in pairs(self.sell) do
		local template = ItemSet[v]
		if template then
			putInt(self.id)
			putLong(v)
			putByte(2)
			
			if template:isDedicated() then
				putString("[LV" .. (template.level or 1) .. "][" .. _ObjectToString(template.vocation)
						.. "]["	.. template.name .. "][" .. getValueDescribe(template:getRawValue()) .. "]")
			else
				putString("[LV" .. (template.level or 1) .. "][" .. template.name .. "][" 
							.. getValueDescribe(template:getRawValue()) .. "]")
			end
			
			putInt(_EquipQualityGetColor(template.color or EquipQuality.White))
			
			fill = fill + 1
		else
			log.error("NPC", npcid, "出售任务", self.id,"中", v, "不存在！")
		end
	end
	
	fillPlaceholder('b', fill)
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _BuyTask:showItemInfo(jrole, npcid, deep, tid, num)
	if not self:checkTid(tid) then
		log.error("出售任务 " , self.id , "并未包含" , tid)
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
		return
	end
	
	local template = ItemSet[tid]
	if not template then
		log.error("购买的物品", tid, "不存在！")
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
		return
	end
	
	local describe = template:getDescribe()
	
	local unitPrice = ItemSet[tid]:getRawValue()
	
	local regular
	if template.type == ItemConst.Prop then
		local gold = _RoleGetGold(jrole)
		local power = gold / unitPrice - gold / unitPrice % 1
		regular = table.getNoMoreThan({1, 2, 5, 10, 20, 50, 100, }, power > 0 and power or 1)
	else--装备只能买一件
		regular = { 1, }
	end
	
	prepareBody()
	
	putShort(0)
	
	fillNpcItemOP(	template:getDescribe(), 
					_EquipQualityGetColor(template.color or EquipQuality.White), 
					regular,
					self.buyMenu)
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _BuyTask:checkTid(tid)
	for _, v in pairs(self.sell) do
		if v == tid then
			return true
		end
	end
end

--这里客户端带过来的一定是tid
function _BuyTask:buyItem(jrole, npcid, deep, tid, num)

	if num <= 0 then 
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "购买失败！")
		return
	end
	
	if not self:checkTid(tid) then
		log.error("出售任务 " , self.id , "并未包含" , tid)
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
		return
	end

	if not Bag:checkAdd(jrole, {items = { [tid] = num, }, } ) then
		replyMessage(jrole, 3, MsgID.MsgID_Talk_To_Npc_Resp, "包裹已满，请先清理包裹！")
		return
	end
	
	local template = ItemSet[tid]
	if template.type == ItemConst.Equip then
		num = 1
	end
	
	local gold = _RoleGetGold(jrole)
	local cost = template:getRawValue() * num
	if cost > gold then
		replyMessage(jrole, 4, MsgID.MsgID_Talk_To_Npc_Resp, "资金不足，无法购买！")
		return
	end
	
	_RoleSetGold(jrole, gold - cost)
	
	prepareBody()
	
	putShort(0)
	
	fillNpcRewind(2, self.id, "购买成功，获得：/" .. template.name .. "×" .. num)
	
	fillAttributes(jrole)
	
	local itemsTbl = template:creatJavaItem(num)
	for _, v in ipairs(itemsTbl) do
		fillBagAdd(v)
		local tid = _ItemGetTid(v)
		local uid = _ItemGetUid(v)
		local num=_ItemGetStorage(v)
		log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$10#$" .. ItemSet[tid].name .."#$" .. num.. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(tid).."#$".._Long2String(uid))
	end
	
	Bag:addJItem(jrole, itemsTbl)
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)

	--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$10#$" .. template.name .. "#$" .. num .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$" .. cost .. "")
end
