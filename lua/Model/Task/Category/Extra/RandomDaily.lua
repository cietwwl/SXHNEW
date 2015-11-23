
_RandomDaily = _RandomTask:new()

function _RandomDaily:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

_RandomDaily._layout = {
	extra = {
		"刷新时间",
		"当前子任务id或下一个子任务id（视子任务是否完成而定）",--必填
	}
}

-------------------------------------------------------------------------------
--子任务交付
function _RandomDaily:consignSubtask(jrole)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	
	local step = (task_state and _GetTaskStateStep(task_state) or 1)
	task_state = task_state or luajava.new(TaskState, self.id)
	if step == self.maxStep then
		_CompleteTask(task_state)
		_TaskStateExtraSetI(task_state, 1, _GetNextFreshMin(self.freshOnTheHour))
	else
		_CompleteSubtask(_GetSubState(task_state))
		_TaskStateExtraSetI(task_state, 2, self.detail[math.random(2, #self.detail)])
	end

	_AddTaskState(role_task, task_state)
end

function _RandomDaily:acceptTask(jrole)
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

function _RandomDaily:consignTask(jrole)
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
