
_JoinGangFight = _Task:newInstance()

function _JoinGangFight:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end


_JoinGangFight = _Task:newInstance()

_JoinGangFight.TalkMenu = {
	{ 1, "确定", 0, },
	{ 2, "返回", 0, },
}
function _JoinGangFight:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _JoinGangFight:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	putByte(1)
	putString(self.name)
	putInt(0)
	return true
end

function _JoinGangFight:dialog(jrole, npcid, deep,uid)

	if deep == 1 then
		self:acceptDialog(jrole, npcid, deep,uid)
	elseif deep == 2 then	
		self:showChoice(jrole, npcid, deep,uid)
	elseif deep == 3 then
		self:showSure(jrole, npcid, deep,uid)
	elseif deep == 4 then
		self:doJoinGang(jrole, npcid, deep, uid)
	else
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
	end
end


function _JoinGangFight:acceptDialog(jrole, npcid, deep,uid)

	prepareBody()
	putShort(0)
	fillNpcDialog(self.id, 0, 1,  "/" .. self.acceptDialogs[deep], self.color)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
	
end

function _JoinGangFight:showChoice(jrole, npcid, deep,uid)
	prepareBody()
		
	putShort(0)
	
	putShort(300)
	
	placeholder('b')
	local fill = 0
	
		
		local list = _GetGangFight()
		for v in jsetIter(list) do
			putInt(self.id)
			putLong(v)
			putByte(1)
			putString(_GetGangFightName(v))
			putInt(_EquipQualityGetColor(EquipQuality.Red))
			fill = fill + 1
		end
	
	fillPlaceholder('b', fill)
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
	
end

function _JoinGangFight:showSure(jrole, npcid, deep, uid)

	prepareBody()
	putShort(0)
	fillNpcItemOP("确定吗?",getRGB(0),{1},self.TalkMenu)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)

end

function _JoinGangFight:doJoinGang(jrole, npcid, deep, uid)

 		local hour = _GetNowHour()
    	local min =  _GetNowMin() 

		if (hour < 6) then
		    replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "今天的帮战报名还未开始！")
			return
		end	
		if hour >=11  then
			replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "今天的帮战报名已经结束！")
			return
		end


	
		if _GetIfHas(jrole) then
			replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "你已报过名！")
		  	return
		end

		if not _GetIfGang(jrole) then
		   replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "尚未加入帮会,不能参加帮战！")
		   return
		end
		
		if not _IfIsInTheGang(jrole,uid) then
		   replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "你不属于这个帮！")
		   return
		end
		
	   if _GetRoleLevel(jrole)<60 then
	      replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "你的等级不足60级,不能参加！")
	      return
	   end	
		
		
		if _ifGangFightEnough(uid) then
			replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "参战名单已满！")
		  	return
		end
		
--[[
	for i=1,7 do
	 _PutProperty(_RoleGetId(jrole),uid)
	end
]]

	if _PutProperty(_RoleGetId(jrole),uid) then
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "参加帮战成功")
	else
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "参加帮战失败")	
	end

end



