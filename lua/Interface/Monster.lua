
function creatJMonster(monster)
	return luajava.new(JMonster, monster.id, monster.name, monster.color, monster.HP, monster.MP, monster.groupid, 
						monster.animi or 3, monster.atkSpd or 1, monster.vocation or Vocation.SHAQ)
end

function initMonster(monsterid, avgLevel, playerNum, monsterList)

	
	local monster = MonsterSet[monsterid]
	if not monster then
		return
	end
	
	local num
	if monster.type == MonsterType.Common then
		if avgLevel >= 1 and avgLevel <= 10 then--1~10级
			if playerNum == 3 then--三人则2怪
				num = 2
			else--1或2人则与怪数目相同
				num = playerNum
			end
		elseif avgLevel >= 11 and avgLevel <= 25 then--11～25级
			num = playerNum + math.random(0,1)
		elseif playerNum == 1 then--26～40级单人2或三怪
			num = 2 + math.random(0,1)
		else--26～40级多人则3怪
			num = 3
		end
	elseif monster.type == MonsterType.NPCFight then
		num = 1
	else
		print("怪物类型未知" .. monster.type)
	end
	
	for i = 1, num do
		monsterList:add(creatJMonster(monster))
	end
end

--BOSS卡战斗
function initBossCardMonster(monsterid, monsterNum, monsterList)
	local monster = MonsterSet[monsterid]
	if not monster then
		return
	end
	
	local num
	if monster.type == MonsterType.Common then
		if monsterNum > 3 then
			num = 3
		else
			num = monsterNum
		end
	elseif monster.type == MonsterType.NPCFight then
		num = 1
	else
		print("怪物类型未知" .. monster.type)
	end
	
	for i = 1, num do
		monsterList:add(creatJMonster(monster))
	end
end

