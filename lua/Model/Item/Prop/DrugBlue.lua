
_DrugBlue = _Prop:new()

function _DrugBlue:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _DrugBlue:canUse(jrole)
	if (self.level or 0) > _GetRoleLevel(jrole) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "级别过低不能使用！")
		return
	end
	
	return true
end

function _DrugBlue:useItem(jrole, item)
	---------------------------------------------------------------------------
	--返回提示
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name.."恢复了"..self.value .."点魔法值")
	--返回提示
	---------------------------------------------------------------------------
	--加魔
	local mp = _RoleGetMP(jrole) + self.value
	local maxMp = _RoleGetMaxMP(jrole)
	_RoleSetMP(jrole, mp > maxMp and maxMp or mp)
	--加魔
	---------------------------------------------------------------------------
	--同步物品与魔法
	--log.item("[" .. getUTF8(jrole:getNick()) .. "][" .. jrole:getRoleid() .. "][使用物品][" .. self.name .. "][1个]")

	--log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	prepareBody()
	
	fillBagDel(item:getUid())
	
	Bag:delItem(jrole, item:getUid(), 1)
	
	fillAttributes(jrole)
	
	putShort(0)
	
	sendMsg(jrole)
	--同步物品与魔法
	---------------------------------------------------------------------------
end

function _DrugBlue:getDescribe()
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
		
		tbl[#tbl + 1] =  "魔法+" .. self.value
		tbl[#tbl + 1] =  "恢复魔法值"
		
		self.__describe = table.concat(tbl, "/")
	end

	return self.__describe
end

function _DrugBlue:getUseMenu()
	return	{
				{ 0, "使用", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end
					
_DrugBlue.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}

function _DrugBlue:fillBagAdd(jItem)
	putShort(204)--Serial
	
	putLong(self.id)--id
	putShort(_ItemGetStorage(jItem))--数量
	putShort(self.overlay or 20)--堆叠数量
	putInt(_ItemGetFeatures(jItem))--物品属性
	
	putString(self.name)
	putInt(self.color or 0)
	
	putInt(0)
	putInt(self.value)
end

function _DrugBlue:getAuctionCategory()
	return 2 ^ _EnumOrdinal(AutionType.FunctionalProps) + 2 ^ _EnumOrdinal(AutionType.Medicines)
end
