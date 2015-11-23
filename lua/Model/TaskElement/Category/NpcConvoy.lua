
_NpcConvoy = _TaskElement:newInstance()

function _NpcConvoy:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _NpcConvoy:acceptDialog(jrole, npcid, deep, task)
	prepareBody()
	putShort(0)
	
	if deep <= #self.acceptDialogs then
		fillNpcDialog(task.id, 0, 1, self:getName(task) .. "/" .. self.acceptDialogs[deep], self.color)
	else
		fillNpcDialog(task.id, 0, 0, "任务【" .. self:getName(task) .. "】已接受", self.color)
		
		task:accpetSubtask(jrole)
		
		fillFreshNPCState(jrole)
		
		_TaskAddMonitor(jrole, task.id)
		
		--通知客户端画NPC
		local npc = NPCSet[npcid]
		local NPCname = npc.nick 
		local groupid = self.group or npc.group
		
		fillConvoyNpcInfo(groupid,NPCname)
	end
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _NpcConvoy:consignDialog(jrole, npcid, deep, task)
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
			
			_TaskDelMonitor(jrole, task.id)
			
			fillConvoyNpcEnd()
		end
	end
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _NpcConvoy:dialog(jrole, npcid, deep, task, sub_state)
	if self:getState(jrole, npcid, sub_state) == 1 then
		self:acceptDialog(jrole, npcid, deep, task)
	else
		self:consignDialog(jrole, npcid, deep, task)
	end
end

function _NpcConvoy:getState(jrole, npcid, sub_state)
	if npcid == self.acceptNpc then
		return 1
	end
	
	return npcid == self.consignNpc and 3 or 0
end

function _NpcConvoy:findPath(jrole, sub_state)
	if not sub_state or _IsSubtastFinished(sub_state) then
		return NPCSet[self.acceptNpc]:getLocation()
	end
	
	return NPCSet[self.consignNpc]:getLocation()
end

function _NpcConvoy:getAcceptState()
	return self.acceptNpc ~= self.consignNpc
end

function _NpcConvoy:quickFinishState(jrole, sub_state)
	_SetSubstateI(sub_state, 1, 0)
end

function _NpcConvoy:seconedTick(jrole, curSeconed, task)
	local jcoords = _RoleGetCoords(jrole)
	local mapid = _CoordsGetAttr(jcoords)
	local map = MapSet[mapid]
	
	if map.noSneakAtk then return end
	
	local monsters = map.monsters
	
	if #monsters == 0 then
		return
	end
	
	local monster_id = monsters[math.random(1, #monsters)].id
	local monster = MonsterSet[monster_id]
	
	if not monster or monster.type == MonsterType.NPCFight then return end
	--护送过程中强制随机战斗
	--得到人物的战斗状态如果正在战斗不做处理
	--否则向客户端发消息随机进入战斗
	
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, task.id)
	local sub_state = _GetSubState(task_state)
	
	if not _GetBattle(jrole) then
		if curSeconed - _GetSubstateI(sub_state, 2) > 5 then
			if self.rand < math.random() then return end
			
			prepareBody()
			fillFightReq(monster_id)
			sendMsg(jrole)
			
			_SetSubstateI(sub_state, 2, curSeconed)
		end
	else
		_SetSubstateI(sub_state, 2, 0)
	end
end