function getMonsterLoot(monsterId, monsterNum, lootList)

	local monster = MonsterSet[monsterId]
	local drop = monster.drop
	for i = 1, random.getn(drop.maxn) do
		local status, rand_value = pcall(drop.rand, drop)
		if status then
			if rand_value then
				local tid = rand_value[math.random(1, #rand_value)]
				local template = ItemSet[tid]
				if template then
					lootList:add(template:creatJavaItem()[1])
				else
					debug.traceback("怪物", monster.name, "掉落", tid, "不存在")
				end
			end
		else
			debug.getlocal()
		end
	end
end

DoubleExp = {

	['shenxunhesrv000'] = {
		start = os.time{ year = 2012, month = 7, day = 6, hour = 0, min = 0, sec = 0, },
		terminal = os.time{ year = 2012, month = 7, day = 16, hour = 0, min = 0, sec = 0, },
		{ 0, 23, mul = 10,},
	},

	['shenxunhesrv001'] = {
		start = os.time{ year = 2012,  month = 7, day = 6, hour = 0, min = 0, sec = 0, },
		terminal = os.time{ year = 2012,month = 7, day = 16, hour = 0, min = 0, sec = 0, },
		--{ 13, 14, mul = 3,},
	    --{ 11, 12, mul = 2,},
		--{ 18, 19, mul = 2,},
		{ 10, 12, mul = 2,},
	},

	['shenxunhesrv002'] = {
		start = os.time{ year = 2012,  month = 7, day = 6, hour = 0, min = 0, sec = 0, },
		terminal = os.time{ year = 2012, month = 7, day = 16, hour = 0, min = 0, sec = 0, },
		--{ 11, 12, mul = 3,},
		--{ 11, 12, mul = 2,},
		{ 12, 14, mul = 2,},
	},

	['shenxunhesrv003'] = {
		start = os.time{ year = 2012, month = 7, day = 6, hour = 0, min = 0, sec = 0, },
		terminal = os.time{ year = 2012,month = 7, day = 16,hour = 0, min = 0, sec = 0, },
		--{ 10, 11, mul = 3,},
		--{ 11, 12, mul = 2,},
		{ 14, 16, mul = 2,},
	},
	
	['shenxunhesrv004'] = {
		start = os.time{ year = 2012, month = 7, day = 6, hour = 0, min = 0, sec = 0, },
		terminal = os.time{ year = 2012,month = 7, day = 16, hour = 0, min = 0, sec = 0, },
		--{ 12, 13, mul = 3,},
		--{ 11, 12, mul = 2,},
		{ 16, 18, mul = 2,},
	},
	
	['shenxunhesrv005'] = {
		start = os.time{ year = 2012, month = 7, day = 6, hour = 0, min = 0, sec = 0, },
		terminal = os.time{ year = 2012,month = 7, day = 16, hour = 0, min = 0, sec = 0, },
		--{ 12, 13, mul = 3,},
		--{ 11, 12, mul = 2,},
		{ 18, 20, mul = 2,},
	},
	
	['shenxunhesrv006'] = {
        start = os.time{ year = 2012,  month = 7, day = 6, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2012,month = 7, day = 16, hour = 0, min = 0, sec = 0, },
        --{ 12, 13, mul = 3,},
        --{ 12, 14, mul = 2,},
        { 20, 22, mul = 2,},
    },
    
    ['shenxunhesrv007'] = {
        start = os.time{ year = 2012,  month = 7, day = 20, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2012,month = 7, day = 30, hour = 0, min = 0, sec = 0, },
        --{ 12, 13, mul = 3,},
        --{ 12, 14, mul = 2,},
        { 10, 12, mul = 2,},
        { 22, 24, mul = 2,},
    },
    
    ['shenxunhesrv008'] = {
        start = os.time{ year = 2012,  month = 7, day = 20, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2012,month = 7, day = 30, hour = 0, min = 0, sec = 0, },
        --{ 12, 13, mul = 3,},
        --{ 12, 14, mul = 2,},
        { 10, 12, mul = 2,},
        { 22, 24, mul = 2,},
    },
    
    ['shenxunhesrv009'] = {
        start = os.time{ year = 2012,  month = 7, day = 20, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2012,month = 7, day = 30, hour = 0, min = 0, sec = 0, },
        --{ 12, 13, mul = 3,},
        --{ 12, 14, mul = 2,},
        { 10, 12, mul = 2,},
        { 22, 24, mul = 2,},
    },
    
    ['shenxunhesrv010'] = {
        start = os.time{ year = 2012,  month = 7, day = 20, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2012,month = 7, day = 30, hour = 0, min = 0, sec = 0, },
        --{ 12, 13, mul = 3,},
        --{ 12, 14, mul = 2,},
        { 10, 12, mul = 2,},
        { 22, 24, mul = 2,},
    },
    
    ['shenxunhesrv011'] = {
        start = os.time{ year = 2013,  month = 7, day = 26, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2013,month = 5, day = 25, hour = 0, min = 0, sec = 0, },
        { 20, 24, mul = 2,},
    },
    
    ['shenxunhesrv012'] = {
        start = os.time{ year = 2013,  month = 10, day = 25, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2013,month = 11, day = 25, hour = 0, min = 0, sec = 0, },
        { 20, 24, mul = 2,},
    },
	['shenxunhesrv013'] = {
        start = os.time{ year = 2013,  month = 10, day = 25, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month = 1, day = 1, hour = 0, min = 0, sec = 0, },
        { 20, 24, mul = 2,},
    },
	['shenxunhesrv014'] = {
        start = os.time{ year = 2014,  month = 1, day = 9, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month = 2, day = 9, hour = 0, min = 0, sec = 0, },
        { 20, 24, mul = 2,},
    },
    ['shenxunhesrv015'] = {
        start = os.time{ year = 2014,  month = 4, day = 29, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month = 5, day = 29, hour = 0, min = 0, sec = 0, },
        { 20, 24, mul = 2,},
    },
	['shenxunhesrv016'] = {
        start = os.time{ year = 2014,  month = 2, day = 27, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month = 3, day = 27, hour = 0, min = 0, sec = 0, },
        { 20, 24, mul = 2,},
    },
	['shenxunhesrv017'] = {
        start = os.time{ year = 2014,  month = 6, day = 9, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month = 7, day = 9, hour = 0, min = 0, sec = 0, },
        { 20, 24, mul = 2,},
    },
	['shenxunhesrv018'] = {
        start = os.time{ year = 2014,  month =7, day =7, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month =8, day = 8, hour = 0, min = 0, sec = 0, },
        { 20, 24, mul = 2,},
    },
	['shenxunhesrv019'] = {
        start = os.time{ year = 2014,  month = 8, day = 12, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month = 9, day = 12, hour = 0, min = 0, sec = 0, },
        { 20, 24, mul = 2,},
    },
	['shenxunhesrv020'] = {
        start = os.time{ year = 2014,  month = 9, day = 15, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month = 10, day = 15, hour = 0, min = 0, sec = 0, },
        { 20, 24, mul = 2,},
    },
	['shenxunhesrv021'] = {
        start = os.time{ year = 2014,  month = 11, day = 4, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month = 12, day = 4, hour = 0, min = 0, sec = 0, },
        { 20, 24, mul = 2,},
    },
	['shenxunhesrv022'] = {
        start = os.time{ year = 2014,  month = 12, day = 4, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2015,month = 1, day = 4, hour = 0, min = 0, sec = 0, },
        { 20, 24, mul = 2,},
    }
	,
    ['kuyusrv000'] = {
		start = os.time{ year = 2012, month = 7, day = 6, hour = 0, min = 0, sec = 0, },
		terminal = os.time{ year = 2012, month = 7, day = 16, hour = 0, min = 0, sec = 0, },
		{ 0, 23, mul = 10,},
	},

	['kuyusrv001'] = {
		start = os.time{ year = 2012,  month = 8, day = 24, hour = 0, min = 0, sec = 0, },
		terminal = os.time{ year = 2012,month = 9, day = 20, hour = 0, min = 0, sec = 0, },
		{ 8, 10, mul = 2,},
		{ 20, 22, mul = 2,},
	},

	['kuyusrv002'] = {
		start = os.time{ year = 2012,  month = 8, day = 22, hour = 0, min = 0, sec = 0, },
		terminal = os.time{ year = 2012, month = 9, day = 20, hour = 0, min = 0, sec = 0, },
		{ 10, 12, mul = 2,},
		{ 22, 24, mul = 2,},
	},

	['kuyusrv003'] = {
		start = os.time{ year = 2012, month = 7, day = 6, hour = 0, min = 0, sec = 0, },
		terminal = os.time{ year = 2012,month = 7, day = 16,hour = 0, min = 0, sec = 0, },
		{ 14, 16, mul = 2,},
	},
	
	['kuyusrv004'] = {
		start = os.time{ year = 2012, month = 7, day = 6, hour = 0, min = 0, sec = 0, },
		terminal = os.time{ year = 2012,month = 7, day = 16, hour = 0, min = 0, sec = 0, },
		--{ 12, 13, mul = 3,},
		--{ 11, 12, mul = 2,},
		{ 16, 18, mul = 2,},
	},
	
	['kuyusrv005'] = {
		start = os.time{ year = 2012, month = 11, day = 2, hour = 0, min = 0, sec = 0, },
		terminal = os.time{ year = 2012,month = 11, day = 30, hour = 0, min = 0, sec = 0, },
		--{ 12, 13, mul = 3,},
		--{ 11, 12, mul = 2,},
		{ 1, 23, mul = 2,},
	},
	
	['kuyusrv006'] = {
        start = os.time{ year = 2012,  month = 11, day = 29, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2012,month = 12, day = 10, hour = 0, min = 0, sec = 0, },
        --{ 12, 13, mul = 3,},
        --{ 12, 14, mul = 2,},
       { 1, 23, mul = 2,},
    },
    
    ['kuyusrv007'] = {
        start = os.time{ year = 2012,  month = 7, day = 20, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2012,month = 7, day = 30, hour = 0, min = 0, sec = 0, },
        --{ 12, 13, mul = 3,},
        --{ 12, 14, mul = 2,},
        { 10, 12, mul = 2,},
        { 22, 24, mul = 2,},
    },
    
    ['kuyusrv008'] = {
        start = os.time{ year = 2012,  month = 7, day = 20, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2012,month = 7, day = 30, hour = 0, min = 0, sec = 0, },
        --{ 12, 13, mul = 3,},
        --{ 12, 14, mul = 2,},
        { 10, 12, mul = 2,},
        { 22, 24, mul = 2,},
    },
    
    ['kuyusrv009'] = {
        start = os.time{ year = 2012,  month = 7, day = 20, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2012,month = 7, day = 30, hour = 0, min = 0, sec = 0, },
        --{ 12, 13, mul = 3,},
        --{ 12, 14, mul = 2,},
        { 10, 12, mul = 2,},
        { 22, 24, mul = 2,},
    },
    
    ['kuyusrv010'] = {
        start = os.time{ year = 2013,  month = 3, day = 28, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2013,month = 4, day = 23, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    },
    
    ['kuyusrv011'] = {
        start = os.time{ year = 2013,  month = 4, day = 12, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2013,month = 5, day = 07, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    }
    ,
    
    ['kuyusrv012'] = {
        start = os.time{ year = 2013,  month = 4, day = 26, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2013,month = 5, day = 28, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    }  ,
    
    ['kuyusrv013'] = {
        start = os.time{ year = 2013,  month = 4, day = 12, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2013,month = 5, day = 07, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    }  ,
    
    ['kuyusrv014'] = {
        start = os.time{ year = 2013,  month = 6, day = 28, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2013,month = 7, day = 28, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    }  ,
      ['kuyusrv015'] = {
        start = os.time{ year = 2013,  month =7, day = 26, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2013,month = 8, day = 25, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    }  ,
    
    ['kuyusrv016'] = {
        start = os.time{ year = 2013,  month =8, day = 29, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2013,month = 9, day = 29, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    }  , 
    ['kuyusrv017'] = {
        start = os.time{ year = 2013,  month =9, day = 26, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2013,month = 11, day = 26, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    }  ,
     ['kuyusrv018'] = {
        start = os.time{ year = 2013,  month =9, day = 26, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month = 1, day = 29, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    }  ,
	['kuyusrv019'] = {
        start = os.time{ year = 2014,  month =1, day = 12, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month =2, day = 12, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    }  , 
	['kuyusrv020'] = {
        start = os.time{ year = 2014,  month =2, day = 27, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month = 3, day = 27, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    }  ,
	['kuyusrv021'] = {
        start = os.time{ year = 2014,  month =4, day = 30, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month = 5, day = 30, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    }  ,
	['kuyusrv022'] = {
        start = os.time{ year = 2014,  month =6, day = 10, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month = 7, day = 10, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    }  ,
	
	['kuyusrv023'] = {
        start = os.time{ year = 2014,  month =7, day = 11, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month = 8, day = 11, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    }  ,
	['kuyusrv024'] = {
        start = os.time{ year = 2014,  month =8, day = 14, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month = 9, day = 15, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    }  ,
	['kuyusrv025'] = {
        start = os.time{ year = 2014,  month =9, day = 15, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month = 10, day = 15, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    } ,
	['kuyusrv026'] = {
        start = os.time{ year = 2014,  month =11, day = 4, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2014,month = 12, day = 4, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    }  ,
	['kuyusrv027'] = {
        start = os.time{ year = 2014,  month =12, day = 5, hour = 0, min = 0, sec = 0, },
        terminal = os.time{ year = 2015,month = 1, day = 5, hour = 0, min = 0, sec = 0, },
 		{ 20, 24, mul = 2,},
    } 
	,
	
    ['androidsrv001'] = {
		start = os.time{ year = 2012,  month = 7, day = 6, hour = 0, min = 0, sec = 0, },
		terminal = os.time{ year = 2012,month = 7, day = 16, hour = 0, min = 0, sec = 0, },
		--{ 13, 14, mul = 3,},
	    --{ 11, 12, mul = 2,},
		--{ 18, 19, mul = 2,},
		{ 10, 12, mul = 2,},
	},
    ['androidsrv002'] = {
		start = os.time{ year = 2012,  month = 7, day = 6, hour = 0, min = 0, sec = 0, },
		terminal = os.time{ year = 2012,month = 7, day = 16, hour = 0, min = 0, sec = 0, },
		--{ 13, 14, mul = 3,},
	    --{ 11, 12, mul = 2,},
		--{ 18, 19, mul = 2,},
		{ 10, 12, mul = 2,},
	},
    ['androidsrv003'] = {
		start = os.time{ year = 2012,  month = 7, day = 6, hour = 0, min = 0, sec = 0, },
		terminal = os.time{ year = 2012,month = 7, day = 16, hour = 0, min = 0, sec = 0, },
		--{ 13, 14, mul = 3,},
	    --{ 11, 12, mul = 2,},
		--{ 18, 19, mul = 2,},
		{ 10, 12, mul = 2,},
	},
    ['androidsrv004'] = {
		start = os.time{ year = 2014,  month =4, day = 30, hour = 0, min = 0, sec = 0, },
		terminal = os.time{ year = 2014,month = 5, day = 30, hour = 0, min = 0, sec = 0, },
		--{ 13, 14, mul = 3,},
	    --{ 11, 12, mul = 2,},
		--{ 18, 19, mul = 2,},
		{ 10, 12, mul = 2,},
	}
}

function getMonsterExp(monsterid)
	local server = DoubleExp[Conf:getSp()..TianLongServer.srvId]
	local curtime = os.time()

	if server and curtime >= server.start and curtime <= server.terminal then
		local curhour = os.date("*t", curtime).hour
		for _, v in ipairs(server) do
			if curhour >= v[1] and curhour < v[2] then 
				return MonsterSet[monsterid].EXP * v.mul
			end
		end
	end
	
	return MonsterSet[monsterid].EXP
end

function getMonsterGold(monsterid)
	return MonsterSet[monsterid]:getGold()
end

---------------------------------------------------------------------------------------
------------------怪物死亡事件，检测并更新任务状态，若有掉落物品则返回--------------------
function monsterDie(jbattle, jrole, jbonus)
	local teamLeft = jbattle:getTeamLeft()
	if teamLeft:isEmpty() or teamLeft:get(0):getType() ~= 1 then
		return
	end
	
	local role_task = _GetRoleTask(jrole)
	local freshNpc
	local prompt = { }
	
	for k in jlistIter(teamLeft) do
		local monster = MonsterSet[k:getId()]
         
		--------------------------------检查任务---------------------------
		for _, v in ipairs(monster.task) do
			local task_state = _GetTaskState(role_task, v)
			if task_state and not _IsTaskFinished(task_state) then
				local element = TaskElementSet[TaskSet[v].detail[_GetTaskStateStep(task_state)]]
				local update
				
				if element.type == TaskElement.SpacialNpcFight then
				
					 update = element:updateState(jrole,monster, task_state, prompt)
                else
                
					 update = element:updateState(monster, _GetSubState(task_state), prompt)
				end 
				freshNpc = freshNpc or update
			end
		end
	end
	
	if freshNpc then
		prepareBody()
		
		fillFreshNPCState(jrole)
		
		putShort(0)
		
		sendMsg(jrole)
	end
	
	if #prompt > 0 then 
		jbonus:setTaskprop(table.concat(prompt, "/"))
	end
end

--新npc战斗结束
function npcFight(jbattle, jrole)
	local teamLeft = jbattle:getTeamLeft()
	if teamLeft:isEmpty() or teamLeft:get(0):getType() ~= 1 then
		return
	end
	
	local role_task = _GetRoleTask(jrole)
	local freshNpc
	local prompt = { }
	
	for k in jlistIter(teamLeft) do
		local monster = MonsterSet[k:getId()]

		--------------------------------检查任务---------------------------
		for _, v in ipairs(monster.task) do
			local task_state = _GetTaskState(role_task, v)
			if task_state and not _IsTaskFinished(task_state) then
				local element = TaskElementSet[TaskSet[v].detail[_GetTaskStateStep(task_state)]]
				if element.type == 6 then
					local update = element:updateState(monster, _GetSubState(task_state), prompt)
					freshNpc = freshNpc or update
				end
			end
		end
	end
	
	if freshNpc then
		prepareBody()
		
		fillFreshNPCState(jrole)
		
		putShort(0)
		
		sendMsg(jrole)
	end
	
end

function getMonsterLv(id)
	return MonsterSet[id].level
end

function getMonsterPDef(id)
	return MonsterSet[id].PDef
end

function getMonsterMDef(id)
	return MonsterSet[id].MDef
end
