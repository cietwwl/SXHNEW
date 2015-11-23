
_DismissGang = _Task:newInstance()

function _DismissGang:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _DismissGang:shownInTalkList(jrole)
	if jrole:getJobTitle():highRigths() and _IsGangLoaded(_RoleGetGangid(jrole)) then
		putInt(self.id)
		putLong(0)
		putByte(1)--直接对话
		putString(self.name)
		putInt(0)
		return true
	end
end

function _DismissGang:dialog(jrole, npcid, deep)
	if deep <= #self.acceptDialogs then
		prepareBody()
		putShort(0)
		fillNpcDialog(self.id, 0, 1, self.name .. "/" .. self.acceptDialogs[deep], self.color)
		putShort(0)
		sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
	elseif deep == #self.acceptDialogs + 1 then
		prepareBody()
		putShort(0)
		fillNpcDialog(self.id, 0, 1, self.name .. "/确定要解散帮会？", self.color)
		putShort(0)
		sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
	elseif not _IsGangLoaded(_RoleGetGangid(jrole)) then
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "尚未加入帮会！")
	elseif not jrole:getJobTitle():highRigths() then
		replyMessage(jrole, 3, MsgID.MsgID_Talk_To_Npc_Resp, "没有相关的权限！")
	else
		GangService:dismiss(jrole)
		replyMessage(jrole, 4, MsgID.MsgID_Talk_To_Npc_Resp, "操作已提交～")
	end
end
