package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Scanner;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.community.Communitys;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.gang.GangJobTitle;
import com.joyveb.tlol.role.Role;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCardService;
import com.joyveb.tlol.role.Vocation;
import com.joyveb.tlol.store.Store;
import com.joyveb.tlol.task.Task;
import com.joyveb.tlol.util.Log;
import com.joyveb.tlol.util.StringToInt;

public class RoleData extends Role {
	/** 角色职业 */
	private byte vocation; // 0 侠客 1 术士 2刺客
	
	/**帮派职位 */
	private byte jobTitle;
	
	private String friends;
	private String marry;
	private String master;
	private String apprentice;
	private String appdelmaster;
	/**黑名单 */
	private String foes;
	/**仇人 **/
	private String enemies;

	private String skills;

	private String tasks;

	/** 用户拥有的物品 */
	private String storeStr = "";

	private String buff;

	/** 所在地图 */
	private short lastmap;

	/** 所在x位置 */
	private int lastmapx;
	/** 所在位置y */
	private int lastmapy;

	/** 是否被追杀 */
	public boolean isNotFatwa;
	
	
	public String getStoreStr() {
		return storeStr;
	}

	public byte getVocation() {
		return vocation;
	}

	public void setVocation(byte vocation) {
		this.vocation = vocation;
	}

	public byte getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(byte jobTitle) {
		this.jobTitle = jobTitle;
	}

	public void setStoreStr(String storeStr) {
		if (storeStr == null)
			this.storeStr = "";
		else
			this.storeStr = storeStr;
	}

	public String getFriends() {
		return friends;
	}

	public void setFriends(String friends) {
		this.friends = friends;
	}
	
	public String getMarry() {
		return marry;
	}

	public void setMarry(String marry) {
		this.marry = marry;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}
	
	public String getApprentice() {
		return apprentice;
	}

	public void setApprentice(String apprentice) {
		this.apprentice = apprentice;
	}
	
	public String getAppdelmaster() {
		return appdelmaster;
	}

	public void setAppdelmaster(String appdelmaster) {
		this.appdelmaster = appdelmaster;
	}

	public String getEnemies() {
		return enemies;
	}

