package com.learn.californium.server_dtls.easy_basic_demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;

import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedMultiPskStore;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyServer {
	
	private static final int DEFAULT_PORT = 5656;
	private static final Logger LOG = LoggerFactory
			.getLogger(MyServer.class.getName());
	
	private static final String KEY_STORE_LOCATION = "mycerts/my_own/mykeystore.jks";
	private static final char[] KEY_STORE_PASSWORD = "myKeyStoreAdministrator".toCharArray();
	private static final String TRUST_STORE_LOCATION = "mycerts/my_own/mykeystore_truststore.jks";
	private static final char[] TRUST_STORE_PASSWORD = "myTrustStoreAdministrator".toCharArray();
	
	
	private DTLSConnector dtlsConnector;
	
	public MyServer() {
		
	}
	
	public void my_configureToPrepare() {
		AdvancedMultiPskStore pskStore = new AdvancedMultiPskStore();
		// put in the PSK store the default identity/psk for tinydtls tests
		pskStore.setKey("Client_identity", "secretPSK".getBytes());
		try {
			// load the key store
			String myusr_path = System.getProperty("user.dir");
			//ע�� ��Ȼ�Ҵ�����ʱ������ ��Сд mykeystoreAlias
			//�� ò�� ʹ�õ�ʱ�� ��������Ҫȫ��Сд�� ���ܶ�Ӧ�ĵ�
			SslContextUtil.Credentials serverCredentials = SslContextUtil.loadCredentials(
					myusr_path + "\\" + KEY_STORE_LOCATION, "mykeystorealias", KEY_STORE_PASSWORD,
					KEY_STORE_PASSWORD);
			Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(
					myusr_path + "\\" + TRUST_STORE_LOCATION, "mytruststorealias", TRUST_STORE_PASSWORD);
			
			DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder();
			builder.setRecommendedCipherSuitesOnly(false);
			builder.setAddress(new InetSocketAddress(DEFAULT_PORT));
			builder.setAdvancedPskStore(pskStore);
			
			//builder.setIdentity(clientCredentials.getPrivateKey(), clientCredentials.getCertificateChain(),CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509);
			//��Ϊ���Լ����ɵ�֤�� ���� RAW_PUBLIC_KEY ���� �ҿ��Բ����� CertificateType.X_509, �Ҿ��� �����һ�� CertificateType.X_509 Ӧ����Ϊ�� �Է� ��������֤�鲻��  RAW_PUBLIC_KEY ���Ϳ�������Ϊ���ܵĵ�֤������ 
			builder.setIdentity(serverCredentials.getPrivateKey(), serverCredentials.getCertificateChain(),CertificateType.RAW_PUBLIC_KEY);
			
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
