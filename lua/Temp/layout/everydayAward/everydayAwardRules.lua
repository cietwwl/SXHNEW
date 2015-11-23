Actions.everydayAwardRules.layout={
	title = [=[每日开奖规则]=],
	x = 0,
	y = 0,
	width = 100,
	height = 100,
	
	tags = {
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'rules',
			value = [=[a) 玩家背包内必须有相应钥匙才可开启宝箱/b) 玩家每日可去洛阳城善财童子处免费领取1把银钥匙/c) 金钥匙可以在商城购买/d) 宝箱开启后奖品以邮件形式下发，请注意查收]=],
			action = '',
			x = 18,
			y = 40,
			width = 200,
			height = 100,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.right,
			name = 'everydayAward',
			value = [=[返回]=],
			action = 'everydayAward',
			x = 173,
			y = 295,
			width = 60,
			height = 18,
		},
	
	}
}