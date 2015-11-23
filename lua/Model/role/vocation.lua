
function getPAtkAnimi(jrole, atk)
	local type = _RoleGetVocation(jrole)
	local gender = _RoleGetSex(jrole)
	local toward = (_RoleGetSeatId(jrole) > 2) and 0 or 1
	
	if _ObjectEquals(type, Vocation.SHAQ) then--侠客
		if gender == 0 then--男
			if toward == 0 then--面向左边
				if atk then
					return 81, 6
				else
					return 81, 8
				end
			else--面向右边
				if atk then
					return 81, 7
				else
					return 81, 9
				end
			end
		else
			if toward == 0 then--面向左边
				if atk then
					return 82, 6
				else
					return 82, 8
				end
			else--面向右边
				if atk then
					return 82, 7
				else
					return 82, 9
				end
			end
		end
	elseif _ObjectEquals(type, Vocation.Warlock) then
		if gender == 0 then
			if toward == 0 then--面向左边
				if atk then
					return 83, 6
				else
					return 83, 8
				end
			else--面向右边
				if atk then
					return 83, 7
				else
					return 83, 9
				end
			end
		else
			if toward == 0 then--面向左边
				if atk then
					return 84, 6
				else
					return 84, 8
				end
			else--面向右边
				if atk then
					return 84, 7
				else
					return 84, 9
				end
			end
		end
	else
		if gender == 0 then
			if toward == 0 then--面向左边
				if atk then
					return 85, 6
				else
					return 85, 8
				end
			else--面向右边
				if atk then
					return 85, 7
				else
					return 85, 9
				end
			end
		else
			if toward == 0 then--面向左边
				if atk then
					return 86, 6
				else
					return 86, 8
				end
			else--面向右边
				if atk then
					return 86, 7
				else
					return 86, 9
				end
			end
		end
	end
end
