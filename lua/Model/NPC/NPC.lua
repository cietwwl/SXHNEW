
_NPC = { }

function _NPC:new(o)
	o = o or { }
	if o.id == 1 then
		table.insert(o.task, 20101108)
	end
	setmetatable(o, self)
	self.__index = self
	return o
end

function _NPC:test()
	return self.nick
end

function _NPC:getLocation()
	local map = MapSet[self.mapid]
	for _, v in pairs(map.npcs) do
		if v.id == self.id then
			return { mapid = map.id, mapx = v.x, mapy = v.y }
		end
	end
	
	debug.traceback("无法获取NPC", self.id, "所在地图！")
end

function _NPC:getState(jrole)
	local state = 0
	
	for _, v in pairs(self.task) do
		local task = TaskSet[v]
		if task and task.getState then
			--0忽略；1表示在接任务的NPC处；2表示交任务NPC处，任务进行中；3表示交任务NPC处，任务已完成
			local task_state = task:getState(jrole, self.id)
			if task_state == 3 then
				return 3
			elseif task_state > state then
				state = task_state
			end
		end
	end
	
	if state ~= 0 then
		return state
	end
	
	if self.feature then
		for _, v in ipairs(self.feature) do
			local task = TaskSet[v]
			if task and task.type == TaskProfile.CreatGang then
				return task:getState(jrole)
			end
		end
	end
	
	return state
end
