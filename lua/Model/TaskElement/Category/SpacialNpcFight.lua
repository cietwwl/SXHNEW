_SpacialNpcFight = _TaskElement:newInstance()

function _SpacialNpcFight:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _SpacialNpcFight:dialog(jrole, npcid, deep, task, sub_state)
    if self:getState(jrole, npcid, sub_state,task) == 1 then
		self:acceptDialog(jrole, npcid, deep, task)
	else
		self:consignDialog(jrole, npcid, deep, task, sub_state)
	end
end

function _SpacialNpcFight:acceptDialog(jrole, npcid, deep, task)
	prepareBody()
	putShort(0)
	
	if deep <= #self.acceptDialogs then
		fillNpcDialog(task.id, 0, 1, self:getName(task) .. "/" .. self.acceptDialogs[deep], self.color)
	else
		fillNpcDialog(task.id, self.monster, 2, "任务【" .. self:getName(task) .. "】已接受", self.color)
		
		task:accpetSubtask(jrole)
		
		fillFreshNPCState(jrole)
	end
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _SpacialNpcFight:consignDialog(jrole, npcid, deep, task,sub_state)
	prepareBody()
	putShort(0)
	if deep <= #self.consignDialogs then
		fillNpcDialog(task.id, 0, 1, self:getName(task) .. "/" .. self.consignDialogs[deep], self.color)
	else
		local awards = self:calcRewards(jrole)
		if not Bag:checkAdd(jrole, awards) then
			fillNpcDialog(task.id, 0, 0, self:getName(task) .. "/" .. "/包裹空间不足！ ", self.color)
		else
			local role_task = _GetRoleTask(jrole)
			local task_state = _GetTaskState(role_task, task.id)
			--在这里发经验
			local param = _TaskStateExtraGetI(task_state,6) * self.rewards.wealth[Economy.Exp]
			
			local prompt = Economy:reward(jrole, awards, param)
			
			fillNpcDialog(task.id, 0, 0, "任务【" .. self:getName(task) .. "】已完成～/" .. (prompt or ""), self.color)
			
			task:consignSubtask(jrole)
			--------------------
		     log.eightBuddha(_GameCharacterGetId(jrole) .. '#$' .. (prompt or "") .. '#$' .. _GetNowTimeString())
		    --------------------
			fillFreshNPCState(jrole)
		end
	end
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _SpacialNpcFight:getState(jrole, npcid, sub_state,task)
		if (not sub_state or (not _IsSubtastFinished(sub_state))) and npcid == self.acceptNpc then
			return 1
		end
			
		if npcid == self.consignNpc and _GetSubstateI(sub_state, 1) == 1 then
			return 3
		end
	 
	   return 0
end

function _SpacialNpcFight:findPath(jrole, jsubState)
	if not jsubState then
		return NPCSet[self.acceptNpc]:getLocation()
	end
	
	if _IsSubtastFinished(jsubState) then
		return
	end
	
	return NPCSet[self.consignNpc]:getLocation()
end

function _SpacialNpcFight:updateState(jrole,monster,task_state)
	local update
	local sub_state =  _GetSubState(task_state)
	if self.monster == monster.id and _GetSubstateI(sub_state, 1) == 0 then
		_SetSubstateI(sub_state, 1, 1)
		_CompleteSubtask(sub_state)
		
		--if  self.nextNPC ~= 0 then
		    --local npc = NPCSet[self.nextNPC]
		    --if npc then
			  -- transToMap(jrole,npc.mapid,npc.id)
			--end
		--else
		    --local npc = NPCSet[self.consignNpc]
		     --if npc then
			  -- transToMap(jrole,npc.mapid,npc.id)
			  -- fillFreshNPCState(jRole)  
			 --end
		--end
		prepareBody()
		fillFreshNPCState(jrole)
		putShort(0)
	    sendMsg(jrole)
		return true
	end
end

function _SpacialNpcFight:quickFinishState(jrole, sub_state)
	_SetSubstateI(sub_state, 1, 1)
end

