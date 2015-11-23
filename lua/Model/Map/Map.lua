
_Map = { }

function _Map:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Map:getArea()
	if not self.areaid then
		for k, v in pairs(AreaSet) do
			local nodes = v:getMaps()
			if nodes[self.id] then
				self.areaid = k
				return k
			end
		end
	end
	
	return self.areaid
end

--广度优先寻路
function _Map:getPathBSF(endMapid)
	local mapQueue = { self.id, }
	mapQueue.serial = 1
	
	local visited = {}
	local reverse = {}
	
	local result
	for i = mapQueue.serial, math.huge do
		result = self.getPathBSF2(mapQueue, endMapid, visited, reverse)
		if result then
			break
		end
		
		if i == 500 then
			log.error "超过搜索上限仍未找到"
			return
		end
	end
	
	local path = {}
	local step = result
	while step[1] ~= mapQueue[1] do
		path[#path + 1] = step
		
		step = reverse[step[1]]
	end
		
	path[#path + 1] = step
	
	return path
end

function _Map.getPathBSF2(mapQueue, endMapid , visited, reverse)
	local curMapid = mapQueue[mapQueue.serial]
	
	if not curMapid then
		error("地图" .. endMapid .. "无法通过寻路到达！")
	end
	
	local curMap = MapSet[curMapid]
	
	if curMap then
		for _, v in pairs(curMap.trans) do
			if not visited[v.destMap] then
				if v.destMap == endMapid then
					return {curMapid, v.x, v.y}
				end
				
				mapQueue[#mapQueue + 1] = v.destMap
				reverse[v.destMap] = {curMapid, v.x, v.y}
			end
		end
	else
		log.error("地图", curMapid, "不存在！")
	end
	
	visited[curMapid] = true
	mapQueue.serial = mapQueue.serial + 1
end
