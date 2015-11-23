Actions.acceptRecord.layout={
	title = [=[老虎棒子鸡]=],
	x = 0,
	y = 0,
	width = 100,
	height = 100,
	
	tags = {
	
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'playerGold',
			value = '',
			action = '',
			x = 11,
			y = 239,
			width = 120,
			height = 12,
		},
							
		{
			type = TagType.input,
			subtype = TagSubType.left,
			name = 'cyclesRules',
			value = [=[游戏规则]=],
			action = 'cyclesRules',
			x = 7,
			y = 295,
			width = 60,
			height = 18,
		},
		
		
		{
			type = TagType.input,
			subtype = TagSubType.right,
			name = 'cycles',
			value = [=[返回]=],
			action = 'cycles',
			x = 173,
			y = 295,
			width = 60,
			height = 18,
		},
	},
}