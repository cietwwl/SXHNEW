Actions.challenge = {
	name = 'challenge',
}

function Actions.challenge:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	if jrole then
		self.layout.tags[7].value = [=[您当前拥有: ]=] .. _CyclesBetCyclesMoney(jrole)
		self.layout.tags[7].display = [=[您当前拥有: ]=] .. _CyclesBetCyclesMoney(jrole)
	end

	self:doDisplay(jrole)
end