
_Indemnify20101108 = _CommonTask:new()

function _Indemnify20101108:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

local function _getDate()
	local java_calendar = Calendar:getInstance()
	java_calendar:set(2010, 10, 8, 20, 0, 0)
	local terminal = java_calendar:getTime()
	return	function ()
				return terminal
			end
end

local getDate = _getDate()

-------------------------------------------------------------------------------
--@return 任务状态：0忽略；1可接任务；2任务进行中；3可交任务
function _Indemnify20101108:getState(jrole, npcid)
	local role_task = _GetRoleTask(jrole)
	if _GetTaskState(role_task, self.id) then
		return 0
	end
	
	local java_regdate = _GetRegdate(jrole)
	if java_regdate and java_regdate:after(getDate()) then
		return 0
	end

	return 1
end

-------------------------------------------------------------------------------
--@return 是否在备忘中显示以及寻路坐标
function _Indemnify20101108:getStateInMEMOS(jrole, diff)
	if self:getState(jrole) == 1 then
		local element = TaskElementSet[self.detail[1]]
		if not element then
			log.error("任务", self.name, "的子任务", self.detail[1], "不存在！")
			return
		end
		
		local npc = element:getAcceptNpc()
		if not npc then
			return
		end
		
		return true, npc:getLocation()
	end
end
