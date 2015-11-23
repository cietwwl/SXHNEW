--开启金币宝箱
Actions.openGoldBox = {
	name = 'openGoldBox',
	--金宝箱扣除金钱数目,当前为2金币
	gold = 50000,
	--金宝箱物品储存表
	goldBoxId = 5078,
	--金币宝箱冷却时间，开启后为6分钟
	coolTime = 6,
}

function Actions.openGoldBox:doAction(roleid, args)
	--获得角色对象
	local jrole = _GetOnline(roleid)
	--获得角色已接受任务列表（必须保证该任务已接受）
	local role_task = _GetRoleTask(jrole)
	--获得角色该宝箱任务
	local task_state = _GetTaskState(role_task, 21030)
	--金币宝箱以存储冷却时间
	local coolTimeTemp =_TaskStateExtraGetI(task_state, 2)
	--今天已开启次数
	local openedNumber = _TaskStateExtraGetI(task_state, 3)
	--每天可以开启总次数
	local sumNumber = _TaskStateExtraGetI(task_state, 4)
	--判断角色是否在线
	if jrole then
		----------------------------------------------------------
		--角色背包剩余容量
		if Bag:remainSpace(jrole) == 0 then
			--//TODO背包不足协议
			--replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp, "包裹空间不足，该物品无法使用！")
			self:error(jrole, "背包空间不足", "openBox")
			return
		end
		
		--角色现有金钱数目
		local roleGold=_RoleGetGold(jrole)
		
		--要被打开的宝箱
		local boxToBeOpened = ItemSet[self.goldBoxId]
		--要扣除的金钱
		local deductMoney = self.gold
		--宝箱开启状态
		local boxIsOpened = _TaskStateExtraGetI(task_state, 5)
		------------------------------------------------------------------------------------

		--开启宝箱前的判断条件
		
		--判断时候有次数开启宝箱
		if openedNumber >= sumNumber then
			--返回次数不足协议
			self:error(jrole, "您今天的开宝箱次数已满20次。请期待明天的手气吧~", "openBox")
			return 
		end
		----判断角色现有金币是否够开当前宝箱
		if roleGold-deductMoney < 0 then
			--返回金币不足协议
			self:error(jrole, "很抱歉，您的金币余额不足。", "openBox")
			return
		end
		
		--取系统当前时间
		local systemTime = _GetMinute()
		--判断冷却时间内该宝箱是否已被开启
		if systemTime < coolTimeTemp and boxIsOpened ~= 0 then
			--返回冷却时间未到
			self:error(jrole, "您的开箱冷却时间未到，可以使用快速刷新跳过冷却时间。", "openBox")
			return
		end
		----------------------------------------------------------------------------
		--开启宝箱	
		--扣钱	
		_RoleSetGold(jrole, roleGold - deductMoney)
		--随机物品
		local rand_content = boxToBeOpened.itempack:rand()
		--随机获得的物品ID
		local itemid = rand_content[1]
		
		--背包增加获得的物品
		local itemsTbl = ItemSet[itemid]:creatJavaItem(1)
		Bag:addJItem(jrole, itemsTbl)
		
		--生成并返回使用物品提示					
		self:commonMessage(jrole, "恭喜您！已成功开启" .. boxToBeOpened.name .. "，获得：/" .. ItemSet[itemid].name .. "X1", "openBox")
		--今天开启次数加1
		openedNumber = openedNumber + 1
		_TaskStateExtraSetI(task_state, 3, openedNumber)
		
		
		--设置宝箱开启状态			
		_TaskStateExtraSetI(task_state, 5, 1)
		--设置冷却时间
		if systemTime >= coolTimeTemp then
			_TaskStateExtraSetI(task_state, 2, systemTime + self.coolTime)
		end
		-----------------------------------------------------------------------------------
		
		prepareBody()
		--同步客户端数据
		for _, v in ipairs(itemsTbl) do
			fillBagAdd(v)
		local tid = _ItemGetTid(v)
	    local uid = _ItemGetUid(v)
	    local num=_ItemGetStorage(v)
	    --//TODO物品增加日志
		--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$7#$" .. ItemSet[itemid].name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
		log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$24#$" .. ItemSet[itemid].name .. "#$" .. num .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(tid).."#$".._Long2String(uid))
		end
		--同步人物基本属性
		fillAttributes(jrole)
		
		--//TODO缺少金钱减少日志
		log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$27#$本次开宝箱获得物品#$获得物品所有个数#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$" .. deductMoney .. "")
		
		putShort(0)
		
		sendMsg(jrole)
	end
end