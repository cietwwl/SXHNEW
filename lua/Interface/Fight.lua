function monsterAtk(monster, destList, atkresult)
	local id = _GameCharacterGetId(monster)
	--print ("type == " .. type, "id = " .. id, "攻击目标个数：" .. destList:size())
	
	local monsterMaxATK = 0
	local monsterMinATK = 0
	
	if _ObjectEquals(MonsterSet[id].vocation, Vocation.Warlock) then
		monsterMaxATK = MonsterSet[id].maxMAtk
		monsterMinATK = MonsterSet[id].minMAtk
	else
		monsterMaxATK = MonsterSet[id].maxPAtk
		monsterMinATK = MonsterSet[id].minPAtk
	end

	for dest in jlistIter(destList) do
		local desttype = _GameCharacterGetType(dest)
		local destid = _GameCharacterGetId(dest)
		
		local miss = 1
		local destVocation = _GameCharacterGetVocation(dest)
		local monsterLevel = MonsterSet[id].level
		local level = _GameCharacterGetLevel(dest)
		local rand = math.random()
		
		if monsterLevel == level then
			if _ObjectEquals(destVocation, Vocation.SHAQ) then 
				miss = rand > 0.85
			elseif _ObjectEquals(destVocation, Vocation.Assassin) then
				miss = rand > 0.75
			elseif _ObjectEquals(destVocation, Vocation.Warlock) then
				miss = rand > 0.8
			end
		elseif monsterLevel < level then
			if _ObjectEquals(destVocation, Vocation.SHAQ) then 
				miss = rand > 0.75
			elseif _ObjectEquals(destVocation, Vocation.Assassin) then
				miss = rand > 0.65
			elseif _ObjectEquals(destVocation, Vocation.Warlock) then
				miss = rand > 0.7
			end
		elseif monsterLevel > level then
			if _ObjectEquals(destVocation, Vocation.SHAQ) then 
				miss = rand > 0.9
			elseif _ObjectEquals(destVocation, Vocation.Assassin) then
				miss = rand > 0.85
			elseif _ObjectEquals(destVocation, Vocation.Warlock) then
				miss = rand > 0.88
			end
		end
		
		local destseat = _GameCharacterGetSeatId(dest)
		local destgroup = _GameCharacterGetAnimeGroup(dest)
		local destAmini = _GameCharacterGetAnime(dest)
		if miss then
			atkresult:add(luajava.new(FightOne, 0, destseat, destgroup, destAmini, 0, 0, 0))
		else
			--print("随机攻击力 : 最大值为: " .. sourceMaxPAtk .. "最小值为: " .. sourceMinPAtk)
			local atk
			if monsterMinATK > monsterMaxATK then
				atk = monsterMaxATK
			else
				atk = math.random(monsterMinATK, monsterMaxATK)
			end
			
			
			local destDef = 0

			local levelDiff = MonsterSet[id].level - _GameCharacterGetLevel(dest)
			
			if _ObjectEquals(MonsterSet[id].vocation, Vocation.Warlock) then
				destDef = _GameCharacterGetmDef(dest)
			else
				destDef = _GameCharacterGetpDef(dest)
			end
			
			if levelDiff < 3 then
				destDef = destDef--默认值
			elseif levelDiff < 6 then
				destDef = destDef * (1 - 0.15)
			elseif levelDiff < 9 then
				destDef = destDef * (1 - 0.40)
			elseif levelDiff < 12 then
				destDef = destDef * (1 - 0.80)
			elseif levelDiff < 15 then
				destDef = destDef * (1 - 0.95)
			elseif levelDiff < 20 then
				destDef = destDef * (1 - 0.99)
			else
				destDef = 0
			end
			
			local atkValue = 0
			
			if _GameCharacterGetHP(dest) > 0 then
				atkValue = atk > destDef and atk - destDef or 1
				--print("战斗结果: " .. "    攻击值:" .. atk .. "    " .. dest:getType() .. "防御值:" .. destDef .. "    结果:" .. atkValue)
				_GameCharacterDecreaseHP(dest, atkValue)--减血！
			end
			atkresult:add(luajava.new(FightOne, 1, destseat, destgroup, destAmini, 0, 1, atkValue))
		end
	end
