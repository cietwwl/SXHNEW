_CommonTask = _Task:newInstance()

function _CommonTask:new(o)
	
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

-------------------------------------------------------------------------------
--@return 任务状态：0忽略；1可接任务；2任务进行中；3可交任务
function _CommonTask:getState(jrole, npcid)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
    
	if task_state then
	
		if _IsTaskFinished(task_state) then
			return 0--此任务已经完结
		end

		local step = _GetTaskStateStep(task_state)
		local sub_state = _GetSubState(task_state)
		if _IsSubtastFinished(sub_state) then
			local next = TaskElementSet[self.detail[step + 1]]
			if next and npcid == next:getAcceptNpc().id then--当前NPC是下一环接受NPC
				return 1
			end
		else
			local element = TaskElementSet[self.detail[step]]
			return element:getState(jrole, npcid, sub_state)
		end
	else
		if self.gangLevel then
			if (not _IsGangLoaded(_RoleGetGangid(jrole))) or (self.gangLevel ~= _RoleGetGangLevel(jrole)) then
				return 0
			end
		end

		---------------------------------------------------------------------------
		--接受等级不够
		local role_level = _GetRoleLevel(jrole)
		if self.minLevel and self.maxLevel then
			if role_level < self.minLevel or role_level > self.maxLevel then
				return 0
			end
		end
		--接受等级不够
		---------------------------------------------------------------------------
		--依赖任务判断
		if self.depends and self.depends > 0 then
			local depend_state = _GetTaskState(role_task, self.depends)
			if not depend_state or not _IsTaskFinished(depend_state) then
				return 0--依赖任务未完成，不可接受
			end
		end
		--依赖任务判断
		---------------------------------------------------------------------------
		if #self.detail > 0 then
			local element = TaskElementSet[self.detail[1]]
			if element then
				local npc = element:getAcceptNpc()
				if npc and npc.id == npcid then
					return 1
				end
			end
		end
	end

	return 0
end

-------------------------------------------------------------------------------
--在NPC对话列表处的显示
function _CommonTask:shownInTalkList(jrole, npcid)
	local state = self:getState(jrole, npcid)
	if state > 0 then
		putInt(self.id)
		putLong(0)
		putByte(1)
		if state == 1 then
			putString(self.name .. "（可接受）")
		elseif state == 2 then
			putString(self.name .. "（未完成）")
		else
			putString(self.name .. "（可交付）")
		end
		putInt(0)

		return true
	end
end

-------------------------------------------------------------------------------
--@return 是否在备忘中显示以及寻路坐标
function _CommonTask:getStateInMEMOS(jrole, diff)
	local role_level = _GetRoleLevel(jrole)
	if (not self.minLevel)  or (not self.maxLevel) then
	     return
	end
	if (role_level < (self.minLevel or 1) - 3) or (role_level > (self.maxLevel or 1)) then
		return
	end

	local role_task = _GetRoleTask(jrole)

	if self.depends and self.depends > 0 then
		local depend_state = _GetTaskState(role_task, self.depends)
		if not depend_state or not _IsTaskFinished(depend_state) then
			return--依赖任务未完成，不可接受
		end
	end

	if #self.detail == 0 then
		return
	end

	local element = TaskElementSet[self.detail[1]]
	if not element then
		log.error("任务", self.name, "的子任务", self.detail[1], "不存在！")
		return
	end

	local npc = element:getAcceptNpc()
	if not npc then
		return
	end

	return true, npc:getLocation()
end

-------------------------------------------------------------------------------
--在备忘中显示
function _CommonTask:showInMEMOS(jrole, diff)
	local state, location = self:getStateInMEMOS(jrole, diff)

	if not state or not location then
		return
	end

	putByte(self.category)
	putInt(self.id)

	local role_level = _GetRoleLevel(jrole)

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

-------------------------------------------------------------------------------
--在任务列表中显示
function _CommonTask:showInTaskList(jrole, task_state)
	local sub_state = _GetSubState(task_state)
	local step = _GetTaskStateStep(task_state)
	local element = TaskElementSet[self.detail[step]]
	-------------------------------------------------------------------------------
	--计算寻路坐标
	local location
	if _IsSubtastFinished(sub_state) then
		local next = TaskElementSet[self.detail[step + 1]]
		if not next then
			log.error(_RoleGetCard(jrole), "：任务", self.name, "第", step + 1, "环异常！")
			return
		end
		location = next:findPath(jrole)
	else
		location = element:findPath(jrole, sub_state)
	end

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
	if _IsSubtastFinished(sub_state) then--上一环已完成
		local next = TaskElementSet[self.detail[step + 1]]--下一环
		putString("去找" .. next:getAcceptNpc().nick .. "！")--提示去找下一环接受NPC
	else
		putString(element:getDescribe(jrole, sub_state))--子任务提示
	end
	putInt(self.color)--任务提示颜色
	--填充任务提示
	-------------------------------------------------------------------------------
	putByte(self.noGiveUp and 2 or 3)

	putByte(0)--菜单/取消提示
	putString("立即前往")
	putInt(0)--颜色
	putShort(location.mapid)
	putInt(location.mapx)
	putInt(location.mapy)

	if not self.noGiveUp then
		putByte(1)--菜单/放弃任务
		putString("放弃任务")
		putInt(0)--颜色
		putShort(0)
		putInt(0)
		putInt(0)
	end

	putByte(2)--菜单/取消提示
	putString("取消提示")
	putInt(0)--颜色
	putShort(0)
	putInt(0)
	putInt(0)

	return true
