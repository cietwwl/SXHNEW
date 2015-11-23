
function daybreakSchedule(curDay)
	log.info("daybreakSchedule", "日期变更")
	
	for k in jlistIter(_GetAllOnlines()) do
		local jrole = _GetOnline(k)
		if jrole then
			local role_task = _GetRoleTask(jrole)
			local task_state = _GetTaskState(role_task, table.getglobal("Temp.Task").Exchange)
			if task_state then
				_TaskStateExtraSetI(task_state, 1, _GetNextFreshMin(0))
				_TaskStateExtraSetI(task_state, 3, 0)
			end
		end
	end
	
	LuaRuntime = { }
	
	log.info("daybreakSchedule", "日期变更操作完成")
end
