
--消息子模块

function fillSkill(skillid, level)
	putShort(102)--Serial
	
	putShort(getSkillIcon(skillid))--图标
	putInt(skillid)--技能id
	putByte(isPassiveSkill(skillid))--主动or被动
	putByte(toEffectNum(skillid, level))--作用人数
	putByte(effectObj(skillid))--作用对象
	putInt(getManaCost(skillid, level))--耗蓝
	putString(getLiteTitle(skillid, level))--精简标题
	putString(getFullTitle(skillid, level))--标题
end

function fillSkillDel(skillid)
	putShort(103)--Serial
	putInt(skillid)--技能id
end
