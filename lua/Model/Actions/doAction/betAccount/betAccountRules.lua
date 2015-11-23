
Actions.betAccountRules = {
	name = 'betAccountRules',
}

function Actions.betAccountRules:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	self:doDisplay(jrole)
end