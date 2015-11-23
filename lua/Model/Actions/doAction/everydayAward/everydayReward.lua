
Actions.everydayAward = {
	name = 'everydayAward',
}

function Actions.everydayAward:doAction(roleid, args)
	local jrole = _GetOnline(roleid)

	self:doDisplay(jrole)
end