
RoleAttributes = { }

RoleAttributes.point = {
	Strength = 1,
	Agility = 2,
	Intellect = 3,
	Vitality = 4,
	
	get = {
		_RoleGetBasicStrength,
		_RoleGetBasicAgility,
		_RoleGetBasicIntellect,
		_RoleGetBasicVitality,
	},
	
	set = {
		_RoleSetStrength,
		_RoleSetAgility,
		_RoleSetIntellect,
		_RoleSetVitality,
	},

}
