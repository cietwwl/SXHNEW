
_TPScroll = _Prop:new()

function _TPScroll:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _TPScroll:canUse(jrole)
	if _RoleGetTeam(jrole) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "队伍中不允许回城！")
		return
	end
	
	return true
end

function _TPScroll:useItem(jrole, item)

	---------------------------------------------------------------------------
	--计算目标地图及坐标
	local destmap = MapSet[self.destMap]
	local x
	local y
	if destmap then
		if self.taskid then
			if not jrole:getTasks():checkTaskExist(self.taskid) then
				replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp, self.errorPrompt or "使用失败！")
				return
			end
		end
		
		for _, v in pairs(destmap.npcs) do
			if NPCSet[v.id].transferable then
				x = v.x
				y = v.y
				break
			end
		end
	else
		local lastmapid = _CoordsGetAttr(_RoleGetCoords(jrole))
		local lastmap = MapSet[lastmapid]
		local area = AreaSet[lastmap:getArea()]
		
		if area then
			destmap, x, y = unpack(area:getTransCoords())
		end
		
		if not destmap then
			for _, v in pairs(lastmap.trans) do
				local map = MapSet[v.destMap]
				if map then
					area = AreaSet[map:getArea()]
					if area then
						destmap, x, y = unpack(area:getTransCoords())
						if destmap then
							break
						end
					end
				end
			end
		end
	end
	
	if not destmap then
		replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp, "回城失败！")
		return
	end
	
	if not x then
		log.error("使用【", self.name, "】回城至地图【", destmap.name, "】，id【", destmap.id, "】没有传送NPC！")
		replyMessage(jrole, 4, MsgID.MsgID_Item_Do_Resp, "回城失败！")
		return
	end
	
	local theta = math.random() * 2 * math.pi
	local destx = x + math.sin(theta) * 16
	local desty = y + math.cos(theta) * 16
	--计算目标地图及坐标
	---------------------------------------------------------------------------
	--使用物品回复
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name .. "～")
	--使用物品回复
	---------------------------------------------------------------------------
	--删除物品并同步
	
	prepareBody()
	
	fillBagDel(item:getUid())

	--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	Bag:delItem(jrole, item:getUid(), 1)
	
	putShort(0)
	
	sendMsg(jrole)
	
	--删除物品并同步
	---------------------------------------------------------------------------
	--强制客户端切换地图
	forceMap(jrole, luajava.new(Coords, destmap.id, destx, desty))
	--_ForceChangeMap(jrole, luajava.new(Coords, destmap.id, destx, desty))
	--强制客户端切换地图
	---------------------------------------------------------------------------
end

function _TPScroll:getDescribe()
	if not self.__describe then
		local tbl = { }
		tbl[#tbl + 1] = self.name
		for _, v in ipairs(self.feature or { }) do
			if _ItemFeatureIsShow(v) then
				tbl[#tbl + 1] = _ItemFeatureGetName(v)
			end
		end
		tbl[#tbl + 1] = self.describe
		
		tbl[#tbl + 1] = "使用等级：" .. (self.level or 1)
		
		self.__describe = table.concat(tbl, "/")
	end
	
	return self.__describe
end

function _TPScroll:getUseMenu()
	return	MapSet[self.destMap]
			and
			{
				{ 0, "传送", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
			or
			{
				{ 0, "回城", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end
			
_TPScroll.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}
function _TPScroll:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end