end



function phyAtk(source, destList, atkresult)
	--print('物理攻击')
	local type = _GameCharacterGetType(source)--0为人1为怪
	local id = _GameCharacterGetId(source)
	--print ("type == " .. type, "id = " .. id, "攻击目标个数：" .. destList:size())
	
	local sourceMaxPAtk = (type == 0) and _GameCharacterGetMaxPAtk(source) or MonsterSet[id].maxPAtk + MonsterSet[id].maxMAtk
	local sourceMinPAtk = (type == 0) and _GameCharacterGetMinPAtk(source) or MonsterSet[id].minPAtk + MonsterSet[id].minMAtk
	
	local sourceVocation = type == 0 and _GameCharacterGetVocation(source) or nil
	
	--[[ 在这里添加buff影响   ]]
	
	for dest in jlistIter(destList) do
		local desttype = _GameCharacterGetType(dest)
		local destid = _GameCharacterGetId(dest)
		
		local miss = 1
		local bigHit = 1
		if type == 0 and desttype == 0 then--人打人
			miss = (math.random() < _GameCharacterGetEvade(dest) / _GameCharacterGetHit(source) / 100)
		elseif type == 1 and desttype == 1 then--怪打怪？
			
		elseif type == 0 and desttype == 1 then--人打怪
			local level = _GameCharacterGetLevel(source)
			local monsterLevel = MonsterSet[destid].level 
			local rand = math.random()
			

			if level == monsterLevel then
				if _ObjectEquals(sourceVocation, Vocation.SHAQ) then 
					miss = rand > 0.9
				elseif _ObjectEquals(sourceVocation, Vocation.Assassin) then
					miss = rand > 0.98
				elseif _ObjectEquals(sourceVocation, Vocation.Warlock) then
					miss = rand > 0.92
				end
			elseif level < monsterLevel then
				if _ObjectEquals(sourceVocation, Vocation.SHAQ) then 
					miss = rand > 0.8
				elseif _ObjectEquals(sourceVocation, Vocation.Assassin) then
					miss = rand > 0.93
				elseif _ObjectEquals(sourceVocation, Vocation.Warlock) then
					miss = rand > 0.85
				end
			elseif level > monsterLevel then
				if _ObjectEquals(sourceVocation, Vocation.SHAQ) then 
					miss = rand > 0.95
				elseif _ObjectEquals(sourceVocation, Vocation.Assassin) then
					miss = rand > 1
				elseif _ObjectEquals(sourceVocation, Vocation.Warlock) then
					miss = rand > 0.97
				end
			end
		else--怪打人
			local destVocation = _GameCharacterGetVocation(dest)
			local monsterLevel = MonsterSet[id].level
			local level = _GameCharacterGetLevel(dest)
			local rand = math.random()
			if monsterLevel == level then
				if _ObjectEquals(sourceVocation, Vocation.SHAQ) then 
					miss = rand > 0.85
				elseif _ObjectEquals(sourceVocation, Vocation.Assassin) then
					miss = rand > 0.75
				elseif _ObjectEquals(sourceVocation, Vocation.Warlock) then
					miss = rand > 0.8
				end
			elseif monsterLevel < level then
				if _ObjectEquals(sourceVocation, Vocation.SHAQ) then 
					miss = rand > 0.75
				elseif _ObjectEquals(sourceVocation, Vocation.Assassin) then
					miss = rand > 0.65
				elseif _ObjectEquals(sourceVocation, Vocation.Warlock) then
					miss = rand > 0.7
				end
			elseif monsterLevel > level then
				if _ObjectEquals(sourceVocation, Vocation.SHAQ) then 
					miss = rand > 0.9
				elseif _ObjectEquals(sourceVocation, Vocation.Assassin) then
					miss = rand > 0.85
				elseif _ObjectEquals(sourceVocation, Vocation.Warlock) then
					miss = rand > 0.88
				end
			end
		end
		
		local destseat = _GameCharacterGetSeatId(dest)
		local destgroup = _GameCharacterGetAnimeGroup(dest)
		local destAmini = _GameCharacterGetAnime(dest)
		local sourceCrit  = _GameCharacterGetCrit(source)
		--这里判断是否暴击   暴击率 = 0.0004 * 暴击属性值 ^ 0.834956       暴击加成 2
		local hitRand = math.random()
		bigHit = hitRand <  (0.0004 * sourceCrit ^ 0.834956)
		if miss then
			atkresult:add(luajava.new(FightOne, 0, destseat, destgroup, destAmini, 0, 0, 0))
		else
			--print("随机攻击力 : 最大值为: " .. sourceMaxPAtk .. "最小值为: " .. sourceMinPAtk)
			local atk
			if sourceMinPAtk > sourceMaxPAtk then
				atk = sourceMaxPAtk
			else
				atk = math.random(sourceMinPAtk, sourceMaxPAtk)
			end
			
			-- 攻击UP
			if _GameCharacterHasBuff(source, BuffConst.ATK) then
				atk = _GameCharacterFixValueAfterBuff(source, BuffConst.ATK, atk)
			end
			
			local destDef = 0
			if type == 1 and desttype == 0 then
				local levelDiff = MonsterSet[id].level - _GameCharacterGetLevel(dest)
				destDef = _GameCharacterGetpDef(dest)
				if levelDiff < 3 then
					destDef = destDef--默认值
				elseif levelDiff < 6 then
					destDef = destDef * (1 - 0.15)
				elseif levelDiff < 9 then
					destDef = destDef * (1 - 0.40)
				elseif levelDiff < 12 then
					destDef = destDef * (1 - 0.80)
				elseif levelDiff < 15 then
					destDef = destDef * (1 - 0.95)
				elseif levelDiff < 20 then
					destDef = destDef * (1 - 0.99)
				else
					destDef = 0
				end
			elseif type == 0 and desttype == 0 then
				destDef = _GameCharacterGetpDef(dest)
			else
				destDef = MonsterSet[destid].PDef
				--print("怪物ID " .. destid .. "怪物物防: " .. destDef)
			end
			
			-- 防御UP
			if _GameCharacterHasBuff(dest, BuffConst.DFC) then
				destDef = _GameCharacterFixValueAfterBuff(dest, BuffConst.DFC, destDef)
			end
			
			local atkValue = 0
			local hitType = 1
			if _GameCharacterGetHP(dest) > 0 then
				atkValue = atk > destDef and atk - destDef or 1
				--print("战斗结果: " .. "    攻击值:" .. atk .. "    " .. dest:getType() .. "防御值:" .. destDef .. "    结果:" .. atkValue)
				--jFight:setFightValue(atkValue)
				if bigHit then
				   atkValue = atkValue * 2
                                   hitType = 3
				end
				_GameCharacterDecreaseHP(dest, atkValue)--减血！
			--else
				--print("鞭尸中....")
				--jFight:setFightValue(0)
			end
			
			atkresult:add(luajava.new(FightOne, hitType, destseat, destgroup, destAmini, 0, 1, atkValue))
			--atkresult:add(jFight)
		end
	end
