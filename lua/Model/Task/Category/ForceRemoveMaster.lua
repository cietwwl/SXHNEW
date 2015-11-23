
_ForceRemoveMaster = _Task:newInstance()

function _ForceRemoveMaster:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end


_ForceRemoveMaster.TalkMenu = {
	{ 1, "确定", 0, },
	{ 2, "返回", 0, },
}


function _ForceRemoveMaster:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	putByte(1)--直接对话
	putString(self.name)
	putInt(0)
	return true
end


function _ForceRemoveMaster:dialog(jrole, npcid, deep)
		if deep == 1 then
			self:getState(jrole, npcid,deep)
		elseif deep == 2 then
			prepareBody()
			putShort(0)
			fillNpcDialog(self.id, 0, 1, self.name .. "/" .. self.acceptDialogs[1], self.color)
			putShort(0)
			sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
			
		elseif deep == 3 then
			self:showSure(jrole,npcid,deep)
		elseif deep == 4 then	
			self:doRmMaster(jrole,npcid,deep)
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "强制解除师傅成功~")
		else	
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
		end
end

function _ForceRemoveMaster:getState(jrole, npcid,deep)
		if not _GetIfHaveMaster(jrole)	then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "你没有师傅！！！")
			return
		end
				
		if _RoleGetGold(jrole)<10000 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "解除师傅需要1金，请准备好了再来！！！")
			return
		end
		
		if _GetRoleLevel(jrole)>=50	then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "你已出师！！！")
			return
		end
		
		
		prepareBody()
		putShort(0)
		fillNpcDialog(self.id, 0, 1, self.name .. "/" .."你要解除你的师傅？" , self.color)
		putShort(0)
		sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end
function _ForceRemoveMaster:showSure(jrole,npcid,deep)
	
	prepareBody()
	putShort(0)
	fillNpcItemOP("确定要解除师傅吗？？？",getRGB(0),{1},self.TalkMenu)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end	
function _ForceRemoveMaster:doRmMaster(jrole,npcid,deep)
	local otherId = _GetMasterId(jrole)
	_RoleSetGold(jrole,(_RoleGetGold(jrole)-10000))
	
	if not _GetIfMasterOnlineToRemoveSelf(jrole) then
		_ForceRemoveRoleMaster(jrole)
	end	
	Log:info(Log.MARRY,"9#$".._RoleGetId(jrole).."#$".._RoleGetNick(jrole).."#$"..otherId.."#$#$#$1#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
end		