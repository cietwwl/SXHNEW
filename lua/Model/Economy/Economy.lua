
Economy = {
	Gold = 1,--金币
	Exp = 2,--经验
	GangTri = 3,--帮贡
	
	"经验",
	"金币",
	"帮贡 ",
}

function Economy:reward(jrole, awards, param)
	local prompt = {}
	table.insert(prompt, "获得：")
	
	if awards.wealth then
		local gold =  awards.wealth[self.Gold]
		local exp = awards.wealth[self.Exp] 
		-- 如果param 不为空则说明奖励经验和金币需要计算
		if param then
	
		   if exp then
		      exp = param
		   end
		   if gold then
		      gold = param /15
		   end
		end
		if gold then
			    local oldgold = _RoleGetGold(jrole)
				local gold = oldgold + gold
				_RoleSetGold(jrole, gold)
				local difgold=gold - oldgold
			log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$3#$本次获得所有物品#$获得物品所有个数#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$" .. difgold .. "")
			table.insert(prompt, "金币+" .. getValueDescribe(difgold))
		end
		
		local upgrade
		if exp then
			table.insert(prompt, "经验+" .. exp)
			upgrade = tryUpgrade(jrole,exp)
		end
		
		if awards.wealth[self.GangTri] then
			if _IsGangLoaded(_RoleGetGangid(jrole)) then
				table.insert(prompt, "帮贡+" .. awards.wealth[self.GangTri])
				_GangUpdateTribute(jrole, awards.wealth[self.GangTri])
			end
		end
		
		if exp and not upgrade then
			fillAttributes(jrole)
		end
	end
	
	if awards.items then
		for k, v in pairs(awards.items) do
			if type(k) == "number" then
				local template = ItemSet[k]
				if template then
					local jItems = template:creatJavaItem(v)
					
					if awards.items.func then
						awards.items.func(jItems)
					end
					
					Bag:addJItem(jrole, jItems)
					for _, v in pairs(jItems) do
						fillBagAdd(v)
					end
	
					--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$4#$" .. template.name .. "#$" .. v .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	
	                log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$4#$" .. template.name .. "#$" .. v .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(k).."#$".._Long2String(k))
	                
					table.insert(prompt, template.name .. "×" .. v)
				else
					log.error("物品", k, "不存在！")
				end
			end
		end
	end
	
	if #prompt > 1 then
		return table.concat(prompt, "/")
	end
end


function Economy:reward2(jrole, awards, param)
	local prompt = {}
	table.insert(prompt, "获得：")
	
	if awards.wealth then
		local gold =  awards.wealth[self.Gold]
		local exp = awards.wealth[self.Exp] 
		-- 如果param 不为空则说明奖励经验和金币需要计算
		if param then
	
		   if exp then
		      exp = param
		   end
		   if gold then
		      gold = param /15
		   end
		end
		if gold then
			    local oldgold = _RoleGetGold(jrole)
				local gold = oldgold + gold
				_RoleSetGold(jrole, gold)
				local difgold=gold - oldgold
			log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$3#$本次获得所有物品#$获得物品所有个数#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$" .. difgold .. "")
			table.insert(prompt, "金币+" .. getValueDescribe(difgold))
		end
		
		local upgrade
		if exp then
			table.insert(prompt, "经验+" .. exp)
			upgrade = tryUpgrade(jrole,exp)
		end
		
		if awards.wealth[self.GangTri] then
			if _IsGangLoaded(_RoleGetGangid(jrole)) then
				table.insert(prompt, "帮贡+" .. awards.wealth[self.GangTri])
				_GangUpdateTribute(jrole, awards.wealth[self.GangTri])
			end
		end
		
		if exp and not upgrade then
			fillAttributes(jrole)
		end
	end
	
	if awards.items then
		for k, v in pairs(awards.items) do
			if type(k) == "number" then
				local template = ItemSet[k]
				if template then
					local jItems = template:creatJavaItem(v)
					
					if awards.items.func then
						awards.items.func(jItems)
					end
					
					Bag:addJItem(jrole, jItems)
					for _, v in pairs(jItems) do
						fillBagAdd(v)
					end
	
					--log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$4#$" .. template.name .. "#$" .. v .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
	
	                log.item("" .. _RoleGetUserId(jrole) .."#$" .. _RoleGetId(jrole) .. "#$4#$" .. template.name .. "#$" .. v .. "#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$".. 0 .."#$".._Long2String(k).."#$".._Long2String(k))
	                
					table.insert(prompt, template.name .. "×" .. v)
				else
					log.error("物品", k, "不存在！")
				end
			end
		end
	end
	
	if #prompt > 1 then
		return table.concat(prompt, "/")
	end
end
