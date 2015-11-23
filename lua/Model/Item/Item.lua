
--装备及道具类型常量
ItemConst = {
	Prop = 0,
	Equip = 1,
	TaskProp = 2,
}

_Item = { }

function _Item:new(template)
	if template.type == ItemConst.Prop then
		if template.subtype == PropConst.Drug then
			if template.specificType == DrugConst.Blue then
				return _DrugBlue:new(template)
			elseif template.specificType == DrugConst.Red then
				return _DrugRed:new(template)
			else
				log.error("类型未实现？" .. template.name)
			end
		elseif template.subtype == PropConst.SkillBook then
			return _SkillBook:new(template)
		elseif template.subtype == PropConst.GiftBag then
			return _GiftBag:new(template)
		elseif template.subtype == PropConst.TPScroll then
			return _TPScroll:new(template)
		elseif template.subtype == PropConst.MarryRing then
			return _MarryRing:new(template)	
		elseif template.subtype == PropConst.Trumpet then
			ItemConst.TrumpetTemp = ItemConst.TrumpetTemp or template.id
			return _Trumpet:new(template)
		elseif template.subtype == PropConst.RandomBox then
			return _RandomBox:new(template)
		elseif template.subtype == PropConst.Coupon then
			return _Coupon:new(template)
		elseif template.subtype == PropConst.Grocery then
			return _Grocery:new(template)
		elseif template.subtype == PropConst.Buff then
			return _Buff:new(template)
		elseif template.subtype == PropConst.WashPoint then 
			return _WashPoint:new(template)
		elseif template.subtype == PropConst.BagExt then
			return _BagExt:new(template)
		elseif template.subtype == PropConst.Scapegoat then
			ItemConst.ScapegoatTemp = ItemConst.ScapegoatTemp or { }
			ItemConst.ScapegoatTemp[template.id] = { template.minlevel, template.maxlevel, }
			return _Scapegoat:new(template)
		elseif template.subtype == PropConst.ShedAdd then
			return _ShedAdd:new(template)
		elseif template.subtype == PropConst.ShedExt then
			return _ShedExt:new(template)
		elseif template.subtype == PropConst.TaskScroll then
			return _TaskScroll:new(template)
		elseif template.subtype == PropConst.JoinCommunity then
			return _JoinCommunity:new(template)
		elseif template.subtype == PropConst.CharmProp then
			return _CharmProp:new(template)
		elseif template.subtype == PropConst.Epithet then
			return _Epithet:new(template)
		elseif template.subtype == PropConst.Fashion then
			return _Fashion:new(template)
		elseif template.subtype == PropConst.GangProp then
			return _GangProp:new(template)
		elseif template.subtype == PropConst.MagicWeaponRST then
			return _MagicWeaponRST:new(template)
		elseif template.subtype == PropConst.WashMurderPunish then
			return _WashMurderPunish:new(template)
		elseif template.subtype == PropConst.AgainstSneakAttack then
			table.getglobal("Temp.Item").AgainstSneakAttack = template.id
			return _Grocery:new(template)
		elseif template.subtype == PropConst.RefineNecessary then
			table.getglobal("Temp.Item").RefineNecessary = template.id
			return _EquipRefine:new(template)
		elseif template.subtype == PropConst.EquipProtect then
			return _EquipRefine:new(template)
		elseif template.subtype == PropConst.NecessaryProtect then
			return _EquipRefine:new(template)
		elseif template.subtype == PropConst.ItemRefineSuccessRate then
			return _EquipRefine:new(template)
		elseif template.subtype == PropConst.PerpetuateEquip then
			return _InterimEquipPerm:new(template)
		elseif template.subtype == PropConst.WashSneakAttackNum then
			return _WashSneakAttackNum:new(template)
		elseif template.subtype == PropConst.BossCard then
			return _BossCard:new(template)
		else
			log.error("subtype类型未实现？" .. template.name)
		end
	elseif template.type == ItemConst.Equip then
		return _Equip:new(template)
	elseif template.type == ItemConst.TaskProp then
		return _TaskProp:new(template)
	else
		log.error("类型未实现", template.type)
	end
end
