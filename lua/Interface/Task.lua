
----------------------------------------------------------------------------------------
-------------------------------------获取所有已接任务------------------------------------
function getAccepted(jrole)
	prepareBody()
	putShort(0)
	putShort(0)
	
	placeholder('b')
	
	local wrote = 0
	
	for k, v in jmapIter(_GetTaskStates(_GetRoleTask(jrole))) do
		if not _IsTaskFinished(v) then
			local task = TaskSet[k]
			if task and task.showInTaskList and task:showInTaskList(jrole, v) then
				wrote = wrote + 1
				if wrote >= 20 then
					break
				end
			end
		end
	end

	fillPlaceholder('b', wrote)
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Task_List_Resp)
end

----------------------------------------------------------------------------------------
-------------------------------------获取所有可接任务------------------------------------
function getAcceptable(jrole)
	prepareBody()
	putShort(0)
	putShort(0)
	
	placeholder('b')
	
	local wrote = 0
	
	local role_task = _GetRoleTask(jrole)
	
	local gangId = _RoleGetGangid(jrole)
	
	for k, v in pairs(TaskSet) do
		
		if ( not _GetTaskState(role_task, k) and gangId == 0 and v.gangLevel == nil ) or ( not _GetTaskState(role_task, k) and gangId > 0 ) then
			if v.showInMEMOS and v:showInMEMOS(jrole, 3) then			
				wrote = wrote + 1
				if wrote == 20 then
					break
				end
			end
		end
	end
	
	fillPlaceholder('b', wrote)
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Task_List_Resp)
end

----------------------------------------------------------------------------------------
------------------------------------------取消任务---------------------------------------
function cancelTask(jrole, taskid)
	local task = TaskSet[taskid]
	
	local cancel = task:cancelTask(jrole)
	prepareBody()
	if cancel then
		putShort(0)
		putString("任务【" .. task.name .. "】已放弃！")
	else
		putShort(1)
		putString("放弃失败！")
	end
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Task_Del_Resp)
	
	prepareBody()
	
	fillFreshNPCState(jrole)
	
	putShort(0)
	
	sendMsg(jrole)
end
function checkHemony(jrole)
 local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, 24000)
	if task_state and (not _IsTaskFinished(task_state)) then
	print(jrole:getName())
	     local task = TaskSet[24000]
	          _SetHegemonyComplete(jrole)
             _CompleteTask(task_state)
             _TaskStateExtraSetI(task_state, 1, _GetNextFreshMin(task.freshOnTheHour))
	         _AddTaskState(_GetRoleTask(jrole), task_state)
    end
  end

--角色上线的时候整理任务状态
function checkTask(jrole)
	local role_task = _GetRoleTask(jrole)
	local curDay = _GetDay()
	local curMin = _GetMinute()
	local curSeconed = _GetSecond()
	
	Temp.task_version = Temp.task_version or _GetTaskVersion()
	
	for k, v, iter in jmapIter(_GetTaskStates(role_task)) do
			local task = TaskSet[k]
			if not task then
				_IterRemove(iter)
			elseif not task:self_check(jrole, v, curMin, curSeconed) then
				_IterRemove(iter)
			end

	end
	
	local TimingAward = table.getglobal("Temp.Task").TimingAward
	if TimingAward then
		if not _GetTaskState(role_task, TimingAward) then
			_AddTaskState(role_task, TaskSet[TimingAward]:newState(jrole, curSeconed))
		end
	end
		
end
