
local Rookie = {}

Rookie.map = 0

function initRookie(rookie)
	--出生地图	
	rookie:setLastmap(Rookie.map)
	
	--计算出生坐标
	if not Rookie.mapx then
		local rookieMap = MapSet[Rookie.map]
		
		Rookie.mapx = rookieMap.width * rookieMap.cell / 2
		Rookie.mapy = rookieMap.height * rookieMap.cell / 2
	end
	
	local mapx = Rookie.mapx + math.random(-30, 30)
	local mapy = Rookie.mapy + math.random(-30, 30)
	
	rookie:setLastmapx(mapx)
	rookie:setLastmapy(mapy)
	
	local vocation = rookie:getVocation()
	local template
	if vocation == _EnumOrdinal(Vocation.SHAQ) then
		template = ItemSet[25]
	elseif vocation == _EnumOrdinal(Vocation.Warlock) then
		template = ItemSet[29]
	else
		template = ItemSet[28]
	end
	
	local jitem = template:creatJavaItem()[1]
		
	local jstore = JStore:defaultStore(nil)
	local inusePack = jstore:getPack(0)
	_PackAddItem(inusePack, jitem)
	rookie:setStoreStr(jstore:serialize())
end
