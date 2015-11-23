
Actions.buyjinyaoshi = {
	name = 'buyjinyaoshi',
	--金宝箱物品储存表
	boxId = 5124,
	--金钥匙
	boxKey = 31094,
	--金钥匙的数量
	keyNum = 0;
}

function Actions.buyjinyaoshi:doAction(roleid, args)
	--获得角色对象
	local jrole = _GetOnline(roleid)
	
	--判断角色是否在线
	if jrole then
		--要购买的金钥匙的数量
		self.keyNum = args.jinyaoshinumber
		--角色现有元宝数目	
		local roleYuanbao = _RoleGetMoney(jrole)	
	
		if Bag:remainSpace(jrole)<math.ceil((self.keyNum-0)/20) then
			self:error(jrole, "背包空间不足,请先清理背包", "everydayAward")
			return
		end
		if (self.keyNum-0)<=0 then
			self:commonMessage(jrole, "钥匙数必须大于0", "everydayAward")
			return
		end
		--扣除玩家花费的元宝数量
			local gameSubtractInfo  = luajava.new(GameSubtract, _RoleGetUserId(jrole), _RoleGetJoyId(jrole), (self.keyNum)*45-0, 0, 0, roleYuanbao, 0, yuanBaoOpConst.BUY_JINYAOSHI)
			PayManager:postSubTask(_RoleGetYuanBaoOp(jrole), gameSubtractInfo)
		
	end			
end
function Actions.buyjinyaoshi:jinyaoshikouchuyuanbao(jrole)
	   --背包增加金钥匙
	   local itemsTbl = ItemSet[31094]:creatJavaItem((self.keyNum - 0))
	   
		
		self:commonMessage(jrole, "恭喜您！得到了"..self.keyNum.."把金钥匙", "everydayAward")
		if (self.keyNum-0) > 0 then
			Bag:addJItem(jrole, itemsTbl)
			
			prepareBody()
			for _, v in ipairs(itemsTbl) do
				
				fillBagAdd(v)
			end
	
			fillAttributes(jrole)
			putShort(0)
		 
		end 
		sendMsg(jrole)
end

function Actions.buyjinyaoshi:YuanbaoSyntony(jrole, isSuccess)

	if isSuccess then
		self:jinyaoshikouchuyuanbao(jrole)
		-----------------------------------------------------------------------------------
		
	else	
		self:error(jrole, "很抱歉，您的元宝余额不足。", "everydayAward")
	end
end