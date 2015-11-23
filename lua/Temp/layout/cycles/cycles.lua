Actions.cycles.layout={
	title = [=[老虎棒子鸡]=],
	x = 0,
	y = 0,
	width = 100,
	height = 100,
	
	tags = {
								
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'challenge',
			value = [=[我要挑战]=],
			action = 'challenge',
			x = 21,
			y = 86,
			width = 85,
			height = 24,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'acceptEntry',
			value = [=[我要应战]=],
			action = 'acceptEntry',
			x = 133,
			y = 85,
			width = 85,
			height = 24,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'challengeRecord',
			value = [=[挑战记录]=],
			action = 'challengeRecord',
			x = 19,
			y = 180,
			width = 85,
			height = 24,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'acceptRecord',
			value = [=[应战记录]=],
			action = 'acceptRecord',
			x = 133,
			y = 180,
			width = 85,
			height = 24,
		},
		
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'playerGold',
			value = '',
			action = '',
			x = 22,
			y = 233,
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