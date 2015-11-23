
_ScrollTask = _CommonTask:new()

function _ScrollTask:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

--不在备忘中显示
function _ScrollTask:showInMEMOS(jrole, diff)
end

-------------------------------------------------------------------------------
--在任务列表中显示
function _ScrollTask:showInTaskList(jrole, task_state)
	local sub_state = _GetSubState(task_state)
	local step = _GetTaskStateStep(task_state)
	local element = TaskElementSet[self.detail[step]]
	-------------------------------------------------------------------------------
	--计算寻路坐标
	local location
	if _IsSubtastFinished(sub_state) then
		local next = TaskElementSet[self.detail[step + 1]]
		if not next then
			log.error(_RoleGetNick(jrole), "：任务", self.name, "第", step + 1, "环异常！")
			return
		end
		location = next:findPath(jrole)
	else
		location = element:findPath(jrole, sub_state)
	end
	
	if not location then
		return
	end
	--计算寻路坐标
	-------------------------------------------------------------------------------
	--填充任务基本属性
	putByte(self.category)
	putInt(self.id)
	putString(self.name)
	putInt(self.color)--任务名称颜色
	--填充任务基本属性
	-------------------------------------------------------------------------------
	--填充任务提示
	
	local direct_consign
	if _IsSubtastFinished(sub_state) then--上一环已完成
		local next = TaskElementSet[self.detail[step + 1]]--下一环
		putString("去找" .. next:getAcceptNpc().nick .. "！")--提示去找下一环接受NPC
	else
		if step == #self.detail then
			local state = element:getState(jrole, element.consignNpc, sub_state)
			if state == 3 then
				putString("任务已完成，请点击提交！")
				
				direct_consign = true
			end
		end
		
		if not direct_consign then
			putString(element:getDescribe(jrole, sub_state))--子任务提示
		end
	end
	
	putInt(self.color)--任务提示颜色
	--填充任务提示
	-------------------------------------------------------------------------------
	putByte(self.noGiveUp and 2 or 3)
	
	if direct_consign then
		putByte(3)--菜单/取消提示
		putString("提交任务")
		putInt(0)--颜色
		putShort(0)
		putInt(0)
		putInt(0)
	else
		putByte(0)--菜单/取消提示
		putString("立即前往")
		putInt(0)--颜色
		putShort(location.mapid)
		putInt(location.mapx)
		putInt(location.mapy)
	end
	
	if not self.noGiveUp then
		putByte(1)--菜单/放弃任务
		putString("放弃任务")
		putInt(0)--颜色
		putShort(0)
		putInt(0)
		putInt(0)
	end
	
	putByte(2)--菜单/取消提示
	putString("取消提示")
	putInt(0)--颜色
	putShort(0)
	putInt(0)
	putInt(0)
	
	return true
end

function _ScrollTask:directConsign(jrole)
	local role_task = _GetRoleTask(jrole)
	
	local task_state = _GetTaskState(role_task, self.id)
	if not task_state then
		replyMessage(jrole, 3, MsgID.Task_Finish_Resp, "提交失败！任务尚未接受！")
		return
	end
	
	if _IsTaskFinished(task_state) then
		replyMessage(jrole, 4, MsgID.Task_Finish_Resp, "提交失败！")
		return
	end
	
	local sub_state = _GetSubState(task_state)
	if _IsSubtastFinished(sub_state) then
		replyMessage(jrole, 5, MsgID.Task_Finish_Resp, "提交失败！")
		return
	end
	
	local subtaskid = self.detail[#self.detail]
	local element = TaskElementSet[subtaskid]
	local state = element:getState(jrole, npcid, sub_state)
	if state ~= 3 then
		replyMessage(jrole, 6, MsgID.Task_Finish_Resp, "提交失败！任务尚未完成！")
		return
	end
	
	local awards = element:calcRewards(jrole)--计算当前任务奖励物品个数
	if not Bag:checkAdd(jrole, awards) then
		replyMessage(jrole, 7, MsgID.Task_Finish_Resp, "包裹空间不足！")
	else
		prepareBody()
		putShort(0)
		
		local prompt = Economy:reward(jrole, awards)
		fillNpcDialog(task.id, 0, 0, "任务【" .. self:getName(task, "之") .. "】已完成～/" .. (prompt or ""), self.color)
		
		_DelTaskState(_GetRoleTask(jrole), self.id)
		
		fillFreshNPCState(jrole)
	end
	
	sendMsg(jrole, MsgID.Task_Finish_Resp)
end
