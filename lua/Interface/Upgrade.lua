
MAXLEVEL = 500 

local function req(level)
	if level == 0 then
		return 0
	end
	
	if level <= 40 then
		return math.floor(67 ^ level ^0.22 * (1 + (level / 10 - level / 10 % 1) ^ 0.92 * 1))
	elseif level <= 50 then
		return math.floor((65^(level^0.23))*(1+((level / 10 - level / 10 % 1) ^ 0.95)*0.96))
	elseif level < 95 then
		return math.floor((67^(level^0.235))*(1+((level / 10 - level / 10 % 1) ^ 0.95)*0.96))
	elseif level < 110 then
		return math.floor(1845541*1.4^(level-94))
	elseif level>255 then
		return 2142523680
	else
		return math.floor(32623*1.01*level^2)
	end
end

function getUpgradeRequire(roleLevel)
	return req(roleLevel)
end

function checkLevel(level)
	return level > 0 and level <= MAXLEVEL
end