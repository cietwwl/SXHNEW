
local function fillMenu(menu)
	putByte(#menu)
	for k, v in ipairs(menu) do
		putByte(v[1])
		putString(v[2])
		putInt(v[3])
	end
end

--周围列表
local player_menu = {
	
	{1, "邀请队伍", 0},
	{2, "申请入队", 0},
	{3, "切磋", 0},
	{4, "偷袭", 0},
	{5, "密语", 0},
	{11, "查看", 0},
	{6, "加入好友", 123123}, 
	{10, "邀请入帮", 0}, 
	{7, "加入黑名单", 999999}, 
	{8, "返回", 0},
}

function fillPlayerMenu()
	fillMenu(player_menu)
end

local single_friend_menu = {
	{1, "密语", 0},
	{2, "删除", 0},
	{4, "邀请组队", 0},
	{5, "申请入队", 0},
	--{6, "邀请入队", 0}, 
	{10, "邀请入帮", 0}, 
	{7, "返回", 0},
}

function fillSingleFriendMenu()
	fillMenu(single_friend_menu)
end

local skipper_friend_menu = {
	{1, "密语", 0},
	{2, "删除", 0},
	--{4, "邀请组队", 0},
	--{5, "申请入队", 0},
	{6, "邀请入队", 0}, 
	{10, "邀请入帮", 0}, 
	{7, "返回", 0},
}

function fillSkipperFriendMenu()
	fillMenu(skipper_friend_menu)
end

local teamer_friend_menu = {
	{1, "密语", 0},
	{2, "删除", 0},
	--{4, "邀请组队", 0},
	--{5, "申请入队", 0},
	--{6, "邀请入队", 0}, 
	{10, "邀请入帮", 0}, 
	{7, "返回", 0},
}

function fillTeamerFriendMenu()
	fillMenu(teamer_friend_menu)
end

local foe_menu = {
	--{0, "查看", 0},
	{1, "密语", 0},
	{2, "删除", 0},
	{9, "加入仇人列表",444444},
	{7, "返回", 0},
}

function fillFoeNameMenu()
	fillMenu(foe_menu)
end

local marry_menu = {
	
	{0, "密语", 0},
	{1, "邮件", 0},
	{2, "返回", 0},
}

function fillMarryandMaterMenu()
	fillMenu(marry_menu)
end

local enemy_menu = {
	--{0, "查看", 0},
	{1, "加入好友", 0},
	{2, "加入黑名单", 0},
	{3, "删除", 0},
}

function fillEnemyNameMenu()
	fillMenu(enemy_menu)
end
