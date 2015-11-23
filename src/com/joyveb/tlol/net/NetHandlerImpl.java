package com.joyveb.tlol.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.joyveb.tlol.util.Log;

public final class NetHandlerImpl implements Runnable, NetHandler {

	private SocketChannel socketChannel;
	private SelectionKey selectionKey; // 字节缓冲器
	public static final int RCV_BUF_SIZE = 4096; // 此类的设计中，此两个缓冲的尺寸必须>=2
	public static final int SND_BUF_SIZE = 4096;
	private ByteBuffer inputBuffer = ByteBuffer.allocateDirect(RCV_BUF_SIZE)
			.order(ByteOrder.LITTLE_ENDIAN);
	private ByteBuffer outputBuffer = ByteBuffer.allocateDirect(SND_BUF_SIZE)
			.order(ByteOrder.LITTLE_ENDIAN); // 发送和接收用包的池
	public static final int POOL_SIZE = 100;
	private ConcurrentLinkedQueue<IncomingMsg> recvPool = new ConcurrentLinkedQueue<IncomingMsg>();
	private ConcurrentLinkedQueue<byte[]> sendPool = new ConcurrentLinkedQueue<byte[]>();
	private byte state = STATE_NORMAL;
	// private Logger netStatLogger = Logger.getLogger("netstat");
	private Reactor reactor;
	/**
	 * 安全参数
	 */
	public static final int MAX_PKG_LENGTH = 163840; // 允许最大包长度16384
	public static int networkMaxPkgLen = MAX_PKG_LENGTH;
	/**
	 * 最大允许网络数据包
	 */
	public static final int MAX_POOL_SIZE = 512; // 允许最大池长度
	public static int networkMaxPoolSize = MAX_POOL_SIZE;
	/**
	 * 连接超时
	 */
	private long lastReceiveTime = 0;
	public static final int TIME_OUT = 300 * 1000;

	/**
	 * 记录时间
	 */
	// private long lastRecordReceiveTime = 0;
	// private long lastRecordSendTime = 0;
	// private int dataSentLen = 0;
	// private int dataRcvLen = 0;

	/**
	 * 关注事件read write等
	 */
	private int op_hot;

	public NetHandlerImpl(final Reactor r, final SocketChannel sc) throws IOException {

		reactor = r;
		socketChannel = sc;
		op_hot = SelectionKey.OP_READ | SelectionKey.OP_WRITE; // 重新将这个socketChannel注册成read
		reactor.readyForReg(this);
		r.getSelector().wakeup();
		lastReceiveTime = System.currentTimeMillis();
		/**
		 * 初始化BUF状态
		 */
		outputBuffer.flip();
		inputBuffer.clear();

	}
	@Override
	public void register() {
		try {
			selectionKey = socketChannel
					.register(reactor.getSelector(), op_hot);

			selectionKey.attach(this);

			if (socketChannel.isConnectionPending()) {
				socketChannel.finishConnect();
			}

		} catch (Exception ex) {
			Log.error(Log.NET, "register", ex);
			// BasicService.formatLogError("register", ex);
			// ex.printStackTrace();
		}

	}