	public void setEnemies(String enemies) {
		this.enemies = enemies;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public void setTasks(String tasks) {
		this.tasks = tasks;
	}

	public String getTasks() {
		return tasks;
	}

	public String getBuff() {
		return buff;
	}

	public void setBuff(String buff) {
		this.buff = buff;
	}

	RoleDataStruct roleDataStruct = new RoleDataStruct();

	private void resetBasicPoints(){
		strength = agility = intellect = vitality = level;
	}
	
	private boolean checkRoleBasicPoint(){
		return level * 7 - 3 >= strength + agility + intellect + vitality;
	}

	/**
	 * 将RoleData转换为RoleBean
	 * @return RoleBean
	 */
	@SuppressWarnings("unchecked")
	public RoleBean toRole() {
		RoleBean role = new RoleBean();

		try {
			role.setStore(Store.deserialize(role, this.storeStr));
		}catch(Exception e) {
			Log.error(Log.ERROR, "toRole", e);
			return null;
		}
		
		role.setRoleid(this.getRoleid());
		role.setZoneid(this.getZoneid());
		role.setUserid(this.getUserId());

		role.setNick(this.getName());
		//如果是游客，上线时提示改名
		if(role.getNick().startsWith("游客")){
			role.setCanChangeName(true);
		}else{
			role.setCanChangeName(false);
		}
		role.setSex(this.getSex());
		role.setVocation(Vocation.values()[this.getVocation()]);
		role.setAnimeGroup(this.getAnimeGroup());
		role.setAnime(this.getAnime());
		role.setLevel(this.getLevel());
		role.setGold(this.getGold());
		role.setMark(this.getMark());
		role.setMoney(this.getMoney());
		role.getCoords().setMap(this.getLastmap()).setX(this.getLastmapx()).setY(this.getLastmapy());
		role.setStrength(this.getStrength());
		role.setAgility(this.getAgility());
		role.setIntellect(this.getIntellect());
		role.setVitality(this.getVitality());
		role.setOnlineSec(this.getOnlineSec());
		role.setEXP(this.getEXP());
		role.setTasks(new Task(role, this.getTasks()));
		role.setRacingMarks(this.getRacingMarks());
		role.setHonor(this.getHonor());
		role.setEvil(this.getEvil());
		role.setSneakAttackNum(this.getSneakAttackNum());
		role.setLastBattleTime(this.getLastBattleTime());
		if (marry != null) {
			Scanner sc = new Scanner(marry);
			while (sc.hasNextInt())
				role.addMarry(sc.nextInt());
		}
		if (master != null) {
			Scanner sc = new Scanner(master);
			while (sc.hasNextInt())
				role.addMaster(sc.nextInt());
		}
		if (apprentice != null) {
			Scanner sc = new Scanner(apprentice);
			while (sc.hasNextInt())
				role.addApprentice(sc.nextInt());
		}
		if (appdelmaster != null) {
			Scanner sc = new Scanner(appdelmaster);
			while (sc.hasNextInt())
				role.addAppdelmaster(sc.nextInt());
		}
		
		if (friends != null) {
			Scanner sc = new Scanner(friends);
			while (sc.hasNextInt())
				role.addFriend(sc.nextInt());
		}

		if (foes != null) {
			Scanner sc = new Scanner(foes);
			while(sc.hasNextInt())
				role.addFoe(sc.nextInt());
		}
		
		if (enemies != null) {
			Scanner sc = new Scanner(enemies);
			while(sc.hasNextInt())
				role.addEnemy(sc.nextInt());
		}

		RoleCardService.INSTANCE.loadCard(role, role.getFrends(), role.getFoes(),role.getMarry(),role.getMaster(),role.getApprentice(),role.getEnemys());

		role.loadSkill(skills);
		
		role.getBuffManager().deserialize(this.getBuff());
		role.setJoyid(this.getJoyid());

		role.setEpithet(this.getEpithet());
		role.setCommunity(this.getCommunity());
		Communitys.INSTANCE.addRole(role);

		role.setRegdate(this.regdate);
		role.setLogoff(this.logoff);
		role.setMarryringtime(this.marryringtime);
		role.setRemovemastertime(this.removemastertime);
		role.setRemoveapptime(this.removeapptime);
	
		role.setCharm(charm);

		role.setGangid(gangid);
		role.setJobTitle(GangJobTitle.getInstance(jobTitle));
		role.setTotalKillPlayerNum(totalKillPlayerNum);
		role.setKillPlayerNum(killPlayerNum);
		role.setResetKillPlayerNumLeftTime(resetKillPlayerNumLeftTime);
		role.setIsNotFatwa(isNotFatwa);
		
		//这两个需要放到上限计算出来之后
		role.setHP(this.HP);
		role.setMP(this.MP);
		return role;
	}

	public RoleData toData() {
		return this;
	}
	
	public RoleDataStruct getRoleDataStruct() {
		return roleDataStruct;
	}

	public void setRoleDataStruct(RoleDataStruct roleDataStruct) {
		this.roleDataStruct = roleDataStruct;
	}
	
	public class RoleDataStruct extends DataStruct {
		private RoleData roleData = RoleData.this;
		
		@Override
		public boolean readFromRs(ResultSet rs) throws SQLException {
			// 这里为了兼容roleListData的调用没有写rs.next
			// rs.next在commonParser里被调用
			setRoleid(rs.getInt(1));
			setZoneid(rs.getShort(2));
			setUserId(rs.getInt(3));

			setName(rs.getString(4));
			setSex(rs.getByte(5));
			setVocation(rs.getByte(6));
			setAnimeGroup(rs.getShort(7));
			setAnime(rs.getShort(8));
			setLevel(rs.getShort(9));
			setGold(rs.getInt(10));
			setMark(rs.getInt(11));
			//setMoney(rs.getInt(12));
			setLastmap(rs.getShort(13));
			setLastmapx(rs.getInt(14));
			setLastmapy(rs.getInt(15));
			setHP(rs.getInt(16));
			setMP(rs.getInt(17));
			setStrength(rs.getInt(18));
			setAgility(rs.getInt(19));
			setIntellect(rs.getInt(20));
			setVitality(rs.getInt(21));
			setOnlineSec(rs.getInt(22));
			setEXP(rs.getInt(23));
			setTasks(rs.getString(24));
			setFriends(rs.getString(25));
			setFoes(rs.getString(26));
			setSkills(rs.getString(27));
			setStoreStr(rs.getString(28));
			setBuff(rs.getString(29));
			setJoyid(rs.getString(30));
			epithet = rs.getString(31);
			community = rs.getLong(32);
			regdate = rs.getTimestamp(33);
			logoff = rs.getTimestamp(34);
			charm = rs.getInt(35);
			gangid = rs.getLong(36);
			jobTitle = rs.getByte(37);
			totalKillPlayerNum = rs.getInt(38);
			killPlayerNum = rs.getShort(39);
			resetKillPlayerNumLeftTime = rs.getLong(40);
			setRacingMarks(rs.getInt(41));
			setHonor(rs.getInt(42));
			setEvil(rs.getInt(43));
			setSneakAttackNum(rs.getInt(44));
			setLastBattleTime(rs.getTimestamp(45));
			setMarry(rs.getString(46));
			setMaster(rs.getString(47));
			setApprentice(rs.getString(48));
			setAppdelmaster(rs.getString(49));
			marryringtime = rs.getTimestamp(50);
			removemastertime = rs.getTimestamp(51);
			removeapptime = rs.getTimestamp(52);
			setEnemies(rs.getString(53));

			try {
				if(rs.getInt(54) == 1){
					setIsNotFatwa(true);
				}else{
					setIsNotFatwa(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
				setIsNotFatwa(false);
			}
		
			
			
			if(!checkRoleBasicPoint()){
				Log.error(Log.ERROR, "发现错误的基础属性点: strength: " + strength + " agility: " + agility + " intellect: " + intellect + " vitality: " + vitality + " level: " + level);
				resetBasicPoints();
			}
			
			return true;
		}
		

		
		@Override
		public PreSql getPreSql_delete() {
			PreSql preSql = new PreSql();
//			preSql.sqlstr = "delete from tbl_tianlong_role" + "_"
//					+ TianLongServer.srvId + " t where t.nid = ?";
//			preSql.parameter.add(roleid);
			
			preSql.sqlstr = "update tbl_tianlong_role"
					+ "_"
					+ TianLongServer.srvId
					+ " "
					+ "set SRACINGMARK = 1  where nid = ? ";
			preSql.parameter.add(roleid);
			
			return preSql;
		}
		
		@Override
		public PreSql getPreSql_insert() {
			PreSql preSql = new PreSql();
			preSql.sqlstr = "insert into tbl_tianlong_role"
					+ "_"
					+ TianLongServer.srvId
					+ "(nid, nzoneid, nuserid, snick, nsex, nvocation, ngroup, namini, nlevel, ngold, "
					+ "nmark, nmoney, nlastmap, nlastmapx, nlastmapy, nred, nblue, nstrength, nagility, nintellect, "
					+ "nstamina, nonlinesec, nexp, citems, dregdate, nlastbattletime, marry,master,apprentice,appdelmaster,marryringtime,removemastertime,removeapptime,isNotFatwa ) "
					+ "values (seq_tianlong_role_" + TianLongServer.srvId
					+ ".nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
					+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + "?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?)";
			preSql.parameter.add(zoneid);
			preSql.parameter.add(userId);
			preSql.parameter.add(name);
			preSql.parameter.add(sex);
			preSql.parameter.add(vocation);
			preSql.parameter.add(animeGroup);
			preSql.parameter.add(anime);
			preSql.parameter.add(level);
			preSql.parameter.add(gold);
			preSql.parameter.add(mark);
			preSql.parameter.add(money);
			preSql.parameter.add(lastmap);
			preSql.parameter.add(lastmapx);
			preSql.parameter.add(lastmapy);
			preSql.parameter.add(HP);
			preSql.parameter.add(MP);
			preSql.parameter.add(strength);
			preSql.parameter.add(agility);
			preSql.parameter.add(intellect);
			preSql.parameter.add(vitality);
			preSql.parameter.add(onlineSec);
			preSql.parameter.add(EXP);
			preSql.parameter.add(storeStr);
			preSql.parameter.add(regdate == null ? null : new Timestamp(regdate
					.getTime()));
			preSql.parameter.add(new Timestamp(regdate.getTime()));
			preSql.parameter.add(marry);
			preSql.parameter.add(master);
			preSql.parameter.add(apprentice);
			preSql.parameter.add(appdelmaster);
			preSql.parameter.add(marryringtime == null ? null : new Timestamp(marryringtime.getTime()));
			preSql.parameter.add(removemastertime == null ? null : new Timestamp(removemastertime.getTime()));
			preSql.parameter.add(removeapptime == null ? null : new Timestamp(removeapptime.getTime()));
			if(isNotFatwa == false){
				preSql.parameter.add(0);
			}else{
				preSql.parameter.add(1);			
			}
			return preSql;
		}
		
		public PreSql getPreSql_update() {

			PreSql preSql = new PreSql();
			preSql.sqlstr = "update tbl_tianlong_role"
					+ "_"
					+ TianLongServer.srvId
					+ " "
					+ "set snick = ?, ngroup = ?, namini = ?, nlevel = ?, ngold = ?, nmark = ?, nlastmap = ?, nlastmapx = ?, nlastmapy = ?, "
					+ "nred = ?, nblue = ?, nstrength = ?, nagility = ?, nintellect = ?, nstamina = ?, "
					+ "nonlinesec = ?, nexp = ?, ctask = ?, sfriends = ?, sfoes = ?, skills = ?, citems = ?, sbuffs = ?, "
					+ "SEPITHET = ?, NCOMMUNITY = ?, dlogoff = ?, ncharm = ?, ngangid = ?, njobtitle = ?, NTOTALKILLNUM = ?, NKILLNUM = ?, NRESETKILLNUMTIME = ? , "
					+ "NHONOR = ?, NEVIL = ?, NSNEAKATTACKNUM = ?, NLASTBATTLETIME = ? ,MARRY = ?, MASTER=? ,APPRENTICE=? , APPDELMASTER=?,MARRYRINGTIME = ? , REMOVEMASTERTIME = ?,REMOVEAPPTIME = ?,SENEMIES = ?,isNotFatwa = ? "
					+ "where nid = ?";

			preSql.parameter.add(name);
			// preSql.parameter.add(sex);
			// preSql.parameter.add(vocation);
			preSql.parameter.add(animeGroup);
			preSql.parameter.add(anime);
			preSql.parameter.add(level);
			preSql.parameter.add(gold);
			preSql.parameter.add(mark);
			preSql.parameter.add(lastmap);
			preSql.parameter.add(lastmapx);
			preSql.parameter.add(lastmapy);
			preSql.parameter.add(HP);
			preSql.parameter.add(MP);
			preSql.parameter.add(strength);
			preSql.parameter.add(agility);
			preSql.parameter.add(intellect);
			preSql.parameter.add(vitality);
			preSql.parameter.add(onlineSec);
			preSql.parameter.add(EXP);
			preSql.parameter.add(tasks);
			preSql.parameter.add(friends);
			preSql.parameter.add(foes);
			preSql.parameter.add(skills);
			preSql.parameter.add(storeStr);
			preSql.parameter.add(buff);
			preSql.parameter.add(epithet);
			preSql.parameter.add(community);
			preSql.parameter.add(logoff == null ? null : new Timestamp(logoff.getTime()));
			preSql.parameter.add(charm);
			preSql.parameter.add(gangid);
			preSql.parameter.add(jobTitle);
			preSql.parameter.add(totalKillPlayerNum);
			preSql.parameter.add(killPlayerNum);
			preSql.parameter.add(resetKillPlayerNumLeftTime);
//			preSql.parameter.add(racingMarks);
			preSql.parameter.add(honor);
			preSql.parameter.add(evil);
			preSql.parameter.add(sneakAttackNum);
			preSql.parameter.add(lastBattleTime == null ? null : new Timestamp(lastBattleTime.getTime()));
			preSql.parameter.add(marry);
			preSql.parameter.add(master);
			preSql.parameter.add(apprentice);
			preSql.parameter.add(appdelmaster);
			preSql.parameter.add(marryringtime == null ? null : new Timestamp(marryringtime.getTime()));
			preSql.parameter.add(removemastertime == null ? null : new Timestamp(removemastertime.getTime()));
			preSql.parameter.add(removeapptime == null ? null : new Timestamp(removeapptime.getTime()));
			preSql.parameter.add(enemies);
			if(isNotFatwa == false){
				preSql.parameter.add(0);
			}else{
				preSql.parameter.add(1);			
			}
			/***************************************************************************/

			preSql.parameter.add(roleid);

			String savedData = "userid " + userId + " | " + "roleid " + roleid
					+ " | " + "name " + name + " | " + "sex " + sex + " | "
					+ "vocation " + vocation + " | " + "animeGroup " + animeGroup
					+ " | " + "anime " + anime + " | " + "level " + level + " | "
					+ "gold " + gold + " | " + "mark " + mark + " | " + "money "
					+ money + " | " + "lastmap " + lastmap + " | " + "lastmapx "
					+ lastmapx + " | " + "lastmapy " + lastmapy + " | " + "HP "
					+ HP + " | " + "MP " + MP + " | " + "strength " + strength
					+ " | " + "agility " + agility + " | " + "intellect "
					+ intellect + " | " + "vitality " + vitality + " | "
					+ "onlineSec " + onlineSec + " | " + "EXP " + EXP + " | "
					+ "tasks " + tasks + " | " + "friends " + friends + " | "
					+ "foes " + foes + " | " + "skills " + skills + " | "
					+ "store " + storeStr + " | epithet " + epithet + " | community "
					+ community + " | charm " + charm + " | gangid " + gangid
					+ " | jobtitle " + jobTitle+ " | honor " + honor+ " | evil " + evil 
					+ " | sneakAttackNum " + sneakAttackNum + " | lastBattleTime " + lastBattleTime + " | marry "
							+ marry + " | master " + master + " | apprentice "
							+ apprentice + " | appdelmaster " + appdelmaster
							+ " | marryringtime " + marryringtime
							+ " | removemastertime " + removemastertime  
							+ " | removeapptime " + removeapptime + " | enemies " + enemies;

			Log.info(Log.DATA, savedData);
			return preSql;
		}

		@Override
		public PreSql getPreSql_query() {
			PreSql preSql = new PreSql();
			preSql.sqlstr = "select t1.NID, t1.NZONEID, t1.NUSERID, t1.SNICK, t1.NSEX, t1.NVOCATION, t1.NGROUP, t1.NAMINI, "
					+ "t1.NLEVEL, t1.NGOLD, t1.NMARK, t1.NMARK, t1.NLASTMAP, t1.NLASTMAPX, t1.NLASTMAPY, t1.NRED, "
					+ "t1.NBLUE, t1.NSTRENGTH, t1.NAGILITY, t1.NINTELLECT, t1.NSTAMINA, t1.NONLINESEC, t1.NEXP, t1.CTASK, "
					+ "t1.SFRIENDS, t1.SFOES, t1.SKILLS, t1.citems, t1.SBUFFS, t1.NUSERID, t1.SEPITHET, t1.NCOMMUNITY, t1.dregdate, "
					+ "t1.dlogoff, t1.ncharm, t1.ngangid, t1.njobtitle, t1.NTOTALKILLNUM, t1.NKILLNUM, t1.NRESETKILLNUMTIME, t1.SRACINGMARK, "
					+ "t1.NHONOR, t1.NEVIL, t1.NSNEAKATTACKNUM, t1.NLASTBATTLETIME, t1.MARRY, t1.MASTER, t1.APPRENTICE, t1.APPDELMASTER, t1.MARRYRINGTIME, t1.REMOVEMASTERTIME, t1.REMOVEAPPTIME,t1.SENEMIES,t1.isNotFatwa "
					+ "from TBL_TIANLONG_ROLE"
					+ "_"
					+ TianLongServer.srvId
					+ " t1 "
					+ "where t1.snick = ? and t1.SRACINGMARK=0 ";

			preSql.parameter.add(name);
			return preSql;
		}

		public void setRoleData(RoleData roleData) {
			this.roleData = roleData;
		}

		public RoleData getRoleData() {
			return roleData;
		}
		
	}
	
	public short getLastmap() {
		return lastmap;
	}

	public void setLastmap(short lastmap) {
		this.lastmap = lastmap;
	}

	public int getLastmapx() {
		return lastmapx;
	}

	public void setLastmapx(int lastmapx) {
		this.lastmapx = lastmapx;
	}

	public int getLastmapy() {
		return lastmapy;
	}

	public void setLastmapy(int lastmapy) {
		this.lastmapy = lastmapy;
	}

	@Override
	public byte jobTitle() {
		return this.jobTitle;
	}

	@Override
	public String getSkillStr() {
		return this.skills;
	}

	@Override
	public byte getVocationCode() {
		return this.vocation;
	}

	public String getFoes() {
		return foes;
	}

	public void setFoes(String foes) {
		this.foes = foes;
	}

	public boolean getIsNotFatwa() {
		return isNotFatwa;
	}

	public void setIsNotFatwa(boolean isNotFatwa) {
		this.isNotFatwa = isNotFatwa;
	}
}
