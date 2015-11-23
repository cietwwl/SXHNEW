
Actions.challengeStick = {
	name = 'challengeStick',
}

function Actions.challengeStick:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	if jrole then
		
		local goldForCycles = tonumber(args.goldForCycles) * 100
		
		if goldForCycles < 1000 then
			--返回至少要押注其中一个并且不得小于10银币
			self:error(jrole, "押注最小金额为10银币", "challenge")
			return
		end
		
		--角色现有金钱数目
		local roleGold = _RoleGetGold(jrole)
		
		if goldForCycles > roleGold then
			--返回至少要押注其中一个并且不得小于1银币
			self:error(jrole, "太可惜了，您的现金不足。", "challenge")
			return
		end
		
		
		--角色剩余金币(扣除押大小所需金币)
		_RoleSetGold(jrole, roleGold - goldForCycles)
		log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$37#$本次老虎棒子鸡获得物品#$获得物品所有个数#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$" .. goldForCycles .. "")
		--添加到发起挑战列表了
		_ChallengeAddCyclesMessage( roleid, goldForCycles, 1)
		--发送消息
		self:commonMessage(jrole, "您已经成功创建了对所有玩家的挑战，请耐心等待其它玩家来挑战吧！", "cycles")
		
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