
Actions.cyclesRules = {
	name = 'cyclesRules',
}

function Actions.cyclesRules:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	self:doDisplay(jrole)
end