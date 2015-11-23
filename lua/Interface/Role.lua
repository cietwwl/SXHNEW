
function roleOnline(jrole)
	if _RoleGetOnlineSec(jrole) == 0 then
		local maxHp = _RoleGetMaxHP(jrole)
		_RoleSetHP(jrole, maxHp)
		local maxMp = _RoleGetMaxMP(jrole)
		_RoleSetMP(jrole, maxMp)
	end

	sendGetRoleInfo(jrole)

	rectifyCoords(jrole)

	checkTask(jrole)
end

------------------------昵称颜色------------------------
function getNickColor(identify)
	return 0
end

-------------------------------升级
function upgrade(jrole, level, exp)
	local lastLevel = _GetRoleLevel(jrole)

	if exp then
		_RoleSetExp(jrole, exp)
	end

	local levelDiff = level - lastLevel
	for i, v in ipairs(RoleAttributes.point.get) do
		RoleAttributes.point.set[i](jrole, v(jrole) + levelDiff)
	end

	_SetRoleLevel(jrole, level)

	fillAttributes(jrole)
	fillAttributesDes(jrole)
	
	fillSystemPrompt("恭喜你已升至" .. level .. "级~")	
	
	
	--系统分级开放以后 
	-- 在这里加处理方法判断当前等级达到一定等级
	--则发邮件或者系统公告  提示玩家可以使用的功能
	if lastLevel < 10 and level >= 10 then
	    
	    MailManager:sendSysMail(_RoleGetId(jrole),"地图传送","恭喜你，现在可以使用地图传送功能了。点击菜单项“引导”下的“传送”，选择自己想去的地图就可以了。",0,nil)
	elseif  lastLevel < 20 and level >= 20 then
	    local item = ItemSet[6020]:creatJavaItem()[1]
	    
	    MailManager:sendSysMail(_RoleGetId(jrole),"师徒系统","恭喜你，现在可以拜师学艺了，请根据任务提示拜访指定NPC邮件任务卷“师徒指引”",0,item)
	elseif  lastLevel < 40 and level >= 40 then
	    MailManager:sendSysMail(_RoleGetId(jrole),"武林秘闻","武林中有很多隐秘的传闻都与至尊令有关，持有至尊令的话，你就可以在武林百晓生那里得到这些秘闻的线索。邮件任务卷“武林秘闻录”。",0,ItemSet[6021]:creatJavaItem()[1])
	    MailManager:sendSysMail(_RoleGetId(jrole),"至尊令","恭喜你，等级达到40就可以领取至尊令参加丰富多彩的日常活动了。邮件任务卷“至尊令”",0,ItemSet[6022]:creatJavaItem()[1])
	elseif  lastLevel < 50 and level >= 50 then
	    MailManager:sendSysMail(_RoleGetId(jrole),"狮王争霸","恭喜你，等级达到50就可以参加狮王争霸大赛，夺的大赛前十名还可以获得各种稀有奖励哦。加油吧邮件任务卷“狮王争霸邀请函”。",0,ItemSet[6023]:creatJavaItem()[1])
	   
	    MailManager:sendSysMail(_RoleGetId(jrole),"夫妻系统","恭喜你，现在可以与异性结为武林侠侣，同游江湖了，请根据任务提示拜访指定NPC。邮件任务卷“江湖侠侣”。",0,ItemSet[6025]:creatJavaItem()[1])
	    MailManager:sendSysMail(_RoleGetId(jrole),"八部浮屠","贫僧少林觉远，最近少林叛逆携八部浮屠重现江湖，关于这件事，我们少林总需对天下有个交待。现广发英雄帖，请少侠来我少林一叙，了解这段往事。邮件任务卷“八部浮屠”。",0,ItemSet[6026]:creatJavaItem()[1])
	elseif lastLevel < 60 and level >= 60 then
	     MailManager:sendSysMail(_RoleGetId(jrole),"追杀令","有仇不报非君子，武林中最为隐秘的杀手组织“追星楼”最近重出江湖，除了收费高点，他们的信誉绝对是最好的。邮件任务卷“武林追杀”。",0,ItemSet[6024]:creatJavaItem()[1])
	end
	
end

---------------------------尝试升级
function tryUpgrade(jrole, gainExp)
	if not gainExp then
		return
	end

	local exp = _RoleGetExp(jrole)
	local level = _GetRoleLevel(jrole)
	local upgradeReq = getUpgradeRequire(level)
	exp = exp + gainExp
	exp = exp < 0 and 0 or exp

	if    exp >= upgradeReq then
		for i = level, math.huge do
			exp = exp - upgradeReq
			level = level + 1
			upgradeReq = getUpgradeRequire(level)

			if exp < upgradeReq then
				break
			elseif level == MAXLEVEL then
				exp = upgradeReq
				break
			end
		end

		upgrade(jrole, level, exp)

		return true
	else
		_RoleSetExp(jrole, exp < upgradeReq and exp or upgradeReq)
		fillAttributes(jrole)
	end
end

----------------------------------------------------------------------------
-------------------------------角色死亡触发事件------------------------------
function roleFlee(jrole, battelType)
	local freshNpcState

	local PVE = battelType == JBattle.BATTLE_PVE

	local role_task = _GetRoleTask(jrole)
	local task_state_all = _GetTaskStates(role_task)

	for k, v, iter in jmapIter(task_state_all) do
		local task = TaskSet[k]
		if task and (task.type == TaskProfile.CommonTask or task.type == TaskProfile.Daily) then
			if not _IsTaskFinished(v) then
				local step = _GetTaskStateStep(v)
				local element = TaskElementSet[task.detail[step]]

				local sub_state = _GetSubState(v)

				if not element then
					_IterRemove(iter)
				elseif element.type == TaskElement.NPCFight then
					if not _IsSubtastFinished(sub_state) and _GetSubstateI(sub_state, 1) == 0 then
						if step == 1 then
							_IterRemove(iter)
						else
							_TaskStateStepDown(v)
						end

						freshNpcState = true
					end
				elseif element.type == TaskElement.NpcConvoy then
					if PVE and not _IsSubtastFinished(sub_state) then
						prepareBody()
						fillConvoyNpcEnd()
						sendMsg(jrole)

						freshNpcState = true
					end
				end
			end
		end
	end

	if freshNpcState then
		prepareBody()

		fillFreshNPCState(jrole)

		putShort(0)

		sendMsg(jrole)
	end
end

roleDie = roleFlee

function duelOver(jrole, winner)
	local role_task = _GetRoleTask(jrole)

	local taskid = table.getglobal("Temp.Task").Exchange
	local task = TaskSet[taskid]
	local task_state

	if task then
		task_state = _GetTaskState(role_task, taskid)
	end

	if task_state then
		return task:update(jrole, task_state, winner)
	elseif winner then
		return "战斗胜利！"
	else
		return "战斗失败！"
	end
end

function roleShout(jrole)
	local jItem, template, storage = Bag:getItemInPack(jrole, ItemConst.TrumpetTemp)

	prepareBody()

	fillBagDel(jItem:getUid())

	Bag:delItem(jrole, jItem:getUid(), 1)

	putShort(0)

	sendMsg(jrole)
end

function getDecreasedExp(lv)
	return 30^((math.floor(1+lv/10)^0.3))*2.7
end

function getDecreasedGold(lv)
	return (1+math.floor(lv/10)*lv/4*0.8)*10
end
