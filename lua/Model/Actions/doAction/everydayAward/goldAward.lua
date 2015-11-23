
Actions.goldAward = {
	name = 'goldAward',
	--金宝箱物品储存表
	boxId = 5124,
	--金钥匙
	boxKey = 31094,
}

function Actions.goldAward:doAction(roleid, args)

	--获得角色对象
	local jrole = _GetOnline(roleid)
	
	--判断角色是否在线
	if jrole then
		--查看角色剩余金钥匙数量
		local roleBoxKey = _RoleBoxKey(jrole,self.boxKey)
		--如果金钥匙数量大于0，则可以开启金宝箱
		if roleBoxKey == 1 then
			
			--要被打开的宝箱
			local boxToBeOpened = ItemSet[self.boxId]
			
			--随机物品
			local rand_content = boxToBeOpened.itempack:rand()
			
			--随机获得的物品ID
			local itemid = rand_content[1]
			
			--背包增加获得的物品
			local itemsTbl = ItemSet[itemid]:creatJavaItem(1)
			
			--通过邮件发送物品
			--Bag:addJItem(jrole, itemsTbl)
			for _, v in pairs(itemsTbl) do
				_GetBox(jrole,v)
				local tid = _ItemGetTid(v)
			    local uid = _ItemGetUid(v)
			    local num = 1
			    --物品增加日志
				log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$49#$" .. ItemSet[itemid].name .. "#$" .. num .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(tid).."#$".._Long2String(uid))
			end
			
			prepareBody()
			--金宝箱开启成功，扣除一把金钥匙
			fillBagDel(self.boxKey)
			Bag:delItem(jrole, self.boxKey, 1)
			--使用金钥匙日志
			log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$金钥匙#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(31094).."#$".._Long2String(31094))
			fillAttributes(jrole)
			putShort(0)
			sendMsg(jrole)
			
			self:commonMessage(jrole, "恭喜您！开启了金宝箱，请注意查收邮件。", "everydayAward")
		else 
			--self:redirect(jrole, "jinyaoshi")
			self:commonMessage(jrole, "对不起，您背包里的金钥匙不足，请到商城购买。", "everydayAward")
		end
	end
end