
function sendMsg(jrole, msgid, info)
	Message:sendMsg(jrole, msgid or MsgID.MsgID_Special_Train, info)
end

function replyMessage(jrole, errno, msgid, info)
	Message:replyMessage(jrole, errno, msgid, info)
end

function placeholder(...)
	bodyMark()

	for _, type in ipairs(arg) do
		if type == 1 or type == 'b' or type == 'B' or type == 'byte' or type == 'Byte' then
			putByte(0)
		elseif type == 2 or type == 's' or type == 'S' or type == 'short' or type == 'Short' then
			putShort(0)
		elseif type == 4 or type == 'i' or type == 'I' or type == 'int' or type == 'Int' then
			putInt(0)
		elseif type == 8 or type == 'l' or type == 'L' or type == 'long' or type == 'Long' then
			putLong(0)
		else
			error("占位符类型不能为：" .. tostring(type))
		end
	end
end

function fillPlaceholder(...)
	local position = getPosition()
	
	bodyReset()
	
	for i = 1, #arg / 2 do
		local type = arg[i * 2 - 1]
		local value = arg[i * 2] 
		if type == 1 or type == 'b' or type == 'B' or type == 'byte' or type == 'Byte' then
			putByte(value)
		elseif type == 2 or type == 's' or type == 'S' or type == 'short' or type == 'Short' then
			putShort(value)
		elseif type == 4 or type == 'i' or type == 'I' or type == 'int' or type == 'Int' then
			putInt(value)
		elseif type == 8 or type == 'l' or type == 'L' or type == 'long' or type == 'Long' then
			putLong(value)
		else
			error("占位符类型不能为：" .. tostring(type))
		end
	end

	setPosition(position)
end
