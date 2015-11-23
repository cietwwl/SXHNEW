
_TaskScroll= _Prop:new()

function _TaskScroll:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _TaskScroll:canUse(jrole)
	return true
end

function _TaskScroll:useItem(jrole, item)
	local role_level = _GetRoleLevel(jrole)
	if (self.minLevel or 0) > role_level then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "级别过低不能使用！")
		return
	end
	
	if role_level > (self.maxLevel or 100) then
		replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp, "级别过高不能使用！")
		return
	end
	
	local function valid(each_taskid)
		return not _GetTaskState(_GetRoleTask(jrole), each_taskid)
	end
	local entry = self.tasks:rand(valid)
	if not entry then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "所有任务都在进行中，没有可用的任务！")
		return
	end
	
	local task = TaskSet[entry[1]]
	if not task or not task.getAcceptState then
		log.info("卷轴对应的任务", taskid, "不存在")
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "使用失败！")
		return
	end
	
	local task_state = task:getAcceptState(jrole)
	if not task_state then
		replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp, "使用失败！")
		return
	end
	
	_AddTaskState(_GetRoleTask(jrole), task_state)
	
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name .. "/任务" .. task.name .. "已接受～")
	
	---------------------------------------------------------------------------
	--更新附近NPC状态并提示
	prepareBody()
	
	fillBagDel(item:getUid())

	--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	Bag:delItem(jrole, item:getUid(), 1)
	
	fillFreshNPCState(jrole)
	
	putShort(0)
	
	sendMsg(jrole)
end

function _TaskScroll:getDescribe()
	if not self.__describe then
		local tbl = { }
		tbl[#tbl + 1] = self.name
		for _, v in ipairs(self.feature or { }) do
			if _ItemFeatureIsShow(v) then
				tbl[#tbl + 1] = _ItemFeatureGetName(v)
			end
		end
		tbl[#tbl + 1] = self.describe
		
		tbl[#tbl + 1] = "使用等级：" .. (self.level or 1)
		
		self.__describe = table.concat(tbl, "/")
	end
	
	return self.__describe
end

function _TaskScroll:getUseMenu()
	return	{
				{ 0, "使用", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end
			
_TaskScroll.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}

function _TaskScroll:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end
