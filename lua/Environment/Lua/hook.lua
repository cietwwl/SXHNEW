
debug.hook = function (limit)
	local count = 0
	local function limit_hook(event, line)
		if limit and type(limit) == "number" and count < limit then
			print(debug.getinfo(2).short_src  .. ":" .. line)
			count = count + 1
		end
	end
			
	if limit then
		debug.sethook(limit_hook, "l")
	else
		debug.sethook()
	end
end