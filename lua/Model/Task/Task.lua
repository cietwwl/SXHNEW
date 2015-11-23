
--任务概述
TaskProfile = {
	CommonTask = 1,--普通实现
	Buy = 2,--买
	Sell = 3,--卖
	Exchange = 4,--兑换
	Mall = 5,--商城
	Daily = 6,--每日任务
	Barter = 7,--以物易物
	RandomTask = 8,--随机任务
	Depot = 9,--进入仓库
	ScrollTask = 10,--任务卷轴接受的任务
	CreatGang = 11,
	DismissGang = 12,
	RandomDaily = 13,--随机每日任务
	Auction = 14,
	TimingAward = 15,
	ClearRedName = 16,--清除/减少罪恶值
	HonorBuy = 17, --荣誉值购买
	GetItemTask = 18,--补偿奖励任务
	Fatwa = 19,--武林追杀令
	Marry = 20,--结婚
	Master = 21,--师徒
	BreakUp = 22,--离婚
	RemoveMaster = 23,--出师评价
	ForceRemoveMarry = 24,--强制离婚
	ForceRemoveMaster = 25,--强制解除师傅
	ForceRemoveApprentice = 26,--强制解除徒弟
	ReRing = 27,--回收戒指
	EightBuddhaTask = 28, -- 链任务
	MartialCheats = 29, -- 池任务 
	HegemonyTask = 30,
	GangFight = 31,--建立帮战
	JoinGangFight = 32,--参加帮战
	Boss =33,--世界BOSS
	ZhuanFu=36 ,--转服

	Indemnify20101108 = { },
}


TaskCategory = {
	"主线任务",
	"支线任务",
	"日常任务",
	"运营任务",
	
	PrimaryMission = 1,
	BonusMission = 2,
	DailyMission = 3,
	OperationMission = 4,
}

_Task = { }

function _Task:newInstance(o)
	o = o or { }
	setmetatable(o, self)
	self.__index = self
	return o
end

function _Task:new(template)

	if template.type == TaskProfile.CommonTask then
		return _CommonTask:new(template)
	elseif template.type == TaskProfile.Buy then
		return _BuyTask:new(template)
	elseif template.type == TaskProfile.Sell then
		return _SellTask:new(template)
	elseif template.type == TaskProfile.Exchange then
		table.getglobal("Temp.Task").Exchange = template.id
		return _ExchangeTask:new(template)
	elseif template.type == TaskProfile.Mall then
		return _MallTask:new(template)
	elseif template.type == TaskProfile.Daily then
		return _DailyTask:new(template)
	elseif template.type == TaskProfile.Barter then
		return _Barter:new(template)
	elseif template.type == TaskProfile.RandomTask then
		return _RandomTask:new(template)
	elseif template.type == TaskProfile.Depot then
		return _Depot:new(template)
	elseif template.type == TaskProfile.Indemnify20101108 then
		return _Indemnify20101108:new(template)
	elseif template.type == TaskProfile.ScrollTask then
		return _ScrollTask:new(template)
	elseif template.type == TaskProfile.CreatGang then
		table.getglobal("Temp.Task").CreatGang = template.id
		return _CreatGang:new(template)
	elseif template.type == TaskProfile.DismissGang then
		return _DismissGang:new(template)
	elseif template.type == TaskProfile.RandomDaily then
		return _RandomDaily:new(template)
	elseif template.type == TaskProfile.Auction then
		return _AuctionTask:new(template)
	elseif template.type == TaskProfile.ClearRedName then
		return _ClearTask:new(template)
	elseif template.type == TaskProfile.HonorBuy then
		return _HonorBuyTask:new(template)
	elseif template.type == TaskProfile.Marry then
		return _Marry:new(template)
	elseif template.type == TaskProfile.Master then
		return _Master:new(template)
	elseif template.type == TaskProfile.BreakUp then
		return _BreakUp:new(template)
	elseif template.type == TaskProfile.RemoveMaster then
		return _RemoveMaster:new(template)
	elseif template.type == TaskProfile.ForceRemoveMaster then
		return _ForceRemoveMaster:new(template)
	elseif template.type == TaskProfile.ForceRemoveApprentice then
		return _ForceRemoveApprentice:new(template)	
	elseif template.type == TaskProfile.ForceRemoveMarry then
		return _ForceRemoveMarry:new(template)
	elseif template.type == TaskProfile.ReRing then
		return _ReRing:new(template)	
	elseif template.type == TaskProfile.GetItemTask then
		return _GetItemTask:new(template)
	elseif template.type == TaskProfile.TimingAward then
		table.getglobal("Temp.Task").TimingAward = template.id
		return _TimingAward:new(template)
	elseif template.type == TaskProfile.Fatwa then
		return _Fatwa:new(template)
	elseif template.type == TaskProfile.GangFight then
		return _GangFight:new(template)
	elseif template.type == TaskProfile.JoinGangFight then
		return _JoinGangFight:new(template)
	elseif template.type == TaskProfile.Boss then
		return _Boss:new(template)	
	elseif template.type == TaskProfile.ZhuanFu then
		return _ZhuanFu:new(template)	
	elseif template.type == TaskProfile.MartialCheats then
	     return _MartialCheatsTask:new(template)
	elseif template.type == TaskProfile.EightBuddhaTask then
	     return _EightBuddhaTask:new(template)
	elseif template.type == TaskProfile.HegemonyTask then
	     return _HegemonyTask:new(template)
	else
		print(template.name .. "类型未知")
	end
end

function _Task:shownInTalkList()
end

function _Task:findPath()
	return { mapid = 0, mapx = 0, mapy = 0, }
end

function _Task:showInMEMOS()
end

function _Task:acceptTask()
	log.error("接受任务" .. self.name .. "失败，此任务不允许快速接受！")
end

function _Task:consignTask()
	log.error("完成任务" .. self.name .. "失败，此任务不允许快速完成！")
end

-------------------------------------------------------------------------------
--取消任务
function _Task:cancelTask(jrole)
	if self.noGiveUp then
		return
	end
	
	_DelTaskState(_GetRoleTask(jrole), self.id)
	
	return true
end

function _Task:self_check(jrole, task_state, curMin)
end