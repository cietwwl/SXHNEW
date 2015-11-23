
function effect(buff, value)

	if buff:getEffectId() == BuffEffectConst.MULTIPLE then
		local round = math.round(value * buff:getEffectValue() / 100)
		return round >= 1 and round or 1
	elseif buff:getEffectId() == BuffEffectConst.GAIN then
		local effectValue = buff:getEffectValue()
		if effectValue > value then
			buff:setEffectValue(effectValue - value)
			return value
		else
			buff:setEffectValue(0)
			return effectValue
		end
	end
	
	return value
end