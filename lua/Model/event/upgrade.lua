
local event = table.getglobal("event")

function event.upgrade(jrole, fromLevel, toLevel)
	local role_task = _GetRoleTask(jrole)
	
	if toLevel == 50 then
		_LevelRmMaster(jrole)
	end
	

	_upLevelAddHPandMp(jrole)
	
	prepareBody()
	
	local ascender = fromLevel < toLevel and 1 or -1
	for level = fromLevel + (ascender > 0 and ascender or 0), toLevel, ascender do
		if UpgradeEvent[level] then
			for _, v in ipairs(UpgradeEvent[level]) do
				local task = TaskSet[v[1]]
				if task then
					if v.type * ascender == 1 then
						local task_state = task:getAcceptState(jrole)
						if task_state then
							_AddTaskState(role_task, task_state)
							fillSystemPrompt("任务【" .. task.name .. "】已接受～")
						end
					elseif v.type * ascender == -1 then
						_DelTaskState(role_task, v[1])
						fillSystemPrompt("任务【" .. task.name .. "】已消失～")
					end
				end
			end
		end
	end
	
	
	
	if getPosition() > 4 then
		putShort(0)
	
		sendMsg(jrole)
	end
end
