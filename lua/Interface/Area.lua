
--检查地图—区域映射是否正确
function checkArea(jrole, areaid)
	local mapid = _CoordsGetAttr(_RoleGetCoords(jrole))
	local curAreaid = MapSet[mapid]:getArea()
	if not curAreaid then
		replyMessage(jrole, 2, MsgID.MsgID_Get_MapInfo_Resp, "此处无法获取地图！")
		return false
	end
	
	if areaid ~= 0 then
		if not AreaSet[areaid] then
			replyMessage(jrole, 3, MsgID.MsgID_Get_MapInfo_Resp, "获取地图失败，请稍后再试～")
			return false
		end
	end
	
	return true
end

function getArea(mapid)
	return MapSet[mapid]:getArea()
end

--填充所有区域地图数据：id、name
function fillAreas()
	if not AreaSet then
		putShort(0)
		return
	end
	
	placeholder('s')
	
	local areaCount = 0
	
	for _, v in pairs(AreaSet) do
		putShort(v.id)
		if v.name then
			putString(v.name)
		else
			putString('')
		end
		
		
		areaCount = areaCount + 1
	end
	
	fillPlaceholder('s', areaCount)
end

---------填充指定区域地图
function fillArea(areaid)
	local area = AreaSet[areaid]
	
	if not area then
		log.error("区域", areaid, "不存在")
		return
	end
	
	putShort(area.id)
	putShort(area.width)
	putShort(area.height)
	
	placeholder('s')
	
	local nodes = 0	
	
	for _, v in pairs(area.nodes) do
		putShort(v.id)
		putByte(v.type)
		putShort(v.col)
		putShort(v.row)
		if v.type == 0 or v.type == 1 then
			putString(MapSet[v.id].name)
		else
			putShort(0)
		end
		
		if not v.linkMapID then
			putShort(-1)
			putShort(0)
			putShort(0)
		else
			putShort(v.linkMapID)
			putShort(v.linkMapRow)
			putShort(v.linkMapCol)
		end
		
		putShort(0)
		
		nodes = nodes + 1
	end
	
	fillPlaceholder('s', nodes)
end
