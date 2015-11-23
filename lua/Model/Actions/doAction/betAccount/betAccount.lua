
Actions.betAccount = {
	name = 'betAccount',
}

function Actions.betAccount:doAction(roleid, args)
	local jrole = _GetOnline(roleid)

	if jrole then
		
		local x = 10
		local y = 85
		local width =  100
		local height = 18 
		local i = 1
		while i <= 16 do
				local str = i+2 ..  "点 比率 "  .. _GetOddsByBet(i)
				self:addListItem( "var" .. i , i, "betResult", str, x, y, width, height)
				if i%2==0 then 
					x = x - 120
					y = y + 20			
				else	
					x = x + 120
				end
					i = i + 1
		end
		self.layout.tags[3].display = [=[上局结果: ]=] .. _BetAccountgetLastRoundResult() .. [=[ 点 ]=] 
		self.layout.tags[4].display = [=[开奖时间剩余: ]=] .. _BetAccountGetRemainTime()
	end

	self:doDisplay(jrole)
	self:removeAllListItems()
end