end

--技能攻击
function skillAtk(source, skillId, skillLv, destList)
	--print('技能攻击')
	local type = _GameCharacterGetType(source)--0为人1为怪
	local id = _GameCharacterGetId(source)
	
	--print ("type == " .. type, "id = " .. id, "攻击目标个数：" .. destList:size())
	
	--print("技能ID:" .. skillId .. "技能等级:" .. skillLv)
	local atkValue = SkillSet[skillId].value[skillLv]
	local effectNum = SkillSet[skillId].effectNum[skillLv]
	--print("魔法攻击原始值:" .. atkValue)
	local sourceMaxAty = 0
	local sourceMinAty = 0
	
	local rate = 1
	if effectNum == 1 then
		local skillLeveMax = 20 -- 这个值随着技能等级增加变化
		if skillLv > 10 then
			rate = rate + (skillLv - 10) / skillLeveMax
		end
	end
	
	if _ObjectEquals(_GameCharacterGetVocation(source), Vocation.SHAQ)
			or _ObjectEquals(_GameCharacterGetVocation(source), Vocation.Assassin) then
		sourceMaxAty = (type == 0) and _GameCharacterGetMaxPAtk(source) * rate + atkValue or MonsterSet[id].maxPAtk
		sourceMinAty = (type == 0) and _GameCharacterGetMinPAtk(source) + atkValue or MonsterSet[id].minPAtk
	elseif _ObjectEquals(_GameCharacterGetVocation(source), Vocation.Warlock)
			and SkillSet[skillId].isHarmful == 1 then
		sourceMaxAty = (type == 0) and _GameCharacterGetMaxMAtk(source) * 1.3 * rate + atkValue or MonsterSet[id].maxMAtk
		sourceMinAty = (type == 0) and _GameCharacterGetMinMAtk(source) * 0.9 + atkValue or MonsterSet[id].minMAtk
	elseif _ObjectEquals(_GameCharacterGetVocation(source), Vocation.Warlock)
			and SkillSet[skillId].isHarmful == 0 then
		sourceMaxAty = (type == 0) and _GameCharacterGetMaxMAtk(source) * 0.15 + atkValue or MonsterSet[id].maxMAtk
		sourceMinAty = (type == 0) and _GameCharacterGetMinMAtk(source) * 0.08 + atkValue or MonsterSet[id].minMAtk
	end
	
	--print("经计算 最小魔法伤害值: " .. sourceMinAty .. "最大魔法伤害值: " .. sourceMaxAty)
	--[[ 在这里添加buff影响   ]]
	
	local atkresult = luajava.new(ArrayList)
	
	for i = 1, destList:size() do
		local dest = destList:get(i - 1)
		local desttype = _GameCharacterGetType(dest)
		local destid = _GameCharacterGetId(dest)
		
		atkresult:add(1)
		atkresult:add(_GameCharacterGetSeatId(dest))
		
		--这里暂时使用物理攻击的动画
		if desttype == 0 then
			local group, animi = getPAtkAnimi(source)
			atkresult:add(group)
			atkresult:add(animi)
		else
			local monster = MonsterSet[destid]
			atkresult:add(monster.groupid)
			atkresult:add(monster.animi or 1)
			--atkresult:add(monster.defGroup)
			--atkresult:add(monster.defAnimi)
		end
	
		--print("随机攻击力 : 最大值为: " .. sourceMaxAty .. "最小值为: " .. sourceMinAty)
		local atk
		if sourceMinAty > sourceMaxAty then
			atk = sourceMaxAty
		else
			atk = math.random(sourceMinAty, sourceMaxAty)
		end
		
		if _GameCharacterHasBuff(source, BuffConst.ATK) then
			atk = _GameCharacterFixValueAfterBuff(source, BuffConst.ATK, atk)
		end
		
		atkresult:add(0)--操作血量
		atkresult:add(SkillSet[skillId].isHarmful)--减少/增加血量
		
		local destDef
		
		if _ObjectEquals(_GameCharacterGetVocation(source), Vocation.SHAQ)
				or _ObjectEquals(_GameCharacterGetVocation(source), Vocation.Assassin) then
			destDef = desttype == 0 and _GameCharacterGetpDef(dest) or MonsterSet[destid].PDef
		else
			destDef = desttype == 0 and _GameCharacterGetmDef(dest) or MonsterSet[destid].MDef
		end
			
		if type == 1 and desttype == 0 then
			local levelDiff = MonsterSet[id].level - _GameCharacterGetLevel(dest)
			if levelDiff < 3 then
				destDef = destDef
			elseif levelDiff < 6 then
				destDef = destDef * (1 - 0.15)
			elseif levelDiff < 9 then
				destDef = destDef * (1 - 0.40)
			elseif levelDiff < 12 then
				destDef = destDef * (1 - 0.80)
			elseif levelDiff < 15 then
				destDef = destDef * (1 - 0.95)
			elseif levelDiff < 20 then
				destDef = destDef * (1 - 0.99)
			else
				destDef = 0
			end
		end
		
		if _GameCharacterHasBuff(dest, BuffConst.DFC) then
				destDef = _GameCharacterFixValueAfterBuff(dest, BuffConst.DFC, destDef)
		end
		
		--print("魔法攻击战斗结果: " .. "    攻击值:" .. atk .. "    防御值:" .. destDef .. "    --结果:" .. (atk > destDef and atk - destDef or 1))

		if SkillSet[skillId].isHarmful == 1 then 
			if _GameCharacterGetHP(dest) > 0 then
				--print("伤害魔法, 结果: " .. (atk > destDef and atk - destDef or 1))
				atkresult:add(atk > destDef and atk - destDef or 1)
			else
				atkresult:add(0)
			end
		else
			--print("非伤害魔法, 结果: " .. atk)
			--注目前假设技能只会影响血量
			if _GameCharacterGetHP(dest) > 0 then
				if _GameCharacterGetHP(dest) + atk > _GameCharacterGetMaxHP(dest) then
					atkresult:add(_GameCharacterGetMaxHP(dest) - _GameCharacterGetHP(dest))
					--print("实际加血量为: (超量)" .. (RoleSet[destid].maxHp - dest:getHP()))
				else
					atkresult:add(atk)
					--print("实际加血量为: (正常)" .. atk)
				end
			else
				atkresult:add(0)
			end
		end
	end

	return atkresult
