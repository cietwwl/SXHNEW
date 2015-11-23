Actions.goldBoxRules = {
	name = 'goldBoxRules',
}

function Actions.goldBoxRules:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	self:doDisplay(jrole)
end