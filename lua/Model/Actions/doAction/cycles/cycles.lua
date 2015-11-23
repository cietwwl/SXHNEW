Actions.cycles = {
	name = 'cycles',
}

function Actions.cycles:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	if jrole then
		self.layout.tags[5].value = [=[您当前拥有: ]=] .. _CyclesBetCyclesMoney(jrole)
		self.layout.tags[5].display = [=[您当前拥有: ]=] .. _CyclesBetCyclesMoney(jrole)
	end

	self:doDisplay(jrole)
end