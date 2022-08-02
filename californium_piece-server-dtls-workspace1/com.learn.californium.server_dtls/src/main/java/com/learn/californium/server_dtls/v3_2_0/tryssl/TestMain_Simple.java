package com.learn.californium.server_dtls.v3_2_0.tryssl;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.elements.util.SslContextUtil.Credentials;
import org.eclipse.californium.elements.util.StringUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.SingleNodeConnectionIdGenerator;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedMultiPskStore;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedSinglePskStore;
import org.eclipse.californium.scandium.dtls.x509.SingleCertificateProvider;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.learn.californium.server_dtls.v3_2_0.easy_basic.MyRawDataChannelImpl;
import com.learn.californium.server_dtls.v3_2_0.tryssl.OpenSslProcessUtil.AuthenticationMode;

/**
 * ref:californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/openssl/OpenSslServerInteroperabilityTest.java 
 * 
 * 
 * @author laipl
 *
 */
public class TestMain_Simple {

	public TestNameLoggerRule name = new TestNameLoggerRule();

	private static final InetSocketAddress BIND = new InetSocketAddress(InetAddress.getLoopbackAddress(), 0);
	private static final InetSocketAddress DESTINATION = new InetSocketAddress(InetAddress.getLoopbackAddress(),
			ScandiumUtil.PORT);
	
	private static final int myDefaultPort = 5656;
	private static final String ACCEPT = "127.0.0.1:" + myDefaultPort;

	private static OpenSslProcessUtil processUtil;
	private static ScandiumUtil scandiumUtil;
	
	
	
	//ref:californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/ConnectorUtil.java
	private static final String SERVER_NAME = "server";
	
	public static final String TRUST_CA = "ca";
	public static final String TRUST_ROOT = "root";
	/**
	 * DTLS connector.
	 */
	private DTLSConnector connector;
	/**
	 * Credentials for ECDSA base cipher suites.
	 */
	private Credentials serverCredentials;
	
	/**
	 * Specific credentials for ECDSA base cipher suites to be used by the next test.
	 */
	private Credentials nextCredentials;
	private Certificate[] trustedCertificates;
	private Certificate[] trustRoot;
	
	
	
	private static final String KEY_STORE_LOCATION = "mycerts/my_own/mykeystore.jks";
	//private static final char[] KEY_STORE_PASSWORD = "myKeyStoreAdministrator".toCharArray();
	private static final char[] KEY_STORE_PASSWORD = "SksOneAdmin".toCharArray();
	private static final String TRUST_STORE_LOCATION = "mycerts/my_own/mykeystore_truststore.jks";
	//private static final char[] TRUST_STORE_PASSWORD = "myTrustStoreAdministrator".toCharArray();
	private static final char[] TRUST_STORE_PASSWORD = "StsOneAdmin".toCharArray();
	
	
	public static final String OPENSSL_PSK_IDENTITY = "Client_identity";
	public static final byte[] OPENSSL_PSK_SECRET = "secretPSK".getBytes();

	
	//ref:californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/openssl/OpenSslServerAuthenticationInteroperabilityTest.java
	private static CipherSuite cipherSuite;
	
	public String serverPemCertificate="mykeystorepem.pem";
	public String serverTrustStorePemCertificate="mykeystore_truststorepem.pem.pem";
	
	
	public void add(List<String> args, OpenSslProcessUtil.AuthenticationMode authMode, String chain)
			throws IOException, InterruptedException {
		switch (authMode) {
		case PSK:
			break;
		case CERTIFICATE:
			args.add("-no-CAfile");
			break;
		case CHAIN:
			args.add("-no-CAfile");
			args.add("-cert_chain");
			args.add(chain);
			break;
		case TRUST:
			args.add("-CAfile");
			args.add(serverTrustStorePemCertificate);
			args.add("-build_chain");
			break;
		}
	}
	
