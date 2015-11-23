
_DailyTask = _CommonTask:new()

function _DailyTask:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

_DailyTask._layout = {
	extra = {
		"刷新时间",
	}
}

-------------------------------------------------------------------------------
--子任务交付
function _DailyTask:consignSubtask(jrole)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	
	local step = task_state and _GetTaskStateStep(task_state) or 1
	task_state = task_state or luajava.new(TaskState, self.id)
	if step == #self.detail then
		_CompleteTask(task_state)
		_TaskStateExtraSetI(task_state, 1, _GetNextFreshMin(self.freshOnTheHour))
	else
		_CompleteSubtask(_GetSubState(task_state))
	end

	_AddTaskState(role_task, task_state)
end

function _DailyTask:acceptTask(jrole)
	local task_state = self:getAcceptState(jrole)
	if not task_state then
		return
	end
	
	_TaskStateExtraSetI(task_state, 1, _GetNextFreshMin(self.freshOnTheHour))
	
	_AddTaskState(_GetRoleTask(jrole), task_state)
	
	prepareBody()
	
	fillFreshNPCState(jrole)
	
	fillSystemPrompt("任务" .. self.name .. "已接受～")
	
	putShort(0)
	
	sendMsg(jrole)
end

function _DailyTask:consignTask(jrole)
	
	local task_state = luajava.new(TaskState, self.id)
	_CompleteTask(task_state)
	_TaskStateExtraSetI(task_state, 1, _GetNextFreshMin(self.freshOnTheHour))
	_AddTaskState(_GetRoleTask(jrole), task_state)
	
	prepareBody()
	
	fillFreshNPCState(jrole)
	
	fillSystemPrompt("任务" .. self.name .. "已完成～")
	
	putShort(0)
	
	sendMsg(jrole)
end

function _DailyTask:self_check(jrole, task_state, curMin)
	if _IsTaskFinished(task_state) then
		if Temp.task_version == 1 then
			return--直接清除每日任务数据使之成为可接
		elseif curMin >= _TaskStateExtraGetI(task_state, 1) then
			return--过了时间清除
		end
	end
	
	--未清除则调用_CommonTask的default_self_check
	return self:default_self_check(jrole, task_state, curMin)
end

