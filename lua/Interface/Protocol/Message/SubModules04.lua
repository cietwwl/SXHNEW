
function fillSystemPrompt(prompt)
	putShort(400)
	putString(prompt)
	putInt(0xff0000)
end

function fillPrompt(prompt)
	putShort(401)
	putString(prompt)
	putInt(0)
end