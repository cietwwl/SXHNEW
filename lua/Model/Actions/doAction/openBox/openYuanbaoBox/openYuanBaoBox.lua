Actions.openYuanbaoBox = {
	name = 'openYuanbaoBox',
	task = 21030,
}

function Actions.openYuanbaoBox:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	if jrole then
		local role_task = _GetRoleTask(jrole)
		
		local task_state = _GetTaskState(role_task, 21030)
		--取系统当前时间
		local systemTime = _GetMinute()
				
		
		if (not task_state) or (systemTime >= _TaskStateExtraGetI(task_state, 1)) then
			task_state = luajava.new(TaskState, 21030)
			_AddTaskState(role_task, task_state)
			--下次刷新时间,现在是4点
			_TaskStateExtraSetI(task_state, 1, _GetNextFreshMin(4))
			
			--设置金币宝箱的冷却时间
		 	_TaskStateExtraSetI(task_state, 2, 0)
		 	--设置已开启次数
		 	_TaskStateExtraSetI(task_state, 3, 0)
		 	local vipBuff = _RoleGetBuff(jrole, BuffConst.VIP)
		 	if vipBuff then
			 	local vipLevel = _RoleGetBuffLevel(jrole, BuffConst.VIP)
			 	if vipLevel == 2 then
			 		--每天可以开启总次数 vip2多开5次
			 		_TaskStateExtraSetI(task_state, 4, TaskSet[self.task].openBoxNumber + 5)
			 	end
			 	if vipLevel == 3 then
			 		--每天可以开启总次数 vip3多开10次
			 		_TaskStateExtraSetI(task_state, 4, TaskSet[self.task].openBoxNumber + 10)
			 	end
			else
				 _TaskStateExtraSetI(task_state, 4, TaskSet[self.task].openBoxNumber)
			end
		 	--设置金币的金宝箱开启状态为未开启
		 	_TaskStateExtraSetI(task_state, 5, 0)
		 	--设置金币的银宝箱开启状态为未开启
		 	_TaskStateExtraSetI(task_state, 6, 0)
		 	--设置金币的铜宝箱开启状态为未开启
		 	_TaskStateExtraSetI(task_state, 7, 0)
		 	--设置元宝宝箱冷却时间
			_TaskStateExtraSetI(task_state, 8, 0)
			--设置元宝的金宝箱开启状态为未开启
			_TaskStateExtraSetI(task_state, 9, 0)
			--设置元宝的银宝箱开启状态为未开启
			_TaskStateExtraSetI(task_state, 10, 0)
			--设置元宝的铜宝箱开启状态为未开启
			_TaskStateExtraSetI(task_state, 11, 0)
				
		end
		
	end
	self:doDisplay(jrole)
end