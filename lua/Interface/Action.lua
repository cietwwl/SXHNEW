

function callAction(roleid, actionName, args)

	if Actions[actionName] then
		args = args or "nil"
		local callStr = 'Actions.' .. actionName .. ':doAction' ..'(' .. roleid .. ', ' .. args .. ')'
		--print('callStr is ' .. callStr)
		loadstring(callStr)()
	end
	
end