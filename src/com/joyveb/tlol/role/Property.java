package com.joyveb.tlol.role;

import com.joyveb.tlol.item.Bonus;

/**
 * 角色属性
 */
public enum Property implements PropertyModifier, PropertyRespond {
	/**
	 * 等级
	 */
	Level("等级", null, LevelResponder.INSTANCE),
	
	/** 力量 */
	Strength("力　　量", null, StrengthResponder.INSTANCE),

	/** 敏捷 */
	Agility("敏　　捷", null, AgilityResponder.INSTANCE),

	/** 智力 */
	Intellect("智　　力", null, IntellectResponder.INSTANCE),

	/** 体质 */
	Vitality("体　　质", null, VitalityResponder.INSTANCE),

	/** 物理攻击 */
	PAtk("物理攻击", PAtkModifier.INSTANCE, null),

	/** 最小物攻 */
	MinPAtk("最小物攻", null, null),

	/** 最大物攻 */
	MaxPAtk("最大物攻", null, null),

	/** 法术攻击 */
	MAtk("法术攻击", MAtkModifier.INSTANCE, null),
	
	/** 最小法攻 */
	MinMAtk("最小法攻", null, null),

	/** 最大法攻 */
	MaxMAtk("最大法攻", null, null),

	/** 物理防御 */
	PDef("物理防御", null, null),

	/** 法术防御 */
	MDef("法术防御", null, null),

	/** 命中*/
	Hit("命　　中", null, null),

	/** 躲闪*/
	Evade("躲　　闪", null, null),
	
	/** 暴击*/
	Crit("暴　　击", null, null),

	/** 攻速度*/
	AtkSpd("速　　度", null, null),

	/** 生命上限*/
	MaxHp("生命上限", null, MaxHpResponder.INSTANCE),

	/** 魔法上限*/
	MaxMp("魔法上限", null, MaxMpResponder.INSTANCE);

	/** 属性名称 */
	private final String name;
	
	/** 属性修改器 */
	private final PropertyModifier modifier;
	
	/** 属性变化响应 */
	private final PropertyRespond responder;

	/**
	 * @param name 属性名称
	 * @param modifier 属性修改器
	 * @param responder 属性变化响应
	 */
	private Property(final String name, final PropertyModifier modifier, final PropertyRespond responder) {
		this.name = name;
		
		if(modifier == null)
			this.modifier = new SimpleModifier(this);
		else
			this.modifier = modifier;
		
		this.responder = responder;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void addBonus(final RoleBean role, final Bonus bonus) {
		modifier.addBonus(role, bonus);
	}

	@Override
	public void changeValue(final RoleBean role, final double change) {
		if(change == 0)
			return;
		
		modifier.changeValue(role, change);
	}

	@Override
	public void removeBonus(final RoleBean role, final Bonus bonus) {
		modifier.removeBonus(role, bonus);
	}

	@Override
	public void respond(final RoleBean role, final double change) {
		if(responder != null)
			responder.respond(role, change);
	}

}

/**
 * 物理攻击修改器
 * @author Sid
 */
enum PAtkModifier implements PropertyModifier {
	/** 单例 */
	INSTANCE;

	@Override
	public void changeValue(final RoleBean role, final double change) {
		PropertyMan man = role.getPropertyMan();
		
		man.changeProperty(Property.MinPAtk, change);
		
		man.changeProperty(Property.MaxPAtk, change);
	}

	@Override
	public void addBonus(final RoleBean role, final Bonus bonus) {
		PropertyMan man = role.getPropertyMan();
		
		double rawMinPAtkValue = man.getDynamicProperty(Property.MinPAtk);
		man.changeProperty(Property.MinPAtk, bonus.takeEffect(rawMinPAtkValue) - rawMinPAtkValue);
		
		double rawMaxPAtkValue = man.getDynamicProperty(Property.MaxPAtk);
		man.changeProperty(Property.MaxPAtk, bonus.takeEffect(rawMaxPAtkValue) - rawMaxPAtkValue);
	}

	@Override
	public void removeBonus(final RoleBean role, final Bonus bonus) {
		PropertyMan man = role.getPropertyMan();
		
		double rawMinPAtkValue = man.getDynamicProperty(Property.MinPAtk);
		man.changeProperty(Property.MinPAtk, bonus.loseEffect(rawMinPAtkValue) - rawMinPAtkValue);
		
		double rawMaxPAtkValue = man.getDynamicProperty(Property.MaxPAtk);
		man.changeProperty(Property.MaxPAtk, bonus.loseEffect(rawMaxPAtkValue) - rawMaxPAtkValue);
	}

}

/**
 * 法术攻击修改器
 * @author Sid
 */
enum MAtkModifier implements PropertyModifier {
	/** 单例 */
	INSTANCE;

	@Override
	public void changeValue(final RoleBean role, final double change) {
		PropertyMan man = role.getPropertyMan();
		
		man.changeProperty(Property.MinMAtk, change);
		
		man.changeProperty(Property.MaxMAtk, change);
	}

