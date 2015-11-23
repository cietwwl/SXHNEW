
_DrugRed = _Prop:new()

function _DrugRed:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _DrugRed:canUse(jrole)
	if (self.level or 0) > _GetRoleLevel(jrole) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "级别过低不能使用！")
		return
	end
	
	return true
end

function _DrugRed:useItem(jrole, item)
	---------------------------------------------------------------------------
	--返回提示
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name.."恢复了"..self.value.."点生命值")
	--返回提示
	---------------------------------------------------------------------------
	--加血
	local hp = _RoleGetHP(jrole) + self.value
	local maxHp = _RoleGetMaxHP(jrole)
	_RoleSetHP(jrole, hp > maxHp and maxHp or hp)
	--加血
	---------------------------------------------------------------------------
	--同步物品与血量
	--log.item("[" .. getUTF8(jrole:getNick()) .. "][" .. jrole:getRoleid() .. "][使用物品][" .. self.name .. "][1个]")

	--log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	prepareBody()
	
	fillBagDel(item:getUid())
	
	Bag:delItem(jrole, item:getUid(), 1)
	
	fillAttributes(jrole)
	
	putShort(0)
	
	sendMsg(jrole)
	--同步物品与血量
	---------------------------------------------------------------------------
end

function _DrugRed:getDescribe()
	if not self.__describe then
		local tbl = { }
		tbl[#tbl + 1] = self.name
		for _, v in ipairs(self.feature or { }) do
			if _ItemFeatureIsShow(v) then
				tbl[#tbl + 1] = _ItemFeatureGetName(v)
			end
		end
		
		tbl[#tbl + 1] = "使用等级  " .. (self.level or 1)
		
		tbl[#tbl + 1] = self.describe
		
		tbl[#tbl + 1] =  "生命+" .. self.value
		tbl[#tbl + 1] =  "恢复生命值"
		
		self.__describe = table.concat(tbl, "/")
	end
	
	return self.__describe
end

function _DrugRed:getUseMenu()
	return	{
				{ 0, "使用", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end
		
_DrugRed.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}

function _DrugRed:fillBagAdd(jItem)
	putShort(204)--Serial
	
	putLong(self.id)--id
	putShort(_ItemGetStorage(jItem))--数量
	putShort(self.overlay or 20)--堆叠数量
	putInt(_ItemGetFeatures(jItem))--物品属性
	
	putString(self.name)
	putInt(self.color or 0)
	
	putInt(self.value)
	putInt(0)
end

function _DrugRed:getAuctionCategory()
	return 2 ^ _EnumOrdinal(AutionType.FunctionalProps) + 2 ^ _EnumOrdinal(AutionType.Medicines)
end
