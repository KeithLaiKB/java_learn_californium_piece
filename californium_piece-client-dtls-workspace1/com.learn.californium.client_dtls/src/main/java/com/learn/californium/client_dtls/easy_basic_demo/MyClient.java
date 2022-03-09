package com.learn.californium.client_dtls.easy_basic_demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.californium.elements.AddressEndpointContext;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedSinglePskStore;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyClient {
	
	private DTLSConnector dtlsConnector;
	private static final Logger LOG = LoggerFactory.getLogger(MyClient.class.getName());
	//
	//
	private AtomicInteger clientMessageCounter = new AtomicInteger();
	
	
	
	//private static String payload = "HELLO WORLD";
	
	
	private static final String KEY_STORE_LOCATION = "mycerts/my_own/myclientakeystore.jks";
	private static final char[] KEY_STORE_PASSWORD = "myKeyStoreAdministrator".toCharArray();
	private static final String TRUST_STORE_LOCATION = "mycerts/other_own/mykeystore_truststore.jks";
	private static final char[] TRUST_STORE_PASSWORD = "myTrustStoreAdministrator".toCharArray();
	
	private String name;
	

	public MyClient() {
	}
	

	
	public void my_configureToPrepare() {
		try {
			// load key store
			/*
			SslContextUtil.Credentials clientCredentials = SslContextUtil.loadCredentials(
					SslContextUtil.CLASSPATH_SCHEME + KEY_STORE_LOCATION, "client", KEY_STORE_PASSWORD,
					KEY_STORE_PASSWORD);
			Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(
					SslContextUtil.CLASSPATH_SCHEME + TRUST_STORE_LOCATION, "root", TRUST_STORE_PASSWORD);
			 */
			
			String myusr_path = System.getProperty("user.dir");
			//注意 虽然我创建的时候是有 大小写 mykeystoreAlias
			//但 貌似 使用的时候 在这里需要全部小写， 才能对应的到
			//serverCredentials 改成了 clientCredentials 
			/*
			SslContextUtil.Credentials clientCredentials = SslContextUtil.loadCredentials(
					myusr_path + "\\" + KEY_STORE_LOCATION, "mykeystorealias", KEY_STORE_PASSWORD,
					KEY_STORE_PASSWORD);
			Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(
					myusr_path + "\\" + TRUST_STORE_LOCATION, "mytruststorealias", TRUST_STORE_PASSWORD);
			*/
			SslContextUtil.Credentials clientCredentials = SslContextUtil.loadCredentials(
					myusr_path + "\\" + KEY_STORE_LOCATION, "myclientakeystorealias", KEY_STORE_PASSWORD,
					KEY_STORE_PASSWORD);
			Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(
					myusr_path + "\\" + TRUST_STORE_LOCATION, "mytruststorealias", TRUST_STORE_PASSWORD);
			
			DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder();
			builder.setAdvancedPskStore(new AdvancedSinglePskStore("Client_identity", "secretPSK".getBytes()));
			
			//builder.setIdentity(clientCredentials.getPrivateKey(), clientCredentials.getCertificateChain(),CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509);
			//因为我自己生成的证书 我是 RAW_PUBLIC_KEY 所以 我可以不加上 CertificateType.X_509, 我觉得 它多加一个 CertificateType.X_509 应该是为了 以防 例如我们证书不是  RAW_PUBLIC_KEY 他就考虑你认为可能的的证书类型 
			builder.setIdentity(clientCredentials.getPrivateKey(), clientCredentials.getCertificateChain(),CertificateType.RAW_PUBLIC_KEY);
			
			builder.setAdvancedCertificateVerifier(StaticNewAdvancedCertificateVerifier.builder()
					.setTrustedCertificates(trustedCertificates).setTrustAllRPKs().build());
			builder.setConnectionThreadCount(1);
			dtlsConnector = new DTLSConnector(builder.build());
			//
			//
			MyRawDataChannelImpl myRawDataChannelImpl1 = new MyRawDataChannelImpl(this);
			//myRawDataChannelImpl1.setMessageCounter(TestMainClient.messageCounter);
			dtlsConnector.setRawDataReceiver(myRawDataChannelImpl1);
			//dtlsConnector.setRawDataReceiver(new MyRawDataChannelImpl(dtlsConnector));

		} catch (GeneralSecurityException | IOException e) {
			LOG.error("Could not load the keystore", e);
		}
	}
	//
	//
	//
	public void setDtlsConnector(DTLSConnector dtlsConnector) {
		this.dtlsConnector = dtlsConnector;
	}
	public DTLSConnector getDtlsConnector() {
		return dtlsConnector;
	}


	
	
	
	//
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	//


	//
	//
	//
	//
	// 先将connector打开, 不是指 开始 发送数据
	public void startConnector() {
		try {
			this.dtlsConnector.start();
		} catch (IOException e) {
			LOG.error("Cannot start connector", e);
		}
	}
	// 关闭 connector
	public int stopConnector() {
		if (dtlsConnector.isRunning()) {
			dtlsConnector.destroy();
		}
		return clientMessageCounter.get();
	}
	
	
	
	
	
	
	public void startTest(InetSocketAddress peer) {
		RawData data = RawData.outbound(TestMainClient.payload.getBytes(), new AddressEndpointContext(peer), null, false);
		dtlsConnector.send(data);
	}
	




	
	
	
	public void receive(RawData raw) {

		TestMainClient.messageCounter.countDown();
		long c = TestMainClient.messageCounter.getCount();
		if (LOG.isInfoEnabled()) {
			//LOG.info("Received response: {} {}", new Object[] { new String(raw.getBytes()), c });
			LOG.info("Received response: {}", new Object[] { new String(raw.getBytes())});
		}
		// 如果 message 还没发完, 则继续发消息 
		if (0 < c) {
			//clientMessageCounter.incrementAndGet();
			int int_msg_nxt = clientMessageCounter.incrementAndGet();
			int int_msg_now = int_msg_nxt - 1;
			
			try {
				//RawData data = RawData.outbound((TestMainClient.payload + this.getName() + c + ".").getBytes(), raw.getEndpointContext(), null, false);
				RawData data = RawData.outbound((TestMainClient.payload + this.getName() + int_msg_now + ".").getBytes(), raw.getEndpointContext(), null, false);
				
				// 发下一个消息
				dtlsConnector.send(data);
			} catch (IllegalStateException e) {
				LOG.debug("send failed after {} messages", (c - 1), e);
			}
		} else {
			dtlsConnector.destroy();
		}

	}
	
}
