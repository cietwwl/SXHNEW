Actions.cyclesRules.layout={
	title = [=[老虎棒子鸡规则]=],
	x = 0,
	y = 0,
	width = 100,
	height = 100,
	
	tags = {
		{
			type = TagType.label,
			subtype = TagSubType.none,
			name = 'cycles',
			value = [=[输赢规则：棒vs老虎-棒胜，老虎vs鸡-老虎胜，鸡vs棒-鸡胜。/挑战规则： 玩家选择老虎、棒子或鸡，等待其他玩家进行挑战。/应战规则：玩家可刷新挑战列表点击玩家进行挑战，但应战玩家金币必须大于等于挑战玩家的下注。]=],
			action = '',
			x = 18,
			y = 40,
			width = 200,
			height = 240,
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
	
	}
}