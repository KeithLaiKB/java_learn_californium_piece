package com.learn.californium.client_dtls.v3_2_0.tryssl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.californium.elements.AddressEndpointContext;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConfig.DtlsRole;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedSinglePskStore;
import org.eclipse.californium.scandium.dtls.x509.SingleCertificateProvider;
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
	//private static final char[] KEY_STORE_PASSWORD = "myKeyStoreAdministrator".toCharArray();
	private static final char[] KEY_STORE_PASSWORD = "CksOneAdmin".toCharArray();
	//private static final String TRUST_STORE_LOCATION = "mycerts/other_own/mykeystore_truststore.jks";
	//private static final String TRUST_STORE_LOCATION = "mycerts/other_own/myclientakeystore_truststore.jks";
	private static final String TRUST_STORE_LOCATION = "mycerts/my_own/myclientakeystore_truststore.jks";
	//private static final char[] TRUST_STORE_PASSWORD = "myTrustStoreAdministrator".toCharArray();
	//private static final char[] TRUST_STORE_PASSWORD = "myTrustStoreAdministrator".toCharArray();
	//private static final char[] TRUST_STORE_PASSWORD = "myTrustStoreAdministrator".toCharArray();
	private static final char[] TRUST_STORE_PASSWORD = "CtsOneAdmin".toCharArray();
	
	private String name;
	

	public MyClient() {
	}
	

	
	public void my_configureToPrepare() {
		DefinitionsProvider DEFAULTS = new DefinitionsProvider() {

			@Override
			public void applyDefinitions(Configuration config) {
				config.set(DtlsConfig.DTLS_CONNECTION_ID_LENGTH, 6);
				config.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, false);
			}

		};
		
		
		
		try {
			
			String myusr_path = System.getProperty("user.dir");

			// ref: scandium ->ConnectorUtil  constructor from  californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/ConnectorUtil.java 
			SslContextUtil.Credentials clientCredentials = SslContextUtil.loadCredentials(
					myusr_path + "\\" + KEY_STORE_LOCATION, "myclientakeystorealias", KEY_STORE_PASSWORD,
					KEY_STORE_PASSWORD);
			Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(
					myusr_path + "\\" + TRUST_STORE_LOCATION, "myclientatruststorealias", TRUST_STORE_PASSWORD);
			
			
			
			// ref: test form californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/openssl/OpenSslServerAuthenticationInteroperabilityTest.java
			DtlsConnectorConfig.Builder builder = DtlsConnectorConfig.builder(new Configuration())
					.set(DtlsConfig.DTLS_ROLE, DtlsRole.CLIENT_ONLY);
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			Configuration configuration = Configuration.createWithFile(Configuration.DEFAULT_FILE, "DTLS example client", DEFAULTS);
			DtlsConnectorConfig.Builder builder = DtlsConnectorConfig.builder(configuration);
			
			builder.setAdvancedPskStore(new AdvancedSinglePskStore("Client_identity", "secretPSK".getBytes()));
			
			//builder.setCertificateIdentityProvider(new SingleCertificateProvider(clientCredentials.getPrivateKey(), clientCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509));
			//因为我自己生成的证书 我是 RAW_PUBLIC_KEY 所以 我可以不加上 CertificateType.X_509, 我觉得 它多加一个 CertificateType.X_509 应该是为了 以防 例如我们证书不是  RAW_PUBLIC_KEY 他就考虑你认为可能的的证书类型 
			builder.setCertificateIdentityProvider(new SingleCertificateProvider(clientCredentials.getPrivateKey(), clientCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY));
			
			builder.setAdvancedCertificateVerifier(StaticNewAdvancedCertificateVerifier.builder()
					.setTrustedCertificates(trustedCertificates).setTrustAllRPKs().build());
			//builder.setConnectionThreadCount(1);
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
			System.out.println("Received my message:"+ new String(raw.getBytes()));
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