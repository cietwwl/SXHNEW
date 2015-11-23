
_CharmProp = _Prop:new()

function _CharmProp:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _CharmProp:canUse(jrole)
	local role_level = _GetRoleLevel(jrole)
	if role_level < (self.minLevel or 0) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "级别过低不能使用！")
		return
	end
	
	if role_level > (self.maxLevel or 100) then
		replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp, "级别过高不能使用！")
		return
	end
	
	local charm = _RoleGetCharm(jrole)
	if charm == 99999999 then
		replyMessage(jrole, 4, MsgID.MsgID_Item_Do_Resp, "声望已经达到上限，不能使用该物品！")
		return
	end
	
	return true
end

function _CharmProp:useItem(jrole, item)
	local charm = _RoleGetCharm(jrole)
	---------------------------------------------------------------------------
	--修改魅力值
	--log.item("[" .. getUTF8(jrole:getNick()) .. "][" .. jrole:getRoleid() .. "][使用物品][" .. self.name .. "][1个]")
	--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	_RoleSetCharm(jrole, charm + self.value)
	--修改魅力值
	---------------------------------------------------------------------------
	--返回提示
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name .. "/当前声望：" .. _RoleGetCharm(jrole))
	--返回提示
	---------------------------------------------------------------------------
	--同步物品与背包属性
	prepareBody()
	
	fillBagDel(item:getUid())
	
	Bag:delItem(jrole, item:getUid(), 1)
	
	putShort(0)
	
	sendMsg(jrole)
	--同步物品与背包属性
	---------------------------------------------------------------------------
end

function _CharmProp:getDescribe()
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
		tbl[#tbl + 1] =  "声望+" .. self.value
		
		self.__describe = table.concat(tbl, "/")
	end

	return self.__describe
end

function _CharmProp:getUseMenu()
	return	{
				{ 0, "使用", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end
					
_CharmProp.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}
