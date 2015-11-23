
_RandomTask = _Task:newInstance()

function _RandomTask:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

_RandomTask._layout = {
	extra = {
		"刷新时间",--可选，每日任务中使用
		"当前子任务id或下一个子任务id（视子任务是否完成而定）",--必填
	}
}

-------------------------------------------------------------------------------
--随机任务必须在接受下个子任务之前就已经确定是什么子任务
--@return 任务状态：0忽略；1可接任务；2任务进行中；3可交任务
function _RandomTask:getState(jrole, npcid)
	local role_task = _GetRoleTask(jrole)
	
	local task_state = _GetTaskState(role_task, self.id)
	
	if task_state then--接受过任务
		---------------------------------------------------------------------------
		--任务完结判断
		if _IsTaskFinished(task_state) then
			return 0
		end
		--任务完结判断
		---------------------------------------------------------------------------
		--返回子任务状态
		local sub_state = _GetSubState(task_state)
		local subtaskid = _TaskStateExtraGetI(task_state, 2)
		
		local element = TaskElementSet[subtaskid]
		
		if _IsSubtastFinished(sub_state) then
			local next = element
			if next and npcid == next:getAcceptNpc().id then--当前NPC是下一环接受NPC
				return 1
			end
		else
			return element:getState(jrole, npcid, sub_state)
		end
		--返回子任务状态
		---------------------------------------------------------------------------
	else--未接受任务
		---------------------------------------------------------------------------
		--帮派判断
		if self.gangLevel then
			if (not _IsGangLoaded(_RoleGetGangid(jrole))) or (self.gangLevel ~= _RoleGetGangLevel(jrole)) then
				return 0
			end
		end
		--帮派判断
		---------------------------------------------------------------------------
		--等级判断
		local role_level = _GetRoleLevel(jrole)
		if role_level < self.minLevel or role_level > self.maxLevel then
			return 0
		end
		--等级判断
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
		--返回初始任务状态
		if #self.detail > 0 then
			local element = TaskElementSet[self.detail[1]]
			if element then
				local npc = element:getAcceptNpc()
				if npc and npc.id == npcid then
					return 1
				end
			end
		end
		--返回初始任务状态
		---------------------------------------------------------------------------
	end

	return 0
end

-------------------------------------------------------------------------------
--在NPC对话列表处的显示
function _RandomTask:shownInTalkList(jrole, npcid)
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
function _RandomTask:getStateInMEMOS(jrole, diff)
	---------------------------------------------------------------------------
	--等级判断
	local role_level = _GetRoleLevel(jrole)
	if (role_level < (self.minLevel or 1) - 3) or (role_level > self.maxLevel) then
		return
	end
	--等级判断
	---------------------------------------------------------------------------
	--依赖任务判断
	if self.depends and self.depends > 0 then
		local depend_state = _GetTaskState(_GetRoleTask(jrole), self.depends)
		if not depend_state or not _IsTaskFinished(depend_state) then			
			return 0--依赖任务未完成，不可接受
		end
	end
	--依赖任务判断
	---------------------------------------------------------------------------
	
	if #self.detail == 0 then
		return
	end
	
	local element = TaskElementSet[self.detail[1]]
	local npc = element:getAcceptNpc()
	if not npc then
		return
	end
	
	local location = npc:getLocation()
	if not location then
		return
	end
	
	return true, npc:getLocation()
end

-------------------------------------------------------------------------------
--在备忘中显示
function _RandomTask:showInMEMOS(jrole, diff)
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

function _RandomTask:showInTaskList(jrole, task_state)
	local sub_state = _GetSubState(task_state)
	local step = _GetTaskStateStep(task_state)
	
	local subtaskid = _TaskStateExtraGetI(task_state, 2)
	local element = TaskElementSet[subtaskid]
		
	-------------------------------------------------------------------------------
	--计算寻路坐标
	local location
	if _IsSubtastFinished(sub_state) then
		location = element:findPath(jrole)
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
		putString("去找" .. element:getAcceptNpc().nick .. "！")--提示去找下一环接受NPC
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
function _RandomTask:dialog(jrole, npcid, deep)

	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	
	local element
	local sub_state
	
	if task_state then
		if _IsTaskFinished(task_state) then
			replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, "该任务已完成，请勿连续点击！")
			return
		end
		
		sub_state = _GetSubState(task_state)
		local subtaskid = _TaskStateExtraGetI(task_state, 2)
		element = TaskElementSet[subtaskid]
	else
		element = TaskElementSet[self.detail[1]]
	end
	
	element:dialog(jrole, npcid, deep, self, sub_state)--进入子任务逻辑
end

-------------------------------------------------------------------------------
--子任务接受
function _RandomTask:accpetSubtask(jrole)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	
	if task_state then
		_TaskStateStepUp(task_state)
	else
		local task_state = luajava.new(TaskState, self.id)
		_TaskStateExtraSetI(task_state, 2, self.detail[1])
		_AddTaskState(role_task, task_state)
	end
end

-------------------------------------------------------------------------------
--子任务交付
function _RandomTask:consignSubtask(jrole)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	
	local step = (task_state and _GetTaskStateStep(task_state) or 1)
	if self.recurrent and step == self.maxStep then
		_DelTaskState(role_task, self.id)
	else
		task_state = task_state or luajava.new(TaskState, self.id)
		if step == self.maxStep then
			_CompleteTask(task_state)
		else
			_CompleteSubtask(_GetSubState(task_state))
			local subid = self.detail[math.random(2, #self.detail)]
			_TaskStateExtraSetI(task_state, 2, self.detail[math.random(2, #self.detail)])
		end
		_AddTaskState(role_task, task_state)
	end
end

function _RandomTask:getAcceptState(jrole)
	if not TaskElementSet[self.detail[1]]:getAcceptState() then
		log.error("接受任务[" .. self.name .. "][" .. self.id .. "]失败，此任务不允许通过命令接受！")
		return
	end
	
	local task_state = luajava.new(TaskState, self.id)
	_TaskStateExtraSetI(task_state, 2, self.detail[1])
	
	return task_state
end

-------------------------------------------------------------------------------
--命令行接受任务
function _RandomTask:acceptTask(jrole)
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
function _RandomTask:consignTask(jrole)
	local task_state = luajava.new(TaskState, self.id)
	_CompleteTask(task_state)
	_AddTaskState(_GetRoleTask(jrole), task_state)
	
	prepareBody()
	
	fillFreshNPCState(jrole)
	
	fillSystemPrompt("任务" .. self.name .. "已完成～")
	
	putShort(0)
	
	sendMsg(jrole)
end

function _RandomTask:quickFinish(jrole, step)
	log.info("此任务无法使用该命令！")
	return
end
	