
Actions.bigOrSmallRules = {
	name = 'bigOrSmallRules',
}

function Actions.bigOrSmallRules:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	self:doDisplay(jrole)
end