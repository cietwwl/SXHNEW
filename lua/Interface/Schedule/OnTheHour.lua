
function onTheHourSchedule(totalHour)
	log.info("onTheHourSchedule", totalHour, "整点刷新")
	
	local curHour = totalHour % 24
	local onTheHour = Daily[curHour - curHour % 1]
	
	if onTheHour then
		for k in jlistIter(_GetAllOnlines()) do
			local jrole = _GetOnline(k)
			if jrole then
				local role_task = _GetRoleTask(jrole)
				
				for _, v in pairs(onTheHour) do
					local task_state = _GetTaskState(role_task, v)
					if task_state then
						if _IsTaskFinished(task_state) then
							_DelTaskState(role_task, v)
						end
					end
				end
				
				if curHour == 4 then
					local task_state = _GetTaskState(role_task, 21030)
					if task_state then
						TaskSet[21030]:reset_data(jrole, task_state)
					end
				end
			end
		end
	end
	log.info("onTheHourSchedule", totalHour, "整点刷新操作完成")
end