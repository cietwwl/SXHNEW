_MartialCheatsTask = _CommonTask:new()
--武林秘闻
function _MartialCheatsTask:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end
function _MartialCheatsTask:dialog(jrole, npcid, deep,uid)
    local level
    if _GameCharacterGetLevel(jrole) < 40 then
	   replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, "您的等级未达到40级，只有40级以后才能接此任务！")
	   return
	end
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	local element = self:getSelectElement(jrole)
	local token = 0
	if element == nil then
		replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, "无适合任务！")
		return
	end
	local sub_state
	if task_state then
		if _IsTaskFinished(task_state) then
			replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, "该任务已完成，请勿连续点击！")
			return
		end
		
		sub_state = _GetSubState(task_state)

		if  _IsSubtastFinished(sub_state) then
			replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, "本日武林秘史任务已完成！")
			return
		else
		    
		end
		 
	else
		if  deep == 1 and uid == 0 then
		   self:taskChoiceList(jrole,element)
		   return
		elseif deep == 2 then
			 if Bag:getItemCountByTid(jrole, 31124) < 3 then 
            	replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, "至尊令数量不足！")
	   			return
	         end
		end
	end
	element:dialog(jrole, npcid, deep, self, sub_state)--进入子任务逻辑
end

function _MartialCheatsTask:acceptTask(jrole)
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

--交子任务，如果子任务，则此任务池的任务记为全部完成
function _MartialCheatsTask:consignSubtask(jrole)
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

function _MartialCheatsTask:accpetSubtask(jrole)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	local element = self:getSelectElement(jrole)
	if task_state then

			local index = 1
			for k,v in pairs(self.detail) do
			    if v == element.id then
			      index = k
			    end
			end
		    _TaskOnltStepUp(task_state,index)
		    _TaskStateExtraSetI(task_state,9,1)
		    
	else
		_AddTaskState(role_task, luajava.new(TaskState, self.id))
	    local realTaskState = _GetTaskState(role_task, self.id)
	    if realTaskState then
			local index = 1
			for k,v in pairs(self.detail) do
			    if v == element.id then
			      index = k
			    end
			end
		    _TaskOnltStepUp(realTaskState,index)
		    _TaskStateExtraSetI(realTaskState,9,1)
	   end
	end
	
	
end


function _MartialCheatsTask:self_check(jrole, task_state, curMin)
	if _IsTaskFinished(task_state) then
		if Temp.task_version == 1 then
			return       --直接清除每日任务数据使之成为可接
		elseif curMin >= _TaskStateExtraGetI(task_state, 1) then
			return   --过了时间清除
		end
	end
	
	--未清除则调用_CommonTask的default_self_check
	
	return self:default_self_check(jrole, task_state, curMin)
end

function _MartialCheatsTask:taskChoiceList(jRole,elementTask)
   prepareBody()
	putShort(0)
	
	bodyMark()
	putShort(300)--子模块300
	putByte(1)  -- 任务个数
	--[[
    putInt(self.id)
	putLong(1) --此字段表示第几个  根据这个判断选择了几个至尊令
	putByte(2)
	putString(elementTask.name .. "（至尊令1）")
	putInt(0)
	
    putInt(self.id)
	putLong(2) --此字段表示第几个  根据这个判断选择了几个至尊令
	putByte(2)
	putString(elementTask.name .. "（至尊令2）")
	putInt(0)
	]]
    putInt(self.id)
	putLong(3) --此字段表示第几个  根据这个判断选择了几个至尊令
	putByte(2)
	putString(elementTask.name .. "（至尊令3）")
	putInt(0)
	 
	putShort(0)
	sendMsg(jRole, MsgID.MsgID_Talk_To_Npc_Resp)
	

end


function _MartialCheatsTask:getSelectElement(jrole)
   -- 首先根据用户等级       得到可选任务池
   local selects = {}
   local count = 0
   local level = _GameCharacterGetLevel(jrole)
    for k,v in pairs (self.detail) do
       local element = TaskElementSet[v]
       if level >= element.minLevel and level <= element.maxLevel then
          count = count + 1
          selects[count] = v
       end
    end
    --根据人的id和本日日期得到一个固定任务
    if count == 0 then
    	return nil
    else
    	local index = _GetTaskColmunNumber(jrole,count) 
    	for k,v in pairs(selects) do
    	   if k == index then
    	    element = TaskElementSet[v]
    	     return element
    	   end
    	end
    end
     
end

--@return 任务状态：0忽略；1可接任务；2任务进行中；3可交任务
function _MartialCheatsTask:getState(jrole, npcid)
	local role_task = _GetRoleTask(jrole)

	local task_state = _GetTaskState(role_task, self.id)
    
	if task_state then
		if _IsTaskFinished(task_state) then
			return 0--此任务已经完结
			
		end
		local step = _GetTaskStateStep(task_state)
		local sub_state = _GetSubState(task_state)
        
		if _IsSubtastFinished(sub_state) then
				return 0
		else
			local element = self:getSelectElement(jrole)
			local state = element:getState(jrole, npcid, self,sub_state)
			
			return state
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

function getCalExp(jrole, element, tokenCount)
   local realExp = 0 
   local level = _GameCharacterGetLevel(jrole)
   local difficultyDegree = 1
   local basicValue = 1
   if element.rewards.wealth[Economy.Exp] then
    difficultyDegree = element.rewards.wealth[Economy.Exp]
   end
   
   if tokenCount == 1 then
   		tokenCount = 1
   elseif tokenCount == 2 then
   		tokenCount = 2
   elseif tokenCount == 3 then
   		tokenCount = 2.8
   end 					
   
   if level >= 40 and level <= 49 then
   		realExp = (17000 - level * 25) * (0.4 + difficultyDegree * 0.75) * tokenCount
   elseif level >= 50 and level <= 59 then
   		realExp = (20500 - level * 30) * (0.4 + difficultyDegree * 0.75) * tokenCount
   elseif level >= 60 and level <= 69 then
   		realExp = (43000 - level * 50) * (0.4 + difficultyDegree * 0.75) * tokenCount
   elseif level >= 70 and level <= 79 then
   		realExp = (50500 - level * 50) * (0.4 + difficultyDegree * 0.75) * tokenCount
   elseif level >= 80 and level <= 89 then
   		realExp = (57000 - level * 50) * (0.4 + difficultyDegree * 0.75) * tokenCount
   elseif level >= 90 and level <= 94 then
   		realExp = (65000 - level * 50) * (0.4 + difficultyDegree * 0.75) * tokenCount
   elseif level >= 95 then 
   		realExp = (71000 - level * 50) * (0.4 + difficultyDegree * 0.75) * tokenCount
   end
   
   return realExp
end




