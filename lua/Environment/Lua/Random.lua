
math.randomseed(os.time())

-------------------------------------------------------------------------------

random = { }

function random.nilable(randTbl, total_rand, valid)
	local rand = math.random() * (total_rand or 10000)
	local interval = 0
	for _, v in ipairs(randTbl) do
		if not valid or valid(v[1]) then
			if rand >= interval and rand <= interval + v.rand then
				return v
			else
				interval = interval + v.rand
			end
		end
	end
end

function random.nonempty(randTbl, valid)
	local total_rand = randTbl._total_rand
	if not total_rand then
		total_rand = 0
		
		for _, v in ipairs(randTbl) do
			if not valid or valid(v[1]) then
				total_rand = total_rand + v.rand
			end
		end
		
		if not randTbl.volatile then
			randTbl._total_rand = total_rand
		end
	end
	
	return random.nilable(randTbl, total_rand, valid)
end

--随机自然数，概率递减
function random.getn(maxn)
	local rand = math.random()
	local interval = 0
	for i = 1, maxn do
		local range = 0.55 * 0.45 ^ (i - 1) + 0.45 ^ maxn / maxn
		if rand >= interval and rand <= interval + range then
			return i
		else
			interval = interval + range
		end
	end
	
	return 0
end
