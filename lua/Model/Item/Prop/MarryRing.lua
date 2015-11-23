
_MarryRing = _Prop:new()

function _MarryRing:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _MarryRing:canUse(jrole)
	if _RoleGetTeam(jrole) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "队伍中不允许！")
		return
	end
	
	return true
end

function _MarryRing:useItem(jrole, item)
	---------------------------------------------------------------------------
	local lastmapid = nil
	local x = nil
	local y = nil
	
	if not _GetIfMarry(jrole) then
		replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp,"你已离婚，请将戒指交到NPC处回收，以免影响你再婚")
	return
	end
	if not _GetIfSpouseOnline(jrole) then
		replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp,"你的配偶不在线，不能使用")
		return
	end	
		
	lastmapid,x,y = _CoordsGetAttr(_RoleGetCoords(_GetSpouseRole(jrole)))
	
	for _,v in pairs({193,203,204,205,194,195,196,197,198,199,200,201,228,229,230}) do
		if lastmapid ==v then
			replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp,"你的配偶所在地图不允许传送")
			return
		end
	end	
		
	if not _GetRingCanUse(jrole,self.txtTime) then
		replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp,"你的戒指冷却未结束，要"..self.txtTime.."小时才能使用一次")
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

	--强制客户端切换地图
	forceMap(jrole, luajava.new(Coords, lastmapid, destx, desty))
	--强制客户端切换地图
	Log:info(Log.MARRY,"4#$".._RoleGetId(jrole).."#$".._RoleGetNick(jrole).."#$#$#$#$0#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	---------------------------------------------------------------------------
end

function _MarryRing:getDescribe()
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

function _MarryRing:getUseMenu()
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
			
_MarryRing.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}
function _MarryRing:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end

