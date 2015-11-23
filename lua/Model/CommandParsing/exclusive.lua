
ExclusiveRole = {--内部专用角色
	{
		server = [[凌波微步]];
		
		{ "赵冰", "神龙教Z金猊", 13318, },
		{ "单东海", "神龙教Z短兔", 13322, },
		{ "冯杨", "神龙教Z麦丁", 13442, },
		{ "董哥", "神龙教Z白泽", 13373, },
		{ "王家强", "神龙教Z头陀", 13642, },
		{ "张涛", "神龙教Z国宝", 13454, },
		{ "闫堤", "神龙教Z石头", 13630, },
		{ "李佳", "神龙教Z蟒纱", 13711, },
		{ "李昂", "神龙教Z麒麟", 13678, },
		{ "王泽宇", "神龙教Z杂役", 13676, },
		{ "吕亦涛", "神龙教Z螳螂", 13692, },
	}
}

ExclusiveRole.salaries = {
	item = {
		[20005] = 2,--   背包扩展2个
		[5049] = 10,--    屏霸专用10个（=1000狮子吼）
		[15139] = 5,--   自动补血（大）5个
		[15142] = 5,--   自动补蓝（大）5个
		[20003] = 10,--  金钱UP10个
		[20004] = 10,--  攻击UP10个
		[27003] = 5,--  天龙通用白金票5个
		[3202] = 40,--  金虎符40个
		
		[10066] = 1,--   盘古斧子 神器1把26级
		[10076] = 1,-- 朱雀扇子
		[10086] = 1,-- 弓
	}
}

function ExclusiveRole.check()
	local result
	for _, v in ipairs(ExclusiveRole) do
		if TianLongServer.serverName == v.server then
			for _, role in ipairs(v) do
				if not _GetOnline(role[3]) then
					log.info("发放工资", table.concat(role, "    "), "不在线！")
					result = true
				end
			end
		end
	end
	
	for k, v in pairs(ExclusiveRole.salaries.item) do
		if not ItemSet[k] then
			log.info("发放工资", "物品", k, "不存在！")
			result = true
		end
	end
	
	log.info("没问题")
	return result
end

function ExclusiveRole.payment()
	log.info(TianLongServer.serverName)
	if ExclusiveRole.check() then
		log.info("有问题")
		return
	end
	
	---[[
	for _, v in ipairs(ExclusiveRole) do
		if TianLongServer.serverName == v.server then
			for _, role in ipairs(v) do
				log.info("向【", role[1], "】【", role[2], "】【", role[3], "】发放工资")
				for tid, num in pairs(ExclusiveRole.salaries.item) do
					local jrole = _GetOnline(role[3])
					local template = ItemSet[tid]
					if not template.vocation or template.vocation == jrole:getVocation() then
						local jItems = template:creatJavaItem(num)
						prepareBody()
						for _, v in pairs(jItems) do
							log.info("发放工资", "生成物品id【" .. _Long2String(_ItemGetUid(v)) .. "】数量【" .. _ItemGetStorage(v) .. "】")
							fillBagAdd(v)
						end
						Bag:addJItem(jrole, jItems)
						fillSystemPrompt("恭喜你收到系统发放的【" .. template.name .. "】" .. num .. " 个～")
						putShort(0)
						sendMsg(jrole)
					end
				end
			end
		end
	end
	--]]
end
