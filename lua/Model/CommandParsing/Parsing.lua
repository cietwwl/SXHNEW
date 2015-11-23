
Command = { }

function Command:containsCommand(chat)
	for k in pairs(self) do
		if string.find(chat, k, 1, true) then
			return true
		end
	end
end

-------------------------------------------------------------------------------

--给所有人发公告
function Command.removeMaill(jmx, id)
	if not jmx then
		return "外网客户端不允许调级别"
	end
	_RemoveMailNotice(id)
end


--更改指定在线角色级别
function Command.changeLevel(jmx, roleid, level)
	if not jmx then
		return "外网客户端不允许调级别"
	end
	
	roleid = roleid or 0
	level = level or 0
	
--	if not checkLevel(level) then
	--	log.info("Command.changeLevel", "级别", level, "非法！")
	--	return
	--end
	
	Command.level(jmx, roleid, level)
end

function Command.level(jmx, roleid, level)
	if not jmx then
		return "外网客户端不允许调级别"
	end
	
	roleid = roleid or 0
	level = level or 0
	
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("Command.changeLevel", "角色", roleid, "不在线！")
		return
	end
	
	prepareBody()
	
	upgrade(jrole, level, getUpgradeRequire(level))
	
	fillFreshNPCState(jrole)
			
	putShort(0)
	
	sendMsg(jrole)
end

