

_ReRing = _Task:newInstance()

function _ReRing:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

_ReRing.TalkMenu = {
	{ 1, "确定", 0, },
	{ 2, "返回", 0, },
}


function _ReRing:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	putByte(1)--直接对话
	putString(self.name)
	putInt(0)
	return true
end


function _ReRing:dialog(jrole, npcid, deep)
		if deep == 1 then
			self:getState(jrole, npcid,deep)
		elseif deep <= #self.acceptDialogs+1 then
			prepareBody()
			putShort(0)
			fillNpcDialog(self.id, 0, 1, self.name .. "/" .. self.acceptDialogs[deep-1], self.color)
			putShort(0)
			sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
			
		elseif deep == 3 then
			self:showSure(jrole,npcid,deep)
		elseif deep == 4 then	
			self:doReRing(jrole,npcid,deep)
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "回收戒指成功~")
		else	
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
		end
end

function _ReRing:getState(jrole, npcid,deep)

		
		if _GetIfMarry(jrole) then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "有配偶就不要回收此戒指了")
			return
		end
		
		
		if not _CheckBag(jrole,self.ring) then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "你没有此戒指")
			return
		end
		
		
		
		
		
			
		prepareBody()
		putShort(0)
		fillNpcDialog(self.id, 0, 1, self.name .. "/" .."你要回收戒指吗" , self.color)
		putShort(0)
		sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
			
end
function _ReRing:showSure(jrole,npcid,deep)

	prepareBody()
	putShort(0)
	fillNpcItemOP("确定要回收此戒指吗？？？",getRGB(0),{1},self.TalkMenu)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end	


function _ReRing:doReRing(jrole,npcid,deep)

			
			if _CheckBag(jrole,self.ring) then
				prepareBody()
		
				fillBagDel(self.ring)
			
				Bag:delItem(jrole, self.ring, 1)
				
				putShort(0)
				
				sendMsg(jrole)
			end
		Log:info(Log.MARRY,"10#$".._RoleGetId(jrole).."#$".._RoleGetNick(jrole).."#$#$#$"..self.ring.."#$0#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)

end		