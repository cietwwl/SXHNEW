
_GangProp = _Prop:new()

function _GangProp:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _GangProp:canUse(jrole)
	return true
end

function _GangProp:useItem(jrole, item)
	---------------------------------------------------------------------------
	--条件验证
	if not jrole:getJobTitle():highRigths() or not _IsGangLoaded(_RoleGetGangid(jrole)) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "只能由帮主使用！")
		return
	end
	
	local charm = _RoleGetCharm(jrole)
	if charm < (self.charm or 9999999) then
		replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp, "您的声望不足，不能使用该物品！")
		return
	end
	
	local gold = _RoleGetGold(jrole)
	if gold < (self.gold or 9999999) then
		replyMessage(jrole, 4, MsgID.MsgID_Item_Do_Resp, "背包内资金不足，不能使用该物品！")
		return
	end
	
	local gangid = _RoleGetGangid(jrole)
	local gang = GangService:getGang(gangid)
	local gang_level = gang:getLevel()
	if gang_level < self.level then
		replyMessage(jrole, 5, MsgID.MsgID_Item_Do_Resp, "帮派等级过低，不能使用该物品！")
		return
	elseif gang_level > self.level then
		replyMessage(jrole, 6, MsgID.MsgID_Item_Do_Resp, "帮派等级过高，不能使用该物品！")
		return
	elseif gang:getTribute() < (self.tribute or 0) then
		replyMessage(jrole, 7, MsgID.MsgID_Item_Do_Resp, "总帮贡不足，不能使用该物品！")
		return
	end
	
	gang:resetLevel(gang_level + 1)
	_RoleSetGold(jrole, gold - self.gold)
	
	--条件验证
	---------------------------------------------------------------------------
	--返回提示
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name .. "/当前帮派等级：" .. (gang_level + 1))
	--返回提示
	---------------------------------------------------------------------------
	--同步物品与背包属性
	--log.item(_RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	
	log.item(_RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	
	prepareBody()
	
	fillBagDel(_ItemGetUid(item))
	
	Bag:delItem(jrole, _ItemGetUid(item), 1)
	
	fillAttributes(jrole)
	
	putShort(0)
	
	sendMsg(jrole)
	--同步物品与背包属性
	---------------------------------------------------------------------------
end

function _GangProp:getDescribe()
	if not  self.__describe then
		local des = { }
		des[#des + 1] = self.name
		for _, v in ipairs(self.feature or { }) do
			if _ItemFeatureIsShow(v) then
				tbl[#tbl + 1] = _ItemFeatureGetName(v)
			end
		end
		
		des[#des + 1] = self.describe
		des[#des + 1] = "使用等级：" .. (self.level or 1)
		if self.gold then
			des[#des + 1] = "消耗金币：" .. getValueDescribe(self.gold)
		end
		
		if self.charm then
			des[#des + 1] = "需要声望：" .. self.charm
		end
		
		if self.charm then
			des[#des + 1] = "帮派总贡献：" .. self.tribute
		end
	
		self.__describe = table.concat(des, "/")
	end
	
	return self.__describe
end

function _GangProp:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end

