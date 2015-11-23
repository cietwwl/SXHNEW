
function getValueDescribe(value)
	local Au = value / 10000 - value / 10000 % 1
	local Ag = value % 10000 / 100 - value % 10000 / 100 % 1
	local Cu = value % 100 - value % 100 % 1
	
	local des = { }
	if Au > 0 then
		des[#des + 1] = Au .. "金"
	end
	
	if Ag > 0 then
		des[#des + 1] = Ag .. "银"
	end
	
	if Cu > 0 then
		des[#des + 1] = Cu .. "铜"
	end
	
	if #des == 0 then
		des[#des + 1] = "0铜"
	end
	
	return table.concat(des)
end
