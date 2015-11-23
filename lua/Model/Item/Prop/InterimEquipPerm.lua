
_InterimEquipPerm = _Prop:new()

function _InterimEquipPerm:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

_InterimEquipPerm.ComponentRate = {
	[_EnumName(EquipType.Weapon)] = 0.255,
	[_EnumName(EquipType.Helmet)] = 0.124,
	[_EnumName(EquipType.Cuirass)] = 0.248,
	[_EnumName(EquipType.Cuish)] = 0.149,
	[_EnumName(EquipType.Shoes)] = 0.099,
	[_EnumName(EquipType.Gloves)] = 0.058,
	[_EnumName(EquipType.Belt)] = 0.066,
}

_InterimEquipPerm.QualityRate = {
	[_EnumName(EquipQuality.Blue)] = 3,
	[_EnumName(EquipQuality.Purple)] = 4,
	[_EnumName(EquipQuality.Gold)] = 5,
}

function _InterimEquipPerm:canUse(jrole)
	local item = InusePack:getItemInPack(jrole, self.perpetuate)
	if not item then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "尚未使用" .. _EquipTypeGetName(self.perpetuate) .. "!")
		return
	end
	
	if not item:monitored() then
		replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp, 
					"您使用的" .. _EquipTypeGetName(self.perpetuate) .. "并非限时装备!")
		return
	end
	
	return true
end

function _InterimEquipPerm:useItem(jrole, item)
	local equip = InusePack:getItemInPack(jrole, self.perpetuate)
	
	local tid = _ItemGetTid(equip)
	local tempalte = ItemSet[tid]
	local quality = self.QualityRate[_EnumName(_EquipGetQuality(equip))]
	print(_EquipGetQuality(equip) : getColorCode())
	print(_EnumName(_EquipGetQuality(equip)))
	local cost = (tempalte.serials ^ 2.3) * (quality*40)*(self.ComponentRate[_EnumName(tempalte.subtype)]*1)+100000
	
	local role_gold = _RoleGetGold(jrole)
	if role_gold < cost then
		replyMessage(jrole, 4, MsgID.MsgID_Item_Do_Resp, 
					"资金不足，该装备需要消耗" .. getValueDescribe(cost) .. "!")
		return
	end
	
	_RoleSetGold(jrole, role_gold - cost)
	
	equip:perpetuate()
	
	InusePack:getJPack(jrole):removeMonitor(equip)
	
	replyMessage(jrole, 0, MsgID.MsgID_Item_Do_Resp, 
				"消耗" .. getValueDescribe(cost) .. "/"
				.. _EquipTypeGetName(self.perpetuate) .. _ItemGetName(equip) .. "已升级为永久装备～")
	
	local uid = _ItemGetUid(item)
	
	prepareBody()
	
	fillAttributes(jrole)
	
	fillBagDel(uid)
	
	log.item(_RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. 
			"#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$"
			.._Long2String(item:getTid()).."#$".._Long2String(uid))
	
	Bag:delItem(jrole, uid, 1)
	
	putShort(0)
	
	sendMsg(jrole)
end

function _InterimEquipPerm:getUseMenu()
	return	{
				{ 0, "使用", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end
		
_InterimEquipPerm.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}

function _InterimEquipPerm:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end
