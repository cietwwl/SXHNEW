

_GetItemTask = _CommonTask:new()

function _GetItemTask:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

--在与npc对话框中显示此任务
function _GetItemTask:shownInTalkList(jrole)
   
    if  _GetTaskState(_GetRoleTask(jrole), self.id) then
  
	    if _IsTaskFinished(_GetTaskState(_GetRoleTask(jrole), self.id)) then
	       
		   return false
		else
		    putInt(self.id)
			putLong(0)
			putByte(1)
			putString(self.name)
			putInt(0)
			return true
		end
	else 
	        putInt(self.id)
			putLong(0)
			putByte(1)
			putString(self.name)
			putInt(0)
			return true
	end
	
    
end



