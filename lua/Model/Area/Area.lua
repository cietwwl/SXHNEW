
_Area = { }

function _Area:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Area:getMaps()
	if self.maps then
		return self.maps
	end
	
	self.maps = { }
	for k, v in pairs(self.nodes) do
		if v.type == 0 or v.type == 1 then
			self.maps[v.id] = v.id
		end
	end
	
	return self.maps
end

function _Area:getTransCoords()
	if self.trans then
		return self.trans
	end
	for _, v in pairs(self.nodes) do
		if v.type == 1 then
			local map = MapSet[v.id]
			
			local npcSimple
			for _, v in pairs(map.npcs) do
				if NPCSet[v.id].transferable then
					self.trans = { map, v.x, v.y, }
					
					return self.trans
				end
			end
		end
	end
end
