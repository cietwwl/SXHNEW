
_Equip = {  }

function _Equip:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Equip:getRawValue()
	if self.buyValue then
		return self.buyValue
	end
	
	local subtype = self.subtype
	
	if _ObjectEquals(subtype, EquipConst.Weapon)
		or _ObjectEquals(subtype, EquipConst.Ring)
		or _ObjectEquals(subtype, EquipConst.Necklace)
		or _ObjectEquals(subtype, EquipConst.Trinket) then
		self.buyValue = (self.level + 5) * (self.serials + 3) * (_EnumOrdinal(self.color) + 1) * 6.5 + 5
	elseif _ObjectEquals(subtype, EquipConst.Cuirass) then
		self.buyValue = (self.level + 5) * (self.serials + 3) * (_EnumOrdinal(self.color) + 1) * 4.5 + 5
	else
		self.buyValue = (self.level + 5) * (self.serials + 3) * (_EnumOrdinal(self.color) + 1) * 3.5 + 5
	end
	
	return self.buyValue
end

function _Equip:getSellValue()
	return self.sellValue or self:getRawValue() / 5
end

function _Equip:isDedicated()
	return _EquipTypeIsExclusive(self.subtype)
end
		
_Equip.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}

function _Equip:isUnique()
	return true
end

_Equip.MailFee = { }
_Equip.MailFee[0] = 5
_Equip.MailFee[1] = 30
_Equip.MailFee[2] = 30
_Equip.MailFee[3] = 2000
function _Equip:getMailFee(quality)
	return _Equip.MailFee[quality] or _Equip.MailFee[table.maxn(_Equip.MailFee)]
end

local punch = {
	rand = random.nonempty,
	
	{ rand = 60, 0, },
	{ rand = 20, 1, },
	{ rand = 15, 2, },
	{ rand = 5, 3, },
}
	
