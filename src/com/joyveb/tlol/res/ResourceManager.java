package com.joyveb.tlol.res;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.protocol.RequestResBody;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.util.Log;

public final class ResourceManager extends AgentProxy {

	private static int MAX_DATA_LENGTH_PRE_TIME = 6144;
	private static HashMap<String, GameResource> gameResMap = new HashMap<String, GameResource>();

	public ResourceManager(RoleBean player) {
		this.player = player;
	}
	
	public static void initResource() {
		File directory = new File(TianLongServer.gameResPath);
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isHidden())
				continue;

			// 文件名格式 a01-1或者m01-1
			String[] fileNameAndVer = file.getName().split("-");
			if (fileNameAndVer.length == 2) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(files[i]);
					byte[] fileData = new byte[fis.available()];
					fis.read(fileData);
					GameResource gameRes = new GameResource(fileNameAndVer[0],
							fileData, Integer.parseInt(fileNameAndVer[1]));
					gameResMap.put(fileNameAndVer[0], gameRes);
				} catch (Exception e) {
					Log.error(Log.ERROR, e);
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							Log.error(Log.ERROR, e);
						}
					}
				}
			} else {
				Log.error(Log.ERROR, "错误的资源文件名称: " + files[i].getName());
			}
		}
		Log.info(Log.STDOUT, "加载游戏资源完成");
	}

	public static short getResVer(String resName) {
		GameResource gr = gameResMap.get(resName);
		if (gr == null)
			return 0;
		else
			return (short) gr.getVersion();
	}

	@Override
	public void processCommand(IncomingMsg msg) {
		switch (MsgID.getInstance(msg.getHeader().getMsgID())) {
		case MsgID_Request_Res:
			if (RequestResBody.INSTANCE.readBody(msg.getBody())) {
				GameResource gameRes = null;
				String resName = (RequestResBody.INSTANCE.getResType() == 0 ? "m"
						: "a")
						+ RequestResBody.INSTANCE.getResId();

				// Log.info(Log.STDOUT, "请求文件为 " + resName);

				if ((gameRes = gameResMap.get(resName)) != null) {
					if (RequestResBody.INSTANCE.getOffset() <= gameRes
							.getGameRes().length) {
						prepareBody();
						body.put(RequestResBody.INSTANCE.getResType());
						body.putShort(RequestResBody.INSTANCE.getResId());
						body.putInt(RequestResBody.INSTANCE.getOffset());
						body.putInt(gameRes.getGameRes().length);
						int dataLen = gameRes.getGameRes().length
								- RequestResBody.INSTANCE.getOffset();
						dataLen = dataLen > MAX_DATA_LENGTH_PRE_TIME ? MAX_DATA_LENGTH_PRE_TIME
								: dataLen;
						// Log.info(Log.STDOUT, "发送文件为 " + resName + "大小为 " +
						// dataLen);
						body.putInt(dataLen);
						if (dataLen > 0)
							body.put(gameRes.getGameRes(),
									RequestResBody.INSTANCE.getOffset(),
									dataLen);
						sendMsg(player, MsgID.MsgID_Request_Res_Resp);
					} else {
						Log.error(
								Log.ERROR,
								"请求长度超出资源文件长度 资源文件名为 " + resName + "大小为 "
										+ gameRes.getGameRes().length
										+ "请求偏移量为 "
										+ RequestResBody.INSTANCE.getOffset());
					}
				} else {
					Log.error(Log.ERROR, "请求文件 " + resName + " 不存在!");
				}
			}
			break;
		default:
			Log.error(Log.STDOUT, "processCommand", "unhandled msgid! : " + msg.getHeader().getMsgID());
			break;
		}
	}
}
