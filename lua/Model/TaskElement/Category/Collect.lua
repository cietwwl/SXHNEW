
_Collect = _TaskElement:newInstance()

function _Collect:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Collect:dialog(jrole, npcid, deep, task, sub_state)
	local state = self:getState(jrole, npcid, sub_state)
	
	if state == 1 then
		self:acceptDialog(jrole, npcid, deep, task)
	elseif state == 2 then
		self:ongoingDialog(jrole, npcid, deep, task)
	elseif state == 3 then
		self:consignDialog(jrole, npcid, deep, task)
	else
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "该NPC处任务已完成，请勿连续点击！")
	end
end

function _Collect:acceptDialog(jrole, npcid, deep, task)
	prepareBody()
	putShort(0)
	
	if deep <= #self.acceptDialogs then
		fillNpcDialog(task.id, 0, 1, self:getName(task) .. "/" .. self.acceptDialogs[deep], self.color)
	else
		fillNpcDialog(task.id, 0, 0, "任务【" .. self:getName(task, "之") .. "】已接受", self.color)
		
		task:accpetSubtask(jrole)
		
		fillFreshNPCState(jrole)
	end
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _Collect:consignDialog(jrole, npcid, deep, task)
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
			if prompt then
				fillNpcDialog(task.id, 0, 0, "任务【" .. self:getName(task) .. "】已完成～/" .. prompt, self.color)
			else
				fillNpcDialog(task.id, 0, 0, "任务【" .. self:getName(task) .. "】已完成～", self.color)
			end
			
			local jcontain = Bag:contains(jrole, self:getPackItem())
			for k, v in jmapIter(jcontain) do
				fillBagDel(k, v)
				Bag:delItem(jrole, k, v)
			end
			
			task:consignSubtask(jrole)
			
			fillFreshNPCState(jrole)
		end
	end
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _Collect:getPackItem()
	local jmap = luajava.new(HashMap)
	
	for _, v in ipairs(self.targets) do
		if ItemSet[v.pickup] and ItemSet[v.pickup].type ~= ItemConst.TaskProp then
			jmap:put(v.pickup, v.num)
		end
	end
	
	return jmap
end

function _Collect:ongoingDialog(jrole, npcid, deep, task)
	prepareBody()
	putShort(0)
	
	fillNpcDialog(task.id, 0, 0, self:getName(task) .. "/" .. self.prompt, self.color)
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _Collect:getState(jrole, npcid, sub_state)
	if (not sub_state or _IsSubtastFinished(sub_state)) and npcid == self.acceptNpc then
		return 1
	end
	
	local targets = self.targets
	
	if npcid == self.consignNpc then
		for i = 1, #targets do
			local template = ItemSet[targets[i].pickup]
			if template.type == ItemConst.TaskProp then
				if _GetSubstateI(sub_state, i) < targets[i].num then
					return 2
				end
			else
				if Bag:getItemCountByTid(jrole, template.id) < targets[i].num then
					return 2
				end
			end
		end

		return 3
	end
	
	return 0
end

function _Collect:findPath(jrole, sub_state)
	if not sub_state then
		return NPCSet[self.acceptNpc]:getLocation()
	end
	
	if _IsSubtastFinished(sub_state) then
		return
	end
	
	local targets = self.targets
	
	for i = 1, #targets do
		if ItemSet[targets[i].pickup] and targets[i].kill ~= 0 and 
				_GetSubstateI(sub_state, i) < targets[i].num then--有寻路怪物且此物品收集未完成
			return MonsterSet[targets[i].kill]:getLocation()
		end
	end
	
	return NPCSet[self.consignNpc]:getLocation()
end

function _Collect:getDescribe(jrole, sub_state)
	local targets = self.targets
	
	local des = { }
	des[#des + 1] = self.prompt
	for i = 1, #targets do
		local template = ItemSet[targets[i].pickup]
		local count = (template.type == ItemConst.TaskProp) and _GetSubstateI(sub_state, i) or Bag:getItemCountByTid(jrole, template.id)
		count = count > targets[i].num and targets[i].num or count
		des[#des + 1] = template.name .. "：" .. count .. "|" .. targets[i].num
	end
	
	return table.concat(des, "/")
end

function _Collect:getAcceptState()
	return true
end

function _Collect:updateState(monster, sub_state, prompt)
	local update
		
	local targets = self.targets
	for i = 1, #targets do
		if targets[i].kill == monster.id then
			local old_value = _GetSubstateI(sub_state, i)
			local tid = targets[i].pickup
			local template = ItemSet[tid]
			if template and template.type == ItemConst.TaskProp and old_value < targets[i].num and monster.taskprops[tid] 
					and math.random() < monster.taskprops[tid] / 10000 then
				local rand = math.random() < 0.25 and 2 or 1
				local value = old_value + rand
				if value <= targets[i].num then
					_SetSubstateI(sub_state, i, value)
					prompt[#prompt + 1] = template.name .. "×" .. rand
				else
					_SetSubstateI(sub_state, i, value - 1)
					prompt[#prompt + 1] = template.name .. "×" .. 1
				end
				
				update = true
			end
		end
	end
	
	return update
end

function _Collect:quickFinishState(jrole, sub_state)
	local coll_comn_prop
	
	local targets = self.targets
	for i = 1, #targets do
		local template = ItemSet[targets[i].pickup]
		if template.type == ItemConst.TaskProp then
			_SetSubstateI(sub_state, i, targets[i].num)
		else
			local count = Bag:getItemCountByTid(jrole, template.id)
			if count < targets[i].num then
				if not coll_comn_prop then
					prepareBody()
					coll_comn_prop = true
				end
				
				local itemsTbl = template:creatJavaItem(targets[i].num - count)
				
				for _, v in ipairs(itemsTbl) do
					fillBagAdd(v)
					local tid = _ItemGetTid(v)
				    local uid = _ItemGetUid(v)
				    local itemnum=_ItemGetStorage(v)
				    log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$12#$" .. template.name .. "#$" .. itemnum .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(tid).."#$".._Long2String(uid))
				end
				--log.item("生成物品id【", template.id, "】数量【", targets[i].num - count, "】")
				--log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$12#$" .. template.name .. "#$" .. targets[i].num - count .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
				Bag:addJItem(jrole, itemsTbl)
			end
		end
	end
	
	if coll_comn_prop then
		putShort(0)
	
		sendMsg(jrole)
	end
end
