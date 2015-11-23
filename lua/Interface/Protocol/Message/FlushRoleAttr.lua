
function flushRoleAttr(jrole, prompt)
	prepareBody()
	
	fillAttributes(jrole)
	
	fillAttributesDes(jrole)
	
	if prompt then
		fillSystemPrompt(prompt)
	end
			
	putShort(0)
	
	sendMsg(jrole)
end