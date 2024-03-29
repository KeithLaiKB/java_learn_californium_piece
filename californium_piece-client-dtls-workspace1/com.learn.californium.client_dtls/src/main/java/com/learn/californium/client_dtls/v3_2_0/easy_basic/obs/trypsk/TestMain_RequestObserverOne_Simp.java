package com.learn.californium.client_dtls.v3_2_0.easy_basic.obs.trypsk;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedMultiPskStore;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedSinglePskStore;
import org.eclipse.californium.scandium.dtls.x509.SingleCertificateProvider;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * <p>
 * 							description:																			</br>	
 * &emsp;						client to observe																	</br>
 * &emsp;						in this demo you don't have too much selection operation such as delete				</br>
 * &emsp;						before shutting down, it does not use cancel subscription here						</br>
 * 
 * 							ref:																					</br>	
 * &emsp;						californium/demo-apps/cf-plugtest-client/src/main/java/org/eclipse/californium/plugtests/PlugtestClient.java  	</br>	
 *  																												</br>
 * 
 * </p>
 * @author laipl
 *
 * psk可以不用 公私钥  直接自己写好 允许的客户端钥匙就可以  client这里用AdvancedSinglePskStore 记录允许客户端使用的 presharedkey, 
 * 例如每个用户是用不同的 就会 放不同的preshared key
 * 
 */
public class TestMain_RequestObserverOne_Simp {
	private static int receivedMessageNum 					= 0;
	private static int expectedReceivedMessageNum			= 10;

	public static void main(String[] args) {
    	//
    	String myuri1 	     					= "coaps://localhost:5684/Resource1";
    	CoapHandler myObserveHandler 			= null;
    	//
    	DTLSConnector dtlsConnector;
    	//final Logger LOG = LoggerFactory.getLogger(MyClient.class.getName());
	
    	String KEY_STORE_LOCATION = "mycerts/my_own/myclientakeystore.jks";
    	char[] KEY_STORE_PASSWORD = "CksOneAdmin".toCharArray();
    	//String TRUST_STORE_LOCATION = "mycerts/my_own/myclientakeystore_truststore.jks";
    	String TRUST_STORE_LOCATION = "mycerts/other_own/mykeystore_truststore.jks";
    	//char[] TRUST_STORE_PASSWORD = "CtsOneAdmin".toCharArray();
    	char[] TRUST_STORE_PASSWORD = "StsOneAdmin".toCharArray();

    	
    	
		//psk
		final String PSK_IDENTITY = "password";
		final byte[] PSK_SECRET = "sesame".getBytes();

		final String OPEN_PSK_IDENTITY = "Client_identity";
		final byte[] OPEN_PSK_SECRET = "secretPSK".getBytes();
    	
    	
    	// ref: californium/demo-apps/cf-secure/src/main/java/org/eclipse/californium/examples/SecureClient.java /
    	//------------------------------------for observe---------------------------
    	DefinitionsProvider DEFAULTS = new DefinitionsProvider() {

			@Override
			public void applyDefinitions(Configuration config) {
				config.set(DtlsConfig.DTLS_CONNECTION_ID_LENGTH, 6);
				config.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, false);
			}

		};
		

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
		/*
		SslContextUtil.Credentials clientCredentials = SslContextUtil.loadCredentials(
				myusr_path + "\\" + KEY_STORE_LOCATION, "myclientakeystorealias", KEY_STORE_PASSWORD,
				KEY_STORE_PASSWORD);
		//Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(myusr_path + "\\" + TRUST_STORE_LOCATION, "myclientatruststorealias", TRUST_STORE_PASSWORD);
		
		Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(myusr_path + "\\" + TRUST_STORE_LOCATION, "mytruststorealias", TRUST_STORE_PASSWORD);
		*/
		
