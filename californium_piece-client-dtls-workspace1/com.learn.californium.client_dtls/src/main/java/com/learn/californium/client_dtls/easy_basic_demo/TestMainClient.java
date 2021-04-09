package com.learn.californium.client_dtls.easy_basic_demo;

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
		// ����Ҫ��û����Ϣ�� �������� �ĳ��� ��һ��������
		int length = 64;
		//
		//
		//===========================���� main���� Я���в��������(���)===========================
		//
		//���main������args Я���в���
		if (0 < args.length) {
			//a[0] ����  client �ĸ���
			clients = Integer.parseInt(args[0]);
			if (1 < args.length) {
				//a[1] ����  message �ĸ���
				messages = Integer.parseInt(args[1]);
				if (2 < args.length) {
					//a[2] ����  length
					length = Integer.parseInt(args[2]);
				}
			}
		}
		//
		//maxMessages �ĸ����� messages * clients ����,(��� main���� Я���в���, ���ղ����������� messages �� clients )
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
		//===========================����client, Ϊÿ��client����һ���߳�, ���������̣߳����д�ÿ��client��connector===========================
		List<MyClient> clientList = new ArrayList<>(clients);
		//
		//============= ����һ�� �޶����̸߳��� ���̳߳�, �̹߳��������� DaemonThreadFactory
		// JVM terminates itself when all user threads (non-daemon threads) finish their 
		// execution, JVM    does not    care whether Daemon thread is running or not, 
		// if JVM finds running daemon thread (upon completion of user threads), 
		// it terminates the thread and after that shutdown itself.
		//
		// Ҳ����˵ DaemonThreadFactory ������ DaemonThread ��
		//
		// ���� user threads(non-daemon threads), 
		// 			JVM 	��Ҫ		�ȴ�	���е�  user threads 	������ϵ�ʱ��				��	�� terminate itself 
		// Ȼ�� DaemonThread, 
		// 			JVM 	����Ҫ 	�ȴ�	���� DaemonThread 		������ϵ�ʱ��  				 ������JVM�Լ�
		//				JVM ���Ҫ �ر�, �����Ƿ� �������� DaemonThread ��������
		// 					JVM �������Ȱ� DaemonThread�� ��, Ȼ��							��	shutdown itself
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
				new DaemonThreadFactory("Aux#"));
		//
		System.out.println("Create " + clients + " DTLS example clients, expect to send " + maxMessages +" messages overall.");
		//
		//
		//============= ����clients
		// ������ÿ����һ��client, ������-1��
		// �������ԭ����, ����Ҫ����ÿ��client�߳�, Ȼ��ȥstartConnector Ҳ��Ҫʱ��(��Ϊ����Ҳ�ᴴ��һ���߳�?)
		// �������CountDownLatch �Ļ�, ���ᷢ������:  connector must be started before sending messages is possible
		// Ҳ����˵, connector ��ûstart��, ����ȥ ����������
		//
		// ������������� ˵ ����Щclient �� connector ��Ҫ��   start �꣬ ���ܽ�����һ��
		//
		// ��������ԭ��, �ҳ����� Thread.sleep, ���Խ������
		// 
		// ���ǳ���˼��, execute ������Ǹ�runnable ȥrun��ʱ�� ��һ���̣߳�
		// �����������main����һ���߳�
		// ��Ȼ˵ main���Ѿ�execute��, ��������֤ runnable����Ǹ�run �Ѿ�ִ������ startConnector 
		//
		// ���� client.startConnector д�� num_clients_tmp.countDown ��ǰ��
		// �ø�await ���ų���������, �������� run���startConnector, Ȼ���1, ���Ϊ0ʱ, ���ܷ���
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
			// ���Լ�׷�ٹ���, excute �� ����� runnable ȥstart, 
			// Ҳ����˵�� run()��execute�� ������
			executor.execute(new Runnable() {

				@Override
				public void run() {
					// ÿ�� ÿ�� client start
					client.startConnector();
					// ���� CountDownLatch ��ȥ��һ
					num_clients_tmp.countDown();
				}
			});
		}
		//
		//
		//Thread.sleep(1000);
		//
		// ��� ����� CountDownLatch�� �ı��� start û�� ����Ϊ0, �򲻻���� ִ�� await ����Ĵ��롣
		// �൱�������Լ���ֱ��ÿ���߳��е��Ǹ�client��startConnector ִ���꣬
		// ���� num_clients_tmp.countDown���Լ�һ  ʹ��Ϊ0
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
		// ��Ϊ �첽����Ҫ�ȴ��ش��ģ��ȴ�����Ҫʱ��ģ�
		// ���� �Ҳ����ó�����ô�����
		// ���� ����������س��ٽ�����Ҳ����˵ �㲻����س�����ô�����main����û����
		// �Ӷ� ��ʱ�� ��client�ȵ� �������� ����
		// ��Ȼ�Ļ� �ڵȴ��Ĺ����У��ܺ����Ѿ���������, �����������Щ������ �̰߳� Ҳ�п���û���ˣ�
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
		//===========================������client, ׼����Ŀ��˿�===========================
		// Get peer address
		//============= ׼���� ���� �Ͷ˿�
		InetSocketAddress peer;
		if (args.length == 5) {
			// ���main������args Я����5������
			// �� a[3]��������, �� a[4]���� �˿ں�
			peer = new InetSocketAddress(args[3], Integer.parseInt(args[4]));
		} else {
			// loopback address ָ���� 127.0.0.1
			peer = new InetSocketAddress(InetAddress.getLoopbackAddress(), DEFAULT_PORT);
		}
		//
		//
		//
		//
		//
		//
		//
		//============= ����
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
		// �����е� message������ ���� �������ѭ��
		while (messageCounter.await(DEFAULT_TIMEOUT_NANOS, TimeUnit.NANOSECONDS)==false) {
			long current = messageCounter.getCount();
			// �� ������ DEFAULT_TIMEOUT_NANOS ��ô���ʱ�� 
			// �����ǰ ��message ��Ȼ��û����ȥ, ���޸� ��ʼ��ʱ��
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
		//===========================�ر� client�� connector===========================
		// ��clientͣ����
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
		// ��Ϊ long count = maxMessages - messageCounter.getCount();
		//
		// ��� count == maxMessages ֤��  messageCounter.getCount() �Ѿ��ݼ���0��
		// ��� count < maxMessages  ֤��  messageCounter.getCount() û�еݼ���0
		//
		// ���� maxMessages =100
		// messageCounter.getCount() = 1 
		// �� count = 100 - 1 = 99 < maxMessages, Ҳ����˵ ����Ϣ��û�б�����ȥ 
		if (count < maxMessages) {
			System.out.println("Stale at " + lastMessageCountDown + " messages");
		}
		
		
		//
		//
		//
		// ��� client ����>1
		if (1 < clients) {
			// ��ÿ��client ����Ϣ������������
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