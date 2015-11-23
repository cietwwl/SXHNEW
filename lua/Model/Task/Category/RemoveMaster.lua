
_RemoveMaster = _Task:newInstance()

function _RemoveMaster:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end


_RemoveMaster = _Task:newInstance()

_RemoveMaster.TalkMenu = {
	{ 1, "确定", 0, },
	{ 2, "返回", 0, },
}
function _RemoveMaster:new(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _RemoveMaster:shownInTalkList(jrole)
	putInt(self.id)
	putLong(0)
	putByte(1)
	putString(self.name)
	putInt(0)
	return true
end

function _RemoveMaster:dialog(jrole, npcid, deep,uid)
	if deep == 1 then
		self:acceptDialog(jrole, npcid, deep,uid)
	elseif deep == 2 then	
		self:listItems(jrole, npcid, deep,uid)
	elseif deep == 3 then
		self:showSure(jrole, npcid, deep,uid)
	elseif deep == 4 then
		self:sendItemByMail(jrole, npcid, deep, uid)
	else
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！")
	end
end


function _RemoveMaster:acceptDialog(jrole, npcid, deep,uid)
	if not _GetIfHaveMaster(jrole) or not _GetCanAssessMaster(jrole) then
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "你不符合给师傅评价条件！！！")
		return
	end
	
	prepareBody()
	putShort(0)
	fillNpcDialog(self.id, 0, 1,  "/" .. self.acceptDialogs[deep], self.color)
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _RemoveMaster:listItems(jrole, npcid, deep,uid)
	prepareBody()
	
	putShort(0)
	
	putShort(300)
	
	placeholder('b')
	
	local fill = 0
	for _,v in pairs(self.item) do
		local template = ItemSet[v]
		if template then
			putInt(self.id)
			putLong(v)
			putByte(1)
			
			if fill == 0 then
				putString("[很满意".. "]   [你的师傅将获得 "..template.name.." 大礼包]")
			elseif fill == 1 then
				putString("[比较满意".. "]   [你的师傅将获得 "..template.name.." 大礼包]")
			elseif fill==2 then
				putString("[不满意".. "]   [你的师傅将获得 "..template.name.." 大礼包]")
			end	
			putInt(_EquipQualityGetColor(template.color or EquipQuality.White))
			
			fill = fill + 1
		else
			log.error("NPC", npcid, "出师奖励", self.id,"中", v, "不存在！")
		end
	end
	
	fillPlaceholder('b', fill)
	
	putShort(0)
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _RemoveMaster:showSure(jrole, npcid, deep, uid)
	prepareBody()
	putShort(0)
	
	fillNpcItemOP("确定给这个评价吗！",getRGB(0),{1},self.TalkMenu)
	
	putShort(0)
	
	sendMsg(jrole, MsgID.MsgID_Talk_To_Npc_Resp)
end

function _RemoveMaster:sendItemByMail(jrole, npcid, deep, uid)

	if not _GetIfHaveMaster(jrole) or not _GetCanAssessMaster(jrole) then
		replyMessage(jrole, 10, MsgID.MsgID_Talk_To_Npc_Resp, "你不符合给师傅评价条件！！！")
		return
	end
	
	local template = ItemSet[uid]
	local jItem = template:creatJavaItem(1)[1]
	
	local templatee = ItemSet[8056]
	local jIteme = templatee:creatJavaItem(1)[1]
	local otherId = _GetMasterId(jrole)
	MailManager:sendSysMail(_RoleGetId(jrole), "徒弟得奖励", "恭喜你得到奖励", 0, jIteme)
	MailManager:sendSysMail(_GetMasterId(jrole), "师傅得奖励", "恭喜你得到徒弟评价奖励", 0, jItem)
	
	_RemoveRoleMaster(jrole)
	replyMessage(jrole,1, MsgID.MsgID_Talk_To_Npc_Resp,"评价成功")
	Log:info(Log.MARRY,"7#$".._RoleGetId(jrole).."#$".._RoleGetNick(jrole).."#$"..otherId.."#$#$"..uid.."#$0#$"..os.date("%Y-%m-%d %H:%M:%S").."#$"..TianLongServer.srvId)
end


