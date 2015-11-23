
function GetTransMaps(jRole, mapsInfo) 
  local level = _GameCharacterGetLevel(jRole)
  local area = AreaSet[100]
  for k, v in pairs(area.nodes) do
  	
     local map = MapSet[v.id]
     if map then
     	local mapInfo = luajava.new(MainMapInfo)
     	mapInfo:setId(v.id)
     	mapInfo:setName(v.name)
     	
        local area = AreaSet[map:getArea()]
        mapInfo:setLevel(area.tensLevel)
        local minLevel = area.tensLevel * 10 + 1
        local maxLevel = (area.tensLevel + 1) * 10
        if level <= 10 then
        	mapInfo:setEnable(false)
        elseif level > maxLevel or ((minLevel <= level) and (maxLevel >= level)) then
           mapInfo:setEnable(true)
        else
           mapInfo:setEnable(false)
        end
        mapsInfo:add(mapInfo)
     end
      
  end
end

function TransPlayerToAnotherMap(jRole,mapId)
  	---------------------------------------------------------------------------
  	if _GameCharacterGetLevel(jRole) <= 10 then
  		 replyMessage(jRole, 3, MsgID.MsgID_Get_TransToMap_Resp, "地图传送功能需10级以上才能使用！")
		   return
  	end
  	
  	if _RoleGetTeam(jRole) then
		replyMessage(jRole, 2, MsgID.MsgID_Item_Do_Resp, "队伍中不允许传送！")
		return
	end
	
	--计算目标地图及坐标
	local destMap = MapSet[mapId]  --要传送到的地图
	local x
	local y
	local map = MapSet[mapId]
    if map then
        local roleLevel = _GameCharacterGetLevel(jRole) 
        local area = AreaSet[map:getArea()]
        local minLevel = area.tensLevel * 10 + 1
        local maxLevel = (area.tensLevel + 1) * 10
        if roleLevel > maxLevel or ((minLevel <= roleLevel) and (maxLevel >= roleLevel)) then
	    
	    
	    else
	       replyMessage(jRole, 3, MsgID.MsgID_Get_TransToMap_Resp, "等级未达到！传送至此地图需达到" .. minLevel.."级")
		   return
	    end
	end
	if destMap then
		
		for _, v in pairs(destMap.npcs) do
			if NPCSet[v.id].transferable then
				x = v.x
				y = v.y
				break
			end
		end
	end
	
	if not x then
		replyMessage(jRole, 2, MsgID.MsgID_Get_TransToMap_Resp, "回城失败！")
		return
	end
	
	local theTa = math.random() * 2 * math.pi
	local destX = x + math.sin(theTa) * 16
	local destY = y + math.cos(theTa) * 16
	--计算目标地图及坐标
	---------------------------------------------------------------------------
	--扣除人物传送费用
	local gold = _RoleGetGold(jRole) - _GameCharacterGetLevel(jRole) * 10
	if gold < 0 then
	   replyMessage(jRole, 1, MsgID.MsgID_Get_TransToMap_Resp, "传送费用不足！")
	   return
	else
	   _RoleSetGold(jRole, gold)
	   replyMessage(jRole, 0, MsgID.MsgID_Get_TransToMap_Resp, "传送成功！")
	  flushRoleAttr(jRole)
	end
	
	-----------加日志---------
	--人物id  花费金币   传送的地图名称   时间
	Log:info(Log.TRANS,_GameCharacterGetId(jRole) .. "#$" .. _GameCharacterGetLevel(jRole) * 10  .. '#$' .. destMap.name .. '#$' .. _GetNowTimeString())
	-----------加日志---------
	
	--判断是否在组队状态，是的话直接将全组人出送走
	if not _RoleGetTeam(jRole) or  _GetTeamNum(jRole) == 1 then
	    ---------------------------------------------------------------------------
		--强制客户端切换地图
		
		forceMap(jRole, luajava.new(Coords, destMap.id, destX, destY))
		_ForceChangeMap(jRole, luajava.new(Coords, destMap.id, destX, destY))
		--强制客户端切换地图
		---------------------------------------------------------------------------
	else
	   local members = _RoleGetTeamMemebers(jRole)
	   
       for member in jlistIter(members) do
        ---------------------------------------------------------------------------
		  --强制客户端切换地图
		  forceMap(member, luajava.new(Coords, destMap.id, destX, destY))
		  _ForceChangeMap(member, luajava.new(Coords, destMap.id, destX, destY))
		  --强制客户端切换地图
		---------------------------------------------------------------------------
       end
    end
end


function  transToMap(jRole, mapId,NPCId)
   local destMap = MapSet[mapId]  --要传送到的地图
	local x
	local y
	if destMap then
		
		for _, v in pairs(destMap.npcs) do
			if v.id == NPCId  then
				x = v.x
				y = v.y
				break
			end
		end
	end
	
	local theTa = math.random() * 2 * math.pi
	local destX = x + math.sin(theTa) * 16
	local destY = y + math.cos(theTa) * 16
	
	--判断是否在组队状态，是的话直接将全组人出送走
	if not _RoleGetTeam(jRole) or  _GetTeamNum(jRole) == 1 then
	    ---------------------------------------------------------------------------
		--强制客户端切换地图
		forceMap(jRole, luajava.new(Coords, destMap.id, destX, destY))
		--强制客户端切换地图
		---------------------------------------------------------------------------
	else
	   local members = _RoleGetTeamMemebers(jRole)
	   
       for member in jlistIter(members) do
          ---------------------------------------------------------------------------
		--强制客户端切换地图
		forceMap(member, luajava.new(Coords, destMap.id, destX, destY))
		--强制客户端切换地图
		---------------------------------------------------------------------------
       end
    end
   

end

function  transToMapRandom(jRole, mapId)
   local destMap = MapSet[mapId]  --要传送到的地图
	local x 
	local y
	if destMap then
		local calX = (destMap.width * destMap.cell)/2  
        local calY = (destMap.height * destMap.cell)/2
        
	   local theTa = math.random() * 2 * math.pi
	   x = calX + math.sin(theTa) * 20
	   y = calY + math.cos(theTa) * 20
	    
		--强制客户端切换地图
		forceMap(jRole, luajava.new(Coords, destMap.id, x, y))
		_ForceChangeMap(jRole, luajava.new(Coords, destMap.id, x, y))
		--强制客户端切换地图
	end

end

function transOut(jRole,NPCId) 
  local npc = NPCSet[NPCId]
  local mapId = npc.mapid
 local destMap = MapSet[mapId]  --要传送到的地图
	local x
	local y
	if destMap then
		
		for _, v in pairs(destMap.npcs) do
			if v.id == NPCId  then
				x = v.x
				y = v.y
				break
			end
		end
	end
	
	local theTa = math.random() * 2 * math.pi
	local destX = x + math.sin(theTa) * 16
	local destY = y + math.cos(theTa) * 16
	
	--强制客户端切换地图
	forceMap(jRole, luajava.new(Coords, destMap.id, destX, destY))
	--强制客户端切换地图
	---------------------------------------------------------------------------
   

end