Actions.acceptTiger = {
	name = 'acceptTiger',
}

function Actions.acceptTiger:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	if jrole then
		--角色现有金钱数目
		local roleGold = _RoleGetGold(jrole)
		local inning = args.inning
		if not _QueryChallenge(inning) then
			self:error(jrole, "很可惜您晚了一步，这场挑战已结束，快抢下一个机会吧。", "acceptEntry")
			return
		end 
		local cyclesMessage = _GetChallengeCyclesMessage(inning)
		local goldForCycles = _GetGold(cyclesMessage)
		if goldForCycles < 1000 then
			--返回至少要押注其中一个并且不得小于10银币
			self:error(jrole, "押注最小金额为10银币", "acceptEntry")
			return
		end
		
		
		if goldForCycles > roleGold then
			--返回至少要押注其中一个并且不得小于1银币
			self:error(jrole, "太可惜了，您的现金不足。", "acceptEntry")
			return
		end
		
		--角色剩余金币(扣除押大小所需金币)
		_RoleSetGold(jrole, roleGold - goldForCycles)
		log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$37#$本次老虎棒子鸡获得物品#$获得物品所有个数#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$" .. goldForCycles .. "")
		--发起应战
		if not inning then 
			self:error(jrole, "很可惜您晚了一步，这场挑战已结束，快抢下一个机会吧。", "acceptEntry")
			return
		end
		_CyclesResults(inning, roleid, 0)
		--发送消息
		self:commonMessage(jrole, "恭喜您！挑战成功！再挑战一个来玩吧", "cycles")
		
		---------------------------------------------------------------------------------------
		----同步客户端数据
		--清空缓冲区
		prepareBody()
		--同步人物基本属性
		fillAttributes(jrole)
		putShort(0)
		--发送消息
		sendMsg(jrole)
	end

end