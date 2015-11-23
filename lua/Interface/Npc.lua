
local firstTalk = function(jrole, npcid)
	local talkList = luajava.new(ArrayList)
	
	local npc = NPCSet[npcid]
	if npc == nil then
		log.error("npc不存在：", npcid)
		return taskList
	end
	
	prepareBody()
	putShort(0)
	
	bodyMark()
	putShort(300)--子模块300
	putByte(0)
	
	local fill = 0
	for _, v in pairs(npc.task) do 
		local task = TaskSet[v]
		if task then
		    if task.type == TaskProfile.GetItemTask then
		     debug.hook(100)
		      for k,v in pairs(task.roles) do
		         
		          if jrole:getId() == v  and task:shownInTalkList(jrole, npcid)then
		             fill = fill + 1
		             break
		          end
		      end
			elseif task:shownInTalkList(jrole, npcid) then
			
				fill = fill + 1
			end
		else
			log.error(npc.nick, "任务", v, "不存在")
		end
	end
	
	for _, v in ipairs(npc.feature) do
		local task = TaskSet[v]
		if task then
			if task:shownInTalkList(jrole, npcid) then
				fill = fill + 1
			end
		else
			log.error(npc.nick, "任务", v, "不存在")
		end
	end
	
	if fill > 0 then
		local position = getPosition()
		bodyReset()
		putShort(300)
		putByte(fill)
		
		setPosition(position)
	else
		bodyReset()
		
		fillNpcDialog(0, 0, 0, npc.defaultDialog, 0)
	end
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function talkToNPC(jrole, npcid, deep, taskid, uid, num)
	local npc = NPCSet[npcid]
	if not npc then
		log.error{ npcid = npcid, deep = deep, taskid = taskid, uid = uid, num = num, info = "npc不存在！", }
		return
	end
	
	if deep == 0 then
		return firstTalk(jrole, npcid)
	end
	
	local task = TaskSet[taskid]
	
	task:dialog(jrole, npcid, deep, uid, num)
end
