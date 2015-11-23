
_KillMonster = _TaskElement:newInstance()

function _KillMonster:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _KillMonster:dialog(jrole, npcid, deep, task, sub_state)
	local state = self:getState(jrole, npcid,sub_state)
	if state == 1 then
		self:acceptDialog(jrole, npcid, deep, task, sub_state)
	elseif state == 2 then
		self:ongoingDialog(jrole, npcid, deep, task)
	elseif state == 3 then
		self:consignDialog(jrole, npcid, deep, task, sub_state)
	else
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "该NPC处任务已完成，请勿连续点击！")
	end
end

function _KillMonster:acceptDialog(jrole, npcid, deep, task)

    
	prepareBody()
	putShort(0)
	if deep <= #self.acceptDialogs  then
		fillNpcDialog(task.id, 0, 1, self:getName(task) .. "/" .. self.acceptDialogs[deep], self.color)

	else
		fillNpcDialog(task.id, 0, 0, "任务【" .. self:getName(task) .. "】已接受", self.color)
		
		task:accpetSubtask(jrole)
		
		fillFreshNPCState(jrole)
	end
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
	
	
	
end

function _KillMonster:consignDialog(jrole, npcid, deep, task)
    
	prepareBody()
	putShort(0)
	
	if deep <= #self.consignDialogs then
		fillNpcDialog(task.id, 0, 1, self:getName(task) .. "/" .. self.consignDialogs[deep], self.color)
	else
		local awards  
		       --计算当前任务奖励物品个数
			 awards = self:calcRewards(jrole)
		if not Bag:checkAdd(jrole, awards) then
			fillNpcDialog(task.id, 0, 0, self:getName(task) .. "/" .. "/包裹空间不足！ ", self.color)
		else
		    --经验值是在这里发给玩家的
		    
			local prompt 
			
			    prompt = Economy:reward(jrole, awards)
			fillNpcDialog(task.id, 0, 0, "任务【" .. self:getName(task) .. "】已完成～/" .. (prompt or ""), self.color)
			
			task:consignSubtask(jrole)
			
			fillFreshNPCState(jrole)
		end
	end
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _KillMonster:ongoingDialog(jrole, npcid, deep, task)
	prepareBody()
	putShort(0)
	
	fillNpcDialog(task.id, 0, 0, self:getName(task) .. "/" .. self.prompt, self.color)
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _KillMonster:getState(jrole, npcid, sub_state)
    	if (not sub_state or _IsSubtastFinished(sub_state)) and npcid == self.acceptNpc then
			
			return 1
		end
		
		local targets = self.targets
		
		if npcid == self.consignNpc then
			for i = 1, #targets do
				if _GetSubstateI(sub_state, i) < targets[i].num then
					return 2
			    end
		    end
		    return 3
		end
	return 0
end

function _KillMonster:findPath(jrole, sub_state)
	if not sub_state then
		return NPCSet[self.acceptNpc]:getLocation()
	end
	
	if _IsSubtastFinished(sub_state) then
		return
	end
	
	local targets = self.targets
	
	for i = 1, #targets do
		if _GetSubstateI(sub_state, i) < targets[i].num then
			return MonsterSet[targets[i].kill]:getLocation()
		end
	end
	
	return NPCSet[self.consignNpc]:getLocation()
end

function _KillMonster:getDescribe(jrole, sub_state)
	local targets = self.targets
	
	local des = { }
	des[#des + 1] = self.prompt
	for i = 1, #targets do
		local monster = MonsterSet[targets[i].kill]
		des[#des + 1] = monster.name .. "：" .. _GetSubstateI(sub_state, i) .. "|" .. targets[i].num
	end
	
	return table.concat(des, "/")
end

function _KillMonster:getAcceptState()
	return true
end

function _KillMonster:updateState(monster, sub_state)
	local update
	local targets = self.targets
	for i = 1, #targets do
		if targets[i].kill == monster.id then
			local value = _GetSubstateI(sub_state, i) + 1
			if value <= targets[i].num then
				_SetSubstateI(sub_state, i, value)
				
				update = true
			end
		end
	end
	
	return update
end

function _KillMonster:quickFinishState(jrole, sub_state)
	local targets = self.targets
	for i = 1, #targets do
		_SetSubstateI(sub_state, i, targets[i].num)
	end
end
