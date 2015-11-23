_MartialCheatsKillMonster = _TaskElement:newInstance()

function _MartialCheatsKillMonster:new(o)
  
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _MartialCheatsKillMonster:dialog(jrole, npcid, deep, task, sub_state)

	local state = self:getState(jrole, npcid, task,sub_state)
	if state == 1 then
		self:acceptDialog(jrole, npcid, deep, task)
	elseif state == 2 then
		self:ongoingDialog(jrole, npcid, deep, task)
	elseif state == 3 then
		self:consignDialog(jrole, npcid, deep, task,sub_state)
	else
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "该NPC处任务已完成，请勿连续点击！")
	end
end

function _MartialCheatsKillMonster:acceptDialog(jrole, npcid, deep, task)
    
	prepareBody()
	putShort(0)
	local flag = 0
	
	if deep <= (#self.acceptDialogs + 1)  then
	    fillNpcDialog(task.id, 0, 1, self:getName(task) .. "/" .. self.acceptDialogs[deep - 1], self.color)
	else
		
		 if Bag:getItemCountByTid(jrole, 31124) < 3 then 
	            replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, "至尊令数量不足！")
	   			return
	    end
	
		fillNpcDialog(task.id, 0, 0, "任务【" .. self:getName(task) .. "】已接受", self.color)
		
		task:accpetSubtask(jrole)
		fillFreshNPCState(jrole)
		flag = 1
	end
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
	if flag == 1 then
		local role_task = _GetRoleTask(jrole)
		local task_state = _GetTaskState(role_task, task.id)
		local token = 3
		self:dcreateToken(jrole,task_state, token)
	end
	
end

function _MartialCheatsKillMonster:consignDialog(jrole, npcid, deep, task, sub_state)
    
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
			local param = _GetSubstateI(sub_state,6)
			prompt = Economy:reward2(jrole, awards,param)
			
			fillNpcDialog(task.id, 0, 0, "任务【" .. self:getName(task) .. "】已完成～/" .. (prompt or ""), self.color)
			
			-----------加日志---------
			--人物id  奖励   完成时间
			log.martialcheats(_GameCharacterGetId(jrole) .. '#$' .. (prompt or "") .. '#$' .. _GetNowTimeString())
			-----------加日志---------
			task:consignSubtask(jrole)
			
			fillFreshNPCState(jrole)
		end
	end
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _MartialCheatsKillMonster:ongoingDialog(jrole, npcid, deep, task)
	prepareBody()
	putShort(0)
	
	fillNpcDialog(task.id, 0, 0, self:getName(task) .. "/" .. self.prompt, self.color)
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _MartialCheatsKillMonster:getState(jrole, npcid, task,sub_state)
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

function _MartialCheatsKillMonster:findPath(jrole, sub_state)
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

function _MartialCheatsKillMonster:getDescribe(jrole, sub_state)
	local targets = self.targets
	
	local des = { }
	des[#des + 1] = self.prompt
	for i = 1, #targets do
		local monster = MonsterSet[targets[i].kill]
		des[#des + 1] = monster.name .. "：" .. _GetSubstateI(sub_state, i) .. "|" .. targets[i].num
	end
	
	return table.concat(des, "/")
end

function _MartialCheatsKillMonster:getAcceptState()
	return true
end

function _MartialCheatsKillMonster:updateState(monster, sub_state)
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

function _MartialCheatsKillMonster:quickFinishState(jrole, sub_state)
	local targets = self.targets
	for i = 1, #targets do
		_SetSubstateI(sub_state, i, targets[i].num)
	end
end

function _MartialCheatsKillMonster:dcreateToken(jrole,realTask_state, token)
          
                sub_state = _GetSubState(realTask_state)
			    --根据至尊令数量计算应得经验
			     _SetSubstateI(sub_state,6,getCalExp(jrole,self,token))
			      
			      print('奖励参数：' .. _GetSubstateI(sub_state,6))
			     --扣除至尊令
			     Bag:delItem(jrole, 31124, token)
			     prepareBody()
		         fillBagDel(31124,token)
		         putShort(0)
		         sendMsg(jrole)
end
