Actions.acceptRecord = {
	name = 'acceptRecord',
}

function Actions.acceptRecord:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	if jrole then
		local challengeRecords = _GetAcceptRecord(jrole)
		local x = 11
		local y = 64
		local width =  220
		local height = 36
		for cyclesMessage in jlistIter(challengeRecords) do
			local inning = _GetInning(cyclesMessage)
			local gold = _GetGold(cyclesMessage)
			local challengeRoleid = _GetChallengeRoleid(cyclesMessage)
			local challengeName = _GetName(cyclesMessage)
			local win = _GetWin(cyclesMessage)
			local str = "第" .. inning .. "局，您应战了" .. challengeName .. "，" .. win .. "/您获得了" .. gold/100 .. "银"
			self:addListItem("acceptRecord", inning, "cycles", str, x, y, width, height)
			y = y + height;
		end
	end
	self:doDisplay(jrole)
	self:removeAllListItems()
end