end

function fightItemUse(source, itemId, destList)
	local type = _GameCharacterGetType(source)--0为人1为怪
	local id = _GameCharacterGetId(source)
	
	--print ("type == " .. type, "id = " .. id, "物品作用目标个数：" .. destList:size())

	local atkresult = luajava.new(ArrayList)
	
	if not Bag:checkInPack(source, itemId) then
		replyMessage(source, 2, MsgID.MsgID_Item_Get_Info_Resp, "查看失败！")
		return
	end
	
	local jItem, template, storage = Bag:getItemInPack(source, itemId)
	
	if template.type == ItemConst.Equip then
		print "战斗中尝试使用装备, 非法!"
		return atkresult
	end
	
	--这里未考虑物品会产生负面作用的情况
	--比如玩家丢物品给敌对方 产生 中毒 或者伤害
	--这里目前只有针对己方的逻辑
	local value = template.value
	
	for i = 1, destList:size() do
		local dest = destList:get(i - 1)
		local desttype = _GameCharacterGetType(dest)
		local destid = _GameCharacterGetId(dest)
		
		atkresult:add(1) --攻击类型 (0.Miss,1.普通，2.重击3.致命)
		atkresult:add(_GameCharacterGetSeatId(dest))
		--使用物品动画 暂时没有
		atkresult:add(0)
		atkresult:add(0)
		
		--目前只支持药品 其他未知
		if template.subtype == PropConst.Drug then
			if template.specificType == DrugConst.Red then
				atkresult:add(0) --影响血
				
				atkresult:add(0) --增加
			
				if _GameCharacterGetHP(dest) > 0 then 
					if _GameCharacterGetHP(dest) + template.value > _GameCharacterGetMaxHP(dest) then
						atkresult:add(_GameCharacterGetMaxHP(dest) - _GameCharacterGetHP(dest))
						--print("实际加血量为: (超量)" .. (RoleSet[destid].maxHp - dest:getHP()))
					else
						atkresult:add(template.value)
						--print("实际加血量为: (正常)" .. template.value)
					end
				else
					atkresult:add(0)
				end
				
			elseif template.specificType == DrugConst.Blue then
				atkresult:add(1) --影响魔法
				
				atkresult:add(0) --增加
			
				if _GameCharacterGetHP(dest) > 0 then 
					if _GameCharacterGetMP(dest) + template.value > _GameCharacterGetMaxMP(dest) then
						atkresult:add(_GameCharacterGetMaxMP(dest) - _GameCharacterGetMP(dest))
						--print("实际加魔量为: (超量)" .. (RoleSet[destid].maxMp - dest:getMP()))
					else
						atkresult:add(template.value)
						--print("实际加魔量为: (正常)" .. template.value)
					end
				else
					atkresult:add(0)
				end
			end

			Bag:delItem(source, itemId, 1)
		else
			atkresult:clear()
		end
	end
	
	return atkresult
