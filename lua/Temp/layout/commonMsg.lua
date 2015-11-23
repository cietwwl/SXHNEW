Actions.commonMsg.layout={
	title = [=[提示]=],
	x = 0,
	y = 0,
	width = 100,
	height = 100,
	tags = {
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'defaultMsg',
			value = [=[默认消息]=],
			action = '',
			x = 24,
			y = 40,
			width = 200,
			height = 260,
		},
		
		{
			type = TagType.input,
			subtype = TagSubType.cancle,
			name = 'comMsgAction',
			value = [=[返回]=],
			action = 'comMsgAction',
			x = 173,
			y = 295,
			width = 60,
			height = 18,
		},

	}
}