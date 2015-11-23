Actions.yuanbaoBoxRules.layout={
	title = [=[开宝箱规则]=],
	x = 0,
	y = 0,
	width = 100,
	height = 100,
	
	tags = {
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'yuanbaoBox',
			value = [=[1 开启金银铜任意一种宝箱后，开始冷却计时6分钟内，即在冷却时间内不可再开启此宝箱，但可以开启其余宝箱。/2 快速刷新可以直接跳过冷却时间，开启下一轮宝箱。/3 普通玩家每天开启20次宝箱。刷新时间为凌晨4点。]=],
			action = '',
			x = 18,
			y = 40,
			width = 200,
			height = 240,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.right,
			name = 'openYuanbaoBox',
			value = [=[返回]=],
			action = 'openYuanbaoBox',
			x = 173,
			y = 295,
			width = 60,
			height = 18,
		},
	
	}
}