
Actions.betGuess = {
	name = 'betGuess',
}

function Actions.betGuess:doAction(roleid, args)
	
	local jrole = _GetOnline(roleid)
	--押大小扣除的手续费百分比
	--local commission = 0.05
	--押大的金额
	local goldForBig = tonumber(args.goldForBig) * 100
	--押小的金额
	local goldForSmall = tonumber(args.goldForSmall) * 100
	
	if goldForBig < 0 or goldForSmall < 0 then
		self:error(jrole, "押注金额不能为负", "guess")
		return 
	end
	
	--判断角色是否在线
	if jrole then
		--角色现有金钱数目
		local roleGold = _RoleGetGold(jrole)
		
		if goldForBig < 100 and goldForSmall < 100 then
			--返回至少要押注其中一个并且不得小于1银币
			self:error(jrole, "押注最小金额为1银币", "guess")
			return 
		end
		
		if roleGold < goldForBig + goldForSmall then
			--返回金币不足
			self:error(jrole, "太可惜了，您的现金不足。", "guess")
			return
		end
		
		local deductmoney= goldForBig + goldForSmall
		--角色剩余金币(扣除押大小所需金币)
		_RoleSetGold(jrole, roleGold - goldForBig - goldForSmall)
		
		log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$35#$本次押大小获得物品#$获得物品所有个数#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$" .. deductmoney .. "")
		
		
		--调用押大的处理机制
		if goldForSmall >= 1 then 
			--_guessAddBetRole(roleid, goldForSmall * (1 - commission), 0)
			_guessAddBetRole(roleid, goldForSmall, 0)
		end
		--调用押小的处理机制 
		if goldForBig >= 1 then 
			--_guessAddBetRole(roleid, goldForBig * (1 - commission), 1)
			_guessAddBetRole(roleid, goldForBig, 1)
		end
		self:commonMessage(jrole, "恭喜您！押注成功", "guess")
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