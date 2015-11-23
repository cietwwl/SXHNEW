_EightBuddhaTask = _CommonTask:new()
--八部浮屠
function _EightBuddhaTask:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end


function _EightBuddhaTask:dialog(jrole, npcid, deep,uid)

	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	local element
	local sub_state
    local step
    
	if task_state then
		if _IsTaskFinished(task_state) then
			replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, "该任务已完成，请勿连续点击！")
			return
		end
		step = _GetTaskStateStep(task_state)
		element = TaskElementSet[self.detail[1]]
		sub_state = _GetSubState(task_state)
		if element.consignNpc == npcid then
			if step and uid == 0 then
			    if step == 1 and (not _IsSubtastFinished(sub_state)) then
				   self:showChoice(jrole,0)
				   return
				end
				self:showChoice(jrole,step)
				return
			elseif uid == 1 then
				if _IsSubtastFinished(sub_state) then
					element = TaskElementSet[self.detail[step]]
				else
					if step == 1 then
					    element = TaskElementSet[self.detail[step]]
					end
					element = TaskElementSet[self.detail[step - 1]]
				end
				--直接交任务
				if npcid == element.consignNpc then
				      element:consignDialog(jrole, npcid, deep, self)
				end
				return
		    else
		        
				if  _IsSubtastFinished(sub_state) then
					    _TaskStateStepUp(task_state)
						element = TaskElementSet[self.detail[step + 1]]
				else
					 if step == 0 then
					    step = step + 1
					 end
					 element = TaskElementSet[self.detail[step]]
				end
		    end
	    else
	     
		      if step == 0 then
		     
				  element = TaskElementSet[self.detail[1]]
		      else
		          
				  if _IsSubtastFinished(sub_state) then
				      	if step == 8 then
				      	 _ForceDeleteTeam(jrole)
					      element = TaskElementSet[self.detail[step]]
					      local npc = NPCSet[element.nextNPC]
						  if npc then
					    	   transToMap(jrole,npc.mapid,npc.id)
					    	   return
						  end
					   else
						  element = TaskElementSet[self.detail[step + 1]]
						  _TaskStateStepUp(task_state)
					   end
				  else
					   element = TaskElementSet[self.detail[step]]
				  end
		      end
		end
    else
		element = TaskElementSet[self.detail[1]]
		if uid == 0 then
			self:taskChoiceList(jrole)
			return
		else
			if Bag:getItemCountByTid(jrole, 31124) >= uid then
				_AddTaskState(role_task, luajava.new(TaskState, self.id))
				local realTaskState = _GetTaskState(role_task, self.id)
				local param = 500 * _GetRoleLevel(jrole) 				
				param = param * 2.7
								
				_TaskStateExtraSetI(realTaskState,6,param)
				-----------------扣除至尊令---------------------
				 Bag:delItem(jrole, 31124, uid)
				prepareBody()
				fillBagDel(31124,uid)
				putShort(0)
				sendMsg(jrole)
			    -----------------扣除至尊令---------------------
				element = TaskElementSet[self.detail[1]]

			else

				replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, "至尊令数量不足！")
				return
			end
		end
	end

    --判断现在的NPC是否是要打斗的NPC   是的话直接进入子任务逻辑     不是则将玩家传送至相应的地图中
    if npcid == element.acceptNpc then
    	if _RoleGetTeam(jrole) then
	    	if _GetTeamNum(jrole) == 2 then
		   		local otherId= _GetMarryTeamOtherId(jrole)
		   		local otherRloeBean = _IdGetRole(jrole)
		   		local role_task = _GetRoleTask(otherRloeBean)
			    local task_state = _GetTaskState(role_task, self.id)
			    local sub_state = _GetSubState(task_state)
			    if _IsSubtastFinished(sub_state) then
			    
			  		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "队伍中有人已完成此任务！！！")
					return
			     
			    end
		
			end
					
			if _GetTeamNum(jrole) == 3 then
			
		   		local otherRloeBean = _IdGetRole(jrole)
		   		local role_task = _GetRoleTask(otherRloeBean)
			    local task_state = _GetTaskState(role_task, self.id)
			    local sub_state = _GetSubState(task_state)
			    if _IsSubtastFinished(sub_state) then
			    
			  		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "队伍中有人已完成此任务！！！")
					return
			     
			    end
			    
			    
			    local threeRloeBean = _GetMarryTeamThreeRloeBean(jrole)
		   		local role_task = _GetRoleTask(threeRloeBean)
			    local task_state = _GetTaskState(role_task, self.id)
			    local sub_state = _GetSubState(task_state)
			    if _IsSubtastFinished(sub_state) then
			    
			  		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "队伍中有人已完成此任务！！！")
					return
			     
			    end
	
			end
		
		end		
	   element:dialog(jrole, npcid, deep, self, sub_state)--进入子任务逻辑
	   return
	else
	      _ForceDeleteTeam(jrole)
	    --强制将玩家放入地图中
	    local npc = NPCSet[element.acceptNpc]
		if npc then
	    	transToMap(jrole,npc.mapid,npc.id)
		end
	end
end

