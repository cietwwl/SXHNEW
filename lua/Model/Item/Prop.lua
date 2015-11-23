
PropConst = {
	Drug = 1,--药品
	SkillBook = 2, --技能书
	GiftBag = 3,--宝箱
	TPScroll = 4,--回城卷轴
	Trumpet = 5,--喇叭
	RandomBox = 6,--随机礼包
	Coupon = 7,--兑换券
	Grocery = 8,--杂物
	Buff = 9, --buff
	WashPoint = 10, --洗点券
	BagExt = 11,--背包扩展
	Scapegoat = 12,--替身
	ShedAdd = 13,--增加背包
	ShedExt = 14,--扩充背包
	TaskScroll = 15,--任务卷轴
	JoinCommunity = 16,
	CharmProp = 17,
	Epithet = 18,
	Fashion = 19,--时装
	GangProp = 20,
	MagicWeaponRST = 21,--神兵附加属性重置
	WashMurderPunish = 22,--洗红名
	AgainstSneakAttack = 23, --防偷袭
	RefineNecessary = 24,--升星必需品
	EquipProtect = 25,--装备保护
	NecessaryProtect = 26,--必需品保护
	ItemRefineSuccessRate = 27,--升星成功率
	PerpetuateEquip = 28,
	WashSneakAttackNum = 29, --增加偷袭次数
	BossCard = 30, --BOSS卡
	MarryRing = 31, --结婚戒指
}

DrugConst = {
	Red = 1,--加血
	Blue = 2,--加魔
	RedBlue = 3,--双加
}

_Prop = {  }

function _Prop:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Prop:getRawValue()
	return self.buyValue or 100000000
end

function _Prop:getSellValue()
	return self.sellValue or 1
end

function _Prop:getDescribe()
	if not self.__describe then
		local tbl = { }
		tbl[#tbl + 1] = self.name
		for _, v in ipairs(self.feature or { }) do
			if _ItemFeatureIsShow(v) then
				tbl[#tbl + 1] = _ItemFeatureGetName(v)
			end
		end
		tbl[#tbl + 1] = self.describe
		
		self.__describe = table.concat(tbl, "/")
	end
	
	return self.__describe
end

function _Prop:getUseMenu()
	return	{
				{ 0, "使用", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end
			
_Prop.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}

function _Prop:isUnique()
end

function _Prop:isDedicated()
end

function _Prop:getMailFee()
	return 5
end

function _Prop:creatJavaItem(num)
	local prop = luajava.new(JProp, self.id, num or 1)
	self:resetFeature(prop)
	return { prop }
end

function _Prop:creatJavaItemSingle(num)
	return self:creatJavaItem(num)[1]
end

function _Prop:resetFeature(item)
	if self.feature then
		_ItemSetFeatures(item, unpack(self.feature))
	end
end

function _Prop:fillBagAdd(jItem)
	putShort(206)--Serial
	
	putLong(_ItemGetUid(jItem))--id
	putShort(_ItemGetStorage(jItem))--数量
	putShort(self.overlay or 20)--堆叠数量
	putInt(_ItemGetFeatures(jItem))--物品属性
	
	putString(self.name)
	putInt(0)
end

function _Prop:onObtain()

end

function _Prop:canUse(jrole)
	replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "该物品无法使用！")
end

local AuctionFee = { 10, 18, 32, 32, }

function _Prop:getAuctionFee(halfDays)
	return AuctionFee[halfDays]
end

function _Prop:getAuctionCategory()
	return 2 ^ _EnumOrdinal(AutionType.Grocery) + 2 ^ _EnumOrdinal(AutionType.GroceryOther)
end

_Prop.defaultFunctionalPropsAuctionCategory = 
	2 ^ _EnumOrdinal(AutionType.FunctionalProps) + 2 ^ _EnumOrdinal(AutionType.FunctionalPropsOther)
