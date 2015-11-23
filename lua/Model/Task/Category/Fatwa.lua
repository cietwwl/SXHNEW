--武林追杀令
_Fatwa = _Task:newInstance() 

_Fatwa.confirmMenu = {
	{ 1, "确定", 0, },
	{ 2, "取消", 0, },
}

function _Fatwa:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Fatwa:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	putByte(1)--直接对话
	putString(self.name)
	putInt(0)
	return true
end

function _Fatwa:dialog(jrole, npcid, deep, eid)	
	if deep == 1 then
		self:roleJudgment(jrole, npcid, deep)	
	elseif deep == 2 then
		self:listItems(jrole, npcid, deep)
	elseif deep == 3 then
		self:fatwaItems(jrole, npcid, deep)
	elseif deep == 4 then
		self:showItemInfo(jrole, npcid, deep, eid)
	elseif deep == 5 then
		self:targetJudgment(jrole, npcid, deep, eid)
	else
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "发布失败！")
	end
end

function _Fatwa:roleJudgment(jrole, npcid, deep)
	local role_level  = _GetRoleLevel(jrole)
	if role_level < 60 then 		
		replyMessage(jrole,1, MsgID.MsgID_Talk_To_Npc_Resp, "等级不足！ 少侠还是潜心修炼吧！")
		return
	end		
	prepareBody()
	putShort(0)
	fillNpcDialog(self.id, 0, 1,  "/" .. self.acceptDialogs[deep], self.color)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _Fatwa:listItems(jrole, npcid, deep)
	prepareBody()
		
	putShort(0)	
	
	putShort(300)--列表形式	
	
	placeholder('b')--列表项数量	
	
	local fill = 0	
	
	putInt(self.id)
	putLong(0)
	putByte(2)	
	putString("从仇人列表中选取")	
	putInt(0)
	fill = fill + 1	
	fillPlaceholder('b', fill)
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end
function _Fatwa:fatwaItems(jrole, npcid, deep)
	prepareBody()
		
	putShort(0)	
	
	putShort(300)--列表形式	
	
	placeholder('b')--列表项数量	
	
	local fill = 0	
	if not _ifHasEnemys(jrole) then 		
		replyMessage(jrole,1, MsgID.MsgID_Talk_To_Npc_Resp, "少侠没有仇人！莫开老夫的玩笑！")
		return
	end			
	local list = _getEnemyIdList(jrole)	
	for id in jlistIter(list) do
		if not _getEnemyNameById(id) then
			return
		end
		putInt(self.id)
		putLong(id)
		putByte(2)
		putString(_getEnemyNameById(id))	
		fill = fill + 1	
		putInt(0)
	end
			
	fillPlaceholder('b', fill)
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _Fatwa:showItemInfo(jrole, npcid, deep, eid)
	prepareBody()
	putShort(0)
	
	fillNpcItemOP("哈哈哈！少侠果然是快意恩仇之人，老夫甚是喜欢！你确定要发布么！",getRGB(0),{1},self.confirmMenu)
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _Fatwa:targetJudgment(jrole, npcid, deep, eid)

	local str =  _DoFatwa(jrole,eid)
	----同步客户端数据
	--清空缓冲区
	prepareBody()
	--同步人物基本属性
	fillAttributes(jrole)
	putShort(0)
	--发送消息
	sendMsg(jrole)
	replyMessage(jrole,1, MsgID.MsgID_Talk_To_Npc_Resp,str)
	
end
