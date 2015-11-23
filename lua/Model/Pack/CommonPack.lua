
CommonPack = { }

function CommonPack:new(pid)
	Temp.CommonPack = Temp.CommonPack or { }
	if Temp.CommonPack[pid] then
		return Temp.CommonPack[pid]
	end
	
	local o = { }
	o.packid = pid
	
	setmetatable(o, self)
	self.__index = self
	
	Temp.CommonPack[pid] = o
	return o
end

function CommonPack:checkInPack(jrole, uid)
	return _PackGetItem(_GetPack(jrole, self.packid), uid)
end

function CommonPack:getItemInPack(jrole, uid)
	local pack = _GetPack(jrole, self.packid)
	local item = _PackGetItem(pack, uid)
	local template = ItemSet[_ItemGetTid(item)]
	local storage = _ItemGetStorage(item)
	return item, template, storage
end

function CommonPack:delItem(jrole, uid, num)
	_GetPack(jrole, self.packid):pickItem(uid, num, true)
end

function CommonPack:checkAdd(jrole, rewards)
	if not rewards or not rewards.items then
		return true
	end
	
	local items = table.shallow_copy(rewards.items)
	
	local curStashSize = 0
	
	local pack = _GetPack(jrole, self.packid)
	
	local capacity = _PackGetCapacity(pack)
	
	local pack_items = _PackGetItemsAll(pack)
	
	for k, v in jmapIter(pack_items) do
		if _ItemIsUnique(v) then
			curStashSize = curStashSize + 1
		else
			if items[k] then
				curStashSize = curStashSize + math.ceil((_ItemGetStorage(v) + items[k]) / (ItemSet[k].overlay or 20))
				items[k] = nil
			else
				curStashSize = curStashSize + math.ceil(_ItemGetStorage(v) / (ItemSet[k].overlay or 20))
			end
		end
	end

	for k, v in pairs(items) do
		if type(k) == "number" then
			local template = ItemSet[k]
			if template then
				if template.type == ItemConst.Equip then
					curStashSize = curStashSize + v
				else
					curStashSize = curStashSize + math.ceil(v / (ItemSet[k].overlay or 20))
				end
			else
				log.error("物品", k, "不存在！")
			end
		end
	end
	
	return capacity >= curStashSize
end

function CommonPack:remainSpace(jrole)
	local curStashSize = 0
	
	local pack = _GetPack(jrole, self.packid)
	
	local pack_items = _PackGetItemsAll(pack)
	
	for k, v in jmapIter(pack_items) do
		if _ItemIsUnique(v) then
			curStashSize = curStashSize + 1
		else
			curStashSize = curStashSize + math.ceil(_ItemGetStorage(v) / (ItemSet[k].overlay or 20))
		end
	end

	return _PackGetCapacity(pack) - curStashSize
end

function CommonPack:pureSpace(jrole, items)
	local pureSize = 0
	
	local pack = _GetPack(jrole, self.packid)
	
	for k, v in pairs(items) do
		if ItemSet[k].type == ItemConst.Equip then
			pureSize = pureSize + v
		else
			pureSize = pureSize + math.ceil(v / (ItemSet[k].overlay or 20))
		end
	end

	return pureSize
end

function CommonPack:getJPack(jrole)
	return _GetPack(jrole, self.packid)
end

function CommonPack:addJItem(jrole, jItems)
	for _, v in pairs(jItems) do
		_PackAddItem(_GetPack(jrole, self.packid), v)
	end
end

InusePack = CommonPack:new(0)

Bag = CommonPack:new(1)

function InusePack:getItemInPack(jrole, equip_type)
	return _InusePackGetItem(_GetPack(jrole, self.packid), equip_type)
end

function Bag:getItemCountByTid(jrole, tid)
	return _GetPack(jrole, self.packid):getItemCountByTid(tid)
end

function Bag:contains(jrole, jmap)
	return _GetPack(jrole, self.packid):contains(jmap)
end