		Configuration configuration = Configuration.createWithFile(Configuration.DEFAULT_FILE, "DTLS example client", DEFAULTS);
		DtlsConnectorConfig.Builder builder = DtlsConnectorConfig.builder(configuration);
		
		
		
		
		//psk
		AdvancedSinglePskStore pskStore = new AdvancedSinglePskStore(PSK_IDENTITY, PSK_SECRET);
		// put in the PSK store the default identity/psk for tinydtls tests
		//pskStore.setKey("Client_identity", "secretPSK".getBytes());
		//pskStore.setKey(PSK_IDENTITY, PSK_SECRET);
		//pskStore.setKey(OPEN_PSK_IDENTITY, OPEN_PSK_SECRET);

		//builder.setAdvancedPskStore(new AdvancedSinglePskStore("Client_identity", "secretPSK".getBytes()));
		//builder.setAsList(DtlsConfig.DTLS_CIPHER_SUITES, CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA256, CipherSuite.TLS_PSK_WITH_AES_128_CCM_8);
		//builder.setAsList(DtlsConfig.DTLS_CIPHER_SUITES, CipherSuite.TLS_PSK_WITH_AES_128_CCM_8);
		builder.setAdvancedPskStore(pskStore);
		
		
		
		
		
		
		//builder.setCertificateIdentityProvider(new SingleCertificateProvider(clientCredentials.getPrivateKey(), clientCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509));
		//因为我自己生成的证书 我是 RAW_PUBLIC_KEY 所以 我可以不加上 CertificateType.X_509, 我觉得 它多加一个 CertificateType.X_509 应该是为了 以防 例如我们证书不是  RAW_PUBLIC_KEY 他就考虑你认为可能的的证书类型 
		//builder.setCertificateIdentityProvider(new SingleCertificateProvider(clientCredentials.getPrivateKey(), clientCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY));
		//ref: californium/demo-apps/sc-dtls-example-server/src/main/java/org/eclipse/californium/scandium/examples/ExampleDTLSServer.java
		//builder.setAdvancedCertificateVerifier(StaticNewAdvancedCertificateVerifier.builder().setTrustedCertificates(trustedCertificates).setTrustAllRPKs().build());
		//builder.setConnectionThreadCount(1);
		
		
		
		
		dtlsConnector = new DTLSConnector(builder.build());
		//
		//
		// 因为obs 可以去掉这一段
		/*
		MyRawDataChannelImpl myRawDataChannelImpl1 = new MyRawDataChannelImpl(this);
		//myRawDataChannelImpl1.setMessageCounter(TestMainClient.messageCounter);
		dtlsConnector.setRawDataReceiver(myRawDataChannelImpl1);
		//dtlsConnector.setRawDataReceiver(new MyRawDataChannelImpl(dtlsConnector));
		*/

		// ref: californium/demo-apps/cf-secure/src/main/java/org/eclipse/californium/examples/SecureClient.java /
		//------------------------------------for observe---------------------------		
    	// new client
    	CoapClient client = new CoapClient(myuri1);
		CoapEndpoint.Builder coapEndPointBuilder = new CoapEndpoint.Builder()
				.setConfiguration(configuration)
				.setConnector(dtlsConnector);

		client.setEndpoint(coapEndPointBuilder.build());

        // set handler

		//
    	// set handler for observer method, because observe method needs asynchronous operation
		myObserveHandler = new CoapHandler() {

            @Override
            public void onLoad(CoapResponse response) {
            	System.out.println(response.getResponseText());
            	receivedMessageNum = receivedMessageNum +1;
            }

            @Override
            public void onError() {
            }
        };

        //
        // observe
        CoapObserveRelation coapObRelation1 = client.observe(myObserveHandler);
        //
        //
        //---------------------------------------------
		// 停留一段时间 让server继续运行
        while(receivedMessageNum < expectedReceivedMessageNum) {
        	try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        //
		//------------------- close --------------------------
		// cancel subscription
		// coapObRelation1.proactiveCancel();
		// shutdown client
        client.shutdown();
        //
		//	

    }

}