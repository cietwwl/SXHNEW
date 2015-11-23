
_ForceRemoveApprentice = _Task:newInstance()

function _ForceRemoveApprentice:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end


_ForceRemoveApprentice = _Task:newInstance()

_ForceRemoveApprentice.TalkMenu = {
	{ 1, "确定", 0, },
	{ 2, "返回", 0, },
}
function _ForceRemoveApprentice:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _ForceRemoveApprentice:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	putByte(1)
	putString(self.name)
	putInt(0)
	return true
end

function _ForceRemoveApprentice:dialog(jrole, npcid, deep,uid)
	if deep == 1 then
		self:acceptDialog(jrole, npcid, deep,uid)
	elseif deep == 2 then	
		self:listApps(jrole, npcid, deep,uid)
	elseif deep == 3 then
		self:showSure(jrole, npcid, deep,uid)
	elseif deep == 4 then
		self:removeApp(jrole, npcid, deep, uid)
	else
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
	end
end


function _ForceRemoveApprentice:acceptDialog(jrole, npcid, deep,uid)

	if not _GetIfHaveApprentice(jrole) then
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "你没有徒弟！！！")
		return
	end
	if _RoleGetGold(jrole)<30000 then
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "解除徒弟需要3金，请准备好了再来！！！")
		return
	end

	prepareBody()
	putShort(0)
	fillNpcDialog(self.id, 0, 1,  "/" .. self.acceptDialogs[deep], self.color)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _ForceRemoveApprentice:listApps(jrole, npcid, deep,uid)
	prepareBody()
	
	putShort(0)
	
	putShort(300)
	
	placeholder('b')
	
	local fill = 0
	
	local apprentices = _GetApprentice(jrole)
	for v in jlistIter(apprentices) do

		putInt(self.id)
		putLong(v)
		putByte(1)
		putString(_GetAppNameById(jrole,v))
		putInt(_EquipQualityGetColor(EquipQuality.White))
		fill = fill + 1

	end
	
	fillPlaceholder('b', fill)
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _ForceRemoveApprentice:showSure(jrole, npcid, deep, uid)
	prepareBody()
	putShort(0)
	fillNpcItemOP("确定要解除这个徒弟吗?",getRGB(0),{1},self.TalkMenu)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _ForceRemoveApprentice:removeApp(jrole, npcid, deep, uid)
	_RoleSetGold(jrole,(_RoleGetGold(jrole)-30000))
	if not _GetIfAppOnlineToRemove(jrole,uid) then
		_ForceRemoveRoleApp(jrole,uid)
	end
	replyMessage(jrole,1, MsgID.MsgID_Talk_To_Npc_Resp,"删除成功")
	Log:info(Log.MARRY,"8#$".._RoleGetId(jrole).."#$".._RoleGetNick(jrole).."#$"..uid.."#$#$#$3#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
end


