
_GangFight = _Task:newInstance()

function _GangFight:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end


_GangFight = _Task:newInstance()

_GangFight.TalkMenu = {
	{ 1, "确定", 0, },
	{ 2, "返回", 0, },
}
function _GangFight:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _GangFight:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	putByte(1)
	putString(self.name)
	putInt(0)
	return true
end

function _GangFight:dialog(jrole, npcid, deep,uid)

	if deep == 1 then
		self:acceptDialog(jrole, npcid, deep,uid)
	elseif deep == 2 then
		self:showSure(jrole, npcid, deep, uid)
	elseif deep == 3 then
		self:doGang(jrole, npcid, deep, uid)
	else
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
	end
end


function _GangFight:acceptDialog(jrole, npcid, deep,uid)

	prepareBody()
	putShort(0)
	fillNpcDialog(self.id, 0, 1,  "/" .. self.acceptDialogs[deep], self.color)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
	
end


function _GangFight:showSure(jrole, npcid, deep, uid)

	prepareBody()
	putShort(0)
	fillNpcItemOP("此操作要扣除10个金币，确定吗?",getRGB(0),{1},self.TalkMenu)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)

end


function _GangFight:doGang(jrole, npcid, deep, uid)


		if _RoleGetGold(jrole)<100000 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "建立帮战，最少要10个金币的，准备好了再来！！！")
			return
		end
	
	
	local hour = _GetNowHour()
	local min =  _GetNowMin() 

		if (hour < 6) then
		    replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "今天的帮战报名还未开始！")
			return
		end	
		if (hour >=11)  then
			replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "今天的帮战报名已经结束！")
			return
		end

		if not _GetIfGang(jrole) then
		   replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "尚未加入帮会，不能参加帮战！")
		   return
		end
		
	  if  _GetLeader(jrole) ~= _RoleGetId(jrole) then
	    replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "你不是帮主，不可以创建帮战！")
	    return
	  end
	 
	 if _GetGangCount() > 50 then
	 
	   replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "帮战报名最多50组！")
	    return
	  end
	 
	 
 --[[
      if _GetGangLevel(jrole)<2 then
	    replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "你的帮派不足2级，不能创建帮战！")
	    return
	  end
		]]
	
	  if _GetRoleLevel(jrole)<80 then
	      replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "你的等级不足80级,不能建立！")
	      return
	   end	
	
		
	  if _GetIfHas(jrole) then
		replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "你已报过名！")
		return
	  end

		
	_RoleSetGold(jrole,(_RoleGetGold(jrole)-100000))

	_LeaderSetGangFight(_RoleGetId(jrole))
	
	if _PutProperty(_RoleGetId(jrole),_RoleGetId(jrole)) then
	

		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "建立帮战成功")
	else
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "建立帮战失败")	
	end
	
	
end



