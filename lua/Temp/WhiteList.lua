
WhiteList = { }

WhiteList['srv000'] = {
	SrvName = '测试机';
	
	role = { name = "测试人员", },
}

setmetatable(WhiteList['srv000'], 
			{ 
				__index = function (t, k)
					return type(k) == "number" and rawget(t, "role") or rawget(t, k)
				end
			})

WhiteList['srv001'] = {
	SrvName = '六脉神剑';
}

WhiteList['srv002'] = {
	SrvName = '降龙十八掌';
	
	[100007] = { name = '赵冰', },
	[100003] = { name = '冯杨', },
	[100573] = { name = '董波', },
}

WhiteList['srv003'] = {
	SrvName = '凌波微步';
	
	[13322] = { name = '单东海', },
	[13642] = { name = '王家强', },
}

WhiteList['srv004'] = {
	SrvName = '北冥神功';
}

WhiteList['srv005'] = {
	SrvName = '斗转星移';
}

WhiteList['srv006'] = {
	SrvName = '狮王争霸';
}

WhiteList['srv007'] = {
	SrvName = '雄霸天下';
}
WhiteList['srv008'] = {
	SrvName = '珍珑棋局';
}
WhiteList['srv009'] = {
	SrvName = '听香水榭';
}
WhiteList['srv010'] = {
	SrvName = '龙临天下';
}
WhiteList['srv011'] = {
	SrvName = '11';
}
WhiteList['srv012'] = {
	SrvName = '12';
}
WhiteList['srv013'] = {
	SrvName = '13';
}

WhiteList['srv014'] = {
	SrvName = '14';
}
WhiteList['srv015'] = {
	SrvName = '15';
}
WhiteList['srv016'] = {
	SrvName = '16';
}
WhiteList['srv017'] = {
	SrvName = '17';
}
WhiteList['srv018'] = {
	SrvName = '18';
}
WhiteList['srv019'] = {
	SrvName = '19';
}
WhiteList['srv020'] = {
	SrvName = '19';
}
WhiteList['srv021'] = {
	SrvName = '19';
}
WhiteList['srv022'] = {
	SrvName = '19';
}
WhiteList['srv023'] = {
	SrvName = '19';
}
WhiteList['srv024'] = {
	SrvName = '19';
}
WhiteList['srv025'] = {
	SrvName = '19';
}

WhiteList['srv026'] = {
	SrvName = '19';
}
WhiteList['srv027'] = {
	SrvName = '19';
}
WhiteList['srv028'] = {
	SrvName = '19';
}
WhiteList['srv029'] = {
	SrvName = '19';
}
WhiteList['srv030'] = {
	SrvName = '19';
}
WhiteList['srv031'] = {
	SrvName = '19';
}
WhiteList['srv032'] = {
	SrvName = '19';
}
WhiteList['srv033'] = {
	SrvName = '19';
}