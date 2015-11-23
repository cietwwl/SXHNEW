package com.joyveb.tlol.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.joyveb.tlol.util.Log;

public class Reactor extends Thread {

	public static final int PERIOD = 100; // MS

	private final Selector selector;

	private final ServerSocketChannel serverSocketChannel;

	private SelectionKey serverSelectionKey;

	private final NetListener processor;

	private volatile boolean shutdown = false;

	public static final int MAX_CONNECTION = 20000; //最多允许客户端连接

	private int clientConnectionLimit = MAX_CONNECTION;

	public Reactor(final int port, final NetListener processor) throws IOException {

		this.setName("net");
		this.processor = processor;
		selector = Selector.open();
		serverSocketChannel = ServerSocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress(port);
		serverSocketChannel.socket().bind(isa); // 绑定
		serverSocketChannel.configureBlocking(false); // 非阻塞
		serverSelectionKey = serverSocketChannel.register(selector,
				SelectionKey.OP_ACCEPT); // 注册监听选择器
		serverSelectionKey.attach(new Acceptor());
	}

	public final void shutdown() {
		try {
			shutdown = true;
			selector.wakeup();
		} catch (Exception e) {
			Log.error(Log.NET, "shutdown", e);
		}
	}

	/**
	 * /注册队列
	 * **/
	private ConcurrentLinkedQueue<NetHandler> readyForReg = new ConcurrentLinkedQueue<NetHandler>();

	public final void readyForReg(final NetHandler nh) {
		readyForReg.add(nh);
	}

	/**
	 * 选择器线程，此线程会对所有连接的读写进行响应，通过回调Handle的run()方法实现。
	 */
	public final void run() {

		while (!shutdown) {
			try {
				int num = selector.select(10000);
				// 处理注册
				for (NetHandler nh : readyForReg) {
					nh.register();
					readyForReg.remove(nh);
				}

				long timeElapse = System.currentTimeMillis();

				if (num > 0) {
					Set<SelectionKey> selected = getSelector().selectedKeys();
					Iterator<SelectionKey> it = selected.iterator();
					while (it.hasNext()) {
						dispatch(it.next());
					}
					selected.clear();
				}

				timeElapse = System.currentTimeMillis() - timeElapse;

				if (timeElapse > PERIOD) {
					// 循环时间长于PERIOD可能存在性能问题
					 Log.info(Log.STDOUT, "NET TIME Elapse Per Tick:" + timeElapse);
				} else if (timeElapse >= 0) {
					try {
						Thread.sleep(PERIOD - timeElapse);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} catch (Exception e) {
				Log.error(Log.STDOUT, "Reactor.run", "Ractor select error.");
			}
		}

		try {
			// 对selector 进行close即可完成关闭网络系统
			for (SelectionKey sk : selector.keys()) {
				Object obj = sk.attachment();
				if (obj instanceof NetHandler) {
					((NetHandler) obj)
							.close(NetHandler.STATE_CLOSED_SYS_SHUTDOWN);
				}
			}
			selector.close();
		} catch (IOException iOException) {
			Log.error(Log.NET, "shutdown", iOException);
		}

		try {
			serverSocketChannel.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		Log.info(Log.STDOUT, "网络系统已关闭:" + toString());
	}

	public final int getCurConnections() {
		if (!selector.isOpen()) {
			return 0;
		}
		return selector.keys().size();
	}

	/**
	 * 任务分发，分发到Acceptor或NetHandlerImpl
	 * @param k 
	 */
	private void dispatch(final SelectionKey k) {
		Runnable r = (Runnable) k.attachment();
		if (r != null) {
			r.run();
		}
	}

	/**
	 * 接收者,当监听到一个连接请求时，产生一个接收者
	 * 
	 * 这个任务产生一个Handle，Handle实际上是一个连接的管理器，负责对一个客户的所有通信进行管理
	 */
	class Acceptor implements Runnable {

		/**
		 * 反应器回调接口
		 */
		public void run() {
			try {
				SocketChannel sc = serverSocketChannel.accept();
				sc.configureBlocking(false);
				if (sc != null) {
					if (getCurConnections() > clientConnectionLimit) {
						sc.close();
					} else {
						// accpet后将key的attach转到handle上面
						NetHandlerImpl h = new NetHandlerImpl(Reactor.this, sc);

						// 如果网络监听器不为空，则执行定制操作
						if (processor != null) {
							processor.onAccept(h);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the selector
	 */
	public final Selector getSelector() {
		return selector;
	}
}
