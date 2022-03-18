package com.learn.californium.client_dtls.v3_2_0;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.californium.elements.AddressEndpointContext;
import org.eclipse.californium.elements.Connector;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.elements.RawDataChannel;
import org.eclipse.californium.elements.util.DaemonThreadFactory;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedSinglePskStore;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMainClient {

	private static final int DEFAULT_PORT = 5656;
	private static final long DEFAULT_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(10000);
	private static final Logger LOG = LoggerFactory.getLogger(TestMainClient.class);
	/*
	private static final char[] KEY_STORE_PASSWORD = "endPass".toCharArray();
	private static final String KEY_STORE_LOCATION = "certs/keyStore.jks";
	private static final char[] TRUST_STORE_PASSWORD = "rootPass".toCharArray();
	private static final String TRUST_STORE_LOCATION = "certs/trustStore.jks";
	 */
	/*
	private static final String KEY_STORE_LOCATION = "mycerts/mykeystore.jks";
	private static final char[] KEY_STORE_PASSWORD = "myKeyStoreAdministrator".toCharArray();
	private static final String TRUST_STORE_LOCATION = "mycerts/mykeystore_truststore.jks";
	private static final char[] TRUST_STORE_PASSWORD = "myTrustStoreAdministrator".toCharArray();
	*/

	
	public static CountDownLatch messageCounter;

	public static String payload = "HELLO WORLD_";
	
	
	
	public TestMainClient() {
		
	}






	public static void main(String[] args) throws InterruptedException {
		int clients = 1;
		int messages = 100;
		// 我想要让没条信息的 基础内容 的长度 有一定的限制
		int length = 64;
		//
		//
		//===========================处理 main函数 携带有参数的情况(如果)===========================
		//
		//如果main函数的args 携带有参数
		if (0 < args.length) {
			//a[0] 代表  client 的个数
			clients = Integer.parseInt(args[0]);
			if (1 < args.length) {
				//a[1] 代表  message 的个数
				messages = Integer.parseInt(args[1]);
				if (2 < args.length) {
					//a[2] 代表  length
					length = Integer.parseInt(args[2]);
				}
			}
		}
		//
		//maxMessages 的个数由 messages * clients 决定,(如果 main函数 携带有参数, 则按照参数的来设置 messages 和 clients )
		int maxMessages = (messages * clients);
		//
		messageCounter = new CountDownLatch(maxMessages);
		//
		//
		//
		//
		//
		//
		//
		/*
		while (payload.length() < length) {
			payload += payload;
		}
		payload = payload.substring(0, length);
		*/
		
		//
		//===========================创建client, 为每个client创建一个线程, 并且运行线程，其中打开每个client的connector===========================
		List<MyClient> clientList = new ArrayList<>(clients);
		//
		//============= 创建一个 限定了线程个数 的线程池, 线程工厂类型是 DaemonThreadFactory
		// JVM terminates itself when all user threads (non-daemon threads) finish their 
		// execution, JVM    does not    care whether Daemon thread is running or not, 
		// if JVM finds running daemon thread (upon completion of user threads), 
		// it terminates the thread and after that shutdown itself.
		//
		// 也就是说 DaemonThreadFactory 是生成 DaemonThread 的
		//
		// 关于 user threads(non-daemon threads), 
		// 			JVM 	需要		等待	所有的  user threads 	运行完毕的时候				才	能 terminate itself 
		// 然而 DaemonThread, 
		// 			JVM 	不需要 	等待	所有 DaemonThread 		运行完毕的时候  				 来结束JVM自己
		//				JVM 如果要 关闭, 无论是否 还存在有 DaemonThread 正在运行
		// 					JVM 都将会先把 DaemonThread关 闭, 然后							再	shutdown itself
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
				new DaemonThreadFactory("Aux#"));
		//
		System.out.println("Create " + clients + " DTLS example clients, expect to send " + maxMessages +" messages overall.");
		//
		//
		//============= 创建clients
		// 作用是每生成一个client, 就让它-1，
		// 用这个的原因是, 底下要创建每个client线程, 然后去startConnector 也需要时间(因为里面也会创建一个线程?)
		// 如果不用CountDownLatch 的话, 他会发生问题:  connector must be started before sending messages is possible
		// 也就是说, connector 还没start完, 他就去 发送数据了
		//
		// 所以用这个就是 说 你这些client 的 connector 需要先   start 完， 才能进行下一步
		//
		// 根据这种原理, 我尝试用 Thread.sleep, 可以解决问题
		// 
		// 于是尝试思考, execute 里面的那个runnable 去run的时候 是一个线程，
		// 而我现在这个main又是一个线程
		// 虽然说 main里已经execute了, 但并不保证 runnable里的那个run 已经执行完了 startConnector 
		//
		// 所以 client.startConnector 写在 num_clients_tmp.countDown 的前面
		// 用个await 拦着程序不往下走, 当运行了 run里的startConnector, 然后减1, 最后为0时, 才能放行
		// 
		final CountDownLatch num_clients_tmp = new CountDownLatch(clients);		
		//
		// Create & start clients
		for (int index = 0; index < clients; ++index) {
			//final MyClient client = new MyClient();
			//final MyClient client = new MyClient(dtlsConnector);
			final MyClient client = new MyClient();
			client.setName("Client_idx_"+index);
			
			
			client.my_configureToPrepare();
			//
			clientList.add(client);
			// 我自己追踪过了, excute 会 让这个 runnable 去start, 
			// 也就是说， run()在execute后 会运行
			executor.execute(new Runnable() {

				@Override
				public void run() {
					// 每当 每个 client start
					client.startConnector();
					// 就让 CountDownLatch 就去减一
					num_clients_tmp.countDown();
				}
			});
		}
		//
		//
		//Thread.sleep(1000);
		//
		// 如果 上面的 CountDownLatch类 的变量 start 没有 减到为0, 则不会进行 执行 await 后面的代码。
		// 相当于阻塞自己，直到每个线程中的那个client的startConnector 执行完，
		// 并且 num_clients_tmp.countDown各自减一  使其为0
		num_clients_tmp.await();
		System.out.println(clients + " DTLS example clients started.");
		//
		//
		//
		//
		//
		//
		//---------------------------------------------
		/*
		// 因为 异步，是要等待回传的，等待是需要时间的，
		// 所以 我不能让程序那么快结束
		// 所以 我让你输入回车再结束，也就是说 你不输入回车，那么这个总main函数没走完
		// 从而 有时间 让client等到 传回来的 数据
		// 不然的话 在等待的过程中，总函数已经运行完了, 所以里面的这些变量啊 线程啊 也有可能没有了？
        System.out.println("enter to exit!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
				br.readLine(); 
		} 
		catch (IOException e) { }
		System.out.println("CANCELLATIONING");
		//resp.proactiveCancel();
		System.out.println("CANCELLATION FINISHED");
		*/
		
		
		//
		//===========================创建完client, 准备好目标端口===========================
		// Get peer address
		//============= 准备好 域名 和端口
		InetSocketAddress peer;
		if (args.length == 5) {
			// 如果main函数的args 携带有5个参数
			// 则 a[3]代表域名, 则 a[4]代表 端口号
			peer = new InetSocketAddress(args[3], Integer.parseInt(args[4]));
		} else {
			// loopback address 指的是 127.0.0.1
			peer = new InetSocketAddress(InetAddress.getLoopbackAddress(), DEFAULT_PORT);
		}
		//
		//
		//
		//
		//
		//
		//
		//============= 发送
		long nanos = System.nanoTime();
		long lastMessageCountDown = messageCounter.getCount();

		for (MyClient client : clientList) {
			client.startTest(peer);
		}
		System.out.println("original start nano is "+nanos);
		//
		//
		//
		//
		//
		//
		// Wait with timeout or all messages send.
		// 当所有的 message发完了 才能 结束这个循环
		while (messageCounter.await(DEFAULT_TIMEOUT_NANOS, TimeUnit.NANOSECONDS)==false) {
			long current = messageCounter.getCount();
			// 当 经过了 DEFAULT_TIMEOUT_NANOS 这么多的时间 
			// 如果当前 的message 仍然还没发出去, 则修改 开始的时间
			if (lastMessageCountDown == current && current < maxMessages) {
				// no new messages, clients are stale
				// adjust start time with timeout
				nanos += DEFAULT_TIMEOUT_NANOS; 
				break;
			}
			lastMessageCountDown = current;
		}
		System.out.println("original start nano changed to "+nanos);
		//
		//
		//
		long count = maxMessages - messageCounter.getCount();
		nanos = System.nanoTime() - nanos;
		System.out.println(clients + " DTLS example clients finished.");
		//
		//
		//
		//
		//
		//===========================关闭 client的 connector===========================
		// 把client停下来
		int statistic[] = new int[clients];
		for (int index = 0; index < clients; ++index) {
			MyClient client = clientList.get(index);
			statistic[index] = client.stopConnector();
		}
		//
		//
		//
		//
		System.out.println(count + " messages received, " + (maxMessages) + " expected");
		System.out.println(count + " messages in " + TimeUnit.NANOSECONDS.toMillis(nanos) + " ms");
		System.out.println((count * 1000) / TimeUnit.NANOSECONDS.toMillis(nanos) + " messages per s");
		//
		//
		// 因为 long count = maxMessages - messageCounter.getCount();
		//
		// 如果 count == maxMessages 证明  messageCounter.getCount() 已经递减到0了
		// 如果 count < maxMessages  证明  messageCounter.getCount() 没有递减到0
		//
		// 例如 maxMessages =100
		// messageCounter.getCount() = 1 
		// 则 count = 100 - 1 = 99 < maxMessages, 也就是说 有信息并没有被发出去 
		if (count < maxMessages) {
			System.out.println("Stale at " + lastMessageCountDown + " messages");
		}
		
		
		//
		//
		//
		// 如果 client 数量>1
		if (1 < clients) {
			// 把每个client 的消息数量进行排序
			Arrays.sort(statistic);
			//
			int grouped = 10;
			int last = 0;
			for (int index = 1; index < clients; ++index) {
				if ((statistic[index] / grouped) > (statistic[last] / grouped)) {
					if (statistic[index-1] == statistic[last]) {
						System.out.println((index - last) + " clients with " + statistic[last] + " messages.");
					}
					else {
						System.out.println((index - last) + " clients with " + statistic[last] + " to " + statistic[index-1] + " messages.");
					}
					last = index;
				}
			}
			System.out.println((clients - last) + " clients with " + statistic[last] + " to " + statistic[clients-1] + " messages.");
		}
	}
}