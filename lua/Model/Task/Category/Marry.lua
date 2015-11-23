--MailManager = luajava.bindClass("com.joyveb.tlol.mail.MailManager"):getInstance()

_Marry = _Task:newInstance()

function _Marry:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

_Marry.TalkMenu = {
	{ 1, "确定", 0, },
	{ 2, "返回", 0, },
}


function _Marry:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	putByte(1)--直接对话
	putString(self.name)
	putInt(0)
	return true
end


function _Marry:dialog(jrole, npcid, deep)
		if deep == 1 then
			self:getState(jrole, npcid,deep)
		elseif deep <= #self.acceptDialogs+1 then
			prepareBody()
			putShort(0)
			fillNpcDialog(self.id, 0, 1, self.name .. "/" .. self.acceptDialogs[deep-1], self.color)
			putShort(0)
			sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
			
		elseif deep == 4 then
			self:showSure(jrole,npcid,deep)
		elseif deep == 5 then	
			self:doMarry(jrole,npcid,deep)
		else	
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
		end
end

function _Marry:getState(jrole, npcid,deep)
		
	
		local role_level =  _GetRoleLevel(jrole)
		if role_level < self.minLevel then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "你的级别不足50级！！！")
			return
		end
			
		if _GetIfMarry(jrole) then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "你有侠侣了，不可以重婚哦！！！")
			return
		end	
		
		
		if not _RoleGetTeam(jrole) or  _GetTeamNum(jrole) == 1 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "一个人无法结婚！！！")
			return
		end	
		
		
		if _GetTeamNum(jrole) ~= 2 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "尼玛你想三个人结婚啊，想的美！！！")
			return
		end
		local otherJrole = nil
		local otherId =nil
		if _RoleGetSex(jrole)~=0 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "队长必须是男方！！！")
			return
		elseif _RoleGetSex(jrole)==0 then
			
			otherId= _GetMarryTeamOtherId(jrole)
			otherJrole = _IdGetRole(jrole)
		
			if _RoleGetSex(otherJrole)~=1 then
				replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "同性不可以结婚！！！")
			return
			end
		end
		if _GetIfMarry(otherJrole) then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "你对象有男人哎，让她离了再来吧！！！")
			return
		end		
		
		
		if  _GetRoleLevel(otherJrole) < 50 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "你的未婚妻不足50级！！！")
			return
		end
	
		
		local temp = 0
		for _,v in pairs(self.item) do
		
			if _CheckBag(jrole,v) then
				temp = 1
				break
			end
		end
		
		if temp==0 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "请准备好结婚物品")
			return
		end 
		
		local tempa = 0
		for _,v in pairs({60002,60001,60000}) do
		
			if _CheckBag(jrole,v) then
				tempa = 1
			end
		end
		if tempa ~=0 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "你背包里有未删除的侠侣戒指，请到NPC处回收再来结婚")
			return
		end 
		
		local tempaa = 0
		for _,v in pairs({60002,60001,60000}) do
		
			if _CheckBag(otherJrole,v) then
				tempaa = 1
			end
		end
		if tempaa ~=0 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "组员背包里有未删除的侠侣戒指，请到NPC处回收再来结婚")
			return
		end 
		
		
		--结婚证书已写死31115
		if not _CheckBag(jrole,31115) then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "请准备好结婚证书")
			return
		end
	
		
		prepareBody()
		putShort(0)
		fillNpcDialog(self.id, 0, 1, self.name .. "/" .."你要开始婚礼吗" , self.color)
		putShort(0)
		sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
		
end


function _Marry:showSure(jrole,npcid,deep)
	
	if not _RoleGetTeam(jrole) or  _GetTeamNum(jrole) == 1 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "队员已离开队伍！！！")
			return
		end	
		
		
		if _GetTeamNum(jrole) ~= 2 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "结婚过程不可以加入另一队员！！！")
			return
		end
	
	prepareBody()
	putShort(0)
	fillNpcItemOP("确定要结婚吗？？？",getRGB(0),{1},self.TalkMenu)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end	

