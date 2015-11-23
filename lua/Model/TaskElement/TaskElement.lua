
TaskElement = {
	KillMonster = 1,--杀怪任务
	Collection = 2,--打怪收集掉落任务物品
	NPCFight = 3,--和NPC对话后发起战斗
	NPCDialog = 4,
	NpcConvoy = 5,
	SpacialNpcFight = 7,
	MartialCheatsKillMonster = 8,
	OtherNPCFight = 6,--和NPC对话后发起战斗改进
	
	AvailableRange = 4,--显示可接任务的最大级别差
	
	
}

_TaskElement = { }

function _TaskElement:newInstance(o)
    
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _TaskElement:new(template)
     
	if template.type == TaskElement.NPCDialog then
		return _NpcDialog:new(template)
	elseif template.type == TaskElement.Collection then
		return _Collect:new(template)
	elseif template.type == TaskElement.NPCFight then
		return _NpcFight:new(template)
	elseif template.type == TaskElement.KillMonster then
		return _KillMonster:new(template)
	elseif template.type == TaskElement.NpcConvoy then
		return _NpcConvoy:new(template)
	elseif template.type == TaskElement.OtherNPCFight then
		return _OtherNPCFight:new(template)
	elseif template.type == TaskElement.SpacialNpcFight then
	    return _SpacialNpcFight:new(template)
	elseif  template.type == TaskElement.MartialCheatsKillMonster then
	    return _MartialCheatsKillMonster:new(template)
	end
end

function _TaskElement:getAcceptNpc()
	return NPCSet[self.acceptNpc]
end

function _TaskElement:calcRewards(jrole, npcid)
	local rewards = { }
	rewards.wealth = table.shallow_copy(self.rewards.wealth)
	rewards.items = { }
	for k, v in pairs(self.rewards.items) do
		local template = ItemSet[k]
		if template then
			local count = v
			if k == 31124 then  --判断是否为特殊物品 id ==  是的话进行特殊判断，不是则直接继续
			    local realCount = count + Bag:getItemCountByTid(jrole, k)
			    count = realCount > 10 and (10 - Bag:getItemCountByTid(jrole, k)) or count
				if count > 0 then
					if template:isDedicated() then
						if _ObjectEquals(template.vocation, _RoleGetVocation(jrole)) then
							rewards.items[k] = count
						end
					else
						rewards.items[k] = count
					end
				end
		   else
		        if template:isDedicated() then
					if _ObjectEquals(template.vocation, _RoleGetVocation(jrole)) then
						rewards.items[k] = count
					end
				else
					rewards.items[k] = count
				end
		   end
		else
			log.error("子任务", self.id, "奖励物品", k, "不存在！")
		end
	end
	
	return rewards
end

function _TaskElement:getName(task)
	return task.name == self.name and task.name or task.name .. "之" .. self.name
end

function _TaskElement:getDescribe()
	return self.prompt
end

function _TaskElement:getAcceptState()
end

function _TaskElement:accpetSubtask(jrole)
end

function _TaskElement:consignSubtask(jrole)
end

function _TaskElement:updateState()
end

function _TaskElement:quickFinishState()
end

function delTokens(jRole, itemId, sub_state)
   local count = _GetSubstateI(sub_state,6)
   local item  =  ItemSet[itemId].creatJavaItem(1)
   local uId = item:getUid()
   prepareBody()
   fillBagDel(uId,count)
   Bag:delItem(jRole, uId,count)
   fillAttributes(jRole)
   sendMsg(jRole)
end

