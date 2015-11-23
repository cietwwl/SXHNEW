Actions.goldFastRefreshDeductExpenses = {
	name = 'goldFastRefreshDeductExpenses',
	--快速刷新要扣除的元宝
	deductYuanbao = 5 
}

function Actions.goldFastRefreshDeductExpenses:doAction(roleid, args)
	local jrole = _GetOnline(roleid)
	if jrole then
		
		--扣除元宝
		local gameSubtractInfo = luajava.new(GameSubtract, _RoleGetUserId(jrole), _RoleGetJoyId(jrole), self.deductYuanbao, 0, 0, self.deductYuanbao, 0, yuanBaoOpConst.FASTREFRESH_GOLD_BOX) 
		PayManager:postSubTask(_RoleGetYuanBaoOp(jrole), gameSubtractInfo)
	end
end