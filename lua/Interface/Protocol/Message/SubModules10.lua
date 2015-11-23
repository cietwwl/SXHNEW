--填充护送NPC信息
function fillConvoyNpcInfo(Groupid,NPCname)
	putShort(1000)--Serial
	putShort(Groupid)--动画组id
	putString(NPCname)--NPC名称
end

--填充护送NPC结束
function fillConvoyNpcEnd()
	putShort(1001)--Serial
end
--填充战斗请求
function fillFightReq(Enemyid)
	putShort(1002)--Serial
	putInt(Enemyid)--怪物ID
end