	/**
	 * 反应器回调接口
	 */
	public void run() {
		try {
			if (getState() != STATE_NORMAL) {
				return;
			}

			if (socketChannel.isConnected()) {
				if (selectionKey.isReadable()) {
					receiveImpl();
				}
				if (selectionKey.isWritable()) {
					sendImpl();
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
			close(STATE_CLOSED_UNKNOW);
			// Log.error(Log.NET, toString() + "连接中断", e);
		}

	}
	@Override
	public byte getState() {
		return state;
	}

	public void setState(final byte state) {
		this.state = state;
	}

	/**
	 * 发送数据
	 * @param buf 
	 */
	@Override
	public void send(final ByteBuffer buf) {
		if (buf != null) {
			byte[] sendData = new byte[buf.limit()];
			buf.flip();
			buf.get(sendData);
			// print(sendData);
			sendPool.add(sendData);
			// dataSentLen += sendData.length;
			// 每五分钟记录一次总该
			// if(System.currentTimeMillis() - lastRecordSendTime >= 300 *
			// 1000){
			// lastRecordSendTime = System.currentTimeMillis();
			// netStatLogger.info("[" + toString() + "] " + "sent: " +
			// dataSentLen + " bytes");
			// }
		}
	}

	/**
	 * 接收
	 * @param userId 
	 * @return
	 */
	@Override
	public IncomingMsg receive(final int userId) {
		IncomingMsg tmpdp = null;
		try {
			if (!recvPool.isEmpty()) {
				tmpdp = recvPool.poll();

				// print(tmpdp.getBody().array());

				tmpdp.setId(userId);
				lastReceiveTime = System.currentTimeMillis();
			} else {
				if (System.currentTimeMillis() - lastReceiveTime > TIME_OUT) { // 超过一定时间没有收到数据，直接断开连接
					close(STATE_CLOSED_TIME_OUT);
				}
			}
		} catch (Exception e) {
			close(STATE_CLOSED_UNKNOW);
			// Log.error(Log.NET, toString() + "连接中断", e);
		}

		// if(tmpdp != null)
		// BasicService.formatLog("NetHandlerImpl.receive", "消息接收" +
		// tmpdp.getHeader());
		return tmpdp;
	}

	/**
	 * 关闭连接
	 * @param s 
	 */
	public void close(final byte s) {
		if (state != STATE_NORMAL) {
			return;
		}
		try {
			// netStatLogger.info(toString() + " ,网络关闭原因：" + s);
			setState(s); // 做好标志

			recvPool.clear();
			sendPool.clear();

			// 关闭
			socketChannel.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
     * 
     */
	// ----------------------------------------------------------------------------
	// 收实现
	// ----------------------------------------------------------------------------
	static final int PKG_SIZE_BYTES = 4; // 包长度的字节数
	/**
	 * 用于存放大于缓冲才能收到的包，即需要多次接收
	 */
	private byte[] recv_curPkg = null;
	private int recv_pos = -1; // 包计数器

	/**
	 * 实现接收过程
	 * 
	 * @throws IOException
	 */
	private void receiveImpl() throws IOException {
		// 用inPkgBytes来判断，上一个包是否完接收完整，如果为null,则说明上个包已收完整
		// 如果不为null,则说明上个包还有部分数据未到达
		if (socketChannel.read(inputBuffer) < 0) {
			close(STATE_CLOSED_BY_REMOTE);
		}
		getNextPkg();
	}

	/**
	 * 从缓冲区里返回下一个包,支持大于缓冲区的包
	 * 
	 * @throws IOException
	 */
	private void getNextPkg() throws IOException {

		inputBuffer.flip();

		while (inputBuffer.remaining() >= PKG_SIZE_BYTES // 循环接收包,4为一个整型，表示包长度
				|| (recv_pos >= 0 && inputBuffer.hasRemaining()) // 如果上一个包未接收完成时，继续接收
		) {
			// 如果上个包已收完整，则创建新的包

			if (recv_pos == -1) {

				int pkgLen = inputBuffer.getInt(); // 得到下一个包的长度
				if (pkgLen > networkMaxPkgLen) {
					Log.error(Log.NET, "getNextPkg", "超过允许最大包长度，系统强制断开连接:"
							+ pkgLen);
					close(STATE_CLOSED_PKG_LEN_ILLEGAL);
					return;
				} else if (pkgLen < 0) {
					Log.error(Log.NET, "getNextPkg", "包长度小于0，系统强制断开连接。");
					close(STATE_CLOSED_PKG_LEN_ILLEGAL);
					return;
				} else {
					// 初始化数组
					recv_curPkg = new byte[pkgLen];
					recv_pos = 0;
				}
			}
			int need = recv_curPkg.length - recv_pos;
			if (inputBuffer.remaining() >= need) { // 可以把当前包读完整
				inputBuffer.get(recv_curPkg, recv_pos, need); // 复制缓冲区中的数据到tb中

				int bodyLength = recv_curPkg.length - 14;

				ByteBuffer header = ByteBuffer.allocateDirect(18).order(
						ByteOrder.LITTLE_ENDIAN);
				ByteBuffer body = ByteBuffer.allocateDirect(bodyLength).order(
						ByteOrder.LITTLE_ENDIAN);

				// 前面的INT是包长
				header.position(4);
				header.put(recv_curPkg, 0, 14);
				body.put(recv_curPkg, 14, bodyLength);
				IncomingMsg imsg = new IncomingMsg(this, header, body);

				recvPool.add(imsg);

				if (recvPool.size() > networkMaxPoolSize) {
					Log.error(Log.STDOUT, "getNextPkg", "接收池内包太多，系统强制断开连接。");
					close(STATE_CLOSED_R_POOL_OVERFLOW);
					return;
				}
				recv_curPkg = null;
				recv_pos = -1;

			} else {
				// 如果剩下的字节数，不够一个包则
				int remainBytes = inputBuffer.remaining();
				inputBuffer.get(recv_curPkg, recv_pos, remainBytes);
				// System.out.print("断包!!!!!!!!!!!!");
				// print(recv_curPkg);
				recv_pos += remainBytes;
			}
		}

		// 重新整理包位置
		// 把缓冲区剩下的小于四个字节的内容放在缓冲区首部,以便下个周期连续接收
		if (inputBuffer.remaining() > 0) {
			inputBuffer.compact();
		} else {
			inputBuffer.clear();
		}

	}//

	// ----------------------------------------------------------------------------
	// 发实现
	// ----------------------------------------------------------------------------
	private byte[] send_curPkg = null; // 当前发送包
	private int send_pos = -1; // 当前发送包的当前位置

	/**
	 * 
	 * @throws java.io.IOException
	 */
	private void sendImpl() throws IOException {
		if (sendPool.size() > networkMaxPoolSize) {
			// Log.error(Log.NET, "发送池内包太多，系统强制断开连接。");
			close(STATE_CLOSED_S_POOL_OVERFLOW);
			return;
		}

		outputBuffer.compact(); // 把数据移到前面

		do { // 向缓冲区压数据
			if (send_curPkg == null) {
				if (!sendPool.isEmpty()) {
					send_curPkg = (byte[]) sendPool.poll();
				} else { // 无可发数据时，终止循环
					break;
				}
			}
			if (send_curPkg != null) {
				// 如果不为空，则进行发送操作
				if (send_pos == -1) { // 尚未开始发送
					if (outputBuffer.remaining() < PKG_SIZE_BYTES) {
						break;
					} else {
						// outputBuffer.putShort((short) send_curPkg.length); //
						// 包长度
						send_pos = 0; // 如果此包第一次写向缓冲区时
					}
				}

				// 计算包与缓冲区的差异
				int pkgRemainBytes = send_curPkg.length - send_pos;
				int bufRemainBytes = outputBuffer.remaining();
				if (pkgRemainBytes > bufRemainBytes) { // 如果需发送的数据比缓冲大
					outputBuffer.put(send_curPkg, send_pos, bufRemainBytes);
					send_pos += bufRemainBytes;
					break;
				} else {
					// 如果需发送的数据比缓冲剩余空间小
					outputBuffer.put(send_curPkg, send_pos, pkgRemainBytes); // 包内容
					send_pos = -1;
					send_curPkg = null;
				}
			}
		} while (true);
		outputBuffer.flip();
		if (outputBuffer.hasRemaining()) {
			socketChannel.write(outputBuffer); // 如果上一个包已发送完成，则取出新包发送
		}
	}
	@Override
	public String toString() {
		return this.socketChannel.socket().toString();
	}

	void print(final byte[] b) {
		System.out.println("data length: " + b.length);
		for (int i = 0; i < b.length; i++) {
			System.out.print(Integer.toHexString(b[i]));
		}

		// for (int i = 0; i < b.length; i++) {
		// String s = Integer.toHexString(b[i]);
		// s = "00" + s;
		// String tgt = "";
		// for (int j = 0; j < 2; j++) {
		// tgt = (char) (s.charAt(s.length() - 1 - j)) + tgt;
		// }
		// System.out.print(" " + tgt);
		// }
		// System.out.println();
	}
}
