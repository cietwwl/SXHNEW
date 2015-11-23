Actions.yuanbaoBoxRules = {
	name = 'yuanbaoBoxRules',
}

function Actions.yuanbaoBoxRules:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	self:doDisplay(jrole)
end