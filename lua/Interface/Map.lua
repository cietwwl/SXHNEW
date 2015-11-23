
function initGridMapSys(jGridMapSys)
	log.info("初始化地图系统")
	
	for _, v in pairs(MapSet) do
		_MapPutKeyValue(jGridMapSys, luajava.new(Short, v.id), luajava.new(JGridMap, v.width, v.height))
	end
	
	log.info("初始化地图系统完成")
end

local function fillMapInfo(jrole, mapid)
	local curmap = MapSet[mapid]
	if not curmap then
		purShort(0)
		purShort(0)
		purShort(0)
		return
	end
	
	placeholder('s')
	
	local monsterCount = 0
	for _, v in pairs(curmap.monsters) do
		local monster = MonsterSet[v.id]
		if monster and monster.type ~= MonsterType.NPCFight then
			putInt(monster.id)--怪物id
			putString(monster.name)--名字
			putInt(0xf58220)--名字颜色
			putShort(v.group or v.groupid)--动画组
			putShort(_ResourceManagerGetResVer('a' .. v.group))
			--print('发送怪物动画组ID为 ' .. v.group or v.groupid .. '版本号为 ' .. _ResourceManagerGetResVer('a' .. v.group))
			putShort(v.animi or 0)--动画
			
			putByte(#v.location)--地图中此类型怪物个数
			for i, vv in ipairs(v.location) do
				putByte(i)--序号
				putInt(vv.x)--x坐标
				putInt(vv.y)--y坐标
				putShort(0)--保留
			end
			
			putShort(0)--保留
			
			monsterCount = monsterCount + 1
		end
	end
	
	fillPlaceholder('s', monsterCount)
-------------------------------------------------------------------
	placeholder('s')
	
	local npcCount = 0
	for _, v in pairs(curmap.npcs) do
		local npc = NPCSet[v.id]
		
		if npc then
			putInt(npc.id)
			putString(npc.nick)
			putInt(0x009ad6)--名字颜色
			putShort(v.group)--动画组
			putShort(_ResourceManagerGetResVer('a' .. v.group))
			--print('发送NPC动画组ID为 ' .. v.group .. '版本号为 ' .. _ResourceManagerGetResVer('a' .. v.group))
			putShort(v.cartoon or 0)--动画
			putInt(v.x)
			putInt(v.y)
			putByte(npc:getState(jrole, npc.id))
				--任务标志：0.无任务1.有任务可接2.任务进行中3.有任务可提交.
				--优先级按从大到小依此是3120

			putShort(0)--保留
			npcCount = npcCount + 1
		end 
	end
	
	fillPlaceholder('s', npcCount)
-------------------------------------------------------------------
	placeholder('s')
	
	local transCount = 0
	
	for _, v in pairs(MapSet[mapid].trans) do
		local dest =  MapSet[v.destMap]
		if dest then
			putInt(v.id)--trans id
			putString(dest.name)
			putShort(TransConf.Groupid)
			putShort(TransConf.Animi)
			putInt(v.x)
			putInt(v.y)

			putShort(0)--保留
			
			transCount = transCount + 1
		else
			log.error("地图", mapid, "上的传送点", v.id, "目标地图", v.destMap, "不存在")
		end
	end
	
	fillPlaceholder('s', transCount)
end

function enterMap(jrole, coords)
	coords = coords or _RoleGetCoords(jrole)
	local mapid, mapx, mapy = _CoordsGetAttr(coords)
	prepareBody()
	putShort(0)
	putString(MapPrompts.getPrompt())
	
	print(MapPrompts.getPrompt())
	
	putShort(mapid)
	putShort(_ResourceManagerGetResVer('m' .. mapid))
	
	--print('发送地图动画组ID为 ' .. mapid .. '版本号为 ' .. _ResourceManagerGetResVer('m' .. mapid))
	
	putInt(mapx)
	putInt(mapy)
	fillMapInfo(jrole, mapid)
	
	sendMsg(jrole, MsgID.MsgID_Enter_Map_Resp)
end

-----------------------------------------------------------------------------------------
-------------根据角色当前所在位置判断传送至那张地图及目标地图上到达的传送点位置--------------
function getDestCoords(jrole)	
	local lastmapx, lastmapy, transid = _EnterMapGetAttr()
	
	local trans
	
	--根据坐标修正传送点
	local jcoords = _RoleGetCoords(jrole)
	local mapid = _CoordsGetAttr(jcoords)
	local lastmap = MapSet[mapid]
	for _, v in pairs(lastmap.trans) do
		local distance = math.sqrt((lastmapx - v.x) ^ 2 + (lastmapy - v.y) ^ 2)
		if distance < 2 * 16 * 1.41421 then
			trans = v
			break
		end
	end
	
	if not trans then
		log.error("无法查找传送点，地图", mapid, "X =", lastmapx, "Y =", lastmapy)
		return
	end
	
	local destmap = MapSet[trans.destMap]
	
	local destTrans
	for _, v in pairs(destmap.trans) do
		if type(v) == "table" and v.id == trans.destTrans then
			destTrans = v
			break
		end
	end
	
	if not destTrans then
		log.error("传送点", transid, "目的地不存在") 
		return
	end
	
	local destxt = destTrans.x > destmap.width * 16 / 2 and -1 or 1
	local destyt = destTrans.y > destmap.height * 16 / 2 and -1 or 1
	
	local theta = math.random() * math.pi / 2
	local destx = destTrans.x + destxt * math.sin(theta) * 2 * 16
	local desty = destTrans.y + destyt * math.cos(theta) * 2 * 16
	
	return luajava.new(Coords, trans.destMap, destx, desty)
end

function forceMap(jrole, coords)
     checkHemony(jrole)
	prepareBody()
	
	putString(MapPrompts.getPrompt())
	
	local mapid, mapx, mapy = _CoordsGetAttr(coords)
	putShort(mapid)
	putShort(_ResourceManagerGetResVer('m' .. mapid))
	--print('发送地图动画组ID为 ' .. mapid .. '版本号为 ' .. _ResourceManagerGetResVer('m' .. mapid))
	putInt(mapx)
	putInt(mapy)
	
	fillMapInfo(jrole, coords:getMap())
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Order_Hero_Map)
    

