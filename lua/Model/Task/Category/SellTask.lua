
_SellTask = _Task:newInstance()

function _SellTask:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _SellTask:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	putByte(3)
	putString(self.name)
	putInt(0)
	
	return true
end

function _SellTask:dialog(jrole, npcid, deep, uid, num)
	if deep == 1 then
		self:getItemInfo(jrole, npcid, deep, uid, num)
	elseif deep == 2 then
		self:sellItem(jrole, npcid, deep, uid, num)
	else
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "出售失败！")
	end
end

function _SellTask:getItemInfo(jrole, npcid, deep, uid, num)
	if not Bag:checkInPack(jrole, uid) then
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
		return
	end
	
	local jItem, template, storage = Bag:getItemInPack(jrole, uid)
	
	local describe = template:getDescribe(jItem)
	
	prepareBody()
	
	putShort(0)
	
	fillNpcItemOP(	_ItemGetDescribe(jItem),
					template.color or 0, 
					table.getNoMoreThan({ 1, 2, 5, 10, 20, 50, 100, }, storage),
					template.sellMenu)
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _SellTask:sellItem(jrole, npcid, deep, uid, num)
	if not Bag:checkInPack(jrole, uid) then
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "出售失败！")
		return
	end
	
	local item, template, storage = Bag:getItemInPack(jrole, uid)
	
	if _ItemHasFeature(item, ItemFeature.NoSell) then
		replyMessage(jrole, 3, MsgID.MsgID_Talk_To_Npc_Resp, "该物品无法出售！")
		return
	end
	
	local sell = (num > storage) and storage or num
	
	local gold = template:getSellValue() * sell
	local finalGold = _RoleGetGold(jrole) + gold
	
	_RoleSetGold(jrole, finalGold)
	
	--log.item("[" .. getUTF8(jrole:getNick()) .. "][" .. _RoleGetId(jrole) .. "][出售物品][" .. template.name .. "][" .. sell .. "个]")

	--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$11#$" .. template.name .. "#$" .. sell .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$" .. gold .. "")
	
	log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$11#$" .. template.name .. "#$" .. sell .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$" .. gold .. "#$".._Long2String(template.id).."#$".._Long2String(uid))
	prepareBody()
	
	putShort(0)
	
	fillNpcRewind(1, self.id, "出售成功！/获得：" .. getValueDescribe(gold))

	fillAttributes(jrole)
	
	fillBagDel(uid, sell)
	
	Bag:delItem(jrole, uid, sell)

	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end
