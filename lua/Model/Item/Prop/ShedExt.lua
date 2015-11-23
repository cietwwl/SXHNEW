
_ShedExt = _Prop:new()

function _ShedExt:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _ShedExt:canUse(jrole)
	return true
end

function _ShedExt:useItem(jrole, item)
	---------------------------------------------------------------------------
	--条件验证
	local role_store = _RoleGetStore(jrole)
	if not role_store:canExtShed() then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "仓库已经达到扩充上限，不能再扩充！")
		return
	end
	--条件验证
	---------------------------------------------------------------------------
	--扩展背包
	--log.item("[" .. getUTF8(jrole:getNick()) .. "][" .. jrole:getRoleid() .. "][使用物品][" .. self.name .. "][1个]")
	--log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	role_store:extShed(self.ext)
	--扩展背包
	---------------------------------------------------------------------------
	--返回提示
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name .. "/仓库扩充成功！")
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

function _ShedExt:getDescribe()
	if not self.__describe then
		local tbl = { }
		tbl[#tbl + 1] = self.name
		for _, v in ipairs(self.feature or { }) do
			if _ItemFeatureIsShow(v) then
				tbl[#tbl + 1] = _ItemFeatureGetName(v)
			end
		end
		tbl[#tbl + 1] = self.describe
		
		tbl[#tbl + 1] =  "使用等级：" .. (self.level or 1) .. "/扩充仓库+" .. self.ext .. "容量"
		
		self.__describe = table.concat(tbl, "/")
	end
	
	return self.__describe
end

function _ShedExt:getUseMenu()
	return	{
				{ 0, "使用", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end
					
_ShedExt.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}

function _ShedExt:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end
