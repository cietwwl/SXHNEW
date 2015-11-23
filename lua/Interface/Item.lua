
function hasShoutItem(jrole)
	return Bag:checkInPack(jrole, ItemConst.TrumpetTemp)
end

function canAddToBag(...)
	local itemTbl = { }
	itemTbl.items = { }
	for i = 1, math.huge do
		if arg[i * 2] then
			if arg[i * 2 + 1] ~= 0 then
				itemTbl.items[arg[i * 2]] = arg[i * 2 + 1]
			end
		else
			break
		end
	end

	return Bag:checkAdd(arg[1], itemTbl)
end

function canAddToPack(jrole, packid, tid, num)
	return CommonPack:new(packid + 1):checkAdd(jrole, { items = { [tid] = num, }, })
end

function victim(jrole)
	if not ItemConst.ScapegoatTemp then
		return 
	end
	
	local level = _GetRoleLevel(jrole)
	for k, v in pairs(ItemConst.ScapegoatTemp or { }) do
		if level >= v[1] and level <= v[2] then
			if Bag:checkInPack(jrole, k) then
				prepareBody()
	
				fillBagDel(k)
				
				--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$16#$" .. ItemSet[k].name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
				log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$16#$" .. ItemSet[k].name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(k).."#$".._Long2String(k))
				
				Bag:delItem(jrole, k, 1)
				
				putShort(0)
				
				sendMsg(jrole)
				
				return true
			end
		end
	end
end
