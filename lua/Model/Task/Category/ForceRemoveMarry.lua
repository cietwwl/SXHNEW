
_ForceRemoveMarry = _Task:newInstance()

function _ForceRemoveMarry:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end


_ForceRemoveMarry.TalkMenu = {
	{ 1, "确定", 0, },
	{ 2, "返回", 0, },
}


function _ForceRemoveMarry:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	putByte(1)--直接对话
	putString(self.name)
	putInt(0)
	return true
end


function _ForceRemoveMarry:dialog(jrole, npcid, deep)
		if deep == 1 then
			self:getState(jrole, npcid,deep)
		elseif deep <= #self.acceptDialogs+1 then
			prepareBody()
			putShort(0)
			fillNpcDialog(self.id, 0, 1, self.name .. "/" .. self.acceptDialogs[deep-1], self.color)
			putShort(0)
			sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
			
		elseif deep == 4 then
			self:showSure(jrole,npcid,deep)
		elseif deep == 5 then	
			self:doRmMarry(jrole,npcid,deep)
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "强制离婚成功~")
		else	
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
		end
end

function _ForceRemoveMarry:getState(jrole, npcid,deep)
	
		if not _GetIfMarry then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "你还没有侠侣呢")
			return
		end

		local temp = 0
		for _,v in pairs(self.item) do
		
			if _CheckBag(jrole,v) then
				temp = 1
			end
		end
		
		if temp==0 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "请带齐离婚物品")
			return
		end 
		
		
		local tempp =0
		for _,v in pairs({60002,60001,60000}) do
			
			if _CheckBag(jrole,v) then
				tempp = 1
			end
		end
		
		if tempp==0 then
			replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "请带上你的结婚戒指才能离婚")
			return
		end 
		
		
		prepareBody()
		putShort(0)
		fillNpcDialog(self.id, 0, 1, self.name .. "/" .."你要想离婚？" , self.color)
		putShort(0)
		sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end
function _ForceRemoveMarry:showSure(jrole,npcid,deep)

	prepareBody()
	putShort(0)
	fillNpcItemOP("确定要离婚吗？？？",getRGB(0),{1},self.TalkMenu)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end	
function _ForceRemoveMarry:doRmMarry(jrole,npcid,deep)
	local otherId = _GetRoleSpouseId(jrole)
	for _,v in pairs({60002,60001,60000}) do
			
			if _CheckBag(jrole,v) then
				prepareBody()
		
				fillBagDel(v)
			
				Bag:delItem(jrole, v, 1)
				
				putShort(0)
				
				sendMsg(jrole)
			end
	end	
	for _,v in pairs(self.item) do
		
			if _CheckBag(jrole,v) then
				prepareBody()
		
				fillBagDel(v)
			
				Bag:delItem(jrole, v, 1)
				
				putShort(0)
				
				sendMsg(jrole)
			end
		end
	
	if not _GetIfOnlineToRemoveSelf(jrole) then
		_ForceRemoveRoleMarry(jrole)
	end
	Log:info(Log.MARRY,"3#$".._RoleGetId(jrole).."#$".._RoleGetNick(jrole).."#$"..otherId.."#$#$#$0#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
end		