	@Override
	public void addBonus(final RoleBean role, final Bonus bonus) {
		PropertyMan man = role.getPropertyMan();
		
		double rawMinPAtkValue = man.getDynamicProperty(Property.MinMAtk);
		man.changeProperty(Property.MinMAtk, bonus.takeEffect(rawMinPAtkValue) - rawMinPAtkValue);
		
		double rawMaxPAtkValue = man.getDynamicProperty(Property.MaxMAtk);
		man.changeProperty(Property.MaxMAtk, bonus.takeEffect(rawMaxPAtkValue) - rawMaxPAtkValue);
	}

	@Override
	public void removeBonus(final RoleBean role, final Bonus bonus) {
		PropertyMan man = role.getPropertyMan();
		
		double rawMinPAtkValue = man.getDynamicProperty(Property.MinMAtk);
		man.changeProperty(Property.MinMAtk, bonus.loseEffect(rawMinPAtkValue) - rawMinPAtkValue);
		
		double rawMaxPAtkValue = man.getDynamicProperty(Property.MaxMAtk);
		man.changeProperty(Property.MaxMAtk, bonus.loseEffect(rawMaxPAtkValue) - rawMaxPAtkValue);
	}
	
}

/**
 * 简单属性修改器，修改此属性不会影响别的属性
 * @author Sid
 */
class SimpleModifier implements PropertyModifier {
	/** 作用属性 */
	private Property property;
	
	/**
	 * @param property 作用属性
	 */
	public SimpleModifier(final Property property) {
		this.property = property;
	}
	
	@Override
	public void changeValue(final RoleBean role, final double change) {
		PropertyMan man = role.getPropertyMan();
		
		man.changeProperty(property, change);
	}

	@Override
	public void addBonus(final RoleBean role, final Bonus bonus) {
		PropertyMan man = role.getPropertyMan();
		
		double rawMinPAtkValue = man.getDynamicProperty(property);
		man.changeProperty(property, bonus.takeEffect(rawMinPAtkValue) - rawMinPAtkValue);
	}

	@Override
	public void removeBonus(final RoleBean role, final Bonus bonus) {
		PropertyMan man = role.getPropertyMan();
		
		double rawMinPAtkValue = man.getDynamicProperty(property);
		man.changeProperty(property, bonus.loseEffect(rawMinPAtkValue) - rawMinPAtkValue);
	}

}

/**
 * 等级修改器
 * @author Sid
 */
enum LevelResponder implements PropertyRespond {
	/** 单例 */
	INSTANCE;

	@Override
	public void respond(final RoleBean role, final double change) {
		PropertyMan man = role.getPropertyMan();
		Vocation vocation = role.getVocation();
		
		if(vocation == Vocation.SHAQ) {
			man.changeProperty(Property.MaxHp, change * 25);
			man.changeProperty(Property.MaxMp, change * 5);
		}else if(vocation == Vocation.Warlock) {
			man.changeProperty(Property.MaxHp, change * 10);
			man.changeProperty(Property.MaxMp, change * 20);
		}else {
			man.changeProperty(Property.MaxHp, change * 15);
			man.changeProperty(Property.MaxMp, change * 15);
		}
	}

}

/**
 * 力量响应器
 * @author Sid
 *
 */
enum StrengthResponder implements PropertyRespond {
	/** 单例 */
	INSTANCE;

	@Override
	public void respond(final RoleBean role, final double change) {
		PropertyMan man = role.getPropertyMan();
		Vocation vocation = role.getVocation();
		
		if(vocation == Vocation.SHAQ) {
			man.changeProperty(Property.MinPAtk, change * 2.52);
			
			man.changeProperty(Property.MaxPAtk, change * 2.52);
			
			man.changeProperty(Property.MaxHp, change * 3.1);
			
			man.changeProperty(Property.PDef, change * 1.8);
			
			man.changeProperty(Property.Crit, change * 7);
		}else if(vocation == Vocation.Warlock) {
			man.changeProperty(Property.MinPAtk, change * 0.04);
			
			man.changeProperty(Property.MaxPAtk, change * 0.04);
			
			man.changeProperty(Property.MaxHp, change * 1);
			
			man.changeProperty(Property.PDef, change * 2.4);
			
			man.changeProperty(Property.Crit, change * 3);
		}else {
			man.changeProperty(Property.MinPAtk, change * 0.72);
			
			man.changeProperty(Property.MaxPAtk, change * 0.72);
			
			man.changeProperty(Property.MaxHp, change * 2);
			
			man.changeProperty(Property.PDef, change * 0.72);
			
			man.changeProperty(Property.Crit, change * 4);
		}
	}
	
}

/**
 * 敏捷响应器
 * @author Sid
 *
 */
enum AgilityResponder implements PropertyRespond {
	/** 单例 */
	INSTANCE;

