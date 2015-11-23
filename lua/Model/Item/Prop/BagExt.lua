
_BagExt = _Prop:new()

function _BagExt:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _BagExt:canUse(jrole)
	local role_bag = Bag:getJPack(jrole)
	if not role_bag:canExtBag() then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "背包已经达到上限，不能再扩展！")
		return
	end
	
	return true
end

function _BagExt:useItem(jrole, item)
	local role_bag = Bag:getJPack(jrole)
	---------------------------------------------------------------------------
	--扩展背包
	--log.item("[" .. getUTF8(jrole:getNick()) .. "][" .. jrole:getRoleid() .. "][使用物品][" .. self.name .. "][1个]")
	--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	role_bag:extBag(self.ext)
	--扩展背包
	---------------------------------------------------------------------------
	--返回提示
	local capacity = role_bag:getCapacity()
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name .. "/背包容量扩展为：" .. capacity)
	--返回提示
	---------------------------------------------------------------------------
	--同步物品与背包属性
	prepareBody()
	
	fillBagDel(item:getUid())
	
	Bag:delItem(jrole, item:getUid(), 1)
	
	fillAttributes(jrole)
	
	putShort(0)
	
	sendMsg(jrole)
	--同步物品与背包属性
	---------------------------------------------------------------------------
end

function _BagExt:getDescribe()
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
		tbl[#tbl + 1] =  "背包容量+" .. self.ext
		
		self.__describe = table.concat(tbl, "/")
	end
	
	return self.__describe
end

function _BagExt:getUseMenu()
	return	{
				{ 0, "使用", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end
					
_BagExt.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}

function _BagExt:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end
