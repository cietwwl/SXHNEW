--金币宝箱快速刷新
Actions.fastRefreshYuanbaoBox = {
	name = 'fastRefreshYuanbaoBox',
}

function Actions.fastRefreshYuanbaoBox:doAction(roleid, args)
	--获得角色对象
	local jrole = _GetOnline(roleid)
	--判断角色是否在线
	if jrole then
		--获得角色已接受任务列表（必须保证该任务已接受）
		local role_task = _GetRoleTask(jrole)
		--获得角色该宝箱任务
		local task_state = _GetTaskState(role_task, 21030)
		
		
		local confirmAction = Actions.fastRefreshConfirm
		confirmAction.layout.tags[2].name = "yuanbaoFastRefreshDeductExpense"
		confirmAction.layout.tags[2].action = "yuanbaoFastRefreshDeductExpense"
		confirmAction.layout.tags[3].name = "openYuanbaoBox"
		confirmAction.layout.tags[3].action = "openYuanbaoBox"
		
		if _TaskStateExtraGetI(task_state, 9) == 1 and _TaskStateExtraGetI(task_state, 10) == 1 and _TaskStateExtraGetI(task_state, 11) == 1 and _TaskStateExtraGetI(task_state, 8) >=0 then
			confirmAction.layout.tags[1].value = "您的宝箱都已抽取,确定要支付5元宝进行快速刷新吗?"

			
			--Actions:fastRefreshMessage(jrole, "您的宝箱都已抽取,确定要支付5元宝进行快速刷新吗?" , "deductExpenses", "openBox")
		else
			confirmAction.layout.tags[1].value = "您还有宝箱未抽取,确定要支付5元宝进行快速刷新吗?"
			--Actions:fastRefreshMessage(jrole, "您还有宝箱未抽取,确定要支付5元宝进行快速刷新吗?" , "deductExpenses", "openBox")
		end		
		confirmAction:doDisplay(jrole)
	end
end

function Actions.fastRefreshYuanbaoBox:fastRefresh(jrole)
		--获得角色已接受任务列表（必须保证该任务已接受）
		local role_task = _GetRoleTask(jrole)
		--获得角色该宝箱任务
		local task_state = _GetTaskState(role_task, 21030)
		
		--金币宝箱冷却时间，开启后为6分钟
		_TaskStateExtraSetI(task_state, 8, 0)
		--重置宝箱状态为未开启
		_TaskStateExtraSetI(task_state, 9, 0)
		_TaskStateExtraSetI(task_state, 10, 0)
		_TaskStateExtraSetI(task_state, 11, 0)
		---------------------------------------------------------------
		--刷新成功
		self:commonMessage(jrole, "恭喜您！刷新成功" , "openYuanbaoBox")
		--同步客户端数据
		prepareBody()
		---同步该角色元宝
		fillAttributes(jrole)
		putShort(0)
		
		sendMsg(jrole)
end
function Actions.fastRefreshYuanbaoBox:YuanbaoSyntony(jrole, isSuccess)
	
	if isSuccess then
		self:fastRefresh(jrole)
	else	
		self:error(jrole, "很抱歉，您的元宝余额不足。", "openYuanbaoBox")
	end
end