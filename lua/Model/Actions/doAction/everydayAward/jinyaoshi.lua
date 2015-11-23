Actions.jinyaoshi = {
	name = 'jinyaoshi',
}

function Actions.jinyaoshi:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	self:doDisplay(jrole)
end