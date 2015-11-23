
MonsterType = {
	Common = 1,
	NPCFight = 2,
}

_Monster = { }

function _Monster:new(template)
	if template.type == MonsterType.Common then
		return _Monster:newInstance(template)
	elseif template.type == MonsterType.NPCFight then
		return _NPCFightMonster:newInstance(template)
	end
end

function _Monster:newInstance(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Monster:getLocation()
	local targetMapid = self.map
	local map = MapSet[targetMapid]
	
	if not map then
		debug.traceback("怪物", self.id, self.name, "所在地图：", targetMapid, "不存在！")
		return { mapid = 0, mapx = 0, mapy = 0, }
	end
	
	for _, v in pairs(map.monsters) do
		if v.id == self.id then
			local loc = v.location[math.random(1, #v.location)]
			return { mapid = targetMapid, mapx = loc.x, mapy = loc.y, }
		end
	end
	
	debug.traceback("对应地图上无此怪物：", self.name)
	return { mapid = 0, mapx = 0, mapy = 0, }
end


function _Monster:getGold()
	return math.random(self.dropGoldMin, self.dropGoldMax)
end

_Monster.visible = true

_NPCFightMonster = _Monster:newInstance()

_NPCFightMonster.visible = false

