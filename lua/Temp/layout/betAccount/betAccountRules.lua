Actions.betAccountRules.layout={
	title = [=[元宝赌数]=],
	x = 0,
	y = 0,
	width = 100,
	height = 100,
	
	tags = {
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'rule',
			value = [=[元宝赌数规则：]=],
			action = '',
			x = 18,
			y = 40,
			width = 200,
			height = 20,
		},
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'rule',
			value = [=[1.填入的元宝数目必须大于或者等于20元宝/2.每一次押注时，只能押注一个数字/3.每局可以押注多次/4.每个数字的比率均不一样，都会在数字后面显示]=],
			action = '',
			x = 18,
			y = 60,
			width = 200,
			height = 80,
		},
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'rule',
			value = [=[5.押注时，超过了当前局开始时间，元宝自动转存为下局的押注 /6.可选择刷新获取当前局押注时间以及上局结果/7.当每局结束后，会以邮件的方式通知给押注的玩家胜负结果]=],
			action = '',
			x = 18,
			y = 145,
			width = 200,
			height = 100,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.right,
			name = 'betAccount',
			value = [=[返回]=],
			action = 'betAccount',
			x = 173,
			y = 295,
			width = 60,
			height = 18,
		},
	
	}
}