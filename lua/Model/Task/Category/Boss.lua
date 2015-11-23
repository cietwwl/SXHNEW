
_Boss = _Task:newInstance()

function _Boss:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

_Boss.TalkMenu = {
	{ 1, "确定", 0, },
	{ 2, "返回", 0, },
}


function _Boss:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	putByte(1)--直接对话
	putString(self.name)
	putInt(0)
	return true
end


function _Boss:dialog(jrole, npcid, deep)

		if deep == 1 then
			self:showSure(jrole,npcid,deep)
		elseif deep == 2 then	
			self:doBoss(jrole,npcid,deep)

		else	
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
		end
end


function _Boss:showSure(jrole,npcid,deep)
	prepareBody()
	putShort(0)
	fillNpcItemOP("确定要参加吗？？？",getRGB(0),{1},self.TalkMenu)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end	

function _Boss:doBoss(jrole,npcid,deep)
	local hour = _GetNowHour()
	local min =  _GetNowMin() 
--or (hour==21)
		if not ((hour==10 ) or (hour==22) )then
		    replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "世界BOSS战只在每日两个时段 10-11点，22-23点开启！")
			return
		end	

	local result = _JoinBoss(jrole)
	if  result ==2  then
			replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "您的操作过快！")
			return

	elseif  result ==3  then
			replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "BOSS已被击毁！")
			return

	elseif  result >100 then
			replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "冷却时间未到,还有"..((result*1)-100).."秒！")
			return

	elseif  result ==5 then
			replyMessage(jrole, 2, MsgID.MsgID_Talk_To_Npc_Resp, "意外故障！")
			return

			
	elseif  result==1 then
				replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "报名成功~")
			return
	end		
			
end

