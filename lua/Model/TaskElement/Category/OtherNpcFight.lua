--新的npc战斗
_OtherNPCFight = _TaskElement:newInstance() 

function _OtherNPCFight:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _OtherNPCFight:dialog(jrole, npcid, deep, task, sub_state)
	if self:getState(jrole, npcid, sub_state) == 1 then
		self:acceptDialog(jrole, npcid, deep, task)
	else
		self:consignDialog(jrole, npcid, deep, task)
	end
end

function _OtherNPCFight:acceptDialog(jrole, npcid, deep, task)
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

function _OtherNPCFight:consignDialog(jrole, npcid, deep, task)
	prepareBody()
	putShort(0)
	
	if deep <= #self.consignDialogs then
		fillNpcDialog(task.id, 0, 1, self:getName(task) .. "/" .. self.consignDialogs[deep], self.color)
	else
		local awards = self:calcRewards(jrole)--计算当前任务奖励物品个数
		if not Bag:checkAdd(jrole, awards) then
			fillNpcDialog(task.id, 0, 0, self:getName(task) .. "/" .. "/包裹空间不足！ ", self.color)
		else
			local prompt = Economy:reward(jrole, awards)
			fillNpcDialog(task.id, 0, 0, "任务【" .. self:getName(task) .. "】已完成～/" .. (prompt or ""), self.color)
			
			task:consignSubtask(jrole)
			
			fillFreshNPCState(jrole)
		end
	end
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _OtherNPCFight:getState(jrole, npcid, sub_state)
	if (not sub_state or _IsSubtastFinished(sub_state)) and npcid == self.acceptNpc then
		return 1
	end
	
	if npcid == self.consignNpc and _GetSubstateI(sub_state, 1) == 1 then
		return 3
	end
	
	return 0
end

function _OtherNPCFight:findPath(jrole, jsubState)
	if not jsubState then
		return NPCSet[self.acceptNpc]:getLocation()
	end
	
	if _IsSubtastFinished(jsubState) then
		return
	end
	
	return NPCSet[self.consignNpc]:getLocation()
end

function _OtherNPCFight:updateState(monster, sub_state)
	local update
		
	--if self.monster == monster.id and _GetSubstateI(sub_state, 1) == 0 then
		_SetSubstateI(sub_state, 1, 1)
		return true
	--end
end

function _OtherNPCFight:quickFinishState(jrole, sub_state)
	_SetSubstateI(sub_state, 1, 1)
end