function _EightBuddhaTask:self_check(jrole, task_state, curMin)
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

function _EightBuddhaTask:taskChoiceList(jRole)
   prepareBody()
	putShort(0)

	bodyMark()
	putShort(300)--子模块300
	putByte(1)  -- 任务个数

    --putInt(self.id)
	--putLong(1) --此字段表示第几个  根据这个判断选择了几个至尊令
	--putByte(2)
	--putString(self.name .. "（至尊令1）")
	--putInt(0)

	--putInt(self.id)
	--putLong(2) --此字段表示第几个  根据这个判断选择了几个至尊令
	--putByte(2)
	--putString(self.name .. "（至尊令2）")
	--putInt(0)

	putInt(self.id)
	putLong(3) --此字段表示第几个  根据这个判断选择了几个至尊令
	putByte(2)
	putString(self.name .. "（至尊令3）")
	putInt(0)

	sendMsg(jRole, MsgID.MsgID_Talk_To_Npc_Resp)


end


function _EightBuddhaTask:showChoice(jRole,step)
    prepareBody()
	putShort(0)

	bodyMark()
	putShort(300)--子模块300

    if step > 0 and step <= 7 then
      	putByte(2)  -- 任务个数
      	
      	putInt(self.id)
		putLong(1) --此字断表示选择哪个选项      1 结束挑战领取领取任务奖励       2 传送至八部浮屠塔继续挑战
		putByte(2)
		putString("结束挑战，领取奖励")
		putInt(0)
	
		putInt(self.id)
		putLong(2)
		putByte(2)
		putString("传送至八部浮屠塔")
		putInt(0)
	elseif step == 8 then
		local role_task = _GetRoleTask(jRole)
	    local task_state = _GetTaskState(role_task, self.id)
	    local sub_state = _GetSubState(task_state)
	    if _IsSubtastFinished(sub_state) then
		    putByte(1)
		    putInt(self.id)
			putLong(1) --此字断表示选择哪个选项      1 结束挑战领取领取任务奖励       2 传送至八部浮屠塔继续挑战
			putByte(2)
			putString("领取奖励")
			putInt(0)
		else
		   putByte(2)  -- 任务个数
      	
      	putInt(self.id)
		putLong(1) --此字断表示选择哪个选项      1 结束挑战领取领取任务奖励       2 传送至八部浮屠塔继续挑战
		putByte(2)
		putString("结束挑战，领取奖励")
		putInt(0)
	
		putInt(self.id)
		putLong(2)
		putByte(2)
		putString("传送至八部浮屠塔")
		putInt(0)
		  
		end
    else
        putByte(1)
      	putInt(self.id)
		putLong(2)
		putByte(2)
		putString("传送至八部浮屠塔")
		putInt(0)
    end
    sendMsg(jRole, MsgID.MsgID_Talk_To_Npc_Resp)
end


--子任务接受
function _EightBuddhaTask:accpetSubtask(jrole)
	
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)

	if task_state then
		--_TaskStateStepUp(task_state)
	else
		_AddTaskState(role_task, luajava.new(TaskState, self.id))
	end
end

-------------------------------------------------------------------------------
--子任务交付
function _EightBuddhaTask:consignSubtask(jrole)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	task_state = task_state or luajava.new(TaskState, self.id)

	--如果子任务完成，则将主任务也设置为已完成并存档
	_CompleteTask(task_state)
	_TaskStateExtraSetI(task_state, 1, _GetNextFreshMin(self.freshOnTheHour))
	_AddTaskState(role_task, task_state)
end


--@return 任务状态：0忽略；1可接任务；2任务进行中；3可交任务
function _EightBuddhaTask:getState(jrole, npcid)
	local role_task = _GetRoleTask(jrole)

	local task_state = _GetTaskState(role_task, self.id)
    local element = TaskElementSet[self.detail[1]]
	if task_state then
		if _IsTaskFinished(task_state) then
		   
			return 0  --此任务已经完结
		end
        	
		local step = _GetTaskStateStep(task_state)
		element = TaskElementSet[self.detail[step]]
		local sub_state = _GetSubState(task_state)
		if npcid == element.consignNpc then
		   if step == 0 or (step == 1 and (not _IsSubtastFinished(sub_state))) then
		   		return 1
		   elseif step > 1 or (step == 1 and _IsSubtastFinished(sub_state)) then
		        return 3
		   end
		end
		if _IsSubtastFinished(sub_state) then
		    local cur = TaskElementSet[self.detail[step]]
		    if cur.acceptNpc == npcid then
		       return 3
		    end
			local next = TaskElementSet[self.detail[step + 1]]
			if next and npcid == next:getAcceptNpc().id then--当前NPC是下一环接受NPC
				return 1
			end
		else
			local element = TaskElementSet[self.detail[step]]
			local result = element:getState(jrole, npcid, sub_state)
			
			return result
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
            	
			if element then
				local npc = element:getAcceptNpc()
			
				local conNpc = NPCSet[element.consignNpc]
				
				if npc and ((npc.id == npcid ) or (conNpc.id == npcid)) then
			
					return 1
				end
			end
		end
	end
   
	return 0
end