package com.learn.californium.client_dtls.v3_2_0.easy_basic.obs.tocomparewithonetime;

import java.io.IOException;
import java.net.InetSocketAddress;
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
import org.eclipse.californium.elements.AddressEndpointContext;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedSinglePskStore;
import org.eclipse.californium.scandium.dtls.x509.SingleCertificateProvider;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * ref: californium/demo-apps/sc-dtls-example-client/src/main/java/org/eclipse/californium/scandium/examples/ExampleDTLSClient.java
 * 
 * @author laipl
 *
 */
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
	
	// ref: californium/demo-apps/cf-secure/src/main/java/org/eclipse/californium/examples/SecureClient.java /
	//------------------------------------for observe---------------------------
	CoapClient client = null;
	
	
	public MyClient() {
	}
	

	
	public void my_configureToPrepare(String myuri1) {
		DefinitionsProvider DEFAULTS = new DefinitionsProvider() {

			@Override
			public void applyDefinitions(Configuration config) {
				config.set(DtlsConfig.DTLS_CONNECTION_ID_LENGTH, 6);
				config.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, false);
			}

		};
		
		
		
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
					myusr_path + "\\" + TRUST_STORE_LOCATION, "myclientatruststorealias", TRUST_STORE_PASSWORD);
			
			Configuration configuration = Configuration.createWithFile(Configuration.DEFAULT_FILE, "DTLS example client", DEFAULTS);
			DtlsConnectorConfig.Builder builder = DtlsConnectorConfig.builder(configuration);
			
			builder.setAdvancedPskStore(new AdvancedSinglePskStore("Client_identity", "secretPSK".getBytes()));
			
			//builder.setCertificateIdentityProvider(new SingleCertificateProvider(clientCredentials.getPrivateKey(), clientCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509));
			//因为我自己生成的证书 我是 RAW_PUBLIC_KEY 所以 我可以不加上 CertificateType.X_509, 我觉得 它多加一个 CertificateType.X_509 应该是为了 以防 例如我们证书不是  RAW_PUBLIC_KEY 他就考虑你认为可能的的证书类型 
			builder.setCertificateIdentityProvider(new SingleCertificateProvider(clientCredentials.getPrivateKey(), clientCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY));
			
			//ref: californium/demo-apps/sc-dtls-example-server/src/main/java/org/eclipse/californium/scandium/examples/ExampleDTLSServer.java
			builder.setAdvancedCertificateVerifier(StaticNewAdvancedCertificateVerifier.builder()
					.setTrustedCertificates(trustedCertificates).setTrustAllRPKs().build());
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
			URI uri = null;
			try {
				uri = new URI(myuri1);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			client = new CoapClient(uri);
			CoapEndpoint.Builder coapEndPointBuilder = new CoapEndpoint.Builder()
					.setConfiguration(configuration)
					.setConnector(dtlsConnector);

			client.setEndpoint(coapEndPointBuilder.build());

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
	
	
	
	
	
	
	public void startTest() {
		//
    	// set handler for observer method, because observe method needs asynchronous operation
		CoapHandler myObserveHandler = new CoapHandler() {

            @Override
            public void onLoad(CoapResponse response) {
                //log.info("Command Response Ack: {}, {}", response.getCode(), response.getResponseText());
            	System.out.println("---------------------------------------");
            	System.out.println("on load: " + response.getResponseText());
            	System.out.println("get code: " + response.getCode().name());
            	
            }

            @Override
            public void onError() {
            }
        };
		
        CoapObserveRelation coapObRelation1 = client.observe(myObserveHandler);
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
		 //
        System.out.println("wow_hello");
        //
        if (coapObRelation1!=null) {
        	System.out.println( "coapObRelation1:" + coapObRelation1.toString() );

        } 
        else {
        	System.out.println("Request failed");
        }	
        //
        //
        //---------------------------------------------
        Scanner in =new Scanner(System.in) ;
        int int_choice = 0;
        while(int_choice!=-1) {
        	System.out.println("here is the choice:");
        	System.out.println("-1: to exit");
        	System.out.println("1: to delete");
        	System.out.println("2: to reactiveCancel");
        	System.out.println("3: to proactiveCancel");
        	System.out.println("4: to observe again");
        	System.out.println("enter the choice:");
        	// input
        	int_choice = in.nextInt();
        	if(int_choice==-1) {
        		//System.exit(0);
        		break;
        	}
        	else if(int_choice==1) {
        		//
        		System.out.println("deleteing record");
        		//System.out.println("deleting resources");
        		//
        		// 我认为 delete 挺重要的 所以我这选择的是同步
        		try {
					client.delete();
				} catch (ConnectorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				// 用的是 同步, 对面没回应, 就不能继续往下走
        		//client.delete(myDeleteHandler); // 用的是 异步
        	}
        	else if(int_choice==2) {
        		coapObRelation1.reactiveCancel();				//取消观察状态
        		System.out.println("reactiveCancel");
        		
        	}
        	else if(int_choice==3) {
        		coapObRelation1.proactiveCancel();				//取消观察状态
        		System.out.println("proactiveCancel");
        	}
        	else if(int_choice==4) {
        		coapObRelation1 = client.observe(myObserveHandler); //取消观察状态后 还是可以继续observe的
        		System.out.println("observe again");
        	}
        }
        //
        //---------------------------------------------
        in.close();
        //
        client.shutdown();
        System.exit(0);
	}
	



}