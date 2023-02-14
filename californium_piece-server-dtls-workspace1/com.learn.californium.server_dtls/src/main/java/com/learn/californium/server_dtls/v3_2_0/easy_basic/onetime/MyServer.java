package com.learn.californium.server_dtls.v3_2_0.easy_basic.onetime;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;

import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedMultiPskStore;
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
	

	//ref: californium/demo-apps/sc-dtls-example-server/src/main/java/org/eclipse/californium/scandium/examples/ExampleDTLSServer.java
	public void my_configureToPrepare() {
		DefinitionsProvider DEFAULTS = new DefinitionsProvider() {

			@Override
			public void applyDefinitions(Configuration config) {
				config.set(DtlsConfig.DTLS_CONNECTION_ID_LENGTH, 6);
				config.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, false);
			}

		};
		
		
		
		AdvancedMultiPskStore pskStore = new AdvancedMultiPskStore();
		// put in the PSK store the default identity/psk for tinydtls tests
		pskStore.setKey("Client_identity", "secretPSK".getBytes());
		try {
			// load the key store
			String myusr_path = System.getProperty("user.dir");
			//注意 虽然我创建的时候是有 大小写 mykeystoreAlias
			//但 貌似 使用的时候 在这里需要全部小写， 才能对应的到
			SslContextUtil.Credentials serverCredentials = SslContextUtil.loadCredentials(
					myusr_path + "\\" + KEY_STORE_LOCATION, "mykeystorealias", KEY_STORE_PASSWORD,
					KEY_STORE_PASSWORD);
			Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(
					myusr_path + "\\" + TRUST_STORE_LOCATION, "mytruststorealias", TRUST_STORE_PASSWORD);
			
			Configuration configuration = Configuration.createWithFile(Configuration.DEFAULT_FILE, "DTLS example server", DEFAULTS);
			DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder(configuration);
		
			builder.setAddress(new InetSocketAddress(DEFAULT_PORT));
			builder.setAdvancedPskStore(pskStore);
			
			//builder.setCertificateIdentityProvider(new SingleCertificateProvider(serverCredentials.getPrivateKey(), serverCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509));
			//因为我自己生成的证书 我是 RAW_PUBLIC_KEY 所以 我可以不加上 CertificateType.X_509, 我觉得 它多加一个 CertificateType.X_509 应该是为了 以防 例如我们证书不是  RAW_PUBLIC_KEY 他就考虑你认为可能的的证书类型 
			builder.setCertificateIdentityProvider(new SingleCertificateProvider(serverCredentials.getPrivateKey(), serverCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY));
			
			builder.setAdvancedCertificateVerifier(StaticNewAdvancedCertificateVerifier.builder()
					.setTrustedCertificates(trustedCertificates).setTrustAllRPKs().build());
			dtlsConnector = new DTLSConnector(builder.build());
			dtlsConnector
					.setRawDataReceiver(new MyRawDataChannelImpl(dtlsConnector));

		} catch (GeneralSecurityException | IOException e) {
			LOG.error("Could not load the keystore", e);
		}
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