
Actions.double = {
	name = 'double',
}

function Actions.double:doAction(roleid, args)
	
	local jrole = _GetOnline(roleid)
	
	--押注的金额
	local goldForGuess = tonumber(args.goldForGuess) * 100
	
	if goldForGuess < 0 then
		self:commonMessage(jrole, "押注金额不能为负", "guess")
		return 
	end
	
	--判断角色是否在线
	if jrole then
		--角色现有金钱数目
		local roleGold = _RoleGetGold(jrole)
		
		if goldForGuess < 100 then
			--返回押注金额不能小于1银币
			self:commonMessage(jrole, "押注最小金额为1银币", "guess")
			return 
		end
		
		if roleGold < goldForGuess*1.05 then
			--返回金币不足
			self:commonMessage(jrole, "您背包的内的金额少于您所押金额，请再次输入金额。", "guess")
			return
		end
		
		--角色剩余金币(扣除金币)
		_RoleSetGold(jrole, roleGold - goldForGuess*1.05 )
		
		log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$35#$本次押大小获得物品#$获得物品所有个数#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$" .. goldForGuess*1.05 .. "")
		
		
		--调用押大的处理机制
		_GuessAddRole(roleid, goldForGuess, 4)
		
		self:commonMessage(jrole, "押注成功，请静候此次结果。", "guess")
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