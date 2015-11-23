
Actions.guessRules = {
	name = 'guessRules',
}

function Actions.guessRules:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	self:doDisplay(jrole)
end