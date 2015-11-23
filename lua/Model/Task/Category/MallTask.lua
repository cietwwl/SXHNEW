
_MallTask = _Task:newInstance()

_MallTask.buyMenu = {
	{ 1, "购买", 0, },
	{ 2, "返回", 0, },
}

function _MallTask:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _MallTask:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	putByte(1)
	putString(self.name)
	putInt(self.color or 0)
	return true
end

function _MallTask:dialog(jrole, npcid, deep, uid, num)
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

function _MallTask:listItems(jrole, npcid, deep, uid, num)
	prepareBody()
	
	putShort(303)
	
	putInt(self.id)
	
	putByte(#self.sell)
	
	for _,v in ipairs(self.sell) do
		putString(v.name)
		putInt(0)
	end
	
	placeholder('b')
	local fill = 0
	
	for k,v in ipairs(self.sell) do
		for _,vv in ipairs(v) do
			local item = ItemSet[vv.id]
			if item then
				putLong(vv.id)
				putByte(k)
				
				local unitPrice = self:getValue(vv.id)
				if unitPrice == 0 then
					putString(item.name .. "[展示]")
				else
					putString(item.name .. "[" .. unitPrice .. "元宝]")
				end
				
				putInt(_EquipQualityGetColor(item.color or EquipQuality.White))
				fill = fill + 1
			end
		end
	end
	
	fillPlaceholder('b', fill)
	
	putShort(0)

	sendMsg(jrole, MsgID.MsgID_Shop_GetItem_Resp)--发送这个消息id是为了让客户端切换状态
end

function _MallTask:showItemInfo(jrole, npcid, deep, tid, num)
	if not self:checkTid(tid) then
		log.error("商城任务 " , self.id , "并未包含" , tid)
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
		return
	end
	
	local template = ItemSet[tid]
	if not template then
		log.error("购买的商城物品", tid, "不存在！")
		replyMessage(jrole, 3, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
		return
	end
	
	local unitPrice = self:getValue(tid)
	
	if _RoleHasBuff(jrole, BuffConst.VIP) then
		unitPrice = _RoleFixValueAfterBuff(jrole, BuffConst.VIP, unitPrice)
	end
	
	local regular
	if template.type == ItemConst.Prop then
		local money = _RoleGetMoney(jrole)
		local power = money / unitPrice - money / unitPrice % 1
		regular = table.getNoMoreThan({1, 2, 5, 10, 20, 50, 100, }, power > 0 and power or 1)
	else--装备只能买一件
		regular = { 1, }
	end
	
	prepareBody()
	
	putShort(0)
	
	if template.mall_show then
		template:mall_show()
	else
		putShort(304)--Serial
	end
	
	putString(template:getDescribe())
	
	putInt(_EquipQualityGetColor(template.color or EquipQuality.White))
	
	putByte(#regular)
	
	for _, v in ipairs(regular) do
		putInt(v)
	end
	
	putByte(#self.buyMenu)
	
	for _, v in ipairs(self.buyMenu) do
		putByte(v[1])
		putString(v[2])
		putInt(v[3])
	end
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _MallTask:checkTid(tid)
	for _, v in pairs(self.sell) do
		for _,vv in ipairs(v) do
			if vv.id == tid then
				return true
			end
		end
	end
end

function _MallTask:getValue(tid)
	for _, v in pairs(self.sell) do
		for _,vv in ipairs(v) do
			if vv.id == tid then
				return vv.money
			end
		end
	end
end

function _MallTask:buyItem(jrole, npcid, deep, tid, num)
	if not self:checkTid(tid) then
		log.error("商城购买任务 " , self.id , "并未包含" , tid)
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "购买失败！")
		return
	end
	
	local template = ItemSet[tid]
	if not template then
		log.error("商城购买物品", tid, "不存在！")
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "购买失败！")
		return
	end
	
	if not Bag:checkAdd(jrole, {items = { [tid] = num, }, } ) then
		replyMessage(jrole, 3, MsgID.MsgID_Talk_To_Npc_Resp, "包裹已满，请先清理包裹！")
		return
	end
	
	local money = _RoleGetMoney(jrole)
	local originalAmt = self:getValue(template.id) * num
	local realAmt = originalAmt
	
	if _RoleHasBuff(jrole, BuffConst.VIP) then
		realAmt = _RoleFixValueAfterBuff(jrole, BuffConst.VIP, originalAmt)
	end
	
	if realAmt > money then
		replyMessage(jrole, 4, MsgID.MsgID_Talk_To_Npc_Resp, "资金不足，无法购买！")
		return
	end
	
	if realAmt == 0 then
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "该物品暂未开放, 敬请期待！")
		return
	end
	
	local gameSubtractInfo = luajava.new(GameSubtract, _RoleGetUserId(jrole), _RoleGetJoyId(jrole), realAmt, tid, num, originalAmt, self.id, yuanBaoOpConst.Buy_ITEM_FROM_MALL) 
	
	PayManager:postSubTask(_RoleGetYuanBaoOp(jrole), gameSubtractInfo)
end
