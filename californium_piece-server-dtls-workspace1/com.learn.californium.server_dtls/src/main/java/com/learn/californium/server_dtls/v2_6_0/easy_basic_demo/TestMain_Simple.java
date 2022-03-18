package com.learn.californium.server_dtls.v2_6_0.easy_basic_demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;

import org.eclipse.californium.elements.Connector;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.elements.RawDataChannel;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedMultiPskStore;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMain_Simple {

	private static final int DEFAULT_PORT = 5684;
	private static final Logger LOG = LoggerFactory
			.getLogger(TestMain_Simple.class.getName());
	/*
	private static final char[] KEY_STORE_PASSWORD = "endPass".toCharArray();
	private static final String KEY_STORE_LOCATION = "certs/keyStore.jks";
	private static final char[] TRUST_STORE_PASSWORD = "rootPass".toCharArray();
	private static final String TRUST_STORE_LOCATION = "certs/trustStore.jks";
	*/
	private static final String KEY_STORE_LOCATION = "mycerts/my_own/mykeystore.jks";
	private static final char[] KEY_STORE_PASSWORD = "myKeyStoreAdministrator".toCharArray();
	private static final String TRUST_STORE_LOCATION = "mycerts/my_own/mykeystore_truststore.jks";
	private static final char[] TRUST_STORE_PASSWORD = "myTrustStoreAdministrator".toCharArray();

	private DTLSConnector dtlsConnector;

	public TestMain_Simple() {
		AdvancedMultiPskStore pskStore = new AdvancedMultiPskStore();
		// put in the PSK store the default identity/psk for tinydtls tests
		pskStore.setKey("Client_identity", "secretPSK".getBytes());
		try {
			// load the key store
			/*
			SslContextUtil.Credentials serverCredentials = SslContextUtil.loadCredentials(
					SslContextUtil.CLASSPATH_SCHEME + KEY_STORE_LOCATION, "server", KEY_STORE_PASSWORD,
					KEY_STORE_PASSWORD);
			Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(
					SslContextUtil.CLASSPATH_SCHEME + TRUST_STORE_LOCATION, "root", TRUST_STORE_PASSWORD);
			 */
			//System.out.println(SslContextUtil.CLASSPATH_SCHEME);
			String myusr_path = System.getProperty("user.dir");
			//注意 虽然我创建的时候是有 大小写 mykeystoreAlias
			//但 貌似 使用的时候 在这里需要全部小写， 才能对应的到
			SslContextUtil.Credentials serverCredentials = SslContextUtil.loadCredentials(
					myusr_path + "\\" + KEY_STORE_LOCATION, "mykeystorealias", KEY_STORE_PASSWORD,
					KEY_STORE_PASSWORD);
			Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(
					myusr_path + "\\" + TRUST_STORE_LOCATION, "mytruststorealias", TRUST_STORE_PASSWORD);
			
			DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder();
			builder.setRecommendedCipherSuitesOnly(false);
			builder.setAddress(new InetSocketAddress(DEFAULT_PORT));
			builder.setAdvancedPskStore(pskStore);
			builder.setIdentity(serverCredentials.getPrivateKey(), serverCredentials.getCertificateChain(),
					CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509);
			builder.setAdvancedCertificateVerifier(StaticNewAdvancedCertificateVerifier.builder()
					.setTrustedCertificates(trustedCertificates).setTrustAllRPKs().build());
			dtlsConnector = new DTLSConnector(builder.build());
			dtlsConnector
					.setRawDataReceiver(new RawDataChannelImpl(dtlsConnector));

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

	private class RawDataChannelImpl implements RawDataChannel {

		private Connector connector;

		public RawDataChannelImpl(Connector con) {
			this.connector = con;
		}

		@Override
		public void receiveData(final RawData raw) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Received request: {}", new String(raw.getBytes()));
			}
			RawData response = RawData.outbound("ACK".getBytes(),
					raw.getEndpointContext(), null, false);
			connector.send(response);
		}
	}

	public static void main(String[] args) {

		TestMain_Simple server = new TestMain_Simple();
		server.start();
		try {
			for (;;) {
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
		}
	}
}