Actions.guessRules.layout={
	title = [=[猜猜看规则]=],
	x = 0,
	y = 0,
	width = 100,
	height = 100,
	
	tags = {
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'rules1',
			value = [=[系统会收取5%的手续费。/赔率说明：大小输赢赔率都为1:2。（举例：当你押大单各1银时，此局结果为大单，你获得4银。若此局结果为大双，你获得2银，若此局结果为小双，你获得0银。）]=],
			action = '',
			x = 18,
			y = 40,
			width = 200,
			height = 100,
		},
		
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'rules2',
			value = [=[输赢规则：系统随机5分钟内选出大单，大双，小单，小双，豹子五种结果。您可任意押注。每局结束后，系统会以邮件通知您输赢金额（押注金额最低为1银）。]=],
			action = '',
			x = 18,
			y = 125,
			width = 200,
			height = 100,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.right,
			name = 'guess',
			value = [=[返回]=],
			action = 'guess',
			x = 173,
			y = 295,
			width = 60,
			height = 18,
		},
	
	}
}