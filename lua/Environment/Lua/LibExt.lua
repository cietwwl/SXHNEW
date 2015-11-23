
--------------------table数组部分随机乱序
function table.shuffle(tbl)
	local shuffle = table.deep_copy(tbl)

	if #shuffle > 1 then
		for i = 1, #shuffle - 1 do
			local rand = math.random(i + 1, #shuffle)
			shuffle[i], shuffle[rand] = shuffle[rand], shuffle[i]
		end
	end
	
	return shuffle
end

--------------------table键-值对调
function table.inverse(tbl, inverse)
	inverse = inverse or { }
	for k, v in pairs(tbl) do
		inverse[v] = k
	end
	
	return inverse
end

--------------------table数组部分反向
function table.reverse(tbl)
	local reverse = { }
	for i = 1, math.floor(#tbl / 2) do
		reverse[i], reverse[#tbl + 1 - i] = tbl[#tbl + 1 - i], tbl[i] 
	end
	
	return reverse
end

--------------------table截取子table
function table.subtable(...)
	local tbl = arg[1]
	local subtable = {}
	for i = arg[2], #tbl do
		subtable[#subtable + 1] = tbl[i]
	end
	
	return subtable
end

--------------------table浅复制
function table.shallow_copy(tbl)
	local shallow = { }
	for k, v in pairs(tbl) do
		shallow[k] = v
	end
	
	return shallow
end

--------------------table深复制
function table.deep_copy(tbl) 
	local tracker = { }
	local	function deep_copy(tbl)
				if type(tbl) == "table" and not tracker[tbl] then
					tracker[tbl] = true
					
					local y = { }
					setmetatable(y, getmetatable(tbl))
					
					for k, v in pairs(tbl) do
						y[k] = deep_copy(v)
					end
					
					return y
				else
					return tbl
				end
			end
	
	return deep_copy(tbl)
end

------------将table中的数组部分转换为ArrayList
function table.to_arraylist(tbl)
	local list = luajava.new(ArrayList, #tbl)
	for _, v in ipairs(tbl) do
		list:add(v)
	end
	
	return list
end

---------获取table数组中不大于num的部分
function table.getNoMoreThan(tbl, num)
	num = num or 1
	
	local nums = { }
	for i = 1, #tbl do
		if type(tbl[i]) == "number" then 
			if tbl[i] < num then
				nums[#nums + 1] = tbl[i]
			else
				nums[#nums + 1] = num
				break
			end
		else
			break
		end
	end
	
	return nums
end

-------将table中的数组部分设置为list内容
function table.set_arraylist(list, tbl)
	list:clear()
	
	for _, v in ipairs(tbl) do
		list:add(v)
	end
end

function table.n_insert(tbl, value, n)
	for i = 1, n do
		table.insert(tbl, value)
	end
end

function table.containsValue(tbl, value)
	for _, v in pairs(tbl) do
		if v == value then
			return true
		end
	end
end

function table.natural_number(n)
	local tbl = {}
	for i = 1, n do
		table.insert(tbl, i)
	end
	
	return tbl
end

function table.random_numset(maxn, times)
	local numset = { }
	
	for i = 1, times do
		numset[#numset + 1] = math.random(1, maxn)
	end
	
	return numset
end

function table.getglobal(key_by_key)
	local cur_table = _G

	if not key_by_key then
		return cur_table
	end

	for w in string.gfind(key_by_key, "[%w_]+") do
		local inner_table = rawget(cur_table, w)
		inner_table = inner_table or { }
		rawset(cur_table, w, inner_table)
    	cur_table = inner_table
    end

	return cur_table
end

---------------------------四舍五入
function math.round(x)
	local i, f = math.modf(x)
	return f >= 0.5 and i + 1 or i
end

function table.equals(tbl1, tbl2)
	for k, v in ipairs(tbl1) do
		if tbl2[k] ~= v then
			return
		end
	end
	
	return true
end
