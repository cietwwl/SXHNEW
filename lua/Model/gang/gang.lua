
_Gang = { }

function _Gang:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Gang:getAdditiveDes()
	local info = { }
	info[#info + 1] = ""
	info[#info + 1] = ""
	
	for k, v in pairs(self.benefit or { }) do
		info[#info + 1] = BenefitDes[k] .. "  " .. math.floor(v * 100) .. "%"
	end
	
	return #info > 2 and table.concat(info, "/") or ""
end

function _Gang:belowRequired(jrole)
	if self.charm then
		if _RoleGetCharm(jrole) < self.charm then
			return "尚未达到" .. self.charm .. "声望！"
		end
	end
	
	if self.tribute then
		if _GangGetTributeByRole(jrole) < self.tribute then
			return "尚未达到" .. self.charm .. "帮贡！"
		end
	end
end

function _Gang:chargeback(jrole)
	if self.gold then
	    local oldgold=_RoleGetGold(jrole)
		_RoleSetGold(jrole, _RoleGetGold(jrole) - self.gold)
		local nowgold=_RoleGetGold(jrole)
	    local difgold=oldgold-nowgold
	    log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$21#$帮会升级消耗所有物品#$帮会升级消耗个数#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$" .. difgold .. "")
	end
end
