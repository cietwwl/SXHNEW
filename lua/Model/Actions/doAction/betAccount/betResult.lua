
Actions.betResult = {
	name = 'betResult',
	--押注的点数
	i = 0,
	--押注的元宝数量
	yuanbao = 0,
}

function Actions.betResult:doAction(roleid, args)

	local jrole = _GetOnline(roleid)

	if jrole then
		--押注的点数(i是列表的位置数，点数应该是i+2)
		_, self.i = pairs(args)(args)
		--押注的元宝数目
		self.yuanbao = args.yuanbaoForBet
		--角色现有元宝数目	
		local roleYuanbao = _RoleGetMoney(jrole)
		
		if args.yuanbaoForBet-0>10000 then
			self.layout.tags[1].display = [=[抱歉，押注不能大于10000元宝！ ]=]
			self:doDisplay(jrole)
		elseif args.yuanbaoForBet-0<20 then
			self.layout.tags[1].display = [=[抱歉，押注不能小于20元宝！ ]=]
			self:doDisplay(jrole)
		else
			--扣除玩家花费的元宝数量
			local gameSubtractInfo  = luajava.new(GameSubtract, _RoleGetUserId(jrole), _RoleGetJoyId(jrole), args.yuanbaoForBet-0, 0, 0, roleYuanbao, 0, yuanBaoOpConst.BET_ACCOUNT)
			PayManager:postSubTask(_RoleGetYuanBaoOp(jrole), gameSubtractInfo)
		end
	end
end


function Actions.betResult:YuanbaoSyntony(jrole, isSuccess)

	if isSuccess then
		--角色现有元宝数目	
		local roleYuanbao = _RoleGetMoney(jrole)
		--押注的点数
		local betSize = self.i+2
		--添加元宝赌数任务
		_BetAccountAddBetRole(jrole, self.yuanbao, betSize)
		self:commonMessage(jrole, "押注成功！/您押 " .. betSize .. " 点 " .. self.yuanbao .. " 元宝，还剩 " .. roleYuanbao .. " 元宝", "betAccount")
		-----------------------------------------------------------------------------------
		--同步客户端数据
		prepareBody()
		--同步人物基本属性
		fillAttributes(jrole)
		putShort(0)
		sendMsg(jrole)
	else	
		self:error(jrole, "很抱歉，您的元宝余额不足。", "betAccount")
	end
end