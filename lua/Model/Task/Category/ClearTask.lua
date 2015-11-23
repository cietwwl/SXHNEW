
_ClearTask = _Task:newInstance()

function _ClearTask:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

-------------------------------------------------------------------------------
--在NPC对话列表处的显示
function _ClearTask:shownInTalkList(jrole, npcid)
	
	putInt(self.id)
	putLong(0)
	putByte(1)--直接对话
	putString(self.name)
	putInt(0)
	return true
end

function _ClearTask:dialog(jrole, npcid, deep)

	if deep <= #self.acceptDialogs then
		prepareBody()
		putShort(0)
		fillNpcDialog(self.id, 0, 1, self.name .. "/" .. self.acceptDialogs[deep], self.color)
		putShort(0)
		sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
	elseif deep == #self.acceptDialogs + 1 then
		prepareBody()
		putShort(0)
		fillNpcDialog(self.id, 0, 1, self.name .. "/确定要花费" .. self.gold/10000 .. "金币清除" .. self.evilNum .. "点罪恶值？", self.color)
		putShort(0)
		sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
	else
		local gold = _RoleGetGold(jrole)
		if gold >= self.gold then
			_RoleSetGold(jrole,gold - self.gold)
			_ClearEvil(jrole,self.evilNum)
			--同步客户端数据
			prepareBody()
			--同步人物基本属性
			fillAttributes(jrole)
			putShort(0)
			sendMsg(jrole)
			replyMessage(jrole, 4, MsgID.MsgID_Talk_To_Npc_Resp, "操作已提交～")
		else
			replyMessage(jrole, 4, MsgID.MsgID_Talk_To_Npc_Resp, "金币不足～")
		end
	end		
end
