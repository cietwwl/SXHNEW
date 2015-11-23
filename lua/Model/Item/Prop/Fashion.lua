
_Fashion = _Prop:new()

function _Fashion:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Fashion:canUse(jrole)
	if not _ObjectEquals(self.vocation, _RoleGetVocation(jrole)) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "职业不符，无法使用该物品")
		return
	end
	
	if self.sex ~= _RoleGetSex(jrole) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "性别不符，无法使用该物品")
		return
	end
	
	if _GetRoleLevel(jrole) < (self.level or 1) then
		replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp, "级别过低不能使用！")
		return
	end

	if _RoleGetAnimeGroup(jrole) == self.dest_anime_group then
		replyMessage(jrole, 4, MsgID.MsgID_Item_Do_Resp, "使用失败，与当前效果相同！")
		return
	end
	
	return true
end

function _Fashion:useItem(jrole, item)
	---------------------------------------------------------------------------
	--返回使用物品提示
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name)
	--返回使用物品提示
	---------------------------------------------------------------------------
	--同步数据
	prepareBody()
	
	fillBagDel(item:getUid())
	
	Bag:delItem(jrole, item:getUid(), 1)
	
	--log.item("[" .. getUTF8(jrole:getNick()) .. "][" .. jrole:getRoleid() .. "][使用物品][" .. self.name .. "][1个]")


	--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	
	log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	_RoleSetAnimeGroup(jrole, self.dest_anime_group)
	
	fillFashion(self.dest_anime_group)

	putShort(0)
	
	sendMsg(jrole)
	--同步数据
	---------------------------------------------------------------------------
end

function _Fashion:getUseMenu()
	return	{
				{ 0, "使用", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end

_Fashion.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}

function _Fashion:mall_show()
	putShort(901)
	putShort(self.dest_anime_group)
	putString(self.name)
	putByte(self.sex)
	putByte(_EnumOrdinal(self.vocation))
end

function _Fashion:getItemInfo(jrole, jitem, storage)
	prepareBody()
	
	putShort(0)
	
	putShort(901)--子模块
	putShort(self.dest_anime_group)
	putString(self.name)
	putByte(self.sex)
	putByte(_EnumOrdinal(self.vocation))
	
	local describe = self:getDescribe(jitem)
	putString(self.name .. "/" .. describe)
	putInt(getRGB(self.color))
	
	local regular = table.getNoMoreThan({1, 2, 5, 10, 20, 50, 100, }, jitem:getStorage())
	putByte(#regular)
	for _, v in ipairs(regular) do
		putInt(v)
	end
	
	local useMenu = self:getUseMenu()

	putByte(#useMenu)
	for _, v in ipairs(useMenu) do
		putByte(v[1])
		putString(v[2])
		putInt(v[3])
	end
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Fashion_Info_Resp)
end

function _Fashion:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end
