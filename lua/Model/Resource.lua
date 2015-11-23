
ItemSet = { }

MapSet = { }

AreaSet = { }

MonsterSet = { }

NPCSet = { }

SkillSet = { }

TaskSet = { }

TaskElementSet = { }

GangSet = { }

-----------------以下采用元表实现__newindex，使用rawset避免死循环。这样可避免污染数据集的key-----------------

setmetatable(ItemSet, { __newindex = function (t, k, v) rawset(t, k, _Item:new(v)) end, })

setmetatable(MapSet, { __newindex = function (t, k, v) rawset(t, k, _Map:new(v)) end, })

setmetatable(AreaSet, { __newindex = function (t, k, v) rawset(t, k, _Area:new(v)) end, })

setmetatable(MonsterSet, { __newindex = function (t, k, v) rawset(t, k, _Monster:new(v)) end, })

setmetatable(NPCSet, { __newindex = function (t, k, v) rawset(t, k, _NPC:new(v)) end, })

setmetatable(SkillSet, { __newindex = function (t, k, v) rawset(t, k, _Skill:new(v)) end, })

setmetatable(TaskSet, {__newindex = function (t, k, v) rawset(t, k, _Task:new(v)) end})

setmetatable(TaskElementSet, { __newindex = function (t, k, v) rawset(t, k, _TaskElement:new(v)) end, })

setmetatable(GangSet, { __newindex = function (t, k, v) rawset(t, k, _Gang:new(v)) end, })
