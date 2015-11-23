
Actions.silerAward = {
	name = 'silerAward',
	--银宝箱物品储存表
	boxId = 5123,
	--银钥匙
	boxKey = 31093,
}

function Actions.silerAward:doAction(roleid, args)
	--获得角色对象
	local jrole = _GetOnline(roleid)
	
	--判断角色是否在线
	if jrole then
		--查看角色剩余银钥匙数量
		local roleBoxKey = _RoleBoxKey(jrole,self.boxKey)
		--如果钥匙数量大于0，则可以开启宝箱
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
			--银宝箱开启成功，扣除一把银钥匙
			fillBagDel(self.boxKey)
			Bag:delItem(jrole, self.boxKey, 1)
			log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$5#$银钥匙#$" .. 1 .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(31093).."#$".._Long2String(31093))
			fillAttributes(jrole)
			putShort(0)
			sendMsg(jrole)
			
			self:commonMessage(jrole, "恭喜您！开启了银宝箱，请注意查收邮件。", "everydayAward")
		else 
			self:commonMessage(jrole, "抱歉！您的银钥匙不足，请到NPC购买。", "everydayAward")
		end
	end
end