	public void my_configureToPrepare() {
		processUtil = new OpenSslProcessUtil();
		ProcessResult result = processUtil.getOpenSslVersion(TIMEOUT_MILLIS);
		
		
		//scandiumUtil = new ScandiumUtil(true);

		
		
		assumeNotNull(result);
		assumeTrue(result.contains("OpenSSL 1\\.1\\."));
		processUtil.assumeServerVersion();
		scandiumUtil = new ScandiumUtil(true);
		///////////////ref: init() from  californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/openssl/OpenSslServerInteroperabilityTest.java /////////////////////////////////
		//这一大部分 后面发现是client的
		//---------------------- ref: ScandiumUtil-> ConnectorUtil--------------------------------------
		// load the key store
		String myusr_path = System.getProperty("user.dir");
		//注意 虽然我创建的时候是有 大小写 mykeystoreAlias
		//但 貌似 使用的时候 在这里需要全部小写， 才能对应的到
		try {
			//credentials = SslContextUtil.loadCredentials(SslContextUtil.CLASSPATH_SCHEME + KEY_STORE_LOCATION,SERVER_NAME, KEY_STORE_PASSWORD, KEY_STORE_PASSWORD);
			serverCredentials = SslContextUtil.loadCredentials(
					myusr_path + "\\" + KEY_STORE_LOCATION, "mykeystorealias", KEY_STORE_PASSWORD,
					KEY_STORE_PASSWORD);
			
			//trustCa = SslContextUtil.loadTrustedCertificates(SslContextUtil.CLASSPATH_SCHEME + TRUST_STORE_LOCATION,TRUST_CA, TRUST_STORE_PASSWORD);
			trustedCertificates = SslContextUtil.loadTrustedCertificates(
					myusr_path + "\\" + TRUST_STORE_LOCATION, "mytruststorealias", TRUST_STORE_PASSWORD);
			//trustRoot = SslContextUtil.loadTrustedCertificates(SslContextUtil.CLASSPATH_SCHEME + TRUST_STORE_LOCATION,TRUST_ROOT, TRUST_STORE_PASSWORD);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//------------------------------------------------------------
		
		cipherSuite = CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CCM_8;
		List<CipherSuite> suites = Arrays.asList(cipherSuite);
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		///////////////ref: 相关的test方法    from  californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/openssl/OpenSslServerInteroperabilityTest.java /////////////////////////////////

		//-------------------ref: startupServer(...) from californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/openssl/OpenSslProcessUtil.java  ------------------
		
		OpenSslProcessUtil.AuthenticationMode authMode = AuthenticationMode.CERTIFICATE;
		
		List<String> args = new ArrayList<String>();
		String openSslCiphers = OpenSslUtil.getOpenSslCipherSuites(cipherSuite);
		args.addAll(Arrays.asList("openssl", "s_server", "-4", "-dtls1_2", "-accept", ACCEPT, "-listen", "-verify", "5",
				"-cipher", openSslCiphers));
		if (CipherSuite.containsPskBasedCipherSuite(suites)) {
			//assumePskServerVersion();
			args.add("-psk_identity");
			args.add(OPENSSL_PSK_IDENTITY);
			args.add("-psk");
			args.add(StringUtil.byteArray2Hex(OPENSSL_PSK_SECRET));
		}
		if (CipherSuite.containsCipherSuiteRequiringCertExchange(suites)) {
			args.add("-cert");
			args.add(serverPemCertificate);
			//String chain = CA_CERTIFICATES;
			String chain = "todoooooooo";									//暂时不知道这个干什么的
			//if (SERVER_CA_RSA_CERTIFICATE.equals(serverCertificate)) {
				//chain = CA_RSA_CERTIFICATES;
			//}
			add(args, authMode, chain);
		}
		add(args, curves, sigAlgs);
		print(args);
		execute(args);
		// ensure, server is ready to ACCEPT messages
		assumeTrue(waitConsole("ACCEPT", TIMEOUT_MILLIS));
		return "(" + openSslCiphers.replace(":", "|") + ")";
		
		
		
		
		
		
		
		
		//-------------------ref: build() from californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/ConnectorUtil.java ------------------
		DtlsConnectorConfig.Builder dtlsBuilder = DtlsConnectorConfig.builder(new Configuration());
		
		dtlsBuilder.set(DtlsConfig.DTLS_ADDITIONAL_ECC_TIMEOUT, 1000, TimeUnit.MILLISECONDS)
		.set(DtlsConfig.DTLS_RECEIVER_THREAD_COUNT, 2)
		.set(DtlsConfig.DTLS_CONNECTOR_THREAD_COUNT, 2)
		.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, false)
		.setAddress(new InetSocketAddress(myDefaultPort))
		.setConnectionIdGenerator(new SingleNodeConnectionIdGenerator(6));
		//-------------------------------------------------------------------------------------------------
		
		//-------------------ref: build() from californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/ConnectorUtil.java ------------------
		// 如果是psk才需要
		//dtlsBuilder.setAdvancedPskStore(new AdvancedSinglePskStore(OPENSSL_PSK_IDENTITY, OPENSSL_PSK_SECRET));
		
		//不是psk,而是需要cert的, 则和之前的demo基本一样
		dtlsBuilder.setCertificateIdentityProvider(new SingleCertificateProvider(serverCredentials.getPrivateKey(), serverCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509));
		//-------------------------------------------------------------------------------------------------
		
		
		//------------- 里面这一步 跟之前的demo 基本一样, 只是它分开写了而已------ref: build() from californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/ConnectorUtil.java ------------------
		//如果 我们没有 setAdvancedCertificateVerifier 	则使用下面这一块 
		StaticNewAdvancedCertificateVerifier.Builder builder = StaticNewAdvancedCertificateVerifier.builder();
		builder.setTrustedCertificates(trustedCertificates);			//如果有证书,	则写这个
		//builder.setTrustAllCertificates();							//如果其他,	则写这个, 但我不知道这是干什么的?? 到时有机会再看看
		
		builder.setTrustAllRPKs();
		dtlsBuilder.setAdvancedCertificateVerifier(builder.build());		//把这个builder 放进我们之前的 dtlsBuilder 进行 setAdvancedCertificateVerifier
		//-------------------------------------------------------------------------------------------------
		
		
		//-------------------ref: build() from californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/ConnectorUtil.java ------------------
		
		dtlsBuilder.set(DtlsConfig.DTLS_CIPHER_SUITES, suites);
		connector = new DTLSConnector(dtlsBuilder.build());
		//alertCatcher.resetAlert();
		//connector.setAlertHandler(alertCatcher);
		//nextCredentials = null;
		
		
		//-------------------ref: start() from californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/ScandiumUtil.java
		//Connector connector = getConnector();
		//channel = new SimpleRawDataChannel(1);
		connector.setRawDataReceiver(new MyRawDataChannelImpl(connector));
		try {
			connector.start();
		} catch (BindException e) {
			try {
				Thread.sleep(500);
				connector.start();
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			}
		}
		
		
		
		
		
		
		
		
		//-------------------创建 ssl 的string-------------------------------------------------------------------------------------------------------------------------------------
		String cipher = processUtil.startupServer(ACCEPT, CERTIFICATE, cipherSuite);
		
		//-----------connect------------------
		String message = "Hello OpenSSL!";
		scandiumUtil.send(message, DESTINATION, TIMEOUT_MILLIS);

		assertTrue("handshake failed!", processUtil.waitConsole("CIPHER is ", TIMEOUT_MILLIS));
		assertTrue("wrong cipher suite!", processUtil.waitConsole("CIPHER is " + cipher, TIMEOUT_MILLIS));
		if (misc != null) {
			for (String check : misc) {
				assertTrue("missing " + check, processUtil.waitConsole(check, TIMEOUT_MILLIS));
			}
		}
		assertTrue("openssl missing message!", processUtil.waitConsole(message, TIMEOUT_MILLIS));
		processUtil.send("ACK-" + message);

		scandiumUtil.assertReceivedData("ACK-" + message, TIMEOUT_MILLIS);

		processUtil.stop(TIMEOUT_MILLIS);
	}
	
	
	public static void main(String[] args) {

	}


