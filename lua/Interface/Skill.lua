
function getLiteTitle(SkillId, Lv)
	local skill = SkillSet[SkillId]
	
	if skill == nil then 
		return "error: getLiteTitle"
	else
		return skill.name .. " 等级  " .. Lv
	end
end

function getFullTitle(SkillId, Lv)
	local skill = SkillSet[SkillId]
	
	if skill == nil then 
		return "error: getFullTitle"
	else
		return skill.name .. " 等级  " .. Lv .. " 伤害  " .. skill.value[Lv] .. " 消耗内力 " .. skill.mana[Lv]
	end
end

function getSkillIcon(SkillId)
	local skill = SkillSet[SkillId]
	return skill.icon or 0
end

function isPassiveSkill(SkillId)
	local skill = SkillSet[SkillId]
	
	if not skill then
		log.error("技能" .. SkillId .. "为空")
	end
	
	return skill.passive or 0 --查Bug
end

function toEffectNum(SkillId, level)
	local skill = SkillSet[SkillId]
	
	if skill == nil then print("toEffectNum: skill 为空") end
	return skill.effectNum[level] or 0
end

function effectObj(SkillId)
	local skill = SkillSet[SkillId]
	
	if skill == nil then print("effectObj: skill 为空") end
	
	return skill.effectObj
end

function getManaCost(SkillId, lv)
	if lv > 20 or lv <= 0 then lv = 1 end
	
	local skill = SkillSet[SkillId]
	
	if skill == nil then print("getManaCost: skill 为空") end
	
	return skill.mana[lv]
end

function getSkillStr(JRole)
	local skillTbl = {}
	for skillid, level in jmapIter(_RoleGetSkill(JRole)) do	
		skillTbl[#skillTbl + 1] = skillid .. "," .. level
	end
	
	skillTbl[#skillTbl + 1] = " "
	
	return table.concat(skillTbl, ":")
end
