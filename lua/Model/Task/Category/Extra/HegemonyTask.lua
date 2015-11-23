_HegemonyTask  = _CommonTask:new()

--狮王争霸
_HegemonyTask.findMenu = {
	{ 1, "下一页", 0, },
	{ 2, "返回", 0, },
}

_HegemonyTask.backMenu = {
	{ 2, "返回", 0, },
}

function _HegemonyTask:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _HegemonyTask:dialog(jrole, npcid, deep, uid)

	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	if deep == 1 then
	   self:showChoice(jrole,task_state)
	   return
	elseif deep >= 2 then
		if uid >= 6 then
			replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, "狮王争霸：决出天龙八部的王者，最终还能站得起来，并且积分达到500，可获得丰厚奖品。每天晚上8点到8点30分开放，等级高于50级的玩家可参加。")
			return
		elseif uid >= 2 then
	   		self:showInfoByAcceptLevel(jrole,npcid, deep, uid)
			return
		end
		if task_state and uid <= 1 then
			if _IsTaskFinished(task_state) then
				replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, "........")
					return
			end
		else
			local hour = _GetNowHour()
			local min =  _GetNowMin()
			if (hour == 20 and min > 45) or hour >= 21 then
			    replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, "今天的狮王争霸已经结束！")
				return
			elseif hour == 20 and min >= 30 then
				replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, "今天的狮王争霸报名已结束，请明天在参加！")
				return
			elseif hour == 20  then
				if _RoleGetTeam(jrole) then
					replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, "组队无法进入！")
					return
				end
				
				--如果接受等级确定的话则开始新建任务状态
				if uid == 1 and deep == 2 then
					prepareBody()
					putShort(0)
					fillNpcDialog(self.id, 0, 1, self.name .. "/" .. self.acceptDialogs[deep - 1], self.color)
					putShort(0)
					sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
				end
				
				if  uid == 0 and deep == 3 then
				
					--设置任务等级
					local  acceptLevel
					local roleLv = _GetRoleLevel(jrole) --获取角色等级
					if  roleLv < 60 then
						acceptLevel = 1  -- 对应传送地图203
					elseif roleLv < 70 then
						acceptLevel = 2	 -- 对应传送地图204
					elseif roleLv < 80 then
						acceptLevel = 3	 -- 对应传送地图205
					else
						acceptLevel = 4  -- 对应传送地图193
					end
					
										--_ForceDeleteTeam(jrole)
					local mapId = self.mapDeatil[acceptLevel]
					transToMapRandom(jrole,mapId)
					_AddTaskState(role_task, luajava.new(TaskState, self.id))
										--local task_state = self:getAcceptState(jrole)
					self:acceptTask(jrole)
					_RoleGetInHegemony(jrole)
				end
			else
				replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, "今天的狮王争霸还未开始，请耐心等待！")
				return
			end
		end
	end
end

function _HegemonyTask:self_check(jrole, task_state, curMin)
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


function _HegemonyTask:acceptTask(jrole)
	local task_state = self:getAcceptState(jrole)
	if not task_state then
		return
	end
	_TaskStateExtraSetI(task_state, 1, _GetNextFreshMin(self.freshOnTheHour))

	_AddTaskState(_GetRoleTask(jrole), task_state)
	prepareBody()
	fillFreshNPCState(jrole)
	fillSystemPrompt("任务" .. self.name .. "已接受～")
	putShort(0)
	sendMsg(jrole)
end

