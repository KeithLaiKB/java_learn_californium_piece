package com.learn.californium.server_dtls.v3_2_0.couldbedeleted.trysslapi;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.config.CoapConfig.MatcherMode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.PrincipalEndpointContextMatcher;
import org.eclipse.californium.elements.config.CertificateAuthenticationMode;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.elements.config.SystemConfig;
import org.eclipse.californium.elements.config.TimeDefinition;
import org.eclipse.californium.elements.config.UdpConfig;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.elements.util.StringUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.DtlsHealthLogger;
import org.eclipse.californium.scandium.MdcConnectionListener;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConfig.DtlsRole;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedMultiPskStore;
import org.eclipse.californium.scandium.dtls.resumption.AsyncResumptionVerifier;
import org.eclipse.californium.scandium.dtls.x509.AsyncKeyManagerCertificateProvider;
import org.eclipse.californium.scandium.dtls.x509.AsyncNewAdvancedCertificateVerifier;
import org.eclipse.californium.scandium.dtls.x509.NewAdvancedCertificateVerifier;
import org.eclipse.californium.scandium.dtls.x509.SingleCertificateProvider;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class MyServer {
	
	private static final int DEFAULT_PORT = 5656;
	private static final Logger LOG = LoggerFactory
			.getLogger(MyServer.class.getName());
	
	private static final String KEY_STORE_LOCATION = "mycerts/my_own/mykeystore.jks";
	//private static final char[] KEY_STORE_PASSWORD = "myKeyStoreAdministrator".toCharArray();
	private static final char[] KEY_STORE_PASSWORD = "SksOneAdmin".toCharArray();
	private static final String TRUST_STORE_LOCATION = "mycerts/my_own/mykeystore_truststore.jks";
	//private static final char[] TRUST_STORE_PASSWORD = "myTrustStoreAdministrator".toCharArray();
	private static final char[] TRUST_STORE_PASSWORD = "StsOneAdmin".toCharArray();
	
	
	private DTLSConnector dtlsConnector;
	
	public MyServer() {
		
	}
	
	public String serverCaCrt_file					="s_cacert.crt";
	public String serverCaCrt_file_dir				="/mycerts/oneway_jks/myca";
	private static String serverCaCrt_file_loc = null;
	
	public String serverKey_file					="server_cert.jks";
	public String serverKey_file_dir				="/mycerts/oneway_jks/mycerts";
	private static String serverKey_file_loc = null;
	
	public String serverCrt_file					="server_cert.crt";
	public String serverCrt_file_dir				="/mycerts/oneway_jks/mycerts";
	private static String serverCrt_file_loc = null;
	
	public void my_configureToPrepare() {
		String myusr_path = System.getProperty("user.dir");
		serverCaCrt_file_loc 							= 	myusr_path	+ serverCaCrt_file_dir		+"/" + 	serverCaCrt_file;

		serverKey_file_loc								= 	myusr_path	+ serverKey_file_dir		+"/" + 	serverKey_file;

		serverCrt_file_loc								= 	myusr_path	+ serverCrt_file_dir		+"/" + 	serverCrt_file;

        //////////////////// file->FileInputStream->BufferedInputStream->X509Certificate //////////////////////////////////////
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
        
		
		// Create a KeyStore containing our trusted CAs
		String keyStoreType = KeyStore.getDefaultType();
		KeyStore keyStore=null;
		TrustManagerFactory tmf = null;
		try {
			// Create a KeyStore containing our trusted CAs
			keyStoreType = KeyStore.getDefaultType();
			keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);

			// Create a TrustManager that trusts the CAs in our KeyStore
			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
			tmf.init(keyStore);
		} catch (KeyStoreException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		//  californium暂时不支持ssl + dtls
		 
		// finally, create SSL socket factory
		SSLContext context=null;
		SSLSocketFactory mysocketFactory=null;
		try {
			context = SSLContext.getInstance("TLSv1.3");
			context.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());
		} catch (NoSuchAlgorithmException e2) {
			e2.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		mysocketFactory = context.getSocketFactory();
		*/
		
		
		
		
		
		
		
		
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
				config.set(DtlsConfig.DTLS_CONNECTION_ID_LENGTH, 6);
				config.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, false);
			}

		};
		Configuration dtlsConfig = Configuration.createWithFile(Configuration.DEFAULT_FILE, "DTLS example server", DEFAULTS);
		
		
		
		
		
		
		//////////////不知道需不需要
		TimeDefinition DTLS_HANDSHAKE_RESULT_DELAY = new TimeDefinition("DTLS_HANDSHAKE_RESULT_DELAY",
				"Delay for DTLS handshake results. Only for testing!!!\n0 no delay, < 0 blocking delay, > 0 non-blocking delay.");
		
		int handshakeResultDelayMillis = dtlsConfig.getTimeAsInt(DTLS_HANDSHAKE_RESULT_DELAY,
				TimeUnit.MILLISECONDS);
		//////////////
		
		DtlsConnectorConfig.Builder dtlsConfigBuilder = DtlsConnectorConfig.builder(dtlsConfig);
		
		
		
		
		//CertificateAuthenticationMode cliConfig=null;
		CertificateAuthenticationMode cliConfig_clientAuth=CertificateAuthenticationMode.NONE;
		if (cliConfig_clientAuth != null) {
			dtlsConfigBuilder.set(DtlsConfig.DTLS_CLIENT_AUTHENTICATION_MODE, cliConfig_clientAuth);
		}
		
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
		dtlsConfigBuilder.setAddress(bindToAddress);
		//
		//
		//
		/*
		KeyManager[] serverKyManager = null;
		try {
			serverKyManager = SslContextUtil.loadKeyManager(serverKey_file_loc,null, null, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		X509KeyManager keyManager = SslContextUtil.getX509KeyManager(serverKyManager);
		*/
		//AsyncKeyManagerCertificateProvider certificateProvider = new AsyncKeyManagerCertificateProvider(serverKyManager, CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509);
		//certificateProvider.setDelay(handshakeResultDelayMillis);
		//dtlsConfigBuilder.setCertificateIdentityProvider(certificateProvider);
		
		
		
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
			prvKey = SslContextUtil.loadPrivateKey(serverKey_file_loc, "mySksAlias", "SksOneAdmin".toCharArray(), "SksOneAdmin".toCharArray());
			//pblKey = SslContextUtil.loadPublicKey(serverCrt_file_loc, "", null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		//ref: ref: https://github.com/eclipse/californium/issues/2006
		dtlsConfigBuilder.set(DtlsConfig.DTLS_ROLE, DtlsRole.SERVER_ONLY);
		dtlsConfigBuilder.set(DtlsConfig.DTLS_CLIENT_AUTHENTICATION_MODE, CertificateAuthenticationMode.NONE);
		//dtlsConfigBuilder.setAsList(DtlsConfig.DTLS_CIPHER_SUITES, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256);

		
		// 应该类似于 pahomqtt 的 keyStore.load(null, null);?
		Certificate[] cas= {ca};
		//dtlsConfigBuilder.setCertificateIdentityProvider(new SingleCertificateProvider(prvKey, pblKey));
		dtlsConfigBuilder.setCertificateIdentityProvider(new SingleCertificateProvider(prvKey, cas));
		
		// ref: https://github.com/eclipse/californium/issues/2006  
		// and ExampleDTLSServer() from californium/demo-apps/sc-dtls-example-server/src/main/java/org/eclipse/californium/scandium/examples/ExampleDTLSServer.java 
		// 先加载 x509
		//dtlsConfigBuilder.setAdvancedCertificateVerifier(StaticNewAdvancedCertificateVerifier.builder().setTrustedCertificates(trustedCertificates).setTrustAllRPKs().build());
		// 拆分成下面		
				
			
		/*
		boolean useTrustAll = true;
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
		*/
		
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
		dtlsConnector.setRawDataReceiver(new MyRawDataChannelImpl(dtlsConnector));
		
		
		
		
		
		
		
	}
	
	public void start() {
		try {
			dtlsConnector.start();
			System.out.println("DTLS example server started");
		} catch (IOException e) {
			throw new IllegalStateException(
					"Unexpected error starting the DTLS UDP server", e);
		}
	}
	
	
}