end

function phyFightEffect (source)
	local type = _GameCharacterGetType(source)--0为人1为怪
	local id = _GameCharacterGetId(source)
	--print("type = " .. type .. " id = " .. id)
	
	local fightEffect = luajava.new(ArrayList, 6)
	if type == 0 then
		local group, animi = getPAtkAnimi(source)
		fightEffect:add(group)
		fightEffect:add(6)
		fightEffect:add(0)
		fightEffect:add(0)
		fightEffect:add(11)
		fightEffect:add(0)
	else
		local monster = MonsterSet[id]
		fightEffect:add(monster.groupid)
		fightEffect:add(2)
		fightEffect:add(0)
		fightEffect:add(0)
		fightEffect:add(11)
		fightEffect:add(0)
	end
	
	return fightEffect
end

function skillFightEffect (source, skillId)
	local type = _GameCharacterGetType(source)--0为人1为怪
	local id = _GameCharacterGetId(source)
	--print("type = " .. type .. " id = " .. id)
	
	local fightEffect = luajava.new(ArrayList, 6)
	if type == 0 then
		--返回的结果暂时写死
		local group, animi = getPAtkAnimi(source)
		fightEffect:add(group)
		fightEffect:add(9)
		fightEffect:add(11)
		fightEffect:add(3)
		fightEffect:add(11)
		fightEffect:add(0)
	else
		local monster = MonsterSet[id]
		fightEffect:add(monster.groupid)
		fightEffect:add(2)
		fightEffect:add(11)
		fightEffect:add(3)
		fightEffect:add(11)
		fightEffect:add(0)
	end
	
	return fightEffect
