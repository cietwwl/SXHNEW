
function loadLua()
	local load = { }
	table.insert(load, "Environment")
	table.insert(load, "Model")
	table.insert(load, "Temp")
	table.insert(load, "Interface")
	return table.concat(load, ";")
end