end

FindPathNodeMax = 50

---------------------------------------------------------------------
---------------------------------寻路---------------------------------
function findPath(jrole)
	local startMapid = _CoordsGetAttr(_RoleGetCoords(jrole))
	local endMapid, endX, endY = _FindPathGetAttr()
	
	if not MapSet[endMapid] then
		replyMessage(jrole, 2, MsgID.MsgID_Find_Point_Address_Resp, "寻路失败！")
		return
	end
	
	if startMapid ~= endMapid then
		if MapSet[startMapid].forbidFindPath then 
			replyMessage(jrole, 3, MsgID.MsgID_Find_Point_Address_Resp, "此处无法使用该功能！")
			return
		elseif MapSet[endMapid].forbidFindPath then
			replyMessage(jrole, 4, MsgID.MsgID_Find_Point_Address_Resp, MapSet[endMapid].forbidFindPath)
			return
		end
	end
	
	prepareBody()
	putShort(0)
	putShort(0)
	
	local fill = 0
	
	if startMapid == endMapid then
		putByte(1)
	else
		local path = MapSet[startMapid]:getPathBSF(endMapid)
		
		putByte(#path + 1 > FindPathNodeMax and FindPathNodeMax or #path + 1)
		
		for i = #path, 1, -1 do
			putShort(path[i][1])
			putInt(path[i][2])
			putInt(path[i][3])
			
			fill = fill + 1
			if fill == FindPathNodeMax then
				break
			end
		end
	end
	
	if fill < FindPathNodeMax then
		putShort(endMapid)
		putInt(endX)
		putInt(endY)
	end
	
	putShort(0)
		
	sendMsg(jrole, MsgID.MsgID_Find_Point_Address_Resp)
end

local function availableLoc(jcoords)
	local lastmapid = _CoordsGetAttr(jcoords)
	local lastmap = MapSet[lastmapid]
	
	local destTrans = lastmap.trans[math.random(1, #lastmap.trans)]

	local destxt = destTrans.x > lastmap.width * 16 / 2 and -1 or 1
	local destyt = destTrans.y > lastmap.height * 16 / 2 and -1 or 1
	
	local theta = math.random() * math.pi / 2
	local destx = destTrans.x + destxt * math.sin(theta) * 2 * 16
	local desty = destTrans.y + destyt * math.cos(theta) * 2 * 16
			
	_CoordsSetAttr(jcoords, lastmapid, destx, desty)
end

function checkCoords(jcoords)
	local mapid, mapx, mapy = _CoordsGetAttr(jcoords)
	
	local lastmap = MapSet[mapid]
	if lastmap then
		if mapx > lastmap.width * lastmap.cell or mapx < 0 then
			return
		elseif mapy > lastmap.height * lastmap.cell or mapy < 0 then
			return
		end
	else
		return
	end
	
	return true
end

function rectifyCoords(jrole)
	local jcoords = _RoleGetCoords(jrole)
	if not checkCoords(jcoords) then
		availableLoc(jcoords)
	end
end
