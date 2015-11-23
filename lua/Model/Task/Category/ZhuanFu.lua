
_ZhuanFu = _Task:newInstance()

function _ZhuanFu:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

_ZhuanFu.TalkMenu = {
	{ 1, "确定", 0, },
	{ 2, "返回", 0, },
}


function _ZhuanFu:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	putByte(1)--直接对话
	putString(self.name)
	putInt(0)
	return true
end


function _ZhuanFu:dialog(jrole, npcid, deep)
		if deep == 1 then
			self:getState(jrole, npcid,deep)
		elseif deep <= #self.acceptDialogs+1 then
			prepareBody()
			putShort(0)
			fillNpcDialog(self.id, 0, 1, self.name .. "/" .. self.acceptDialogs[deep-1], self.color)
			putShort(0)
			sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
			
		elseif deep == 9 then
			self:showSure(jrole,npcid,deep)
		elseif deep ==10 then	
			self:doZhuanFu(jrole,npcid,deep)
		else	
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
		end
end

function _ZhuanFu:getState(jrole, npcid,deep)
		

		
		prepareBody()
		putShort(0)
		fillNpcDialog(self.id, 0, 1, self.name .. "/" .."你确定要转服吗？此过程是不可逆的。" , self.color)
		putShort(0)
		sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end


function _ZhuanFu:showSure(jrole,npcid,deep)
	prepareBody()
	putShort(0)
	fillNpcItemOP("再次确定要转服吗？？？",getRGB(0),{1},self.TalkMenu)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end	

function _ZhuanFu:doZhuanFu(jrole,npcid,deep)

		local role_level =  _GetRoleLevel(jrole)

		if  _ZhuanFu_Mail(jrole) ~= 0  then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "操作失败！邮件未清空不可转服！！！")
			return
		end
		
	    if _ZhuanFu_Auction(jrole)==1 then
	    		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "操作失败！拍卖行中有你的物品不可转服！！！")
			return
		end
		
			_ZhuanFu_Qupai(jrole)
			_ZhuanFu_Role(jrole)

			_KickDown(jrole)
			_ZhuanFu_DontLogon(jrole)
			replyMessage(jrole,1, MsgID.MsgID_Talk_To_Npc_Resp,"成功")
end

