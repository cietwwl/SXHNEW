
----------检查是否包含屏蔽词汇
function checkDirtyWord(word)
	for _, v in pairs(_DirtyWords) do
		if string.find(word, v, 1, true) then
			log.info("发现屏蔽词汇" .. word)
			return false
		end
	end
	
	return true
end

-------------------过滤屏蔽词汇
function filterDirtyWord(word)
	word = string.gsub(word, "[%w%.%s]*%.[%a%s]+[/%w%s]+", [[*****]])
	
	for _, v in pairs(_DirtyWords) do
		word = string.gsub(word, v, [[*****]])
	end

	return word
end
