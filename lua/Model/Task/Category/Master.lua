ilManager = luajava.bindClass("com.joyveb.tlol.mail.MailManager"):getInstance()

_Master = _Task:newInstance()

function _Master:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

_Master.TalkMenu = {
	{ 1, "确定", 0, },
	{ 2, "返回", 0, },
}


function _Master:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	putByte(1)--直接对话
	putString(self.name)
	putInt(0)
	return true
end


function _Master:dialog(jrole, npcid, deep)
		
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
			self:doMaster(jrole,npcid,deep)
		else	
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
		end
end

function _Master:getState(jrole, npcid,deep)
		
		local role_level =  _GetRoleLevel(jrole)
		if role_level < self.minLevel then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "师傅要50级才能收徒！！！")
			return
		end
	
		if _GetApprenticeNum(jrole) >=2 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "最多只能收两个徒弟！！！")
			return
		end	
		
		if not _RoleGetTeam(jrole) or  _GetTeamNum(jrole) == 1 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "请组上你的徒弟！！！")
			return
		end	
		
		
		if _GetTeamNum(jrole) ~= 2 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "只能两个人进行收徒！！！")
			return
		end
		 

		otherId= _GetMarryTeamOtherId(jrole)
		otherJrole = _IdGetRole(jrole)
		
		if _GetRoleLevel(otherJrole)< 20 or _GetRoleLevel(otherJrole)>=50 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "徒弟要大于20级小于50级才可拜师！！！")
			return
		end
		
		
		if _GetRoleLevel(otherJrole)+20> _GetRoleLevel(jrole) then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "师傅要比徒弟高20级以上才可以！！！")
			return
		end
	
		
		if _GetIfHaveMaster(otherJrole)	then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "你要收的徒弟已有一个师傅了！！！")
			return
		end

		if not _GetCanMaster(otherJrole) then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "徒弟目前还处于师徒惩罚期间内，无法拜师！！！")
			return
		end
		if not _GetCanApprentice(jrole) then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "你目前还处于师徒惩罚期间内，无法收徒！！！")
			return
		end
	
			
		prepareBody()
		putShort(0)
		fillNpcDialog(self.id, 0, 1, self.name .. "/" .."你要收徒吗" , self.color)
		putShort(0)
		sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
			
end
function _Master:showSure(jrole,npcid,deep)
	if not _RoleGetTeam(jrole) or  _GetTeamNum(jrole) == 1 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "队员已离开队伍！！！")
			return
		end	
		
		
		if _GetTeamNum(jrole) ~= 2 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "收徒过程不可以加入另一队员！！！")
			return
		end
	
	prepareBody()
	putShort(0)
	fillNpcItemOP("确定要收徒吗？？？",getRGB(0),{1},self.TalkMenu)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end	


function _Master:doMaster(jrole,npcid,deep)
		
		local role_level =  _GetRoleLevel(jrole)
		if role_level < self.minLevel then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "收徒失败！师傅要50级才能收徒！！！")
			return
		end
	
		if _GetApprenticeNum(jrole) >=2 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "收徒失败！最多只能收两个徒弟！！！")
			return
		end	
		
		if not _RoleGetTeam(jrole) or  _GetTeamNum(jrole) == 1 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "收徒失败！请组上你的徒弟！！！")
			return
		end	
		
		if _GetTeamNum(jrole) ~= 2 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "收徒失败！只能两个人进行收徒！！！")
			return
		end

		otherId= _GetMarryTeamOtherId(jrole)
		otherJrole = _IdGetRole(jrole)
		
		if _GetRoleLevel(otherJrole)< 20 or _GetRoleLevel(otherJrole)>=50 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "收徒失败！徒弟要大于20级小于50级才可拜师！！！")
			return
		end
		
		if _GetRoleLevel(otherJrole)+20> _GetRoleLevel(jrole) then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "收徒失败！师傅要比徒弟高20级以上才可以！！！")
			return
		end
		
		if _GetIfHaveMaster(otherJrole)	then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "收徒失败！你要收的徒弟已有一个师傅了！！！")
			return
		end

		if not _GetCanMaster(otherJrole) then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "收徒失败！徒弟目前还处于师徒惩罚期间内，无法拜师！！！")
			return
		end
		if not _GetCanApprentice(jrole) then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "收徒失败！你目前还处于师徒惩罚期间内，无法收徒！！！")
			return
		end

		if not _RoleGetTeam(jrole) or  _GetTeamNum(jrole) == 1 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "收徒失败！队员已离开队伍！！！")
			return
		end	
		
		if _GetTeamNum(jrole) ~= 2 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "收徒失败！收徒过程不可以加入另一队员！！！")
			return
		end
	
		
		local otherId1= _GetMarryTeamOtherId(jrole)
		local otherJrole1 = _IdGetRole(jrole)
		local nick = _RoleGetNick(jrole)
		local otherNick = _RoleGetNick(otherJrole1)
			
		_RoleSetApprentice(jrole,otherId1)
		_RoleSetMaster(otherJrole1,_RoleGetId(jrole))
		MailManager:sendSysMail(_RoleGetId(jrole), "恭喜收徒成功", "恭喜你已与"..otherNick.."成为师徒", 0,nil)
		MailManager:sendSysMail(otherId1, "恭喜拜师成功","恭喜你已与"..nick.."成为师徒", 0,nil)
		Broadcast:send("恭喜" .. " " ..nick .." " .."收".. " " ..otherNick.." " .."为徒")
		
		Log:info(Log.MARRY,"5#$".._RoleGetId(jrole).."#$"..nick.."#$"..otherId1.."#$"..otherNick.."#$#$0#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "恭喜你们成为师徒~")
end		