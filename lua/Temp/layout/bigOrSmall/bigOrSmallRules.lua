Actions.bigOrSmallRules.layout={
	title = [=[押大小规则]=],
	x = 0,
	y = 0,
	width = 100,
	height = 100,
	
	tags = {
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'big',
			value = [=[赔率说明:大小赔率为1:2./输赢规则:系统10分钟内随机选出大或小.您可任意押注.每局结束后,系统会以邮件通知您输赢金额.(押注金额最低为1银)请注意您的背包上限为9999金，超出了可是取不出附件的哦~]=],
			action = '',
			x = 18,
			y = 40,
			width = 200,
			height = 240,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.right,
			name = 'bigOrSmall',
			value = [=[返回]=],
			action = 'bigOrSmall',
			x = 173,
			y = 295,
			width = 60,
			height = 18,
		},
	
	}
}