		@BeforeClass
		public static void init() throws IOException, InterruptedException {

		}

		@AfterClass
		public static void shutdown() throws InterruptedException {
			ShutdownUtil.shutdown(scandiumUtil, processUtil);
		}


		/**
		 * @return List of cipher suites.
		 */
		@Parameters(name = "{0}")
		public static Iterable<CipherSuite> cipherSuiteParams() {
			return OpenSslUtil.getSupportedTestCipherSuites();
		}

		@After
		public void stop() throws InterruptedException {
			ShutdownUtil.shutdown(scandiumUtil, processUtil);
		}

		/**
		 * Establish a "connection" and send a message to the server and back to the
		 * client.
		 */
		@Test
		public void testOpenSslServer() throws Exception {
			processUtil.setTag("openssl-server, " + cipherSuite.name());
			String certificate = cipherSuite.getCertificateKeyAlgorithm() == CertificateKeyAlgorithm.RSA ?
					SERVER_RSA_CERTIFICATE : SERVER_CERTIFICATE;
			String cipher = processUtil.startupServer(ACCEPT, CERTIFICATE, certificate, null, null, cipherSuite);

			DtlsConnectorConfig.Builder builder = DtlsConnectorConfig.builder(new Configuration())
					.set(DtlsConfig.DTLS_ROLE, DtlsRole.CLIENT_ONLY);
			scandiumUtil.start(BIND, builder, null, cipherSuite);

			String message = "Hello OpenSSL!";
			scandiumUtil.send(message, DESTINATION, TIMEOUT_MILLIS);

			assertTrue(processUtil.waitConsole("CIPHER is " + cipher, TIMEOUT_MILLIS));
			assertTrue(processUtil.waitConsole(message, TIMEOUT_MILLIS));
			processUtil.send("ACK-" + message);

			scandiumUtil.assertReceivedData("ACK-" + message, TIMEOUT_MILLIS);

			processUtil.stop(TIMEOUT_MILLIS);
		}

