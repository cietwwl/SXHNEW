package com.joyveb.tlol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.fatwa.FatwaManager;
import com.joyveb.tlol.hegemony.Hegemony;
import com.joyveb.tlol.map.GridMapSys;
import com.joyveb.tlol.marry.ApprenticeManager;
import com.joyveb.tlol.marry.MarryManager;
import com.joyveb.tlol.marry.MasterManager;
import com.joyveb.tlol.net.NetHandler;
import com.joyveb.tlol.pay.util.HttpRequest;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.task.TaskState;
import com.joyveb.tlol.user.UserBean;
import com.joyveb.tlol.util.Log;

public final class OnlineService {
	private static Vector<UserBean> unloginUsers = new Vector<UserBean>();

	private static Map<Integer, RoleBean> onlineMap = new ConcurrentHashMap<Integer, RoleBean>();

	private static Map<Integer, Integer> userMap = new ConcurrentHashMap<Integer, Integer>();

	private static Map<Integer, List<Hegemony>> topHegemonys = new HashMap<Integer, List<Hegemony>>();

	private static Map<Integer, Hegemony> hegemonys = new HashMap<Integer, Hegemony>();

	private static boolean start = true;// 狮王争霸是否能结算，true能，false不能
	
	private static List<String> codeList =new ArrayList<String>();
	
	

	public static List<String> getCodeList() {
		return codeList;
	}
	
	public static void sendHttpZhuanFu(RoleBean role) {
		String url= Conf.instance().getUrl()+"userId="+role.getUserid()+"&roleId="+role.getRoleid()+"&zoneId="+ Conf.instance().getSrvId().substring(4)+"&key=ECgntdH3";
		try {
			URL getUrl =new URL(url);
			// 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
			// 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
			HttpURLConnection connection =(HttpURLConnection) getUrl.openConnection();
			// 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到
			// 服务器
			connection.connect();
			// 取得输入流，并使用Reader读取
			BufferedReader reader =new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));//设置编码,否则中文乱码

			String lines;
			while((lines= reader.readLine())!=null){
			        //lines = new String(lines.getBytes(), "utf-8");
			System.out.println(lines);
			}
			reader.close();
			// 断开连接
			connection.disconnect();

