
Actions.bigOrSmall = {
	name = 'bigOrSmall',
}

function Actions.bigOrSmall:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	if jrole then
		self.layout.tags[9].display = [=[可押金额: ]=] .. _BigOrSmallBetMoney(jrole)
		self.layout.tags[10].display = [=[上局结果: ]=] .. _BigOrSmallgetLastRoundResult()
		self.layout.tags[11].display = [=[开奖时间剩余: ]=] .. _BigOrSmallGetRemainTime()
	end

	self:doDisplay(jrole)
end