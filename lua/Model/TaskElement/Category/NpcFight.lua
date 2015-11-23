
_NpcFight = _TaskElement:newInstance()

function _NpcFight:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _NpcFight:dialog(jrole, npcid, deep, task, sub_state)
	
	local result = self:getState(jrole, npcid, sub_state,task)
	
	if result == 0 then
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "任务错误，提交失败！")
    elseif result == 1 then
		self:acceptDialog(jrole, npcid, deep, task)
	else
		self:consignDialog(jrole, npcid, deep, task, sub_state)
	end
end

function _NpcFight:acceptDialog(jrole, npcid, deep, task)
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

function _NpcFight:consignDialog(jrole, npcid, deep, task,sub_state)
	prepareBody()
	putShort(0)
	
	if deep <= #self.consignDialogs then
		fillNpcDialog(task.id, 0, 1, self:getName(task) .. "/" .. self.consignDialogs[deep], self.color)
	else
		local awards = self:calcRewards(jrole)
		if not Bag:checkAdd(jrole, awards) then
			fillNpcDialog(task.id, 0, 0, self:getName(task) .. "/" .. "/包裹空间不足！ ", self.color)
		else
		   local prompt
		    if task.type == TaskProfile.MartialCheats then
			 	prompt= Economy:reward(jrole, awards,_GetSubstateI(sub_state,6) )
			else
			    prompt= Economy:reward(jrole, awards)
			end
			fillNpcDialog(task.id, 0, 0, "任务【" .. self:getName(task) .. "】已完成～/" .. (prompt or ""), self.color)
			
			task:consignSubtask(jrole)
			
			fillFreshNPCState(jrole)
			
			if self:isNpcid(npcid) then 
				if not _RoleGetTeam(jrole) or  _GetTeamNum(jrole) == 1 then
					local nick = _RoleGetNick(jrole)
					Broadcast:send("恭喜" .. " " .. nick .. " 独自" ..self.name .. "!")
				else
					local nick = _RoleGetNick(jrole)
					local num = _GetTeamNum(jrole);
					Broadcast:send("恭喜" .. " " .. nick .." 等" .. num .. "人" .. self.name .. "!")
				end	
			end
		end
	end
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _NpcFight:isNpcid(npcid)
	if npcid == 547  or npcid == 549 or npcid == 552 then --葬鬼谷
		return true
	elseif npcid == 553 or npcid == 554  or npcid == 555 or npcid == 556 then --迷离宫
		return true
	else
		return false
	end
end

function _NpcFight:getState(jrole, npcid, sub_state,task)

    if task and task.type == TaskProfile.MartialCheats then --如果是任务池任务进行特殊处理
        if sub_state and npcid == self.consignNpc and _GetSubstateI(sub_state, 1) == 1 then
			return 3
		end
		
		if sub_state and _IsSubtastFinished(sub_state) then
			return 1
		end
	else
		
		if (not sub_state or _IsSubtastFinished(sub_state)) and npcid == self.acceptNpc then
			return 1
		end
			
		if npcid == self.consignNpc and _GetSubstateI(sub_state, 1) == 1 then
			return 3
		end
		 
	end
	
	return 0
end

function _NpcFight:findPath(jrole, jsubState)
	if not jsubState then
		return NPCSet[self.acceptNpc]:getLocation()
	end
	
	if _IsSubtastFinished(jsubState) then
		return
	end
	
	return NPCSet[self.consignNpc]:getLocation()
end

function _NpcFight:updateState(monster, sub_state)
	local update
	if self.monster == monster.id and _GetSubstateI(sub_state, 1) == 0 then
		_SetSubstateI(sub_state, 1, 1)
		return true
	end
end

function _NpcFight:quickFinishState(jrole, sub_state)
	_SetSubstateI(sub_state, 1, 1)
end
