
function getRGB(color)
	if color == 0 then
		return Color.BLACK:getRGB() % 2 ^ 24
	elseif color == 1 then
		return Color.GREEN:getRGB() % 2 ^ 24
	elseif color == 2 then
		return 0x215fe6
	elseif color == 3 then
		return 0xA020F0
	elseif color == 4 then
		return Color.YELLOW:getRGB() % 2 ^ 24
	end
	
	return Color.BLACK:getRGB() % 2 ^ 24
end
