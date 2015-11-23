
_ExchangeTask = _Task:newInstance()

_ExchangeTask.buyMenu = {
	{ 1, "兑换", 0, },
	{ 2, "返回", 0, },
}

_ExchangeTask._layout = {
	extra = {
		"刷新时间",
		"可用积分",
		"当日次数",
		"每日次数上限（0表示默认）",
	}
}

function _ExchangeTask:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

--@return 任务状态：0忽略；1可接任务；2任务进行中；3可交任务
function _ExchangeTask:getState(jrole, npcid)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	
	if not task_state and npcid == self.acceptNpc and _GetRoleLevel(jrole) >= self.minLevel then
		return 1
	end
	
	if task_state and npcid == self.consignNpc then
		return 3
	end
	
	return 0
end

function _ExchangeTask:shownInTalkList(jrole, npcid)
	local state = self:getState(jrole, npcid)
	
	if state == 1 then
		putInt(self.id)
		putLong(0)
		putByte(1)
		putString(self.name .. "（可接受）")
		putInt(getRGB(self.color))
		
		return true
	elseif state == 3 then
		putInt(self.id)
		putLong(0)
		putByte(1)
		putString(self.consignListName)
		putInt(getRGB(self.color))
		
		return true
	end
end

function _ExchangeTask:dialog(jrole, npcid, deep, uid, num)
	local state = self:getState(jrole, npcid)
	if state == 1 then
		self:acceptDialog(jrole, npcid, deep)
	else
		if deep == 1 then
			self:listItems(jrole, npcid, deep, uid, num)
		elseif deep == 2 then
			self:showItemInfo(jrole, npcid, deep, uid, num)
		elseif deep == 3 then
			self:buyItem(jrole, npcid, deep, uid, num)
		else
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
		end
	end
end

function _ExchangeTask:acceptDialog(jrole, npcid, deep)
	prepareBody()
	putShort(0)
	
	if deep <= #self.acceptDialogs then
		fillNpcDialog(self.id, 0, 1, self.name .. "/" .. self.acceptDialogs[deep], self.color)
	else
		fillNpcDialog(self.id, 0, 0, "任务【" .. self.name .. "】已接受", self.color)
		
		local task_state = luajava.new(TaskState, self.id)
		_TaskStateExtraSetI(task_state, 1, _GetNextFreshMin(0))
		_AddTaskState(_GetRoleTask(jrole), task_state)
		
		fillFreshNPCState(jrole)
	end
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _ExchangeTask:listItems(jrole, npcid, deep, uid, num)
	prepareBody()
	
	putShort(0)
	
	putShort(300)--列表形式
	
	placeholder('b')--列表项数量
	
	local fill = 0
	
	for k, v in pairs(self.exchange) do
		local template = ItemSet[k]
		if template then
			putInt(self.id)
			putLong(k)
			putByte(2)
			
			if template:isDedicated() then
				putString("[LV" .. (template.level or 1) .. "][" .. _ObjectToString(template.vocation) .. "][" 
							.. template.name .. "][" .. v .. "积分]")
			else
				putString("[LV" .. (template.level or 1) .. "][" .. template.name .. "][" .. v .. "积分]")
			end
			
			putInt(_EquipQualityGetColor(template.color or EquipQuality.White))
			
			fill = fill + 1
		else
			log.error("NPC", npcid, "出售物品", k, "为空！")
		end
	end
	
	fillPlaceholder('b', fill)
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _ExchangeTask:showItemInfo(jrole, npcid, deep, tid, num)
	if not self.exchange[tid] then
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
		return
	end
	
	local template = ItemSet[tid]
	if not template then
		replyMessage(jrole, 3, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
		return
	end
	
	local unitPrice = self.exchange[tid]
	
	local regular
	if template.type == ItemConst.Prop then
		local power = self:getPurchasePower(jrole, unitPrice)
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

function _ExchangeTask:getPurchasePower(jrole, unitPrice)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	
	if not task_state then
		return 0
	end
	
	local mark = _TaskStateExtraGetI(task_state, 2)
	
	return mark / unitPrice - mark / unitPrice % 1
end

function _ExchangeTask:buyItem(jrole, npcid, deep, tid, num)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	
	if not task_state then
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "兑换失败！/尚未接受任务【" .. self.name .. "】！")
		return
	end
	
	if not self.exchange[tid] then
		replyMessage(jrole, 3, MsgID.MsgID_Talk_To_Npc_Resp, "兑换失败！")
		return
	end
	
	local template = ItemSet[tid]
	if not template then
		replyMessage(jrole, 3, MsgID.MsgID_Talk_To_Npc_Resp, "兑换失败！")
		return
	end
	
	if not Bag:checkAdd(jrole, {items = { [tid] = num, }, } ) then
		replyMessage(jrole, 4, MsgID.MsgID_Talk_To_Npc_Resp, "包裹已满，请先清理包裹！")
		return
	end
	
	local mark = _TaskStateExtraGetI(task_state, 2)
	
	--local mark = _RoleGetMark(jrole)
	
	local cost = self.exchange[tid] * num
	
	
	if cost > mark then
		replyMessage(jrole, 5, MsgID.MsgID_Item_Do_Resp, "积分不足，无法兑换！")
		return
	end
	
	_TaskStateExtraSetI(task_state, 2, mark - cost)
	
	--_RoleSetMark(jrole,mark - cost)
	
	
	local itemsTbl = template:creatJavaItem(num)
	
	prepareBody()
	
	putShort(0)
	
	fillNpcRewind(2, self.id, "兑换成功，获得：/" .. template.name .. "×" .. num)
	
	for _, v in ipairs(itemsTbl) do
		fillBagAdd(v)
		local tid = _ItemGetTid(v)
	    local uid = _ItemGetUid(v)
	    local itemnum=_ItemGetStorage(v)
		log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$8#$" .. template.name .. "#$" .. itemnum .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(tid).."#$".._Long2String(uid))	
	end
	
	Bag:addJItem(jrole, itemsTbl)
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
	
	--log.item(jrole:getRoleid(), "[兑换物品][" .. template.name .. "][" .. num .. "个]")

	--log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$8#$" .. template.name .. "#$" .. num .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
