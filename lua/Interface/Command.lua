
--命令处理库，jmx命令解析仅限调用其中的函数

function parseCommand(command, jmx)
	local func
	local args = { }

	for argument in string.gmatch(command, "[%w_-]+") do
		if not func then
			func = argument
		else
			if string.find(argument, "^[+-]?%d+$") then
				table.insert(args, tonumber(argument))
			else
				table.insert(args, argument)
			end
		end
	end

	log.info("执行函数：", func, "(", table.concat(args, ", "), ")")
	
	if Command[func] then
		Command[func](not not jmx, unpack(args))
	else
		log.info("命令", command, "无法执行！")
	end
end
