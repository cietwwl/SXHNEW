
function getGangDes(level)
	return  GangSet[level] and GangSet[level]:getAdditiveDes() or ""
end

function gangUpperLimit(level)
	return GangSet[level] and GangSet[level].upperLimit or 0
end

function chkCreatGangConditons(jrole)
	local taskid = table.getglobal("Temp.Task").CreatGang
	return TaskSet[taskid]:checkConditions(jrole)
end

function getCreatGangTaskid()
	return table.getglobal("Temp.Task").CreatGang or 0
end

function gangBenefit(level, bnf, raw_value)
	if GangSet[level].benefit and GangSet[level].benefit[bnf] then
		return (1 + GangSet[level].benefit[bnf]) * raw_value
	else
		return raw_value
	end
end

function creatGangChargeback(jrole)
	local task = TaskSet[table.getglobal("Temp.Task").CreatGang]
	return task:chargeback(jrole)
end