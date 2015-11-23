
_TimingAward = _Task:newInstance()

function _TimingAward:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

_TimingAward._layout = {
	extra = {
		"下次给奖励时间，时间精度为秒",
	}
}

_TimingAward.feature = {
	ItemFeature.Bind,
	ItemFeature.NoSell,
	ItemFeature.NoMail,
}

function _TimingAward.func(jItems)
	for _, v in ipairs(jItems) do
		if ItemSet[_ItemGetTid(v)].type == ItemConst.Equip then
			_ItemSetFeatures(v, unpack(_TimingAward.feature))
		end
	end
end

-------------------------------------------------------------------------------
--在任务列表中显示
function _TimingAward:showInTaskList(jrole, task_state)
	--填充任务基本属性
	putByte(self.category)
	putInt(self.id)
	putString(self.name)
	putInt(self.color)--任务名称颜色
	--填充任务基本属性
	-------------------------------------------------------------------------------
	--填充任务提示
	local step = _GetTaskStateStep(task_state)
	local award = self.award[step]
	local curSeconed = _GetSecond()
	local award_time = _TaskStateExtraGetI(task_state, 1)
	
	if curSeconed >= award_time then
		if Bag:checkAdd(jrole, self.awards) then
			putString("任务已完成，请点击提交！")
			putInt(self.color)--任务提示颜色
			
			putByte(2)
			
			putByte(3)--菜单/取消提示
			putString("提交任务")
			putInt(0)--颜色
			putShort(0)
			putInt(0)
			putInt(0)
		else
			putString("背包已满，请在清理包裹后点击提交！")
			putInt(self.color)--任务提示颜色
			
			putByte(1)
		end
	else
		local left_second = award_time - curSeconed
		local hours = left_second / 3600
		hours = hours - hours % 1
		local minute = (left_second - hours * 3600) / 60
		minute = minute - minute % 1
		
		putString("剩余时间：" .. hours .. "小时" .. minute.. "分钟/请耐心等待！")
		putInt(self.color)--任务提示颜色
		
		putByte(1)
	end
	
	putByte(2)--菜单/取消提示
	putString("取消提示")
	putInt(0)--颜色
	putShort(0)
	putInt(0)
	putInt(0)
	
	return true
end

local Chi = {
	"一",
	"二",
	"三",
	"四",
	"五",
	"六",
	"七",
	"八",
	"九",
	"十",
}
function _TimingAward:directConsign(jrole)
	local role_task = _GetRoleTask(jrole)
	
	local task_state = _GetTaskState(role_task, self.id)
	if not task_state then
		replyMessage(jrole, 3, MsgID.Task_Finish_Resp, "提交失败！任务尚未接受！")
		return
	end
	
	if _IsTaskFinished(task_state) then
		replyMessage(jrole, 4, MsgID.Task_Finish_Resp, "提交失败！任务已提交！")
		return
	end
	
	local step = _GetTaskStateStep(task_state)
	local awards = self.award[step]
	local curSeconed = _GetSecond()
	local award_time = _TaskStateExtraGetI(task_state, 1)
	
	if curSeconed < award_time then
		replyMessage(jrole, 5, MsgID.Task_Finish_Resp, "提交失败！未到给予奖励时间！")
		return
	end
	
	if not Bag:checkAdd(jrole, awards) then	
		replyMessage(jrole, 6, MsgID.Task_Finish_Resp, "提交失败！背包已满！")
		return
	end
	
	---------------------------------奖励物品同步-------------------------------------
	prepareBody()
	awards.items.func = awards.items.func or self.func
	local prompt = Economy:reward(jrole, awards)
	putShort(0)
	sendMsg(jrole)
	
	---------------------------------领取奖励提示--------------------------------------
	replyMessage(jrole, 6, MsgID.Task_Finish_Resp, "【" .. self.name .. "之" .. Chi[step] .. "】已完成～/" .. (prompt or ""))
	
	---------------------------------同步任务状态显示----------------------------------	
	prepareBody()
	if step == #self.award then
		_CompleteTask(task_state)
		
		_TaskDelMonitor(jrole, self.id)
		
		sendMsg(jrole, MsgID.Task_TimeAward_End)
	else
		_TaskStateStepUp(task_state)
		_TaskStateExtraSetI(task_state, 1, curSeconed + self.award[step + 1].period * 60)
		_TaskStateExtraSetI(task_state, 2, 0)
		
		putInt(self.award[step + 1].period * 60)--秒
		sendMsg(jrole, MsgID.Task_TimeAward)
		
		_TaskAddMonitor(jrole, self.id)
	end
end

function _TimingAward:newState(jrole, curSeconed)
	local task_state = luajava.new(TaskState, self.id)
	_TaskStateExtraSetI(task_state, 1, curSeconed + self.award[1].period * 60)
	print('执行_TimingAward:newState')
	prepareBody()
	putInt(self.award[1].period * 60)--秒
	sendMsg(jrole, MsgID.Task_TimeAward)
	print('执行_TimingAward:newState完毕')
	
	_TaskAddMonitor(jrole, self.id)
	
	return task_state
end

function _TimingAward:self_check(jrole, task_state, curMin, curSeconed)
	if _IsTaskFinished(task_state) then
		return true
	end

	local step = _GetTaskStateStep(task_state)
	if step > #self.award or step < 1 then
		return true
	end

	local time_complete = _TaskStateExtraGetI(task_state, 2)
	
	if time_complete == 0 then

		_TaskAddMonitor(jrole, self.id)
		
		_TaskStateExtraSetI(task_state, 1, curSeconed + self.award[step].period * 60)
		
		prepareBody()
		putInt(self.award[step].period * 60)--秒
		sendMsg(jrole, MsgID.Task_TimeAward)
	end
	
	return true
end

function _TimingAward:seconedTick(jrole, curSeconed)
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, self.id)
	
	local award_time = _TaskStateExtraGetI(task_state, 1)
	if curSeconed > award_time then
		prepareBody()
		sendMsg(jrole, MsgID.Task_TimeAward_End)
		
		_TaskStateExtraSetI(task_state, 2, 1)
		
		return true
	end
end