	@Override
	public void respond(final RoleBean role, final double change) {
		PropertyMan man = role.getPropertyMan();
		Vocation vocation = role.getVocation();
		
		if(vocation == Vocation.SHAQ) {
			man.changeProperty(Property.MinPAtk, change * 0.4);
			
			man.changeProperty(Property.MaxPAtk, change * 0.4);
			
			man.changeProperty(Property.Hit, change * 2);
			
			man.changeProperty(Property.Evade, change * 1);
			
			man.changeProperty(Property.AtkSpd, change * 0.35);
		}else if(vocation == Vocation.Warlock) {
			man.changeProperty(Property.MinPAtk, change * 0.1);
			
			man.changeProperty(Property.MaxPAtk, change * 0.1);
			
			man.changeProperty(Property.Hit, change * 5);
			
			man.changeProperty(Property.Evade, change * 3);
			
			man.changeProperty(Property.AtkSpd, change * 0.5);
		}else {
			man.changeProperty(Property.MinPAtk, change * 2.5);
			
			man.changeProperty(Property.MaxPAtk, change * 2.5);
			
			man.changeProperty(Property.Hit, change * 7);
			
			man.changeProperty(Property.Evade, change * 5);
			
			man.changeProperty(Property.AtkSpd, change * 0.6);
		}
	}
	
}

/**
 * 智力响应器
 * @author Sid
 */
enum IntellectResponder implements PropertyRespond {
	/** 单例 */
	INSTANCE;

	@Override
	public void respond(final RoleBean role, final double change) {
		PropertyMan man = role.getPropertyMan();
		Vocation vocation = role.getVocation();
		
		if(vocation == Vocation.SHAQ) {
			man.changeProperty(Property.MaxMp, change * 2);
			
			man.changeProperty(Property.MinMAtk, change * 1.2);
			
			man.changeProperty(Property.MaxMAtk, change * 1.2);
			
			man.changeProperty(Property.MDef, change * 0.96);
			
			man.changeProperty(Property.Evade, change * 1);
		}else if(vocation == Vocation.Warlock) {
			man.changeProperty(Property.MaxMp, change * 10);
			
			man.changeProperty(Property.MinMAtk, change * 3.72);
			
			man.changeProperty(Property.MaxMAtk, change * 3.72);
			
			man.changeProperty(Property.MDef, change * 7.2);
			
			man.changeProperty(Property.Evade, change * 2);
		}else {
			man.changeProperty(Property.MaxMp, change * 4.5);
			
			man.changeProperty(Property.MinMAtk, change * 0.4);
			
			man.changeProperty(Property.MaxMAtk, change * 0.4);
			
			man.changeProperty(Property.MDef, change * 13.6);
			
			man.changeProperty(Property.Evade, change * 1);
		}
	}

}

/**
 * 体质响应器
 * @author Sid
 */
enum VitalityResponder implements PropertyRespond {
	/** 单例 */
	INSTANCE;

	@Override
	public void respond(final RoleBean role, final double change) {
		PropertyMan man = role.getPropertyMan();
		Vocation vocation = role.getVocation();
		
		if(vocation == Vocation.SHAQ) {
			man.changeProperty(Property.MaxHp, change * 25);
			
			man.changeProperty(Property.PDef, change * 1.4);
			
			man.changeProperty(Property.AtkSpd, change * 0.04);
			
			man.changeProperty(Property.MaxMp, change * 2);
			
			man.changeProperty(Property.MinPAtk, change * 1);
			
			man.changeProperty(Property.MaxPAtk, change * 1);
			
			man.changeProperty(Property.MinMAtk, change * 0.5);
			
			man.changeProperty(Property.MaxMAtk, change * 0.5);
		}else if(vocation == Vocation.Warlock) {
			man.changeProperty(Property.MaxHp, change * 10);
			
			man.changeProperty(Property.PDef, change * 1.4);
			
			man.changeProperty(Property.AtkSpd, change * 0.03);
			
			man.changeProperty(Property.MaxMp, change * 1.3);
			
			man.changeProperty(Property.MinPAtk, change * 0.5);
			
			man.changeProperty(Property.MaxPAtk, change * 0.5);
			
			man.changeProperty(Property.MinMAtk, change * 1);
			
			man.changeProperty(Property.MaxMAtk, change * 1);
		}else {
			man.changeProperty(Property.MaxHp, change * 12);
			
			man.changeProperty(Property.PDef, change * 1.2);
			
			man.changeProperty(Property.AtkSpd, change * 0.02);
			
			man.changeProperty(Property.MaxMp, change * 1.5);
			
			man.changeProperty(Property.MinPAtk, change * 0.5);
			
			man.changeProperty(Property.MaxPAtk, change * 0.5);
			
			man.changeProperty(Property.MinMAtk, change * 0.5);
			
			man.changeProperty(Property.MaxMAtk, change * 0.5);
		}
	}

}

/**
 * 
 * @author Sid
 *
 */
enum MaxHpResponder implements PropertyRespond {
	/** 单例 */
	INSTANCE;

	@Override
	public void respond(final RoleBean role, final double change) {
		int hp = (int) (role.getHP());
		role.setHP(hp <= 0 ? 1 : hp);//当前血协变
	}
	
}

/**
 * 
 * @author Sid
 *
 */
enum MaxMpResponder implements PropertyRespond {
	/** 单例 */
	INSTANCE;

	@Override
	public void respond(final RoleBean role, final double change) {
		int mp = (int) (role.getMP());
		role.setMP(mp <= 0 ? 1 : mp);//当前魔协变
	}
	
}
