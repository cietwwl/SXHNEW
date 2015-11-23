Actions.commonError.layout={
	title = [=[出错啦]=],
	x = 0,
	y = 0,
	width = 100,
	height = 100,
	tags = {
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'errorInfo',
			value = [=[出错啦]=],
			action = '',
			x = 24,
			y = 40,
			width = 200,
			height = 260,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.cancle,
			name = 'errorAction',
			value = [=[返回]=],
			action = 'errorAction',
			x = 173,
			y = 295,
			width = 60,
			height = 18,
		},

	}
}