end

-------------------------------------------------------------------------------
--与NPC对话，进入具体任务逻辑内部
function _CommonTask:dialog(jrole, npcid, deep)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)

	local element
	local sub_state

	if task_state then
		if _IsTaskFinished(task_state) then
			replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, "该任务已完成，请勿连续点击！")
			return
		end

		local step = _GetTaskStateStep(task_state)
		sub_state = _GetSubState(task_state)

		if _IsSubtastFinished(sub_state) then
			element = TaskElementSet[self.detail[step + 1]]
		else
			element = TaskElementSet[self.detail[step]]
		end
	else
		element = TaskElementSet[self.detail[1]]
	end

	element:dialog(jrole, npcid, deep, self, sub_state)--进入子任务逻辑
end

-------------------------------------------------------------------------------
--子任务接受
function _CommonTask:accpetSubtask(jrole)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)

	if task_state then
		_TaskStateStepUp(task_state)
	else
		_AddTaskState(role_task, luajava.new(TaskState, self.id))
	end
end

-------------------------------------------------------------------------------
--子任务交付
function _CommonTask:consignSubtask(jrole)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)

	local step = (task_state and _GetTaskStateStep(task_state) or 1)
	if self.recurrent and step == #self.detail then
		_DelTaskState(role_task, self.id)
	else
		task_state = task_state or luajava.new(TaskState, self.id)
		if step == #self.detail then
			_CompleteTask(task_state)
		else
			_CompleteSubtask(_GetSubState(task_state))
		end

		_AddTaskState(role_task, task_state)
	end
end

function _CommonTask:getAcceptState(jrole)
	if not TaskElementSet[self.detail[1]]:getAcceptState() then
		log.error("接受任务[" .. self.name .. "][" .. self.id .. "]失败，此任务不允许通过命令接受！")
		return
	end

	return luajava.new(TaskState, self.id)
end

-------------------------------------------------------------------------------
--命令行接受任务
function _CommonTask:acceptTask(jrole)
	local task_state = self:getAcceptState(jrole)
	if not task_state then
		return
	end

	_AddTaskState(_GetRoleTask(jrole), task_state)

	---------------------------------------------------------------------------
	--更新附近NPC状态并提示
	prepareBody()

	fillFreshNPCState(jrole)

	fillSystemPrompt("任务" .. self.name .. "已接受～")

	putShort(0)

	sendMsg(jrole)
	--更新附近NPC状态并提示
	---------------------------------------------------------------------------
end

-------------------------------------------------------------------------------
--命令行获取任务完成状态，没有给奖励
function _CommonTask:consignTask(jrole)
	local task_state = luajava.new(TaskState, self.id)
	_CompleteTask(task_state)
	_AddTaskState(_GetRoleTask(jrole), task_state)

	prepareBody()

	fillFreshNPCState(jrole)

	fillSystemPrompt("任务" .. self.name .. "已完成～")

	putShort(0)

	sendMsg(jrole)
end

function _CommonTask:quickFinish(jrole, step)
	step = step or #self.detail
	if step < 0 or step > #self.detail then
		log.info("任务步骤超出范围！")
		return
	end

	local task_state = luajava.new(TaskState, self.id, step)
	local sub_state = _GetSubState(task_state)

	local element = TaskElementSet[self.detail[step]]
	element:quickFinishState(jrole, sub_state)

	_AddTaskState(_GetRoleTask(jrole), task_state)

	prepareBody()

	fillFreshNPCState(jrole)

	putShort(0)

	sendMsg(jrole)
end

function _CommonTask:default_self_check(jrole, task_state, curMin)

	if not _IsTaskFinished(task_state) then
		local step = _GetTaskStateStep(task_state)
		local sub_state = _GetSubState(task_state)
		local element = TaskElementSet[self.detail[step]]
		if not element then
			return--失效，移除
		elseif step == 1 and _IsSubtastFinished(sub_state) and #self.detail == 1 then
			_CompleteTask(task_state)
		elseif element.type == TaskElement.NPCFight then
			if not _IsSubtastFinished(sub_state) and _GetSubstateI(sub_state, 1) == 0 then
				if step == 1 then
					return--失效，移除
				else
					_TaskStateStepDown(task_state)
				end
			end
		elseif element.type == TaskElement.NpcConvoy then
			if not _IsSubtastFinished(sub_state) then
				if step == 1 then
					return--失效，移除
				else
					_TaskStateStepDown(task_state)
				end
			end
		end
	end

	return true--状态正常
end

function _CommonTask:self_check(jrole, task_state, curMin)
	return self:default_self_check(jrole, task_state, curMin)
end

function _CommonTask:seconedTick(jrole, curSeconed)
	local role_task = _GetRoleTask(jrole)

	local task_state = _GetTaskState(role_task, self.id)

	if task_state then
		local step = _GetTaskStateStep(task_state)
		local sub_state = _GetSubState(task_state)

		if _IsSubtastFinished(sub_state) then
			return true
		else
			local element = TaskElementSet[self.detail[step]]
			return element:seconedTick(jrole, curSeconed, self)
		end
	end
end
