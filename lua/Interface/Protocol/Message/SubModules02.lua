
function fillInuseAdd(Wearable)
	local template = ItemSet[_ItemGetTid(Wearable)]
	if not template then
		debug.traceback("物品" .. _ItemGetTid(Wearable) .. "不存在！")
		return
	end
	
	template:fillInuseAdd(Wearable)
end

function fillEquipUse(uid)
	putShort(201)--Serial
	putLong(uid)
end

function fillUnwieldEquip(subtype)
	putShort(202)--Serial
	putByte(subtype)
end

function fillInuseDel(jEquip)
	local template = ItemSet[_ItemGetTid(jEquip)]
	if not template then
		debug.traceback("物品" .. _ItemGetTid(jEquip) .. "不存在！")
		return
	end
	putShort(203)--Serial
	putByte(_EnumOrdinal(template.subtype))
end

function fillBagAdd(jItem)
	local tid = jItem:getTid()
	local template = ItemSet[tid]
	if not template then
		debug.traceback("物品" .. tid .. "不存在！")
		return
	end
	
	if not template.fillBagAdd then
		print(template)
	end
	template:fillBagAdd(jItem)
end

function fillFlushEquip(equip)
	putShort(207)--Serial
	putLong(_ItemGetUid(equip))--装备id
	_PutItemNameAddColor(equip)
end

function fillBagDel(uid, num)
	putShort(208)--Serial
	putLong(uid)
	putShort(num or 1)
end
