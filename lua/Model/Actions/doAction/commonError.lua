Actions.commonError = {
	name = 'commonError',
}

function Actions.commonError:doAction(roleid, args)
	--print('called commonError:doAction ' .. roleid .. ' args is ' .. args)
	local jrole = _GetOnline(roleid)
	
	if jrole then
		local redirectAction = actions[args.errorAction]
		if redirectAction then
			redirectAction:doDisplay(jrole)
		else 
			log:error('要转到的页面不存在!')
		end
	else
		log:error('访问action的玩家不在线!')
	end
end