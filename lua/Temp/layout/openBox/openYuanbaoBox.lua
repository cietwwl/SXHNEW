Actions.openYuanbaoBox.layout={
	title = [=[开元宝宝箱]=],
	x = 0,
	y = 0,
	width = 100,
	height = 100,
	
	tags = {
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'yuanbaoGoldBox',
			value = [=[金宝箱]=],
			action = 'openYuanbaoGoldBox',
			x = 20,
			y = 60,
			width = 80,
			height = 45,
		},
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'yuanbaoGoldBoxState',
			value = [=[ 每次开启需100元宝/点击左侧即开启]=],
			action = '',
			x = 120,
			y = 60,
			width = 100,
			height = 80,
		},
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'yuanbaoSilverBox',
			value = [=[银宝箱]=],
			action = 'openYuanbaoSilverBox',
			x = 20,
			y = 115,
			width = 80,
			height = 45,
		},
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'yuanbaoSilverBoxState',
			value = [=[ 每次开启需50元宝/点击左侧即开启]=],
			action = '',
			x = 120,
			y = 115,
			width = 100,
			height = 80,
		},
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'yuanbaoCopperBox',
			value = [=[铜宝箱]=],
			action = 'openYuanbaoCopperBox',
			x = 20,
			y = 170,
			width = 80,
			height = 45,
		},
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'yuanbaoCopperBoxState',
			value = [=[ 每次开启需10元宝/点击左侧即开启]=],
			action = '',
			x = 120,
			y = 170,
			width = 100,
			height = 80,
		},
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'yuanbaoBoxRules',
			value = [=[游戏规则]=],
			action = 'yuanbaoBoxRules',
			x = 20,
			y = 225,
			width = 80,
			height = 45,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.left,
			name = 'fastRefreshYuanbaoBox',
			value = [=[快速刷新]=],
			action = 'fastRefreshYuanbaoBox',
			x = 7,
			y = 295,
			width = 60,
			height = 18,
		},
		{
			type = TagType.input,
			subtype = TagSubType.right,
			name = 'gameList',
			value = [=[返回]=],
			action = 'gameList',
			x = 173,
			y = 295,
			width = 60,
			height = 18,
		},
	},
}