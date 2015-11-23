
_Buff = _Prop:new()

BuffConst = {
	EXP = 1,
	GOLD = 2,
	ATK = 3,
	AFTER_BATTLE_HP = 4,
	VIP = 5,
	OFFLINE_EXP = 6,
	AFTER_BATTLE_MP = 7,
	DFC = 8,
	MARK = 9,
	
	"经验",
	"金钱",
	"攻击",
	"血罐",
	"VIP",
	"离线经验",
	"内力罐",
	"防御",
	"积分",
}

BuffEffectConst = {
	MULTIPLE = 1,
	GAIN = 2,
}

function _Buff:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Buff:canUse(jrole)
	if (self.level or 0) > _GetRoleLevel(jrole) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "级别过低不能使用！")
		return
	end
	
	if not _CanAddBuff(jrole, self.specificType, self.buffLevel) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "有更高级别的法术在发挥作用！")
		return
	end
	
	return true
end

function _Buff:useItem(jrole, item)
	---------------------------------------------------------------------------
	--返回提示
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已使用" .. self.name)
	--返回提示
	---------------------------------------------------------------------------
	--加Buff
	
	local buff = _CreatBuff(self.specificType)
	_BuffSetAttr(buff, self.buffLevel, self.overduetime, self.effect or 0, self.effectValue or 0)
	_RoleAddBuff(jrole, buff)
	
	--加Buff
	---------------------------------------------------------------------------
	--同步物品与血量
	--log.item("[" .. getUTF8(jrole:getNick()) .. "][" .. jrole:getRoleid() .. "][使用物品][" .. self.name .. "][1个]")
	--log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(item:getUid()))
	prepareBody()
	
	fillBagDel(_ItemGetUid(item))
	
	Bag:delItem(jrole, _ItemGetUid(item), 1)
	
	putShort(0)
	
	sendMsg(jrole)
	--同步物品与血量
	---------------------------------------------------------------------------
end

function _Buff:getUseMenu()
	return	{
				{ 0, "使用", 0, },
				{ 3, "丢弃", 0, },
				{ 4, "返回", 0, },
			}
end
					
_Buff.sellMenu = {
	{ 1, "出售", 0, },
	{ 2, "返回", 0, },
}

function _Buff:getAuctionCategory()
	return 2 ^ _EnumOrdinal(AutionType.FunctionalProps) + 2 ^ _EnumOrdinal(AutionType.Buff)
end