			System.out.println(url);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 停服写激活码
	 */
	public static void writeCodeList() {
		try {

			File file = new File("code.txt");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			FileOutputStream outputStream;
			try {
				outputStream = new FileOutputStream(file);

				for (String s : codeList) {
					try {
						outputStream.write(s.getBytes());
						outputStream.write("\r\n".getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean getStart() {
		return start;
	}

	public static void setStart(boolean start) {
		OnlineService.start = start;
	}

	public static Map<Integer, Integer> getUserMap() {
		return userMap;
	}

	public static RoleBean getOnline(final int roleid) {
		return onlineMap.get(roleid);
	}

	private OnlineService() {

	}

	public static RoleBean getOnline(final String nick) {
		ArrayList<Integer> onlineUserids = new ArrayList<Integer>();
		onlineUserids.addAll(onlineMap.keySet());

		for (int roleid : onlineUserids) {
			RoleBean oneOnline = onlineMap.get(roleid);
			if (oneOnline.getNick().equals(nick))
				return oneOnline;
		}

		return null;
	}

	public static boolean isRoleOnline(final int roleid) {
		return onlineMap.containsKey(roleid);
	}

	public static void addUnLoginUser(final UserBean user) {
		unloginUsers.add(user);
	}

	public static void removeUnLoginUser(final UserBean user) {
		unloginUsers.remove(user);
	}

	public static void addOnline(final RoleBean role) {
		onlineMap.put(role.getRoleid(), role);
	}

	public static void deleteOnline(final int roleid) {
		RoleBean role = onlineMap.get(roleid);
		// 判断任务是否带有未完成的狮王争霸任务 有的话将其任务设为已完成
		TaskState ts = role.getTasks().getTaskState(24000);
		if (ts != null && !ts.isFinished()) {
			LuaService.callLuaFunction("checkHemony", role);
		}

		userMap.remove(role.getUserid());

		if (role.getRoleid() == 0)
			return;

		role.setOnlineSec((int) (role.getOnlineSec() + (System.currentTimeMillis() - role.getLoginTime()) / 1000));
		role.setLogoff(new Date());

		// 删除key
		RedisMethod.instance().deleteKey(ConfRedis.instance().getKey(role.getUserid()));
		// 删除key

		if (role.getNetHandler() != null)
			role.getNetHandler().close(NetHandler.STATE_CLOSED_BY_APP_LEVEL);

		role.setNetHandler(null);

		CommonParser.getInstance().postTask(DbConst.ROLE_UPDATE, role, role.toData().getRoleDataStruct());
	}

	public static Vector<UserBean> getUnLogUsers() {
		return unloginUsers;
	}

	public static ArrayList<Integer> getAllOnlines() {
		ArrayList<Integer> onlineUserids = new ArrayList<Integer>();
		onlineUserids.addAll(onlineMap.keySet());
		return onlineUserids;
	}

	public static ArrayList<RoleBean> getSameZone(final RoleBean role) {
		ArrayList<RoleBean> sameZone = new ArrayList<RoleBean>();

		ArrayList<Integer> onlineUserids = new ArrayList<Integer>();
		onlineUserids.addAll(onlineMap.keySet());
		for (int roleid : onlineUserids) {
			RoleBean oneOnline = onlineMap.get(roleid);
			if (oneOnline != null && oneOnline.getZoneid() == role.getZoneid())
				sameZone.add(oneOnline);
		}

		return sameZone;
	}

	public static void addUserRole(final int uid, final int rid) {
		userMap.put(uid, rid);
	}

	public static void delUserRole(final int uid) {
		userMap.remove(uid);
	}

	public static boolean isUserOnline(final int uid) {
		return userMap.containsKey(uid);
	}

	public static int getUserRoleOnline(final int uid) {
		return userMap.get(uid);
	}

	public static int userIdGetRoleID(final int uid) {

		int i = 0;
		try {
			i = userMap.get(uid);
		} catch (Exception e) {
			return 0;
		}
		return i;
	}

	public static int getURMapSize() {
		return userMap.size();
	}

	public static int getRoleOnlineSize() {
		return onlineMap.size();
	}

	public static void removeOnlinePlayer(final int roleId) {
		onlineMap.remove(roleId);
	}

	public static void writeBackDB() {
		for (int rid : OnlineService.getAllOnlines()) {
			RoleBean role = OnlineService.getOnline(rid);
			if (role != null)
				role.updateRole();
		}
	}

	public static void showOnline() {
		int index = 1;
		for (int rid : OnlineService.getAllOnlines()) {
			RoleBean role = OnlineService.getOnline(rid);
			if (role != null) {
				Log.info(Log.STDOUT, index + ".    角色  " + role.getNick() + "  id  " + rid);
				index++;
			}
		}
	}

	public static void minTick(final int curMinute) {
		for (int rid : OnlineService.getAllOnlines()) {
			RoleBean role = OnlineService.getOnline(rid);
			if (role != null)
				role.minTick(curMinute);
		}
	}

	public static void seconedTick(final int curSeconed) {
		Iterator<Entry<Integer, RoleBean>> iterator = onlineMap.entrySet().iterator();
		while (iterator.hasNext()) {
			RoleBean player = iterator.next().getValue();
			player.seconedTick(curSeconed);
			player.seconedFatwa(curSeconed);
		}
	}

	// 回写删除追杀
	public static void writeBackDelFatwa() {
		FatwaManager.getInstance().delFatwa();
	}

	// 回写添加追杀

	public static void writeBackInsertFatwa() {
		FatwaManager.getInstance().insertFatwa();
	}

	public static void writeBackMarryDB() {
		MarryManager.getInstance().breakUp();
	}

	public static void writeBackMasterDB() {
		MasterManager.getInstance().removeMaster();
	}

	public static void writeBackApprenticeDB() {
		ApprenticeManager.getInstance().removeApprentice();
	}

	public static void getInHegemony(RoleBean role) {
		Hegemony h = new Hegemony();
		h.setHegemony(role);
		h.setPoints(100);
		int midResult = role.getLevel() / 10;

		int result = midResult % 5;
		if (role.getLevel() >= 100)
			result = 3;
		result = (result <= 3) ? result : 3;
		h.setAcctLevel(result + 1);
		hegemonys.put(h.getSelfId(), h);

	}

	/**
	 * @function 向map中添加Hegemony对象
	 * @author LuoSR
	 * @date 2012-3-6
	 */
	public static void addHegemonyToMap(HashSet<Hegemony> set, List<Integer> roleids) {

		Collection<Hegemony> h = hegemonys.values();
		for (Hegemony hegemony : h) {

			if (roleids.contains(hegemony.getHegemony().getRoleid())) {
				set.add(hegemony);
			}

		}
	}

	public static List<Integer> removeDuplicateWithOrder(List<Integer> list) {
		Set<Integer> set = new HashSet<Integer>();
		List<Integer> newList = new ArrayList<Integer>();
		for (Iterator<Integer> iter = list.iterator(); iter.hasNext();) {
			int element = iter.next().intValue();
			if (set.add(element)) {
				newList.add(element);
			}
		}
		return newList;
	}

	public static void removeDuplicateEachOrder(List<Integer> list1, List<Integer> list2) {
		for (int i = 0; i < list1.size(); i++) {
			for (int j = 0; j < list2.size(); j++) {
				if (list1.get(i).intValue() == list2.get(j).intValue()) {
					list2.remove(j);
				}
			}
		}
	}

	public static void settleHegemony() {
		if (start == true) {
	
	
			List<Integer> roleids11 = removeDuplicateWithOrder(GridMapSys.INSTANCE.getAllRoleInHegemonyForTimeOut(203));
			List<Integer> roleids22 = removeDuplicateWithOrder(GridMapSys.INSTANCE.getAllRoleInHegemonyForTimeOut(204));
			List<Integer> roleids33 = removeDuplicateWithOrder(GridMapSys.INSTANCE.getAllRoleInHegemonyForTimeOut(205));
			List<Integer> roleids44 = removeDuplicateWithOrder(GridMapSys.INSTANCE.getAllRoleInHegemonyForTimeOut(193));

			
			Set<Integer> roleids111 = new HashSet<Integer>(roleids11);
			Set<Integer> roleids222 = new HashSet<Integer>(roleids22);
			Set<Integer> roleids333 = new HashSet<Integer>(roleids33);
			Set<Integer> roleids444 = new HashSet<Integer>(roleids44);

			List<Integer> roleids1 = new ArrayList<Integer>(roleids111);
			List<Integer> roleids2 = new ArrayList<Integer>(roleids222);
			List<Integer> roleids3 = new ArrayList<Integer>(roleids333);
			List<Integer> roleids4 = new ArrayList<Integer>(roleids444);
			
			// =========================删掉重复ID=====================

			try {
				removeDuplicateEachOrder(roleids3, roleids4);
				removeDuplicateEachOrder(roleids2, roleids4);
				removeDuplicateEachOrder(roleids1, roleids4);
				removeDuplicateEachOrder(roleids2, roleids3);
				removeDuplicateEachOrder(roleids1, roleids3);
				removeDuplicateEachOrder(roleids1, roleids2);
			} catch (Exception e) {
				System.out.println("狮王争霸去重");
				e.printStackTrace();
			}
			// =========================删掉重复ID=====================

			HashSet<Hegemony> set1 = new HashSet<Hegemony>();
			HashSet<Hegemony> set2 = new HashSet<Hegemony>();
			HashSet<Hegemony> set3 = new HashSet<Hegemony>();
			HashSet<Hegemony> set4 = new HashSet<Hegemony>();

			addHegemonyToMap(set1, roleids1);
			addHegemonyToMap(set2, roleids2);
			addHegemonyToMap(set3, roleids3);
			addHegemonyToMap(set4, roleids4);

			List<Hegemony> list1 = new ArrayList<Hegemony>(set1);
			List<Hegemony> list2 = new ArrayList<Hegemony>(set2);
			List<Hegemony> list3 = new ArrayList<Hegemony>(set3);
			List<Hegemony> list4 = new ArrayList<Hegemony>(set4);

			// 已经将不同级别的人分开 下面开始排序，使用专用方法得到名次 最后根据每个人的名次进行发奖
			sortRanking(list1);
			sortRanking(list2);
			sortRanking(list3);
			sortRanking(list4);

			// 名次排完开始发奖 发奖通过名次去任务脚本里去发
			LuaService.callLuaFunction("rewardHegemony", list1);
			LuaService.callLuaFunction("rewardHegemony", list2);
			LuaService.callLuaFunction("rewardHegemony", list3);
			LuaService.callLuaFunction("rewardHegemony", list4);
			// 奖励发放完成开始世界通知并加载排行榜
			sendMsg(list1);
			sendMsg(list2);
			sendMsg(list3);
			sendMsg(list4);
			// TODO 加载排行榜
			setTopHegemonys(1, list1);
			setTopHegemonys(2, list2);
			setTopHegemonys(3, list3);
			setTopHegemonys(4, list4);

			// 最后将hegemonys 清空

			hegemonys.clear();
			start = false;
		}
	}

	private static void sortRanking(List<Hegemony> list) {
		if (list != null) {
			Collections.sort(list);
			int rank = 1;
			int previousPoints = 0;
			for (int i = 0; i < list.size(); i++) {
				Hegemony h = list.get(i);
				h.setResultPoints(h.getPoints());
				if (h.getPoints() < 500) {

				} else if (h.getPoints() != previousPoints) {
					previousPoints = h.getPoints();
					h.setRanking(rank);
					Log.info((byte) 0, h.getHegemony().getName() + ":" + h.getRanking() + ":" + h.getAcctLevel());
					rank++;
				} else if (h.getPoints() == previousPoints) {
					previousPoints = h.getPoints();
					h.setRanking(list.get(i - 1).getRanking());
					rank++;
					Log.info((byte) 0, h.getHegemony().getName() + ":" + h.getRanking() + ":" + h.getAcctLevel());
				}
				LuaService.callLuaFunction("transOut", h.getHegemony(), 522);
			}
		}
	}

	public static void sendMsg(List<Hegemony> map) {
		if (map.size() <= 0)
			return;
		if (map.get(0).getResultPoints() < 500)
			return;
		StringBuilder talk = new StringBuilder("恭喜玩家");
		int level = map.get(0).getAcctLevel();
		int i = 0;
		int count = 0;
		for (Hegemony h : map) {
			if (count == 3) {
				break;
			}
			if (h.getResultPoints() >= 500) {
				if (i == 0) {
					talk.append(h.getHegemony().getName());
				} else {
					talk.append("、");
					talk.append(h.getHegemony().getName());
				}
				i++;
			}
			count++;
		}
		talk.append("获得狮王争霸");
		int minLevel = 50 + ((level - 1) * 10);
		if (minLevel < 80) {
			talk.append(minLevel + "-");
			talk.append(minLevel + 9);
			talk.append("级别");
		} else {
			talk.append("80级以上级别");
		}
		if (i == 1) {
			talk.append("第一名！");
		} else if (i == 2) {
			talk.append("第一、二名！");
		} else {
			talk.append("第一、二、三名！");
		}
		if (i == 0)
			return;
		MessageSend.prepareBody();

		if (talk != null && !"".equals(talk)) {
			MessageSend.putShort((short) 400);
			MessageSend.putString(talk.toString());
			MessageSend.putInt(0xff0000);
		}
		MessageSend.putShort((short) 0);
		for (int rid : OnlineService.getAllOnlines()) {
			RoleBean role = OnlineService.getOnline(rid);
			if (role != null) {
				MessageSend.sendMsg(role, MsgID.MsgID_Special_Train);
			}
		}
	}

	public static void setTopHegemonys(int mapId, List<Hegemony> map) {

		topHegemonys.put(mapId, map);
	}

	public static Hegemony getHegemonys(int roleId) {
		Iterator<Integer> it = hegemonys.keySet().iterator();
		while (it.hasNext()) {
			int selfId = it.next();
			if (hegemonys.get(selfId).getHegemony().getId() == roleId)
				return hegemonys.get(selfId);
		}
		return null;
	}

	public static Map<Integer, Hegemony> getHegemonys() {

		return hegemonys;
	}

	public static String getTopHegemonys(int mapId, int startNum) {
		List<Hegemony> hList = topHegemonys.get(mapId);
		int index = startNum;
		StringBuilder sb = new StringBuilder("");
		sb.append("名次    ");
		sb.append("昵称    ");
		sb.append("积分 /");
		int count = 0;
		if (hList != null)
			for (Hegemony h : hList) {
				if (h.getResultPoints() < 500 || index > (startNum + 2) || index > 10)
					break;
				if (count >= startNum - 1) {
					sb.append("  ");
					sb.append(h.getRanking());
					sb.append("     ");
					sb.append(h.getHegemony().getName());
					sb.append("     ");
					sb.append(h.getPoints());
					sb.append("/");
					index++;
				}
				count++;
			}
		if (count == 0) {
			sb.append("争霸榜还未有人获得名次");
		}
		return sb.toString();
	}

	public static int getTopHegemonyNum(int mapId) {
		List<Hegemony> hList = topHegemonys.get(mapId);
		int count = 0;
		if (hList != null)
			for (Hegemony h : hList) {
				if (h.getResultPoints() < 500 || count >= 10)
					break;

				count++;

			}
		return count;
	}

	public static void failOutHegemony(Hegemony h) {
		// 任务结束将人物设置为已完成并将其Hegemony对象从map中清除
		h.setPoints(0);
		LuaService.callLuaFunction("taskComplete", h.getHegemony());
		int npcid = 522;
		LuaService.callLuaFunction("transOut", h.getHegemony(), npcid);
		hegemonys.remove(h.getSelfId());
	}

	/**
	 * 
	 * 根据userId得到User,断开未登录角色的人的连接
	 * 
	 * @param userId
	 * @return
	 */
	public static void kickUnUser(int userId) {
		for (UserBean userBean : unloginUsers) {
			if (userId == userBean.getUserid()) {
				System.out.println("T掉卡在界面的User userID为:" + userId);
				userBean.getNetHandler().close(NetHandler.STATE_CLOSED_BY_APP_LEVEL);
			}
		}
	}
}