
local task21030 = {
	id = 21030,
	name = "开宝箱",
	openBoxNumber = 20,
}

function task21030:self_check(jrole, task_state, curMin)
	if curMin >= _TaskStateExtraGetI(task_state, 1) then
		self:reset_data(jrole, task_state)
	end
	
	return true
end

function task21030:reset_data(jrole, task_state)
	--设置金币宝箱的冷却时间
	 _TaskStateExtraSetI(task_state, 2, 0)
 	--设置已开启次数
 	_TaskStateExtraSetI(task_state, 3, 0)
 	local vipBuff = _RoleGetBuff(jrole, BuffConst.VIP)
 	if vipBuff then
	 	local vipLevel = _RoleGetBuffLevel(jrole, BuffConst.VIP)
	 	if vipLevel == 1 then
	 		--每天可以开启总次数 vip1多开5次
	 		_TaskStateExtraSetI(task_state, 4, self.openBoxNumber + 5)
	 	else
	 		--每天可以开启总次数 vip1多开10次
	 		_TaskStateExtraSetI(task_state, 4, self.openBoxNumber + 10)
	 	end
	else
		 _TaskStateExtraSetI(task_state, 4, self.openBoxNumber)
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
	--下次刷新时间
	_TaskStateExtraSetI(task_state, 1, _GetNextFreshMin(4))
end

rawset(TaskSet, 21030, task21030)
