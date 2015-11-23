
Actions.challengeRecord = {
	name = 'challengeRecord',
}

function Actions.challengeRecord:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	if jrole then
		 local challengeRecords = _GetChallengeRecord(jrole)
		 local x = 11
		 local y = 64
		 local width =  220
		 local height = 36
		 for cyclesMessage in jlistIter(challengeRecords) do
			local inning = _GetInning(cyclesMessage)
			local gold = _GetGold(cyclesMessage)
			local challengeRoleid = _GetChallengeRoleid(cyclesMessage)
			local acceptName = _GetName(cyclesMessage)
			local win = _GetWin(cyclesMessage)
			local str = "第" .. inning .. "局" .. acceptName .. "接收您的挑战，" .. win .. "/您获得了" .. gold/100 .. "银"  
			self:addListItem("challengeRecord", inning, "cycles", str, x, y, width, height)
			y = y + height;
		end
	end
	self:doDisplay(jrole)
	self:removeAllListItems()
end