
System = luajava.bindClass("java.lang.System")

ArrayList = luajava.bindClass("java.util.ArrayList")

HashMap = luajava.bindClass("java.util.HashMap")

Color = luajava.bindClass("java.awt.Color")

Long = luajava.bindClass("java.lang.Long")

Integer = luajava.bindClass("java.lang.Integer")

Short = luajava.bindClass("java.lang.Short")

Byte = luajava.bindClass("java.lang.Byte")

String = luajava.bindClass("java.lang.String")

Iterator = luajava.bindClass("java.util.Iterator")

Array = luajava.bindClass("java.lang.reflect.Array")

Calendar = luajava.bindClass("java.util.Calendar")

-------------------------------------------------------------------------------

TianLongServer = luajava.bindClass("com.joyveb.tlol.TianLongServer")

Message = luajava.bindClass("com.joyveb.tlol.MessageSend")

OlServ = luajava.bindClass("com.joyveb.tlol.OnlineService")

JBattle = luajava.bindClass("com.joyveb.tlol.battle.Battle")

JMonster = luajava.bindClass("com.joyveb.tlol.battle.MonsterBean")

TaskState = luajava.bindClass("com.joyveb.tlol.task.TaskState")

MsgID = luajava.bindClass("com.joyveb.tlol.protocol.MsgID")

JEquip = luajava.bindClass("com.joyveb.tlol.item.Equip")

JProp = luajava.bindClass("com.joyveb.tlol.item.UbiquitousItem")

JGridMap = luajava.bindClass("com.joyveb.tlol.map.GridMap")

Lua = luajava.bindClass("com.joyveb.tlol.LuaService")

Log = luajava.bindClass("com.joyveb.tlol.util.Log")

Cardinality = luajava.bindClass("com.joyveb.tlol.util.Cardinality").INSTANCE

MailManager = luajava.bindClass("com.joyveb.tlol.mail.MailManager"):getInstance()

JBuff = luajava.bindClass("com.joyveb.tlol.buff.Buff")

JVIPBuff = luajava.bindClass("com.joyveb.tlol.buff.VIPBuff")

Coords = luajava.bindClass("com.joyveb.tlol.map.Coords")

UID = luajava.bindClass("com.joyveb.tlol.util.UID")

FightOne = luajava.bindClass("com.joyveb.tlol.battle.FightOne")

ServerMessage = luajava.bindClass("com.joyveb.tlol.server.ServerMessage")

Communitys = luajava.bindClass("com.joyveb.tlol.community.Communitys").INSTANCE

TopRatedService = luajava.bindClass("com.joyveb.tlol.billboard.TopRatedService").INSTANCE

PayManager = luajava.bindClass("com.joyveb.tlol.pay.connect.ConnectCommonParser"):getInstance()

GameSubtract = luajava.bindClass("com.joyveb.tlol.pay.domain.GameSubtract")

Broadcast = luajava.bindClass("com.joyveb.tlol.schedule.Broadcast")

NetHandler = luajava.bindClass("com.joyveb.tlol.net.NetHandler")

GangService = luajava.bindClass("com.joyveb.tlol.gang.GangService").INSTANCE

GangJobTitle = luajava.bindClass("com.joyveb.tlol.gang.GangJobTitle")

JRole = luajava.bindClass("com.joyveb.tlol.role.RoleBean")

EquipQuality = luajava.bindClass("com.joyveb.tlol.item.EquipQuality")

Vocation = luajava.bindClass("com.joyveb.tlol.role.Vocation")

EquipType = luajava.bindClass("com.joyveb.tlol.item.EquipType")

EquipConst = EquipType

CalcMode = luajava.bindClass("com.joyveb.tlol.util.CalcMode")

Property = luajava.bindClass("com.joyveb.tlol.role.Property")

Bonus = luajava.bindClass("com.joyveb.tlol.item.Bonus")

JStore = luajava.bindClass("com.joyveb.tlol.store.Store")

ItemFeature = luajava.bindClass("com.joyveb.tlol.item.ItemFeature")

BigOrSmallManager = luajava.bindClass("com.joyveb.tlol.bigOrSmall.BigOrSmallManager"):getInstance()

RefineMaterial = luajava.bindClass("com.joyveb.tlol.item.RefineMaterial")

RoleCardService = luajava.bindClass("com.joyveb.tlol.role.RoleCardService").INSTANCE

AutionType = luajava.bindClass("com.joyveb.tlol.auction.AutionType")

AuctionHouse = luajava.bindClass("com.joyveb.tlol.auction.AuctionHouse").INSTANCE

GuessManager = luajava.bindClass("com.joyveb.tlol.guess.GuessManager"):getInstance()

MainMapInfo = luajava.bindClass("com.joyveb.tlol.map.MainMapInfo")

Conf = luajava.bindClass("com.joyveb.tlol.Conf"):instance()
-------------------------------------------------------------------------------
--java map 迭代器
function jmapIter(jmap)
	return	function (iter)
				if _IterHasNext(iter) then
					local entry = _IterGetNext(iter)
					return _EntryGetKey(entry), _EntryGetValue(entry), iter
				end
			end,
			_GetIterator(_MapGetEntrySet(jmap))
end

-------------------------------------------------------------------------------
--java list 迭代器
function jlistIter(jlist)
	return	function (iter)
				if _IterHasNext(iter) then
					return _IterGetNext(iter), iter
				end
			end,
			_GetIterator(jlist)
end

-------------------------------------------------------------------------------
--java set 迭代器
jsetIter = jlistIter