-----创建装备，若参数不为空则指定为制作者
function _Equip:creatJavaItem(num, jrole)
	num = num or 1
	
	local container = { }
	
	for i = 1, num do
		local equip = luajava.new(JEquip)
		_UniqueItemSetUid(equip, UID:next())
		_ItemSetTid(equip, self.id)
		
		self:resetFeature(equip)
		
		_EquipSetType(equip, self.subtype)
		_EquipSetQuality(equip, self.color)
		
		self:resetBasicAttr(equip)
		
		self:resetAdditiveAttr(equip)
		
		if _EquipTypeCanPunch(self.subtype) then
			_EquipSetMountLimit(equip, self.punch or punch:rand()[1])
		end
		
		if jrole then
			_EquipSetProducer(equip, jrole)
		end
		
		if self.star then
			_EquipSetStar(equip, self.star)
		end
		
		if self.timeLen then
			_UniqueItemSetExpire(equip, _GetMinute() + self.timeLen * 60)
		end
	
		container[#container + 1] = equip
	end
	
	return container
end

function _Equip:resetBasicAttr(equip)
	local basicAttr = _EquipGetBasicAttr(equip)
	_ClearCollection(basicAttr)
	for _, v in ipairs(self.attr) do
		_ListAdd(basicAttr, luajava.new(Bonus, v.type, v.calc_mode or CalcMode.Add, v.value))
	end
	_AListTrimToSize(basicAttr)
end

function _Equip:resetAdditiveAttr(equip)
	local additiveAttr = _EquipGetAdditiveAttr(equip)
	_ClearCollection(additiveAttr)
	
	local quality = _EnumOrdinal(self.color)
	if quality > 0 then--非白装
		if self.ext.specified then--特殊指定附加属性的装备
			for _, v in ipairs(self.ext) do
				_ListAdd(additiveAttr, luajava.new(Bonus, v.type, v.calc_mode or CalcMode.Add, v.value))
			end
		else--随机属性的附加装备
			for i = 1, quality do
				local data_group = self.ext:rand()
				_ListAdd(additiveAttr, luajava.new(Bonus, data_group.type, data_group.calc_mode or CalcMode.Add, data_group.value))
			end
		end
	end
	_AListTrimToSize(additiveAttr)
end

function _Equip:resetFeature(equip)
	if self.feature then
		_ItemSetFeatures(equip, unpack(self.feature))
	end
end

function _Equip:creatJavaItemSingle()
	return self:creatJavaItem()[1]
end

function _Equip:fillAdd(wearable, serial)
	putShort(serial)--serial
	
	putInt(_EnumOrdinal(self.subtype))
	
	putLong(_ItemGetUid(wearable))--装备id
	putShort(1)--数量
	putShort(1)--堆叠数量
	putInt(_ItemGetFeatures(wearable))--物品属性
	
	_PutItemNameAddColor(wearable)
end

function _Equip:fillInuseAdd(wearable)
	putShort(200)--serial
	
	putByte(_EnumOrdinal(self.subtype))
	
	putLong(_ItemGetUid(wearable))--装备id
	putInt(_ItemGetFeatures(wearable))--物品属性
	
	_PutItemNameAddColor(wearable)
end

function _Equip:fillBagAdd(wearable)
	putShort(205)--serial
	
	putLong(_ItemGetUid(wearable))--装备id
	putShort(1)--数量
	putShort(1)--堆叠数量
	putInt(_ItemGetFeatures(wearable))--物品属性
	
	_PutItemNameAddColor(wearable)
	
	putByte(_EnumOrdinal(self.subtype))
end

function _Equip:wearItem(jrole, jitem)

end

function _Equip:onObtain(jrole, equip)
end

function _Equip:getDescribe()
	local tbl = { }
	
	if _EquipTypeCanPunch(self.subtype) then
		if self.punch then
			tbl[#tbl + 1] = self.name .. "（" .. self.punch .. "孔）"
		end
	end
	
	if #tbl == 0 then
		tbl[#tbl + 1] = self.name
	end
	
	if self.star then
		local starTbl = { }
		for i = 1, 10 do
			if i <= self.star then
				starTbl[#starTbl + 1] = "★"
			else
				starTbl[#starTbl + 1] = "☆"
			end
		end
		
		tbl[#tbl + 1] = table.concat(starTbl)
	end
	
	tbl[#tbl + 1] = _EquipTypeGetName(self.subtype)
	
	if self.timeLen then
		tbl[#tbl + 1] = "限时：" .. self.timeLen .. "小时"
	end

	for _, v in ipairs(self.feature or { }) do
		if _ItemFeatureIsShow(v) then
			tbl[#tbl + 1] = _ItemFeatureGetName(v)
		end
	end

	tbl[#tbl + 1] = self.describe
	
	for _, v in ipairs(self.attr) do
		tbl[#tbl + 1] = _GetBonusString(v.type, v.calc_mode or CalcMode.Add, v.value)
	end
	
	if _EquipTypeIsExclusive(self.subtype) then
		tbl[#tbl + 1] = "职业限制    " .. _ObjectToString(self.vocation)
	end
	
	tbl[#tbl + 1] = "等级限制    " .. (self.level or 1)
	
	local quality = _EnumOrdinal(self.color)
	if quality > 0 then--非白装
		if self.ext.specified then--特殊指定附加属性的装备
			for _, v in ipairs(self.ext) do
				tbl[#tbl + 1] = _GetBonusString(v.typ, v.calc_mode or CalcMode.Add, v.value)
			end
		else--随机属性的附加装备
			for i = 1, quality do
				tbl[#tbl + 1] = "（随机属性）"
			end
		end
	end
	
	tbl[#tbl + 1] = " "
	
	tbl[#tbl + 1] = "价        格    " .. getValueDescribe(self:getRawValue())
	
	return table.concat(tbl, "/")
end

local ComponentRate = {
	[_EnumName(EquipType.Weapon)] = 0.01,
	[_EnumName(EquipType.Helmet)] = 0.04,
	[_EnumName(EquipType.Cuirass)] = 0.02,
	[_EnumName(EquipType.Cuish)] = 0.03,
	[_EnumName(EquipType.Shoes)] = 0.05,
	[_EnumName(EquipType.Gloves)] = 0.06,
	[_EnumName(EquipType.Belt)] = 0.07,
}

local QualityRate = {
	[_EnumName(EquipQuality.White)] = 0.26,
	[_EnumName(EquipQuality.Green)] = 0.24,
	[_EnumName(EquipQuality.Blue)] = 0.23,
	[_EnumName(EquipQuality.Purple)] = 0.18,
	[_EnumName(EquipQuality.Gold)] = 0.18,
}

function _Equip:calcRefine(equip)
	local star = _EquipGetStar(equip) + 1
	
	local component_rate = ComponentRate[_EnumName(self.subtype)]
	local quality_rate = QualityRate[_EnumName(self.color)]
	
	local necessary_expend = ((star+10)*((1-component_rate)*(1-quality_rate))*self.serials^(1/3))^0.9*0.3
	local success_rate = (1-((1/100)*self.serials)^1/3)*(component_rate+quality_rate)*((((((star+8)^3)+8)-2)/10)^(-((1.9^3)/10))*56)
	local cost_gold = (((star/component_rate/quality_rate)*80)^0.95)/10*(self.serials*(80^0.25)/1000)*0.002 * 10000
	local growth = ((0.01*self.serials)^0.35)*(1/(component_rate+quality_rate))*(star+1*(0.01^0.01))*0.02
	
	_RefineSetProperty(necessary_expend, success_rate, cost_gold, growth)
end

--分解碎片数量
function _Equip:calcDecomposeFragment(equip)
	return self.timeLen and 5 or 
			math.round(math.floor(((self.level or 1)^0.235)-1)*(((self.level or 1)^0.87)/10)+(_EnumOrdinal(self.color)*(self.level or 1))^0.9*0.3)
end

--分解成升星必需品数量
function _Equip:calcDecomposeNecessary(equip)
	return self.timeLen and 2 or math.floor(((self.level or 1)^0.45))+_EnumOrdinal(self.color)*3+1
end

local QualityAuctionFee = {
	[_EnumName(EquipQuality.White)] = { 10, 18, 32, 32, },
	[_EnumName(EquipQuality.Green)] = { 30, 54, 96, 96, },
	[_EnumName(EquipQuality.Blue)] = { 500, 900, 1600, 1600, },
	[_EnumName(EquipQuality.Purple)] = { 1000, 1800, 3200, 3200, },
	[_EnumName(EquipQuality.Gold)] = { 1800, 3240, 5760, 5760, },
}

function _Equip:getAuctionFee(halfDays)
	return QualityAuctionFee[_EnumName(self.color)][halfDays]
end
