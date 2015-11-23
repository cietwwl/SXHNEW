Actions.acceptEntry = {
	name = 'acceptEntry',
}
--可以应战的列表
function Actions.acceptEntry:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	if jrole then
		self.layout.tags[1].value = [=[您当前拥有: ]=] .. _CyclesBetCyclesMoney(jrole)
		self.layout.tags[1].display = [=[您当前拥有: ]=] .. _CyclesBetCyclesMoney(jrole)
		local cyclesMessages = _GetCyclesMessages()
		local x = 10
		local y = 61
		local width =  220
		local height = 18 
		local i = 1
		if cyclesMessages then
			for cyclesMessage in jlistIter(cyclesMessages) do
				local inning = _GetInning(cyclesMessage)
				local gold = _GetGold(cyclesMessage)
				local challengeRoleid = _GetChallengeRoleid(cyclesMessage)
				local challengeName = _GetName(cyclesMessage)
				local str = "第" .. inning .. "局" .. challengeName .. "发起" .. gold/100 .. "银挑战"
				self:addListItem( "var" .. i , inning, "accept", str, x, y, width, height)
				y = y + 18
				i = i + 1
			end
		end
	end

	self:doDisplay(jrole)
	self:removeAllListItems()
end