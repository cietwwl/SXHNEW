
_MagicWeaponRST = _Prop:new()

function _MagicWeaponRST:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

_MagicWeaponRST.range = { }

for i = 10031, 10120 do
	_MagicWeaponRST.range[i] = true
end

_MagicWeaponRST.range[10134] = true
_MagicWeaponRST.range[10144] = true
_MagicWeaponRST.range[10154] = true

function _MagicWeaponRST:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _MagicWeaponRST:canUse(jrole)
	return true
end

function _MagicWeaponRST:useItem(jrole, item)
	---------------------------------------------------------------------------
	--条件验证
	if (self.level or 0) > _GetRoleLevel(jrole) then
		replyMessage(jrole, 2, MsgID.MsgID_Item_Do_Resp, "级别过低不能使用！")
		return
	end
	
	local weapon = InusePack:getItemInPack(jrole, EquipConst.Weapon)
	
	if not weapon then
		replyMessage(jrole, 3, MsgID.MsgID_Item_Do_Resp, "请将神兵装备后，再进行属性重置。")
		return
	end
	
	local tid = _ItemGetTid(weapon)
	if not ItemSet[tid] or not self.range[tid] then
		replyMessage(jrole, 4, MsgID.MsgID_Item_Do_Resp, "请将神兵装备后，再进行属性重置。")
		return
	end
	
	--条件验证
	---------------------------------------------------------------------------
	--重置属性
	local newAdditiveAttr = getUTF8(weapon:resetAdditiveAttr(CommonPack:getJPack(jrole)))
	--重置属性
	---------------------------------------------------------------------------
	--返回提示
	sendMsg(jrole, MsgID.MsgID_Item_Do_Resp, "已重置神兵附加属性～/" .. newAdditiveAttr)
	--返回提示
	---------------------------------------------------------------------------
	--同步数据
	--log.item(_RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$" .. os.date("%Y-%m-%d %H:%M:%S") .. "#$" .. TianLongServer.srvId)
	
	log.item(_RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$" .. self.name .. "#$" .. 1 .. "#$" .. os.date("%Y-%m-%d %H:%M:%S") .. "#$" .. TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(item:getTid()).."#$".._Long2String(_ItemGetUid(item)))
	prepareBody()
	
	fillBagDel(_ItemGetUid(item))
	
	Bag:delItem(jrole, _ItemGetUid(item), 1)

	fillAttributes(jrole)
	
	putShort(0)
	
	sendMsg(jrole)
	--同步物品与血量
	---------------------------------------------------------------------------
end

function _MagicWeaponRST:getAuctionCategory()
	return self.defaultFunctionalPropsAuctionCategory
end