end

function itemUseEffect (source, skillId)
	local type = _GameCharacterGetType(source)--0为人1为怪
	local id = _GameCharacterGetId(source)
	--print("type = " .. type .. " id = " .. id)
	
	local fightEffect = luajava.new(ArrayList, 6)
	if type == 0 then
		--返回的结果暂时写死
		local group, animi = getPAtkAnimi(source)
		fightEffect:add(group)
		fightEffect:add(11)
		fightEffect:add(11)
		fightEffect:add(3)
		fightEffect:add(11)
		fightEffect:add(0)
	else
		local monster = MonsterSet[id]
		fightEffect:add(monster.groupid)
		fightEffect:add(2)
		fightEffect:add(11)
		fightEffect:add(3)
		fightEffect:add(11)
		fightEffect:add(0)
	end
	
	return fightEffect
end

EquipTypeDropRate = {
	--[EquipConst.Weapon] = 0.015,--武器
	[EquipConst.Ring] = 0.09,--戒指
	[EquipConst.Necklace] = 0.085,--项链
	[EquipConst.Trinket] = 0.085,--刺环
	[EquipConst.Helmet] = 0.115,--头盔
	[EquipConst.Cloak] = 0.075,--披风
	[EquipConst.Cuirass] = 0.015,--胸甲
	[EquipConst.Wrists] = 0.1,--玉佩
	[EquipConst.Belt] = 0.0625,--腰带
	[EquipConst.Gloves] = 0.2,--手套
	[EquipConst.Cuish] = 0.0975,--护腿
	[EquipConst.Shoes] = 0.075,--鞋
}

function getRandomEquipType(randomNum)
	local total = 0
	print('randomNum ' .. randomNum)
	for k, v in pairs(EquipTypeDropRate) do
		total = total + v * 10000
		--print('total is ' .. total)
		if total >= randomNum then
			return _EnumOrdinal(k)
		end
	end
	
	--如果EquipTypeDropRate中几率之和小于1有可能上面不会命中这里给一个默认值
	print('error in getRandomEquipType')
	return _EnumOrdinal(EquipConst.Gloves)
end


function convertWinTeamKillNumToRate(maxKillNum)
	if maxKillNum < 10 then
		return 1
	elseif maxKillNum >= 10	and maxKillNum < 20 then
		return 3
	elseif maxKillNum >= 20	and maxKillNum < 50 then
		return 5
	elseif maxKillNum >= 50	and maxKillNum < 100 then
		return 8
	else
		return 15
	end
end

function convertLoseTeamKillNumToRate(maxKillNum)
	if maxKillNum < 10 then
		return 5
	elseif maxKillNum >= 10	and maxKillNum < 20 then
		return 10
	elseif maxKillNum >= 20	and maxKillNum < 50 then
		return 15
	elseif maxKillNum >= 50	and maxKillNum < 100 then
		return 20
	else
		return 35
	end
end

function canPvP(mapId)
	return not MapSet[mapId].noSneakAtk
end