-------------------------------------------------------------------------------
--给指定的在线角色加金币
function Command.addGold(jmx, roleid, value)
	roleid = roleid or 0
	value = value or 0
	
	if value == 0 then
		log.info("Command.addGold", "数量为0！")
		return
	end
	
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("Command.addGold", "角色", roleid, "不在线！")
		return
	end
	
	local oldgold=jrole:getGold()
	local gold = jrole:getGold() + value
	gold = gold > 0 and gold or 0
	gold = gold > 999999 and 999999 or gold
	
	jrole:setGold(gold)
	local difgold=jrole:getGold()-oldgold

	log.item("" .. _RoleGetUserId(jrole)  .."#$" .. _RoleGetId(jrole) .. "#$2#$没有获得任务物品#$物品个数为0#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId.."#$" .. difgold .. "")

	flushRoleAttr(jrole)
end

-------------------------------------------------------------------------------
--发送物品给在线角色
function Command.giveItem(jmx, roleid, tid, num) 
	roleid = roleid or 0
	tid = tid or 0
	num = num or 0
	
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("Command.giveItem", "角色", roleid, "不在线！")
		return
	end
	
	local template = ItemSet[tid]
	if not template then
		log.info("Command.giveItem", "物品", tid, "不存在！")
		return
	end
	
	if num <= 0 or num > 20 then
		log.info("Command.giveItem", "数量为", num)
		return
	end
	
	if not Bag:checkAdd(jrole, { items = { [tid] = num, }, }) then
		log.info("Command.giveItem", "包裹空间不足！")
		return
	end
	
	local jItems = template:creatJavaItem(num)
	
	prepareBody()
	
	for _, v in pairs(jItems) do
		log.info("Command.giveItem", "生成物品id【" .. _Long2String(_ItemGetUid(v)) .. "】数量【" .. _ItemGetStorage(v) .. "】")
		
		fillBagAdd(v)
	end
	
	Bag:addJItem(jrole, jItems)
	
	fillSystemPrompt("恭喜你收到系统发放的【" .. template.name .. "】" .. num .. " 个~")
	
	putShort(0)
	
	sendMsg(jrole)
end

-------------------------------------------------------------------------------
--查看在线角色属性、状态
function Command.view(jmx, roleid)
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("角色", roleid, "不在线")
		return
	end
	
	log.info("查看角色：" .. roleid)
	log.info("用户id：" .. jrole:getUserid())
	log.info("名称：" .. _RoleGetNick(jrole))
	log.info("职业：" .. _ObjectToString(_RoleGetVocation(jrole)))
	log.info("性别：" .. (jrole:getSex() == 0 and "男" or "女"))
	log.info("等级：" .. jrole:getLevel())
	log.info("属性：力量【" .. jrole:getStrength() .. "】" .. 
						" 敏捷【" .. jrole:getStrength() .. "】" .. 
						"智力【" .. jrole:getIntellect() .. "】" .. 
						"体质【" .. jrole:getVitality() .. "】")
	log.info(jrole:getCoords():toString())

	log.info("金币：" .. jrole:getGold())
	log.info("积分：" .. jrole:getMark())
	log.info("游戏币：" .. jrole:getMoney())
	
	log.info("当前血：" .. jrole:getHP())
	log.info("当前魔：" .. jrole:getMP())
	
	log.info("当前经验：" .. jrole:getEXP())
	log.info("当前升级共需经验：" .. jrole:getMaxEXP())
	
	log.info("最小物攻：" .. jrole:getMinPAtk())
	log.info("最大物攻：" .. jrole:getMaxPAtk())
	log.info("最小魔攻：" .. jrole:getMinMAtk())
	log.info("最大魔攻：" .. jrole:getMaxMAtk())
	log.info("物防：" .. jrole:getpDef())
	log.info("法防：" .. jrole:getmDef())
	log.info("命中：" .. jrole:getHit())
	log.info("躲闪：" .. jrole:getEvade())
	log.info("致命：" .. jrole:getCrit())
	log.info("攻速：" .. jrole:getAtkSpd())
	log.info("最大血：" .. jrole:getMaxHP())
	log.info("最大魔：" .. jrole:getMaxMP())

	log.info("好友：")
	log.info(jrole:getFrends():isEmpty() and "无" or jrole:getFriendsString())
	
	log.info("敌人：")
	log.info(jrole:getFoes():isEmpty() and "无" or jrole:getFoesString())
	
	log.info("仇人：")
	log.info(jrole:getEnemys():isEmpty() and "无" or jrole:getEnemysString())

	log.info("任务状态：")
	for k, v in jmapIter(jrole:getTasks():getTaskStates()) do
		local task = TaskSet[k]
		if task then
			log.info(string.rep(" ", 4) .. "任务id" .. k)
			if not task.name then
				print(task)
			end
			log.info(string.rep(" ", 4) .. task.name)
			log.info(string.rep(" ", 4) .. "step = " .. v:getStep())
			log.info(string.rep(" ", 4) .. "states：" .. v:getSubstate():toString())
			log.info(string.rep(" ", 4) .. "extra = " .. v:getExtra():toString())
			if v:isFinished() then
				log.info(string.rep(" ", 4) .. "已终结")
			else
				log.info(string.rep(" ", 4) .. "未终结")
			end
		end
	end
	
	log.info("存储属性：" .. getUTF8(jrole:getStore():getStoreAttr()))

	log.info("身上物品：")
	for _, jEquip in jmapIter(InusePack:getJPack(jrole):getPackItems()) do
		local uid = jEquip:getUid()
		local tid = jEquip:getTid()
		local feature = jEquip:getFeature()
		local template = ItemSet[tid]
		if template then
			log.info(string.rep(" ", 4) .. _ItemGetName(jEquip) .. "  模板id：" .. tid 
						.. "  唯一id：" .. Long:toString(uid) .. "  特征码：" .. feature)
		end
	end
	
	log.info("包裹中物品：")
	local count = 0
	for _, JItem in jmapIter(Bag:getJPack(jrole):getPackItems()) do
		local uid = JItem:getUid()
		local tid = JItem:getTid()
		local feature = JItem:getFeature()
		local template = ItemSet[tid]
		if template then
			log.info(string.rep(" ", 4) .. _ItemGetName(JItem) .. "  模板id：" .. tid 
						.. " 唯一id：" .. Long:toString(uid) .. " 特征码：" .. feature)
			count = count + 1
		end
	end
	
	if count == 0 then
		log.info("无")
	end
	
	log.info("技能：")
	count = 0
	for k, v in jmapIter(jrole:getSkill()) do
		log.info(string.rep(" ", 4) .. SkillSet[k].name .. "  " .. v .. "级")
		count = count + 1
	end
	if count == 0 then
		log.info("无")
	end
	
	log.info("buff：")
	count = 0
	for k in jmapIter(jrole:getBuffManager():getBuffs()) do
		log.info("作用属性    " .. BuffConst[k])
		count = count + 1
	end
	if count == 0 then
		log.info("无")
	end
	
	log.info("帮派id：" .. Long:toString(jrole:getGangid()))
	log.info("帮派职位：" .. getUTF8(jrole:getJobTitle():getDes()))
end

-------------------------------------------------------------------------------
--显示所有在线角色
function Command.showOnline(jmx)
	if not jmx then
		return "外网客户端不允许调级别"
	end
	
	OlServ:showOnline()
end

function Command.online(jmx)
	if jmx then
		log.info("在线人数：", OlServ:getURMapSize())
		log.info("在线角色数：", OlServ:getRoleOnlineSize())
		log.info("user：", OlServ:getUnLogUsers():size())
	else
		return "在线人数：" .. OlServ:getURMapSize() .. "在线角色数：" .. OlServ:getRoleOnlineSize()
	end
	
end
-------------------------------------------------------------------------------
--加帮贡命令
function Command.addTribute(jmx,gangid,roleid,tri)
	if not jmx then
		return "外网客户端不允许加帮贡"
	end
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("角色" .. roleid .. "不在线")
		return
	end
	GangService:addTribute(gangid,jrole,tri)
	
end


-------------------------------------------------------------------------------
--将角色传送至某张地图
function Command.trans(jmx, roleid, mapid)
	---------------------------------------------------------------------------
	--条件验证
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("角色" .. roleid .. "不在线")
		return
	end
	
	if jrole:getTeam() then
		log.info("无法传送到地图" .. mapid .. "，队伍中不允许回城！")
		return
	end
	
	if jrole:getBattle() then
		log.info("无法传送到地图" .. mapid .. "，角色在战斗中！")
		return
	end
	
	local destmap = MapSet[mapid]
	
	if not destmap then
		log.info("无法传送到地图" .. mapid .. "，地图不存在！")
		return
	end
	--条件验证
	---------------------------------------------------------------------------
	--获取目标传送点
	local destTrans = destmap.trans[math.random(1, #destmap.trans)]
	--强制客户端切换地图
	forceMap(jrole, luajava.new(Coords, Command.nearby_coords(destmap.id, destTrans.x, destTrans.y)))
	--强制客户端切换地图
	---------------------------------------------------------------------------
end

-------------------------------------------------------------------------------
--将角色传送至指定地图上指定坐标
function Command.transExact(jmx, roleid, mapid, mapx, mapy)
	---------------------------------------------------------------------------
	--条件验证
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("角色" .. roleid .. "不在线")
		return
	end
	
	if jrole:getTeam() then
		log.info("无法传送到地图" .. mapid .. "，队伍中不允许回城！")
		return
	end
	
	if jrole:getBattle() then
		log.info("无法传送到地图" .. mapid .. "，角色在战斗中！")
		return
	end
	
	local destmap = MapSet[mapid]
	
	if not destmap then
		log.info("无法传送到地图" .. mapid .. "，地图不存在！")
		return
	end
	
	if mapx <= 0 or mapx >= destmap.width * destmap.cell then
		log.info("无法传送到地图" .. mapid .. "，坐标溢出！")
		return
	end
	
	if mapy <= 0 or mapy >= destmap.height * destmap.cell then
		log.info("无法传送到地图" .. mapid .. "，坐标溢出！")
		return
	end
	--条件验证
	---------------------------------------------------------------------------
	--强制客户端切换地图
	forceMap(jrole, luajava.new(Coords, destmap.id, mapx, mapy))
	--强制客户端切换地图
	---------------------------------------------------------------------------
end 

-------------------------------------------------------------------------------
--接受任务
function Command.acceptTask(jmx, roleid, taskid)
	local jrole = _GetOnline(roleid)
	if not jrole then
		if jmx then
			log.info("角色" .. roleid .. "不在线")
			return
		else
			return "角色" .. roleid .. "不在线"
		end
	end
	
	local task = TaskSet[taskid]
	if not task then
		if jmx then
			log.info("任务", taskid, "不存在")
			return
		else
			return "任务" .. taskid .. "不存在"
		end
	end
	
	task:acceptTask(jrole)
end

-------------------------------------------------------------------------------
--交任务
function Command.consignTask(jmx, roleid, taskid)
	local jrole = _GetOnline(roleid)
	if not jrole then
		if jmx then
			log.info("角色" .. roleid .. "不在线")
			return
		else
			return "角色" .. roleid .. "不在线"
		end
	end
	
	local task = TaskSet[taskid]
	if not task then
		if jmx then
			log.info("任务", taskid, "不存在")
			return
		else
			return "任务" .. taskid .. "不存在"
		end
	end
	
	task:consignTask(jrole)
end

--接受任务
function Command.delTask(jmx, roleid, taskid)
	local jrole = _GetOnline(roleid)
	if not jrole then
		if jmx then
			log.info("角色" .. roleid .. "不在线")
			return
		else
			return "角色" .. roleid .. "不在线"
		end
	end
	
	_DelTaskState(_GetRoleTask(jrole), taskid)
	
	prepareBody()
	
	fillFreshNPCState(jrole)
			
	putShort(0)
	
	sendMsg(jrole)
end

function Command.kick(jmx, roleid)
	local jrole = _GetOnline(roleid)
	if not jrole then
		if jmx then
			log.info("角色" .. roleid .. "不在线")
			return
		else
			return "角色" .. roleid .. "不在线"
		end
	end
	jrole:getNetHandler():close(NetHandler.STATE_CLOSED_BY_APP_LEVEL)
end



function Command.kickUnLogUser(jmx,userid)
	_KickUnLogUser(userid)
end



function Command.kickUser(jmx,userid)
	local roleId = _GetRoleId(userid)
	Command.kick(jmx, roleId)
end

function Command.kickAll(jmx)
	if not jmx then
		return "外网客户端不允许调级别"
	end
	
	for roleid in jlistIter(_GetAllOnlines()) do
		local jrole = _GetOnline(roleid)
		if jrole then
			if jrole:getNetHandler() then
				jrole:getNetHandler():close(NetHandler.STATE_CLOSED_BY_APP_LEVEL)
			end
		end
	end
end

function Command.time()
	log.info("当前时间：")
	log.info("天：" .. Cardinality:getDay())
	log.info("小时：" .. Cardinality:getHour())
	log.info("分钟：" .. Cardinality:getMinute())
end

function Command.mail(jmx, roleid, gold, tid, num)
	gold = gold or 0
	if gold < 0 then
		gold = 0
	end

	local template = ItemSet[tid]
	if not template then
		log.info("发送系统邮件失败，物品", tid, "不存在！")
		return
	end
	
	num = num or 1
	if num <= 0 and num % 1 ~= 0 then
		log.info("发送系统邮件失败，错误的数量：", num)
		return
	end
	
	if template:isUnique() then
		num = 1
	end
	
	local jItem = template:creatJavaItem(num)[1]
	
	MailManager:sendSysMail(roleid, gold, jItem)
end

function Command.setexp(jmx, roleid, exp)
	if exp < 0 then
		log.info(" 执行失败！经验小于0 ")
		return
	end
	
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("角色", roleid, "不在线")
		return
	end
	
	jrole:setEXP(exp)
	
	prepareBody()
	
	fillAttributes(jrole)
	
	putShort(0)
	
	sendMsg(jrole)
end

function Command.clearBag(jmx, roleid)
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("角色", roleid, "不在线")
		return
	end
	
	prepareBody()
	
	for _, JItem in jmapIter(_PackGetItemsAll(Bag:getJPack(jrole))) do
		fillBagDel(_ItemGetUid(JItem), _ItemGetStorage(JItem))
	end
	
	_BagClear(jrole)
	
	putShort(0)
	
	sendMsg(jrole)
end

function Command.clearTask(jmx, roleid)
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("角色" .. roleid .. "不在线")
		return
	end

	_ClearMap(_GetTaskStates(_GetRoleTask(jrole)))
	 
	prepareBody()
	
	fillFreshNPCState(jrole)
			
	putShort(0)
	
	sendMsg(jrole)
end

function Command.clearSkill(jmx, roleid)
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("角色" .. roleid .. "不在线")
		return
	end
	
	jrole:getSkill():clear()
end

function Command.clearBuff(jmx, roleid)
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("角色" .. roleid .. "不在线")
		return
	end

	for k, v, iter in jmapIter(jrole:getBuffManager():getBuffs()) do
		v:delFromRole(jrole)
		_IterRemove(iter)
	end
	
end

function Command.resetTHRESHOLD(jmx, threshold)
	if not jmx then
		return "外网客户端不允许执行此命令"
	end
	
	threshold = threshold or 30
	Log:resetTHRESHOLD(threshold)
end

function Command.quickFinishTask(jmx, roleid, taskid, step)
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("角色" .. roleid .. "不在线")
		return
	end
	
	local task = TaskSet[taskid]
	if not task then
		log.info("任务" .. taskid .. "不存在")
		return
	end
	
	task:quickFinish(jrole, step)
end

function Command.charm(jmx, roleid, value)
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("角色" .. roleid .. "不在线")
		return
	end
	
	local charm = _RoleGetCharm(jrole)
	if charm == 99999999 then
		log.info("声望值已经达到上限，不能使用该物品！")
		return
	end
	
	_RoleSetCharm(jrole, charm + value)
end

function Command.toprated()
	TopRatedService:watch()
end

function Command.duelOverMark(jmx, roleid, value)
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("角色" .. roleid .. "不在线")
		return
	end
	
	local role_task = _GetRoleTask(jrole)
	local task_state = _GetTaskState(role_task, table.getglobal("Temp.Task").Exchange)
	
	local mark = _TaskStateExtraGetI(task_state, 2)
	_TaskStateExtraSetI(task_state, 2, mark + value)
	
	_RoleSetMark(jrole, _RoleGetMark(jrole) + value)
end

function Command.exclusive()
	--ExclusiveRole.payment()
end

function Command.openbox(jmx, boxid, maxn)
	if not jmx then
		return "外网客户端不允许执行此命令"
	end

	local box = ItemSet[boxid]
	if not box or box.subtype ~= PropConst.RandomBox then
		log.info("该物品无法打开")
		return
	end
	
	log.info("打开", box.name, maxn, "次")
	
	local stat = { }
	for i = 1, maxn do
		local rand_content = box.itempack:rand()
		local tid = rand_content[1]
		stat[tid] = (stat[tid] or 0) + 1
	end
	
	for k, v in pairs(stat) do
		local item = ItemSet[k]
		if item then
			log.info(item.name, v, "个，概率：", v / maxn)
		end
	end
end

function Command.funcchk(jmx)
	if not jmx then
		return "外网客户端不允许执行此命令"
	end
	
	for k, v in pairs(LuaRuntime) do
		log.info("小时", k)
		for kk, vv in pairs(v) do
			log.info(kk)
			if vv.r.count then 
				log.info("  调用次数：", vv.r.count, "  总耗时：", vv.time, "毫秒  平均耗时：", (vv.time or 0) / vv.r.count, "毫秒")
			else
				log.info("  调用次数：", vv.c.count)
			end
		end
	end
	
	Lua:funcchk()
end

function Command.testBarter(jmx, btrid, maxn)
	if not jmx then
		return "外网客户端不允许执行此命令"
	end
	
	local task = TaskSet[btrid]
	if not task or task.type ~= TaskProfile.Barter then
		log.info("该任务不是兑换任务")
		return
	end
	
	log.info("任务", btrid, "兑换", maxn, "次")
	
	local stat = { }
	
	for times = 1, maxn do
		for i = 1, random.getn(task.barter.maxn) do
			local rand_value = task.barter:rand()
			if rand_value then
				local template = ItemSet[rand_value[math.random(1, #rand_value)]]
				if template then
					stat[template] = (stat[template] or 0) + 1
				end
			end
		end
	end
	
	for k, v in pairs(stat) do
		log.info(k.name, v, "个，每次兑换平均：", v / maxn, "个")
	end
end

function Command.watchEquipExt(jmx, roleid, uid)
	if not jmx then
		return "外网客户端不允许执行此命令"
	end
	
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("角色" .. roleid .. "不在线")
		return
	end
	
	for pack in jlistIter(jrole:getStore():getRolePacks()) do
		local item = pack:getItem(uid)
		if item then
			local equip = ItemSet[item:getTid()]
			if equip and equip.type == ItemConst.Equip then
				local equipExt = equip.ext
				local additive = item:getAdditive()
				
				if equipExt then
					for vv in jlistIter(additive) do
						local curExt = equipExt[vv]
						if curExt and ExtType[curExt.type] then
							log.info(ExtType[curExt.type], "+", curExt.value)
						end
					end
				end
			else
				log.info("物品", uid, "不是装备")
			end
			return
		end
	end
	
	log.info("物品", uid, "不属于角色", roleid)
end

function Command.watchGang(jmx, gangid)
	if not jmx then
		return "外网客户端不允许执行此命令"
	end
	
	if not gangid then
		GangService:watch()
		return
	end
	
	if GangService:isGangDiscarded(gangid) then
		log.info("帮派id" .. gangid .. "已废弃")
		return
	end
	
	if GangService:isGangLoaded(gangid) then
		GangService:getGang(gangid):watch()
	else
		log.info("帮派" .. gangid .. "尚未加载")
	end
end

function Command.tribute(jmx, roleid, tri)
	local jrole = _GetOnline(roleid)
	if not jrole then
		log.info("角色" .. roleid .. "不在线")
		return
	end
	
	local gangid = _RoleGetGangid(jrole)
	
	if GangService:isGangDiscarded(gangid) then
		log.info("帮派id" .. gangid .. "已废弃")
		return
	end
	
	if GangService:isGangLoaded(gangid) then
		local gang = GangService:getGang(gangid)
		gang:updateTribute(jrole, tri)
	else
		log.info("帮派" .. gangid .. "尚未加载")
	end
end

function Command.transNPC(jmx, roleid, npcid)
	if not NPCSet[npcid] then
		log.info("试图传送至不存在的NPC" .. npcid)
		return
	end
	
	local mapid = NPCSet[npcid].mapid
	local map = MapSet[mapid]
	
	for _, v in pairs(map.npcs) do
		if v.id == npcid then
			Command.transExact(jmx, roleid, Command.nearby_coords(mapid, v.x, v.y))
			return
		end
	end
	
	log.info("传送失败，无法获取NPC坐标，请检查地图【" .. map.name .. "，" .. map.id .. "】")
end

function Command.transRole(jmx, roleid, toRoleid, distance)
	local toRole = _GetOnline(toRoleid)
	if not toRole then
		log.info("角色" .. toRoleid .. "不在线")
		return
	end
	
	local jcoords = _RoleGetCoords(toRole)
	local mapid, mapx, mapy = _CoordsGetAttr(jcoords)
	
	Command.transExact(jmx, roleid, Command.nearby_coords(mapid, mapx, mapy, distance))
end

function Command.nearby_coords(mapid, mapx, mapy, distance)
	distance = distance or 1
	
	local map = MapSet[mapid]
	local destxt = mapx > map.width * 16 / 2 and -1 or 1
	local destyt = mapy > map.height * 16 / 2 and -1 or 1
	
	local theta = math.random() * math.pi / 2
	
	local destx = mapx + destxt * math.sin(theta) * distance * 16
	local desty = mapy + destyt * math.cos(theta) * distance * 16
	
	return mapid, destx, desty
end

function Command.watchAuction(jmx, auctionId)
	if auctionId then
		local auctions = AuctionHouse:getAuctions()
		local auction = auctions:get(luajava.new(Long, auctionId))
		
		local info = auction and (auction:detail()) or ("拍卖单号" .. auctionId .. "不存在！")
		if jmx then
			log.info(info)
		else
			return detail
		end
	else
		if jmx then
			AuctionHouse:watch()
		else
			return "外网客户端无法查看全部拍卖信息"
		end
	end
end

function Command.connect()
	local onlines = OlServ:getAllOnlines()
	
	local i = 1
	for roleid in jlistIter(onlines) do
		local jrole = _GetOnline(roleid)
		if jrole then
			if not jrole:isConnected() then
				local cont = { }
				cont[#cont + 1] = i .. "."
				cont[#cont + 1] = "已断开"
				local bat = jrole:getBattle()
				if bat then
					cont[#cont + 1] = "Battle != null"
					
					if bat:isDeadBattle() then
						cont[#cont + 1] = "isDeadBattle"
						cont[#cont + 1] = jrole:isWriteBacking() and "isWriteBacking" or "notWriteBacking"
					else
						cont[#cont + 1] = "notDeadBattle"
					end
				else
					cont[#cont + 1] = "Battle == null"
					cont[#cont + 1] = jrole:isWriteBacking() and "isWriteBacking" or "notWriteBacking"
				end
				
				log.info(table.concat(cont, "  "))
				i = i + 1
			end
		end
	end
end
