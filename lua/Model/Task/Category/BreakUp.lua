MailManager = luajava.bindClass("com.joyveb.tlol.mail.MailManager"):getInstance()

_BreakUp = _Task:newInstance()

function _BreakUp:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

_BreakUp.TalkMenu = {
	{ 1, "确定", 0, },
	{ 2, "返回", 0, },
}



function _BreakUp:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	putByte(1)--直接对话
	putString(self.name)
	putInt(0)
	return true
end


function _BreakUp:dialog(jrole, npcid, deep)
		if deep ==1 then
			self:getState(jrole,npcid,deep)
		elseif deep <= #self.acceptDialogs+1 then
			prepareBody()
			putShort(0)
			fillNpcDialog(self.id, 0, 1, self.name .. "/" .. self.acceptDialogs[deep-1], self.color)
			putShort(0)
			sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
		elseif deep == 4 then
			self:showSure(jrole,npcid,deep)
		elseif deep == 5 then
			self:doBreakUp(jrole,npcid,deep)
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "好吧！尊重你们的意愿，离婚成功~")
		end
end

function _BreakUp:getState(jrole, npcid,deep)
		if not _RoleGetTeam(jrole) or  _GetTeamNum(jrole) == 1 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "请组上你的侠侣！！！")
			return
		end	
		
		
		if _GetTeamNum(jrole) ~= 2 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "队伍只能有两个人！！！")
			return
		end
		
		local otherJrole = nil
		local otherId =nil
		otherId= _GetMarryTeamOtherId(jrole)
		otherJrole = _IdGetRole(jrole)
		
		if _GetRoleSpouseId(jrole)~= otherId then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "不是侠侣，不能离婚！！！")
			return
		end
		
		local temp = 0
		for _,v in pairs(self.item) do
		
			if _CheckBag(jrole,v) then
				temp = 1
			end
		end
		
		if temp==0 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "请准备好离婚证书再来！！！")
			return
		end 
		
		local tempp =0
		for _,v in pairs({60002,60001,60000}) do
			
			if _CheckBag(jrole,v) then
				tempp = 1
			end
		end
		
		if tempp==0 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "请带上你的结婚戒指才能离婚！！！")
			return
		end 
		
		local otherJrole = nil
		local otherId =nil
		otherId= _GetMarryTeamOtherId(jrole)
		otherJrole = _IdGetRole(jrole)
		
		
		local temppp =0
		for _,v in pairs({60002,60001,60000}) do
			
			if _CheckBag(otherJrole,v) then
				temppp = 1
			end
		end
		
		if temppp==0 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "请你的侠侣带上结婚戒指才能离婚")
			return
		end 
		
		
		
		
		prepareBody()
		putShort(0)
		fillNpcDialog(self.id, 0, 1, self.name .. "/" .."你们想要离婚吗" , self.color)
		putShort(0)
		sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
				
end


function _BreakUp:showSure(jrole,npcid,deep)
	if not _RoleGetTeam(jrole) or  _GetTeamNum(jrole) == 1 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "队员已离开队伍！！！")
			return
		end	
		
		
		if _GetTeamNum(jrole) ~= 2 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "离婚过程不可以加入另一队员！！！")
			return
		end
	
	
	
	prepareBody()
	putShort(0)
	fillNpcItemOP("确定要离婚吗？？？",getRGB(0),{1},self.TalkMenu)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end	


function _BreakUp:doBreakUp(jrole, npcid,deep)
		if not _RoleGetTeam(jrole) or  _GetTeamNum(jrole) == 1 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "队员已离开队伍！！！")
			return
		end	
		
		
		if _GetTeamNum(jrole) ~= 2 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "结婚过程不可以加入另一队员！！！")
			return
		end


		local otherJrole = nil
		local otherId =nil
		otherId= _GetMarryTeamOtherId(jrole)
		otherJrole = _IdGetRole(jrole)


		local nick = _RoleGetNick(jrole)
		local otherNick = _RoleGetNick(otherJrole)
		local p =1
		while p<9 do
			for _,v in pairs({60002,60001,60000}) do
				
					prepareBody()
			
					fillBagDel(v)
				
					Bag:delItem(jrole, v, 1)
					
					putShort(0)
					p = p + 1
					sendMsg(jrole)
				
			end
		end
	local n = 1
	while n<9 do
		for _,v in pairs({60002,60001,60000}) do
			
				prepareBody()
		
				fillBagDel(v)
			
				Bag:delItem(otherJrole, v, 1)
				n = n+1
				putShort(0)
				sendMsg(otherJrole)
			
		end
	end
		for _,v in pairs(self.item) do
		
			if _CheckBag(jrole,v) then
				prepareBody()
		
				fillBagDel(v)
			
				Bag:delItem(jrole, v, 1)
				
				putShort(0)
				
				sendMsg(jrole)
			
			end
		end

		
				
		
		_RemoveMarry(jrole)
		_RemoveMarry(otherJrole)
		
		MailManager:sendSysMail(_RoleGetId(jrole), "离婚通知", "你已与"..otherNick.."成功解除了婚姻关系", 0,nil)
		MailManager:sendSysMail(otherId, "离婚通知","你已与"..nick.."成功解除了婚姻关系", 0,nil)

		Log:info(Log.MARRY,"2#$".._RoleGetId(jrole).."#$"..nick.."#$"..otherId.."#$"..otherNick.."#$#$0#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
		
		
end	