
_CreatGang = _Task:newInstance()

function _CreatGang:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _CreatGang:getState(jrole)
	if _GetRoleLevel(jrole) < self.level or _RoleGetGangid(jrole) > 0 then
		return 0
	elseif _GetTaskState(_GetRoleTask(jrole), self.id) then
		return 3
	else
		return 1
	end
end

function _CreatGang:shownInTalkList(jrole)
	local state = self:getState(jrole)
	if state == 0 then
		return
	end
	
	putInt(self.id)
	putLong(0)
	putByte(1)--直接对话，自动扣物品
	putString(self.name .. (state == 3 and "（交任务）" or ""))
	putInt(self.color)
	return true
end

-------------------------------------------------------------------------------
--在任务列表中显示
function _CreatGang:showInTaskList(jrole, task_state)
	local npc = NPCSet[self.consignNpc]
	if not npc then
		return
	end
	-------------------------------------------------------------------------------
	--计算寻路坐标
	local location = npc:getLocation()
	if not location then
		return
	end
	
	--计算寻路坐标
	-------------------------------------------------------------------------------
	--填充任务基本属性
	putByte(self.category)
	putInt(self.id)
	putString(self.name)
	putInt(self.color)--任务名称颜色
	--填充任务基本属性
	-------------------------------------------------------------------------------
	--填充任务提示
	putString(self.prompt)--提示去找下一环接受NPC
	putInt(self.color)--任务提示颜色
	--填充任务提示
	-------------------------------------------------------------------------------
	putByte(3)
	
	putByte(0)--菜单/取消提示
	putString("立即前往")
	putInt(0)--颜色
	putShort(location.mapid)
	putInt(location.mapx)
	putInt(location.mapy)
	
	putByte(1)--菜单/放弃任务
	putString("放弃任务")
	putInt(0)--颜色
	putShort(0)
	putInt(0)
	putInt(0)
	
	putByte(2)--菜单/取消提示
	putString("取消提示")
	putInt(0)--颜色
	putShort(0)
	putInt(0)
	putInt(0)
	
	return true
end

function _CreatGang:dialog(jrole, npcid, deep)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	
	if task_state then
		self:consignDialog(jrole, npcid, deep)
	else
		self:acceptDialog(jrole, npcid, deep)
	end
end

function _CreatGang:acceptDialog(jrole, npcid, deep)
	if deep <= #self.acceptDialogs then
		prepareBody()
		putShort(0)
		fillNpcDialog(self.id, 0, 1, self.name .. "/" .. self.acceptDialogs[deep], self.color)
		sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
	elseif _RoleGetGangid(jrole) > 0 then
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "已经加入帮派！")
	else
		local result = GangSet[1]:belowRequired(jrole)
		if result then
			replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "无法接受该任务！/" .. result)
			return
		end
		
		prepareBody()
		putShort(0)
		
		_AddTaskState(_GetRoleTask(jrole), luajava.new(TaskState, self.id))
		
		fillNpcDialog(self.id, 0, 0, "任务【" .. self.name .. "】已接受", self.color)
	
		fillFreshNPCState(jrole)
		
		sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
	end
end

function _CreatGang:consignDialog(jrole, npcid, deep)
	if deep > #self.consignDialogs then
		return
	end
	
	prepareBody()
	putShort(0)
	fillNpcDialog(self.id, 0, deep == #self.consignDialogs and 3 or 1, 
					self.name .. "/" .. self.consignDialogs[deep], self.color)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _CreatGang:checkConditions(jrole)
	if Bag:getItemCountByTid(jrole, self.prop) == 0 then
		replyMessage(jrole, 10, MsgID.MsgID_Gang_Creat_Resp, "创建失败，没有相关道具！");
		return false
	end
	
	if _RoleGetCharm(jrole) < (self.charm or 99999999) then
		replyMessage(jrole, 11, MsgID.MsgID_Gang_Creat_Resp, "创建失败，声望不足！");
		return false
	end
	
	if _RoleGetGold(jrole) < self.gold then
		replyMessage(jrole, 12, MsgID.MsgID_Gang_Creat_Resp, "创建失败，资金不足！");
		return false
	end
	
	return true
end

function _CreatGang:chargeback(jrole)
	fillBagDel(self.prop)
	Bag:delItem(jrole, self.prop, 1)
	
	local tid = self.prop
	local uid = self.prop
	log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$19#$" .. ItemSet[tid].name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(tid).."#$".._Long2String(uid))
	
	_RoleSetGold(jrole, _RoleGetGold(jrole) - self.gold)

	log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$20#$创建帮会消耗所有物品#$创建帮会消耗个数#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$" .. self.gold .. "")
	
	fillAttributes(jrole)
end
