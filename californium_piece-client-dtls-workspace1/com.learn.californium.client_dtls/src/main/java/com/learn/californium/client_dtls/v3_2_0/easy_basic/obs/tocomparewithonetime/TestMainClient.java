package com.learn.californium.client_dtls.v3_2_0.easy_basic.obs.tocomparewithonetime;

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
	
	public static String myuri1  = "coaps://localhost:5684/hello_observer";
	
	public TestMainClient() {
		
	}





	public static void main(String[] args) {

		MyClient client = new MyClient();
		client.my_configureToPrepare(myuri1);
		client.startTest();
	}
}