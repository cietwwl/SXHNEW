
function sendGetRoleInfo(jrole)
	prepareBody()
	putShort(0)--处理结果：0,成功
	putShort(0)--错误描述长度
	putString(_RoleGetNick(jrole))--昵称
	putByte(_RoleGetCanChangeName(jrole) and 1 or 0)--能否改名
	putInt(_RoleGetColor(jrole))--颜色
	putByte(_RoleGetSex(jrole))--性别：0男，1女
	putByte(_EnumOrdinal(_RoleGetVocation(jrole)))--职业
	putShort(_RoleGetAnimeGroup(jrole))--动画组id
	putShort(_RoleGetAnime(jrole))--动画id
	
	fillAttributes(jrole)--角色基本属性子模块
	
	fillAttributesDes(jrole)--角色属性与状态描述子模块
	
	for skillid, level in jmapIter(_RoleGetSkill(jrole)) do
		fillSkill(skillid, level)
	end
	
	for _, jEquip in jmapIter(_PackGetItemsAll(InusePack:getJPack(jrole))) do
		fillInuseAdd(jEquip)
	end
	
	for _, JItem in jmapIter(_PackGetItemsAll(Bag:getJPack(jrole))) do
		fillBagAdd(JItem)
	end
	
	fillDepotsInfo(jrole)
	
	fillEpithet(jrole)
	
	fillShenZhouChargeInfo(jrole)
	
	_TaskCheckBattle(jrole)
	
	sendMsg(jrole, MsgID.MsgID_Role_Get_Info_Resp)
	
end
