--开启金币宝箱
Actions.openYuanbaoSilverBox = {
	name = 'openYuanbaoSilverBox',
	--银宝箱扣除元宝数目，当前为8元宝
	silver = 50,
	--银宝箱物品储存表
	silverBoxId = 5081,
	--元宝宝箱冷却时间，开启后为6分钟
	coolTime = 6,
}
---------------------------------------------


function Actions.openYuanbaoSilverBox:doAction(roleid, args)
	--获得角色对象
	local jrole = _GetOnline(roleid)
	--获得角色已接受任务列表（必须保证该任务已接受）
	local role_task = _GetRoleTask(jrole)
	--获得角色该宝箱任务
	local task_state = _GetTaskState(role_task, 21030)
	--元宝宝箱当前储存的冷却时间
	local coolTimeTemp =_TaskStateExtraGetI(task_state, 8)
	--今日已开启次数
	local openedNumber = _TaskStateExtraGetI(task_state, 3)
	--每日可以开启总次数
	local sumNumber = _TaskStateExtraGetI(task_state, 4)
	
	--判断角色是否在线
	if jrole then
		----------------------------------------------------------
		--角色背包剩余容量
		if Bag:remainSpace(jrole) == 0 then
			--//TODO背包不足协议
			--replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp, "包裹空间不足，该物品无法使用！")
			self:error(jrole, "背包空间不足", "openYuanbaoBox")
			return
		end
		
		--角色现有元宝数目
		local roleYuanbao=_RoleGetMoney(jrole)
		

		--要扣除的元宝
		local deductYuanbao = self.silver
		--宝箱开启状态(0为未开启，1为已开启)
		local boxIsOpened = _TaskStateExtraGetI(task_state, 10)
		
		----------------------------------------------------
		--开启宝箱前的判断条件
		
		--判断时候有次数开启宝箱
		if openedNumber >= sumNumber then
			--返回次数不足协议
			self:error(jrole, "您今天的开宝箱次数已满20次。请期待明天的手气吧~", "openYuanbaoBox")
			return 
		end
		
		--判断冷却时间
		--取系统当前时间
		local systemTime = _GetMinute()
		--判断冷却时间内该宝箱是否已被开启
		if systemTime < coolTimeTemp and boxIsOpened ~= 0 then
			--返回冷却时间未到
			self:error(jrole, "您的开箱冷却时间未到，可以使用快速刷新跳过冷却时间。", "openYuanbaoBox")
			return
		end
			
		----------------------------------------------------------------------------
		
		
		--角色剩余元宝(扣除开宝箱所需元宝)
		local gameSubtractInfo = luajava.new(GameSubtract, _RoleGetUserId(jrole), _RoleGetJoyId(jrole), deductYuanbao, 0, 0, deductYuanbao, 0, yuanBaoOpConst.OPEN_YUANBAO_SILVER_BOX) 
        PayManager:postSubTask(_RoleGetYuanBaoOp(jrole), gameSubtractInfo)
        --//TODO缺少元宝减少日志
	    log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$32#$本次开宝箱获得物品#$获得物品所有个数#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$" .. deductYuanbao .. "")
		
	end
end

function Actions.openYuanbaoSilverBox:randomItems(jrole)
	--获得角色已接受任务列表（必须保证该任务已接受）
	local role_task = _GetRoleTask(jrole)
	--获得角色该宝箱任务
	local task_state = _GetTaskState(role_task, 21030)
	--随机物品
	local boxToBeOpened = ItemSet[self.silverBoxId]
	local rand_content = boxToBeOpened.itempack:rand()
	--随机获得的物品ID
	local itemid = rand_content[1]
	
	--生成物品JAVA对象
	local itemsTbl = ItemSet[itemid]:creatJavaItem(1)
	--背包增加获得的物品
	if jrole then			
		Bag:addJItem(jrole, itemsTbl)
		--生成并返回使用物品提示					
		self:commonMessage(jrole, "恭喜您！已成功开启" .. boxToBeOpened.name .. "，获得：/" .. ItemSet[itemid].name .. "X1", "openYuanbaoBox")
		
	else
		MailManager:sendSysMail(_RoleGetId(jrole), "", "", 0, itemsTbl)
	end
	
	--今日已开启次数
	local openedNumber = _TaskStateExtraGetI(task_state, 3)
	--今天开启次数加1
	openedNumber = openedNumber + 1
	_TaskStateExtraSetI(task_state, 3, openedNumber)
	
	--设置宝箱开启状态			
	_TaskStateExtraSetI(task_state, 10, 1)
	--元宝宝箱当前储存的冷却时间
	local coolTimeTemp =_TaskStateExtraGetI(task_state, 8)
	--取系统当前时间
	local systemTime = _GetMinute()
	--设置冷却时间
	if systemTime >= coolTimeTemp then
		_TaskStateExtraSetI(task_state, 8, systemTime + self.coolTime)
	end
	-----------------------------------------------------------------------------------
	--同步客户端数据
	prepareBody()
	for _, v in ipairs(itemsTbl) do
		fillBagAdd(v)
		
		local tid = _ItemGetTid(v)
	    local uid = _ItemGetUid(v)
	    local num=_ItemGetStorage(v)
	    --物品增加日志
		--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$7#$" .. ItemSet[itemid].name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
		log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$29#$" .. ItemSet[tid].name .. "#$" .. num .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(tid).."#$".._Long2String(uid))
	end	
	--同步人物基本属性（能否同步元宝）
	fillAttributes(jrole)
	
	putShort(0)
	
	sendMsg(jrole)
end

function Actions.openYuanbaoSilverBox:YuanbaoSyntony(jrole, isSuccess)
	
	if isSuccess then
		self:randomItems(jrole)
	else	
		self:error(jrole, "很抱歉，您的元宝余额不足。", "openYuanbaoBox")
	end
end