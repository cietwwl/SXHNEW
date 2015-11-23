
_SkillBook = _Prop:new()

function _SkillBook:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _SkillBook:canUse(jrole)
	return true
end

function _SkillBook:isDedicated()
	return true
end

function _SkillBook:useItem(jrole, item)
	---------------------------------------------------------------------------
	--条件验证
	if(not _ObjectEquals(_RoleGetVocation(jrole), self.vocation)) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "此技能书不适合你学习")
		return
	elseif self.level > _GetRoleLevel(jrole) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "级别过低不能使用！")
		return
	elseif _RoleGetSkillLevel(jrole, self.skillId) == 0 and self.skillLv > 1 then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "你必须先学会此技能才能升级")
		return
	elseif _RoleGetSkillLevel(jrole, self.skillId) > 0 and self.skillLv - _RoleGetSkillLevel(jrole, self.skillId) > 1 then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "你必须先学会低级技能")
		return
	elseif _RoleGetSkillLevel(jrole, self.skillId) and self.skillLv <= _RoleGetSkillLevel(jrole, self.skillId) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "你已经学会了此技能")
		return
	elseif(_RoleGetExp(jrole) < self.requireExp) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "你没有足够的经验来学习此技能")
		return
	end
	--条件验证
	---------------------------------------------------------------------------
	--返回提示
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name)
	--返回提示
	---------------------------------------------------------------------------
	--同步数据
	prepareBody()
	
	fillBagDel(item:getUid())

	--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	Bag:delItem(jrole, item:getUid(), 1)
	
	local exp = _RoleGetExp(jrole) - self.requireExp
	_RoleSetExp(jrole, exp)
	
	fillAttributes(jrole)
	
	_RoleAddSkill(jrole, self.skillId, self.skillLv)

	fillSkill(self.skillId, self.skillLv)
	
	putShort(0)
	
	sendMsg(jrole)
	--同步数据
	---------------------------------------------------------------------------
end

function _SkillBook:getDescribe()
	if not self.__describe then
		local skill = SkillSet[self.skillId]
		
		local tbl = { }
		tbl[#tbl + 1] = self.name
		for _, v in ipairs(self.feature or { }) do
			if _ItemFeatureIsShow(v) then
				tbl[#tbl + 1] = _ItemFeatureGetName(v)
			end
		end
		
		tbl[#tbl + 1] = self.describe
		
		tbl[#tbl + 1] = _ObjectToString(self.vocation)
		
		tbl[#tbl + 1] = "使用等级" .. (self.level or 1)
		
		tbl[#tbl + 1] = "消耗经验" .. self.requireExp
		
		--tbl[#tbl + 1] = "造成伤害" .. skill.value[self.skillLv]
		
		self.__describe = table.concat(tbl, "/")
	end
	
	return self.__describe
end

function _SkillBook:getUseMenu()
	return	{
				{ 0, "学习", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end
		
_SkillBook.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}

function _SkillBook:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end
