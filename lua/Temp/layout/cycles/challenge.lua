Actions.challenge.layout={
	title = [=[老虎棒子鸡]=],
	x = 0,
	y = 0,
	width = 100,
	height = 100,
	
	tags = {
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'money',
			value = [=[输入金额]=],
			action = '',
			x = 21,
			y = 64,
			width = 40,
			height = 12,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.number,
			name = 'goldForCycles',
			value = [=[0]=],
			action = '',
			x = 84,
			y = 58,
			width = 54,
			height = 23,
		},
		
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'prompt',
			value = [=[您输入的金额不得小于10银币]=],
			action = '',
			x = 12,
			y = 100,
			width = 150,
			height = 12,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'challengeTiger',
			value = [=[老虎]=],
			action = 'challengeTiger',
			x = 12,
			y = 131,
			width = 55,
			height = 24,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'challengeStick',
			value = [=[棒子]=],
			action = 'challengeStick',
			x = 92,
			y = 131,
			width = 55,
			height = 24,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'challengeChicken',
			value = [=[鸡]=],
			action = 'challengeChicken',
			x = 173,
			y = 131,
			width = 55,
			height = 24,
		},
		
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'playerGold',
			value = '',
			action = '',
			x = 11,
			y = 179,
			width = 120,
			height = 12,
		},
		
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'remark',
			value = [=[备注：如果对方获胜您投入的游戏币将全部输光，如果您获胜，您将获得投入游戏币的90%的奖励，如果平局退回游戏币。]=],
			action = '',
			x = 12,
			y = 216,
			width = 203,
			height = 43,
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