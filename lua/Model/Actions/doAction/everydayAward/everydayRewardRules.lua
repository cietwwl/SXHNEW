
Actions.everydayAwardRules = {
	name = 'everydayRewardRules',
}

function Actions.everydayAwardRules:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	self:doDisplay(jrole)
end