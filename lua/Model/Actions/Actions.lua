TagType = {
	input = 0, 
	label = 1, 
	link = 2,
}

TagSubType = {
	none = 0, 
	radio = 1, 
	checkbox = 2, 
	submit = 3,
	text = 4, 
	number = 5,
	cancle = 6,
	left = 7,
	right = 8,
	listItem = 9,
	inputLable = 10,
}


Actions = { }

ActionsMetatable = { }

ActionsMetatable.__index = function (_, n)
	log:error("访问未定义的Action: " .. n)
end

ActionsMetatable.__newindex = function (t, k, v) rawset(t, k, _Action:new(v)) end

setmetatable( Actions, ActionsMetatable )