-- 领奖
function rewardHegemony(hegemonys)
	local task = TaskSet[24000]
	if hegemonys:size() == 0 then
		return 
 	end

	for i = 1, math.huge do
		if i == (hegemonys:size() + 1) then
	    	break
		end
		local item = 0
		local gold = 0 
		local h = hegemonys:get(i - 1)
		local jrole = h:getHegemony()
		local rank = h:getRanking()
		local acceptLevel = h:getAcctLevel()
		local point = h:getPoints()
		if rank == 1 then
			item = task.reward[acceptLevel].firstReward.item
			gold = task.reward[acceptLevel].firstReward.gold
		elseif rank == 2 then
			item = task.reward[acceptLevel].secondReward.item
			gold = task.reward[acceptLevel].secondReward.gold
		elseif rank == 3 then
			item = task.reward[acceptLevel].thirdReward.item
			gold = task.reward[acceptLevel].thirdReward.gold
		elseif rank >= 4 and rank <= 10 then
			gold = task.reward[acceptLevel].otherReward.gold
		end
		--通过邮件将奖励物品和金币发送至玩家手中
		local itemBel
		if item ~= 0 then
			itemBel = ItemSet[item]:creatJavaItem()[1]
		end

		if itemBel and gold > 0 then 
			MailManager:sendSysMail(_RoleGetId(jrole),"狮王争霸奖励","",gold * 10000,itemBel)
			log.hegemony(_RoleGetId(jrole).. '#$' .. '金币:' .. gold * 10000 .. ';物品:'.. ItemSet[item].name  .. '#$' .. _GetNowTimeString())
		elseif gold > 0 then
			MailManager:sendSysMail(_RoleGetId(jrole),"狮王争霸奖励","",gold * 10000,nil)
			log.hegemony(_RoleGetId(jrole).. '#$' .. '金币:' .. gold * 10000 ..  '#$' .. _GetNowTimeString())
		end
		taskComplete(h:getHegemony())
	end 
end

function taskComplete(jrole) 
	local task = TaskSet[24000]
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, task.id)
	task_state = task_state or luajava.new(TaskState, task.id)

	--如果子任务完成，则将主任务也设置为已完成并存档
	_CompleteTask(task_state)
	_TaskStateExtraSetI(task_state, 1, _GetNextFreshMin(task.freshOnTheHour))
	_AddTaskState(role_task, task_state)
end

--@return 任务状态：0忽略；1可接任务；2任务进行中；3可交任务
function _HegemonyTask:getState(jrole, npcid)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	--接受等级不够
	local role_level = _GetRoleLevel(jrole)
	if self.minLevel and self.maxLevel then
		if role_level < self.minLevel or role_level > self.maxLevel then
			return 0
		end
	end
	--接受等级不够
	if task_state then
		return 2
	end
	return 1
end

function _HegemonyTask:getAcceptState(jrole)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	return task_state
end


function _HegemonyTask:showChoice(jRole,task_state)
	prepareBody()
	putShort(0)

	bodyMark()
	putShort(300)--子模块300
	putByte(6)

	putInt(self.id)
	putLong(6)
	putByte(2)
	putString("什么是‘狮王争霸’？")
	putInt(0)

	putInt(self.id)
	putLong(1) --此字断表示选择哪个选项      1 结束挑战领取领取任务奖励       2 传送至八部浮屠塔继续挑战
	putByte(2)
	putString("参加狮王争霸")
	putInt(0)

	putInt(self.id)
	putLong(2)
	putByte(2)
	putString("狮王争霸排行榜(50-59)")
	putInt(0)

	putInt(self.id)
	putLong(3)
	putByte(2)
	putString("狮王争霸排行榜(60-69)")
	putInt(0)

	putInt(self.id)
	putLong(4)
	putByte(2)
	putString("狮王争霸排行榜(70-79)")
	putInt(0)

	putInt(self.id)
	putLong(5)
	putByte(2)
	putString("狮王争霸排行榜(80以上)")
	putInt(0)

	sendMsg(jRole, MsgID.MsgID_Talk_To_Npc_Resp)
end

-- 查看排名
function _HegemonyTask:showInfoByAcceptLevel(jrole,npcid, deep, uid, num)
	local count = _GetTopHegemonyNum(uid - 1)
	local topHemony = _GetTopHegemony(uid - 1, 3 * deep - 5)
	local regular = { 1,}
	if count == 0 then
		replyMessage(jrole, 1, MsgID.MsgID_Talk_To_Npc_Resp, topHemony)
		return
	else 
		prepareBody()
		putShort(0)
		if (count - 3 * deep + 3) > 0 then
			fillNpcItemOP(topHemony, 0, regular,self.findMenu)
		else 
			fillNpcItemOP(topHemony, 0, regular,self.backMenu)
		end
		putShort(0)
		sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
		return 
	end
end