		@Test
		public void testOpenSslServerMultiFragments() throws Exception {
			processUtil.setTag("openssl-server, multifragments per record, " + cipherSuite.name());

			String certificate = cipherSuite.getCertificateKeyAlgorithm() == CertificateKeyAlgorithm.RSA ?
					SERVER_RSA_CERTIFICATE : SERVER_CERTIFICATE;
			String cipher = processUtil.startupServer(ACCEPT, CERTIFICATE, certificate, null, null, cipherSuite);

			DtlsConnectorConfig.Builder builder = DtlsConnectorConfig.builder(new Configuration())
					.set(DtlsConfig.DTLS_ROLE, DtlsRole.CLIENT_ONLY)
					.set(DtlsConfig.DTLS_USE_MULTI_HANDSHAKE_MESSAGE_RECORDS, true);
			scandiumUtil.start(BIND, builder, null, cipherSuite);

			String message = "Hello OpenSSL!";
			scandiumUtil.send(message, DESTINATION, TIMEOUT_MILLIS);

			assertTrue(processUtil.waitConsole("CIPHER is " + cipher, TIMEOUT_MILLIS));
			assertTrue(processUtil.waitConsole(message, TIMEOUT_MILLIS));
			processUtil.send("ACK-" + message);

			scandiumUtil.assertReceivedData("ACK-" + message, TIMEOUT_MILLIS);

			processUtil.stop(TIMEOUT_MILLIS);
		}
	}