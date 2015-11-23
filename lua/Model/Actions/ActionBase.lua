_Action = {}

function _Action:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

--TODO 添加doAction, doDisplay默认方法 

function _Action:doAction(roleid, args)
	print('未处理的action ' .. self.name)
end

function _Action:doDisplay(jrole)	
	if self:fillLayout() then
		sendMsg(jrole, MsgID.Action_Request_Resp)	
	end	
end

function _Action:fillLayout()
	local layout = self.layout
	if layout then
		prepareBody()
		putString(layout.title)
		putShort(layout.x)
		putShort(layout.y)
		putShort(layout.width)
		putShort(layout.height)
		putByte(#layout.tags)
		for _,v in ipairs(layout.tags) do
			putByte(v.type)
			putByte(v.subtype)
			putString(v.name)
			putString(v.value)
			if not v.display then
				v.display = v.value
			end
			putString(v.display)
			putString(v.action)
			putShort(v.x)
			putShort(v.y)
			putShort(v.width)
			putShort(v.height)
		end
	else
		log:error('error in fillLayout, layout does not exist! action: ', self.name)
		return false
	end
	return true
end

function _Action:redirect(jrole, actionName)
	local action = Actions[actionName]
	if action then
		action:doDisplay(jrole)
	else
		self:error(jrole, '您访问的页面不存在!')
	end
end

function _Action:error(jrole, errorMsg, returnActionName)
	local action = Actions['commonError']
	
	if action then
		action.layout.tags[1].display = errorMsg
		
		local returnAction = Actions[returnActionName]
		
		if returnAction then
			action.layout.tags[2].action = returnActionName
			action.layout.tags[2].subtype = TagSubType.right
		end
		action:doDisplay(jrole)
	else
		log:error('默认错误页面不存在!')
	end
end

function _Action:commonMessage(jrole, msg, returnActionName)
	local action = Actions['commonMsg']
	
	if action then
		action.layout.tags[1].display = msg
		
		local returnAction = Actions[returnActionName]
		
		if returnAction then
			action.layout.tags[2].action = returnActionName
			action.layout.tags[2].subtype = TagSubType.right
		end
		action:doDisplay(jrole)
	else
		log:error('默认错误页面不存在!')
	end
end

function _Action:removeAllListItems()
	local layout = self.layout
	if layout then
		for k,v in ipairs(layout.tags) do
			if v.subtype == TagSubType.listItem then
				layout.tags[k] = nil
			end
		end
	end

end

function _Action:addListItem(name, value, action, display, x, y, width, height)
	local layout = self.layout
	if layout then
		layout.tags[#layout.tags + 1] = {
			type = TagType.input,
			subtype = TagSubType.listItem,
			name = name,
			value = value,
			action = action,
			display = display,
			x = x,
			y = y,
			width = width,
			height = height,
		}
	end
end