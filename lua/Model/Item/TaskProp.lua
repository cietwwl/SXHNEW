
_TaskProp = {  }

function _TaskProp:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _TaskProp:getRawValue()
	return 100000000
end

function _TaskProp:getSellValue()
	return 0
end

function _TaskProp:getDescribe()
	debug.traceback("出错了！")
	return ""
end

function _TaskProp:isDedicated()
	return false
end

function _TaskProp:tryUseItem(jrole, item)
	replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "使用失败！")
end

function _TaskProp:isUnique()
end