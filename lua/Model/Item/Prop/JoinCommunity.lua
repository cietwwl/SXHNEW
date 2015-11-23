
_JoinCommunity = _Prop:new()

function _JoinCommunity:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _JoinCommunity:canUse(jrole)
	return true
end

function _JoinCommunity:useItem(jrole, item)
	---------------------------------------------------------------------------
	--删除原有社区，加入新社区
	local community_id = _RoleGetCommunity(jrole)
	if community_id ~= 0 then
		local java_community_last = Communitys:getCommunity(community_id)
		if java_community_last then
			if java_community_last:getItemid() == self.id then
				replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "已经加入该社区！")
				return
			end
			
			java_community_last:delMember(jrole)
		end
	end
	
	local java_community = Communitys:getCommunityByItem(self.id)
	java_community = java_community or Communitys:creatCommunity(self.id, self.community_name)
	
	java_community:addMember(jrole)
	_RoleSetCommunity(jrole, java_community:getId())
	--删除原有社区，加入新社区
	---------------------------------------------------------------------------
	--返回提示
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name .. "/成功加入社区【" .. self.community_name .. "】～")
	--返回提示
	---------------------------------------------------------------------------
	--同步物品与背包属性
	prepareBody()
	
	local uid = _ItemGetUid(item)
	
	fillBagDel(uid)
	
	--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(uid))
	Bag:delItem(jrole, uid, 1)
	
	fillEpithet(jrole)
	
	putShort(0)
	
	sendMsg(jrole)
	--同步物品与背包属性
	---------------------------------------------------------------------------
end

function _JoinCommunity:getDescribe()
	if not self.__describe then
		local tbl = { }
		tbl[#tbl + 1] = self.name
		for _, v in ipairs(self.feature or { }) do
			if _ItemFeatureIsShow(v) then
				tbl[#tbl + 1] = _ItemFeatureGetName(v)
			end
		end
		tbl[#tbl + 1] = self.describe
		
		tbl[#tbl + 1] =  "使用等级：" .. (self.level or 1)
		
		self.__describe = table.concat(tbl, "/")
	end
	
	return self.__describe
end

function _JoinCommunity:getUseMenu()
	return	{
				{ 0, "使用", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end
					
_JoinCommunity.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}

function _JoinCommunity:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end
