
Actions.guess = {
	name = 'guess',
}

function Actions.guess:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	if jrole then
		self.layout.tags[13].display = [=[可押金额: ]=] .. _GuessBetMoney(jrole)
		self.layout.tags[14].display = [=[上局结果: ]=] .. _GuessGetLastRoundResult()
		self.layout.tags[15].display = [=[开奖时间: ]=] .. _GuessGetRemainTime()
	end

	self:doDisplay(jrole)
end