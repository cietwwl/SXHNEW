Actions.everydayAward.layout={
	title = [=[每日开奖]=],
	x = 0,
	y = 0,
	width = 100,
	height = 100,
	
	tags = {		
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'goldBox',
			value = [=[金宝箱]=],
			action = 'everydayAward',
			x = 85,
			y = 40,
			width = 70,
			height = 18,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'goldBox1',
			value = [=[宝箱]=],
			action = 'goldAward',
			x = 25,
			y = 80,
			width = 48,
			height = 48,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'goldBox2',
			value = [=[宝箱]=],
			action = 'goldAward',
			x = 95,
			y = 80,
			width = 48,
			height = 48,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'goldBox3',
			value = [=[宝箱]=],
			action = 'goldAward',
			x = 165,
			y = 80,
			width = 48,
			height = 48,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'silverBox',
			value = [=[银宝箱]=],
			action = 'everydayAward',
			x = 85,
			y = 150,
			width = 70,
			height = 18,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'silverBox1',
			value = [=[宝箱]=],
			action = 'silerAward',
			x = 25,
			y = 190,
			width = 48,
			height = 48,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'silverBox2',
			value = [=[宝箱]=],
			action = 'silerAward',
			x = 95,
			y = 190,
			width = 48,
			height = 48,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.submit,
			name = 'silverBox3',
			value = [=[宝箱]=],
			action = 'silerAward',
			x = 165,
			y = 190,
			width = 48,
			height = 48,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.left,
			name = 'everydayAwardRules',
			value = [=[游戏规则]=],
			action = 'everydayAwardRules',
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