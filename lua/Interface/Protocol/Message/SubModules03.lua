
--填充npc对话子模块，参数：taskid, monsterid, op, dialog, color
--@param op 0关闭、1继续对话2与NPC打斗
function fillNpcDialog(taskid, monsterid, op, dialog, color)
	putShort(301)--Serial
	
	putInt(taskid)
	putInt(monsterid)
	putByte(op)
	putString(dialog)
	putInt(color)
end

function fillFreshNPCState(jrole)
	local mapid = _CoordsGetAttr(_RoleGetCoords(jrole))
	
	local curmap = MapSet[mapid]
	if not curmap then
		return
	end
	
	putShort(302)--Serial
	
	placeholder('b')
	
	local npcCount = 0
	for _, v in pairs(curmap.npcs) do
		local npc = NPCSet[v.id]
		
		if npc then
			putInt(npc.id)
			local state = npc:getState(jrole)
			putInt(npc:getState(jrole))--角色在某个NPC处任务标志：0.无任务 1.有任务可接  2.任务进行中 3.有任务可提交
			putInt(v.x)
			putInt(v.y)
			
			npcCount = npcCount + 1
		end 
	end
	
	fillPlaceholder('b', npcCount)
end


--填充npc对话-物品操作子模块
function fillNpcItemOP(describe, color, opNumTbl, menuTbl)
	putShort(304)--Serial
	
	putString(describe)
	
	putInt(getRGB(color))
	
	putByte(#opNumTbl)
	
	for _, v in ipairs(opNumTbl) do
		putInt(v)
	end
	
	putByte(#menuTbl)
	
	for _, v in ipairs(menuTbl) do
		putByte(v[1])
		putString(v[2])
		putInt(v[3])
	end
end

function fillNpcRewind(deep, taskid, info, color)
	putShort(305)--Serial
	putByte(deep)
	putInt(taskid)
	putString(info)
	putInt(getRGB(color or 0))
end
