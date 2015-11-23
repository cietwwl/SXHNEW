Actions.accept = {
	name = 'accept',
}
--应战出手的页面
function Actions.accept:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	if jrole then
		local _, inning = pairs(args)(args)
		if not inning then
			self:error(jrole, "您没有选择要挑战的人物", "cycles")
			return
		end
		if not _QueryChallenge(inning) then
			self:error(jrole, "很可惜您晚了一步，这场挑战已结束，快抢下一个机会吧。", "acceptEntry")
			return
		end 
		--角色现有金钱数目
		local roleGold = _RoleGetGold(jrole)
		local cyclesMessage = _GetChallengeCyclesMessage(inning)
		local goldForCycles = _GetGold(cyclesMessage)
		if goldForCycles > roleGold then
			--返回至少要押注其中一个并且不得小于1银币
			self:error(jrole, "太可惜了，您的现金不足。", "cycles")
			return
		end
		self.layout.tags[4].value = [=[总局数：]=]
		self.layout.tags[4].display = [=[总局数：]=]
		self.layout.tags[5].value = inning
		self.layout.tags[5].display = inning
		self.layout.tags[6].value = [=[您当前拥有: ]=] .. _CyclesBetCyclesMoney(jrole)
		self.layout.tags[6].display = [=[您当前拥有: ]=] .. _CyclesBetCyclesMoney(jrole)
	end
	self:doDisplay(jrole)
end