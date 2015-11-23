
_NpcDialog = _TaskElement:newInstance()

function _NpcDialog:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _NpcDialog:acceptDialog(jrole, npcid, deep, task , sub_state)
	prepareBody()
	putShort(0)
	
	if deep <= #self.acceptDialogs then
		fillNpcDialog(task.id, 0, 1, self:getName(task) .. "/" .. self.acceptDialogs[deep], self.color)
	elseif not self.consignNpc or self.acceptNpc == self.consignNpc then
		local awards  
		 if task.type == TaskProfile.MartialCheats then      --计算当前任务奖励物品个数
		 	awards = self:calcRewards(jrole, npcid, sub_state)
		 else
			 awards = self:calcRewards(jrole)
		 end
		if not Bag:checkAdd(jrole, awards) then
			fillNpcDialog(task.id, 0, 0, self:getName(task) .. "/" .. "/包裹空间不足！ ", self.color)
		else
			local prompt = Economy:reward(jrole, awards)
			if prompt then
				fillNpcDialog(task.id, 0, 0, "任务【" .. self:getName(task) .. "】已完成～/" .. (prompt and prompt or ''), self.color)
			else
				fillNpcDialog(task.id, 0, 0, "任务【" .. self:getName(task) .. "】已完成～", self.color)
			end

			task:accpetSubtask(jrole)
			task:consignSubtask(jrole)
			
			fillFreshNPCState(jrole)
		end
	else
		fillNpcDialog(task.id, 0, 0, "任务【" .. self:getName(task) .. "】已接受", self.color)
		
		task:accpetSubtask(jrole)
		
		fillFreshNPCState(jrole)
	end
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _NpcDialog:consignDialog(jrole, npcid, deep, task)
	prepareBody()
	putShort(0)
	
	if deep <= #self.consignDialogs then
		fillNpcDialog(task.id, 0, 1, self:getName(task) .. "/" .. self.consignDialogs[deep], self.color)
	else
		local awards = self:calcRewards(jrole)--计算当前任务奖励物品个数
		if not Bag:checkAdd(jrole, awards) then
			fillNpcDialog(task.id, 0, 0, self:getName(task) .. "/包裹空间不足！ ", self.color)
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

function _NpcDialog:dialog(jrole, npcid, deep, task, sub_state)
    if self:getState(jrole, npcid, sub_state) == 1 then
		self:acceptDialog(jrole, npcid, deep, task, sub_state)
	else
		self:consignDialog(jrole, npcid, deep, task)
	end
end

function _NpcDialog:getState(jrole, npcid, sub_state)
		if (not sub_state or _IsSubtastFinished(sub_state)) and npcid == self.acceptNpc then
			return 1
		end
		return npcid == self.consignNpc and 3 or 0
end

function _NpcDialog:findPath(jrole, sub_state)
	if not sub_state or _IsSubtastFinished(sub_state) then
		return NPCSet[self.acceptNpc]:getLocation()
	end
	
	return NPCSet[self.consignNpc]:getLocation()
end

function _NpcDialog:getAcceptState()
	return self.acceptNpc ~= self.consignNpc
end

function _NpcDialog:quickFinishState(jrole, sub_state)
	_SetSubstateI(sub_state, 1, 0)
end