end

function _ExchangeTask:getDescribe(jrole, task_state)
	local tbl = {}
	tbl[#tbl + 1] = self.prompt
	tbl[#tbl + 1] = [[排行榜积分：]] .. _RoleGetMark(jrole)
	tbl[#tbl + 1] = [==[可用消费积分：]==] .. _TaskStateExtraGetI(task_state, 2)
	
	local dayUpperLimit = _TaskStateExtraGetI(task_state, 4)
	if dayUpperLimit == 0 then
		dayUpperLimit = self.dayUpperLimit
	end
	
	tbl[#tbl + 1] = [==[场次：]==] .. _TaskStateExtraGetI(task_state, 3) .. "|" .. dayUpperLimit
	
	return  table.concat(tbl, "/")
end

function _ExchangeTask:showInTaskList(jrole, task_state)
	local location = NPCSet[self.consignNpc]:getLocation()
	if not location then
		return
	end
	
	-------------------------------------------------------------------------------
	--填充任务基本属性
	putByte(self.category)
	putInt(self.id)
	putString(self.name)
	putInt(self.color)--任务名称颜色
	--填充任务基本属性
	-------------------------------------------------------------------------------
	--填充任务提示
	putString(self:getDescribe(jrole, task_state))
	putInt(self.color)--任务提示颜色
	--填充任务提示
	-------------------------------------------------------------------------------
	putByte(2)
	
	putByte(0)--菜单/取消提示
	putString("立即前往")
	putInt(0)--颜色
	putShort(location.mapid)
	putInt(location.mapx)
	putInt(location.mapy)
	
	--[[
	putByte(1)--菜单/放弃任务
	putString("放弃任务")
	putInt(0)--颜色
	putShort(0)
	putInt(0)
	putInt(0)
	--]]
	
	putByte(2)--菜单/取消提示
	putString("取消提示")
	putInt(0)--颜色
	putShort(0)
	putInt(0)
	putInt(0)
	
	return true
end

function _ExchangeTask:showInMEMOS(jrole, diff)
	local role_level = _GetRoleLevel(jrole)
	if role_level < (self.minLevel or 1) - diff then
		return
	end
	
	local location = NPCSet[self.acceptNpc]:getLocation()
	if not location then
		return
	end
	
	putByte(self.category)
	putInt(self.id)
	
	if self.minLevel > role_level then
		putString("LV" .. self.minLevel .. " " .. self.name .. "（未达级别）")--任务名称
		putInt(0x00ff0000)--任务名称颜色
		
		putShort(0)--描述长度
		putInt(0)--描述颜色
		
		putByte(0)--菜单数量0
	else
		putString("LV" .. (self.minLevel > 0 and self.minLevel or 1) .. " " .. self.name)--任务名称
		putInt(self.color)--任务名称颜色
		
		putShort(0)--描述长度
		putInt(0)--描述颜色
		
		putByte(2)--两个菜单

		putByte(0)--菜单/寻路
		putString("立即前往")
		putInt(0)--颜色
		putShort(location.mapid)
		putInt(location.mapx)
		putInt(location.mapy)
		
		putByte(2)--菜单/取消提示
		putString("取消提示")
		putInt(0)--颜色
		putShort(0)
		putInt(0)
		putInt(0)
	end
	
	return true
end

function _ExchangeTask:getAcceptState(jrole)
	local task_state = luajava.new(TaskState, self.id)
	_TaskStateExtraSetI(task_state, 1, _GetNextFreshMin(0))
	return task_state
end

function _ExchangeTask:update(jrole, task_state, winner)
	local curDayDuels = _TaskStateExtraGetI(task_state, 3)
	if curDayDuels < self.dayUpperLimit then
		_TaskStateExtraSetI(task_state, 3, curDayDuels + 1)
		
		local curMark = _TaskStateExtraGetI(task_state, 2)
		
		local mark = winner and self.winner_mark or self.loser_mark
		mark = _RoleHasBuff(jrole, BuffConst.MARK) and  _RoleFixValueAfterBuff(jrole, BuffConst.MARK, mark) or mark
		
		if winner then
			_TaskStateExtraSetI(task_state, 2, curMark + mark)
			_RoleSetMark(jrole, _RoleGetMark(jrole) + mark)
			return "战斗胜利！/获得积分：" .. mark
		else
			_TaskStateExtraSetI(task_state, 2, curMark + mark)
			_RoleSetMark(jrole, _RoleGetMark(jrole) + mark)
			return "战斗失败！/获得积分：" .. mark
		end
	else
		if winner then
			return "战斗胜利！"
		else
			return "战斗失败！"
		end
	end
end

function _ExchangeTask:self_check(jrole, task_state, curMin)
	if curMin > _TaskStateExtraGetI(task_state, 1) then
		_TaskStateExtraSetI(task_state, 1, _GetNextFreshMin(0))
		_TaskStateExtraSetI(task_state, 3, 0)
	end
			
	return true
end

function _ExchangeTask:cancelTask(jrole)
end