function _Marry:doMarry(jrole,npcid,deep)

	local role_level =  _GetRoleLevel(jrole)
		if role_level < self.minLevel then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "结婚失败！你的级别不足50级！！！")
			return
		end
			
		if _GetIfMarry(jrole) then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "结婚失败！你有侠侣了，不可以重婚哦！！！")
			return
		end	
		
		if not _RoleGetTeam(jrole) or  _GetTeamNum(jrole) == 1 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "结婚失败！一个人无法结婚！！！")
			return
		end	
		
		if _GetTeamNum(jrole) ~= 2 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "结婚失败！尼玛你想三个人结婚啊，想的美！！！")
			return
		end
		
		local otherJrole = nil
		local otherId =nil
		
		if _RoleGetSex(jrole)~=0 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "结婚失败！队长必须是男方！！！")
			return
		elseif _RoleGetSex(jrole)==0 then
			
			otherId= _GetMarryTeamOtherId(jrole)
			otherJrole = _IdGetRole(jrole)
		
			if _RoleGetSex(otherJrole)~=1 then
				replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "结婚失败！同性不可以结婚！！！")
			return
			end
		end
		
		if _GetIfMarry(otherJrole) then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "结婚失败！你对象有男人哎，让她离了再来吧！！！")
			return
		end
		
		if  _GetRoleLevel(otherJrole) < 50 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "结婚失败！你的未婚妻不足50级！！！")
			return
		end
		
		local temp = 0
		for _,v in pairs(self.item) do
		
			if _CheckBag(jrole,v) then
				temp = 1
				break
			end
		end
		
		if temp==0 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "结婚失败！请准备好结婚物品")
			return
		end 
		
		local tempa = 0
		for _,v in pairs({60002,60001,60000}) do
		
			if _CheckBag(jrole,v) then
				tempa = 1
			end
		end
		if tempa ~=0 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "结婚失败！你背包里有未删除的侠侣戒指，请到NPC处回收再来结婚")
			return
		end 
		
		local tempaa = 0
		for _,v in pairs({60002,60001,60000}) do
		
			if _CheckBag(otherJrole,v) then
				tempaa = 1
			end
		end
		if tempaa ~=0 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "结婚失败！组员背包里有未删除的侠侣戒指，请到NPC处回收再来结婚")
			return
		end 
		
		
		--结婚证书已写死31115
		if not _CheckBag(jrole,31115) then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "结婚失败！请准备好结婚证书")
			return
		end

	if not _RoleGetTeam(jrole) or  _GetTeamNum(jrole) == 1 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "结婚失败！队员已离开队伍！！！")
			return
		end	
		
		
		if _GetTeamNum(jrole) ~= 2 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "结婚失败！结婚过程不可以加入另一队员！！！")
			return
		end

	local itemm = 0
		for _,v in pairs(self.item) do
			
			if _CheckBag(jrole,v) then
				prepareBody()
		
				fillBagDel(v)
			
				Bag:delItem(jrole, v, 1)
				
				putShort(0)
				
				sendMsg(jrole)
				
				itemm = v
				
				break
			end
		end
		
				prepareBody()
		
				fillBagDel(31115)
			
				Bag:delItem(jrole, 31115, 1)
				
				putShort(0)
				
				sendMsg(jrole)
			
	
				local otherId1= _GetMarryTeamOtherId(jrole)
				local otherJrole1 = _IdGetRole(jrole)
				local nick = _RoleGetNick(jrole)
				local otherNick = _RoleGetNick(otherJrole1)
				
				_RoleSetMarry(jrole,otherId1)
				_RoleSetMarry(otherJrole1,_RoleGetId(jrole))
				
				local val = _GetItem(jrole,itemm)
				
				MailManager:send_GM_Mail(_RoleGetId(jrole), "恭喜结婚成功", "恭喜你已与"..otherNick.."成为侠侣", 0, val)
				MailManager:send_GM_Mail(otherId1, "恭喜结婚成功","恭喜你已与"..nick.."成为侠侣", 0, val)
				Broadcast:send("恭喜" .. " " ..nick .." " .."与".. " " ..otherNick.." " .."结为侠侣，祝百年好合，早生贵子")
				
				
				Log:info(Log.MARRY,"1#$".._RoleGetId(jrole).."#$"..nick.."#$"..otherId1.."#$"..otherNick.."#$"..val.."#$0#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
				replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "恭喜你们成为侠侣，但是江湖险恶，你们要做到不离不弃，互相帮助，愿你们百年好合~")
end

