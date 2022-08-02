package com.learn.californium.client_dtls.v3_2_0.trysslapi;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.X509KeyManager;

import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.config.CoapConfig.MatcherMode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.AddressEndpointContext;
import org.eclipse.californium.elements.PrincipalEndpointContextMatcher;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.elements.config.CertificateAuthenticationMode;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.TimeDefinition;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.elements.util.StringUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.MdcConnectionListener;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.config.DtlsConfig.DtlsRole;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedSinglePskStore;
import org.eclipse.californium.scandium.dtls.resumption.AsyncResumptionVerifier;
import org.eclipse.californium.scandium.dtls.x509.AsyncKeyManagerCertificateProvider;
import org.eclipse.californium.scandium.dtls.x509.NewAdvancedCertificateVerifier;
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
	
	public String clientKey_file					="clienta_cert.jks";
	public String clientKey_file_dir				="/mycerts/oneway_jks/mycerts";
	private static String clientKey_file_loc = null;
	
	public String serverCaCrt_file					="s_cacert.crt";
	public String serverCaCrt_file_dir				="/mycerts/oneway_jks/serverca";
	private static String serverCaCrt_file_loc = null;
	

	
	public void my_configureToPrepare() {
		String myusr_path = System.getProperty("user.dir");
		serverCaCrt_file_loc 							= 	myusr_path	+ serverCaCrt_file_dir		+"/" + 	serverCaCrt_file;
		
		clientKey_file_loc 							= 	myusr_path	+ clientKey_file_dir		+"/" + 	clientKey_file;
		
		////////////////////file->FileInputStream->BufferedInputStream->X509Certificate //////////////////////////////////////
		// ref: https://gist.github.com/erickok/7692592
		
		FileInputStream fis= null;
		CertificateFactory cf = null;
		Certificate ca=null;
		try {
		cf = CertificateFactory.getInstance("X.509");
		// From https://www.washington.edu/itconnect/security/ca/load-der.crt
		fis = new FileInputStream(serverCaCrt_file_loc);
		InputStream caInput = new BufferedInputStream(fis);
		
		try {
			ca = cf.generateCertificate(caInput);
		} finally {
			caInput.close();
		}
		} catch (FileNotFoundException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
		} catch (CertificateException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		
		
		

		/////////////////////// ref: if (protocols.contains(Protocol.DTLS) || protocols.contains(Protocol.TLS)) 
		//      					in californium/demo-apps/cf-plugtest-server/src/main/java/org/eclipse/californium/plugtests/AbstractTestServer.java 
		//
		//
		// ref:https://github.com/eclipse/californium/issues/2006
		//CoapConfig.register();
		DtlsConfig.register();
		//UdpConfig.register();
		
		
		//////////////new configuration
		//choice_1 ref: https://github.com/eclipse/californium/issues/2006
		//Configuration config = Configuration. getStandard();
		
		
		//choice_2 ref:  addEndpoints(...) from californium/demo-apps/cf-plugtest-server/src/main/java/org/eclipse/californium/plugtests/AbstractTestServer.java
		//Configuration dtlsConfig = getConfig(Protocol.DTLS, interfaceType);
		
		//choice_3 ref: californium/demo-apps/sc-dtls-example-server/src/main/java/org/eclipse/californium/scandium/examples/ExampleDTLSServer.java
		DefinitionsProvider DEFAULTS = new DefinitionsProvider() {

			@Override
			public void applyDefinitions(Configuration config) {
				config.set(DtlsConfig.DTLS_CONNECTION_ID_LENGTH, 0);
				config.set(DtlsConfig.DTLS_RECEIVER_THREAD_COUNT, 2);
				config.set(DtlsConfig.DTLS_CONNECTOR_THREAD_COUNT, 2);
			}

		};
		Configuration dtlsConfig = Configuration.createWithFile(Configuration.DEFAULT_FILE, "DTLS example client", DEFAULTS);
		
		
		
		
		
		
		//////////////不知道需不需要
		TimeDefinition DTLS_HANDSHAKE_RESULT_DELAY = new TimeDefinition("DTLS_HANDSHAKE_RESULT_DELAY",
				"Delay for DTLS handshake results. Only for testing!!!\n0 no delay, < 0 blocking delay, > 0 non-blocking delay.");
		
		int handshakeResultDelayMillis = dtlsConfig.getTimeAsInt(DTLS_HANDSHAKE_RESULT_DELAY,
				TimeUnit.MILLISECONDS);
		//////////////
		
		DtlsConnectorConfig.Builder dtlsConfigBuilder = DtlsConnectorConfig.builder(dtlsConfig);
		
		
		
		//CertificateAuthenticationMode cliConfig=null;

		
		//////////////不知道需不需要
		String addr = "192.168.239.137";
		int coapsPort = 5684;
		InetSocketAddress bindToAddress = new InetSocketAddress(addr, coapsPort);
		String tag = "dtls:" + StringUtil.toString(bindToAddress);
		dtlsConfigBuilder.setLoggingTag(tag);
		//////////////
		
		
		
		
		
		//AsyncAdvancedPskStore asyncPskStore = new AsyncAdvancedPskStore(new PlugPskStore());
		//asyncPskStore.setDelay(handshakeResultDelayMillis);
		//dtlsConfigBuilder.setAdvancedPskStore(asyncPskStore);
		//dtlsConfigBuilder.setAddress(bindToAddress);						//不同于server 不需要bind
		//
		//
		//
		/*
		KeyManager[] serverKyManager = null;
		try {
			serverKyManager = SslContextUtil.loadKeyManager(serverCaCrt_file_loc,null, null, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		X509KeyManager keyManager = SslContextUtil.getX509KeyManager(serverKyManager);
		
		AsyncKeyManagerCertificateProvider certificateProvider = new AsyncKeyManagerCertificateProvider(keyManager, CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509);
		certificateProvider.setDelay(handshakeResultDelayMillis);
		dtlsConfigBuilder.setCertificateIdentityProvider(certificateProvider);
		*/
		
		
		/*
		//ref: ref: https://github.com/eclipse/californium/issues/2006
		SslContextUtil.Credentials credentials = null;
		try {
			credentials = SslContextUtil.loadCredentials(serverKey_file_loc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		
		PrivateKey prvKey = null;
		PublicKey pblKey =null;
		try {
			prvKey = SslContextUtil.loadPrivateKey(clientKey_file_loc, "myclientakeystore", "CksOneAdmin".toCharArray(), "CksOneAdmin".toCharArray());
			//pblKey = SslContextUtil.loadPublicKey(serverCrt_file_loc, "", null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		//ref: ref: https://github.com/eclipse/californium/issues/2006
		dtlsConfigBuilder.set(DtlsConfig.DTLS_ROLE, DtlsRole.CLIENT_ONLY);
		CertificateAuthenticationMode cliConfig_clientAuth=CertificateAuthenticationMode.NONE;
		if (cliConfig_clientAuth != null) {
			dtlsConfigBuilder.set(DtlsConfig.DTLS_CLIENT_AUTHENTICATION_MODE, cliConfig_clientAuth);
		}
		dtlsConfigBuilder.setAsList(DtlsConfig.DTLS_CIPHER_SUITES, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256);

		// 应该类似于 pahomqtt 的 keyStore.load(null, null);?
		Certificate[] cas= {ca};
		//dtlsConfigBuilder.setCertificateIdentityProvider(new SingleCertificateProvider(prvKey, pblKey));
		dtlsConfigBuilder.setCertificateIdentityProvider(new SingleCertificateProvider(prvKey, cas));
		//dtlsConfigBuilder.setCertificateIdentityProvider(new SingleCertificateProvider(null, cas));
		
		// ref: https://github.com/eclipse/californium/issues/2006  
		// and ExampleDTLSServer() from californium/demo-apps/sc-dtls-example-server/src/main/java/org/eclipse/californium/scandium/examples/ExampleDTLSServer.java 
		// 先加载 x509
		//dtlsConfigBuilder.setAdvancedCertificateVerifier(StaticNewAdvancedCertificateVerifier.builder().setTrustedCertificates(trustedCertificates).setTrustAllRPKs().build());
		// 拆分成下面		
				
			
		
		boolean useTrustAll = false;
		StaticNewAdvancedCertificateVerifier.Builder verifierBuilder= StaticNewAdvancedCertificateVerifier.builder();
		//AsyncNewAdvancedCertificateVerifier.Builder verifierBuilder = AsyncNewAdvancedCertificateVerifier.builder();
		
		// verifierBuilder settings
		if (useTrustAll==true) {
			verifierBuilder.setTrustAllCertificates();
		} 
		else if (useTrustAll==false) {
			Certificate[] trustedCertificates = {ca};
			verifierBuilder.setTrustedCertificates(trustedCertificates);
		}
		
		// set all raw public keys
		verifierBuilder.setTrustAllRPKs();
		
		NewAdvancedCertificateVerifier verifier = verifierBuilder.build();
		//AsyncNewAdvancedCertificateVerifier verifier = verifierBuilder.build();
		//verifier.setDelay(handshakeResultDelayMillis);
		dtlsConfigBuilder.setAdvancedCertificateVerifier(verifier);
		
		
		AsyncResumptionVerifier resumptionVerifier = new AsyncResumptionVerifier();
		resumptionVerifier.setDelay(handshakeResultDelayMillis);
		dtlsConfigBuilder.setResumptionVerifier(resumptionVerifier);
		dtlsConfigBuilder.setConnectionListener(new MdcConnectionListener());
		/*
		if (dtlsConfig.get(SystemConfig.HEALTH_STATUS_INTERVAL, TimeUnit.MILLISECONDS) > 0) {
			DtlsHealthLogger health = new DtlsHealthLogger(tag);
			dtlsConfigBuilder.setHealthHandler(health);
			add(health);
			// reset to prevent active logger
			dtlsConfigBuilder.set(SystemConfig.HEALTH_STATUS_INTERVAL, 0, TimeUnit.MILLISECONDS);
		}
		*/
		dtlsConnector = new DTLSConnector(dtlsConfigBuilder.build());
		CoapEndpoint.Builder builder = new CoapEndpoint.Builder();
		builder.setConnector(dtlsConnector);
		if (MatcherMode.PRINCIPAL == dtlsConfig.get(CoapConfig.RESPONSE_MATCHING)) {
			builder.setEndpointContextMatcher(new PrincipalEndpointContextMatcher(true));
		}
		builder.setConfiguration(dtlsConfig);
		/*
		CoapEndpoint endpoint = builder.build();
		addEndpoint(endpoint);
		print(endpoint, interfaceType);
		*/
		MyRawDataChannelImpl myRawDataChannelImpl1 = new MyRawDataChannelImpl(this);
		//myRawDataChannelImpl1.setMessageCounter(TestMainClient.messageCounter);
		dtlsConnector.setRawDataReceiver(myRawDataChannelImpl1);
		
		
		
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