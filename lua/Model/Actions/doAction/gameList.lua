
Actions.gameList = {
	name = 'gameList',
}

function Actions.gameList:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	args = args or 'nil'
	--print('called gameList.doAction roleid is ' .. roleid .. ' args is ' .. args)
	self:doDisplay(jrole)
end
