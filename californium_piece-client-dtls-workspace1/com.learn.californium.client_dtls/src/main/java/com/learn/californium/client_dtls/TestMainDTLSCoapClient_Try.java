package com.learn.californium.client_dtls;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.concurrent.TimeUnit;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.InMemoryMessageExchangeStore;
import org.eclipse.californium.core.network.RandomTokenGenerator;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.network.config.NetworkConfig.Keys;
import org.eclipse.californium.core.observe.InMemoryObservationStore;
import org.eclipse.californium.elements.StrictDtlsEndpointContextMatcher;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig.Builder;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedSinglePskStore;

public class TestMainDTLSCoapClient_Try {
	/*
	
	String port1 = "coap://localhost:5656/hello";
	String port2 = "coap://160.32.219.56:5656/hello";		//����������ݮ��, ·�ɸ��ĵ�ַ��192.168.50.178
															// �Ұ�����192.168.50.178:5656 ӳ���160.32.219.56:5656
	String port3 = "coap://160.32.219.56:5657/hello";		//����������ݮ��, ·�ɸ��ĵ�ַ��192.168.50.179
															// �Ұ�����192.168.50.179:5656 ӳ���160.32.219.56:5657
	
	
	
	
	// DTLS config constants
	private static final String PSK_IDENITITY = "client1";
	private static final String PSK_KEY = "key1";
	private static final int NB_RETRANSMISSION = 2;
	private static final int RETRANSMISSION_TIMEOUT = 100; // milliseconds
	
	//@ClassRule
	//public static CoapsNetworkRule network = new CoapsNetworkRule(CoapsNetworkRule.Mode.DIRECT,CoapsNetworkRule.Mode.NATIVE);
	
	private CoapEndpoint myCoapEndpoint;
	
	private void createTestEndpoint() {
		// setup DTLS Config
		Builder builder = new DtlsConnectorConfig.Builder()
				.setAddress(new InetSocketAddress(InetAddress.getLoopbackAddress(), 5656))
				.setLoggingTag("client")
				.setAdvancedPskStore(new AdvancedSinglePskStore(PSK_IDENITITY, PSK_KEY.getBytes()))
				.setMaxRetransmissions(NB_RETRANSMISSION)
				.setRetransmissionTimeout(RETRANSMISSION_TIMEOUT);
		DtlsConnectorConfig dtlsConfig = builder.build();

		// setup CoAP config
		
		
		/////*
		NetworkConfig config = network.createTestConfig()
				.setInt(Keys.ACK_TIMEOUT, 200)
				.setFloat(Keys.ACK_RANDOM_FACTOR, 1f)
				.setFloat(Keys.ACK_TIMEOUT_SCALE, 1f)
				.setLong(Keys.EXCHANGE_LIFETIME, TEST_TIMEOUT_EXCHANGE_LIFETIME)
				.setLong(Keys.MARK_AND_SWEEP_INTERVAL, TEST_TIMEOUT_SWEEP_DEDUPLICATOR_INTERVAL);
		//////*
		
		
		NetworkConfig config = NetworkConfig.getStandard();
		// create endpoint for tests
		DTLSConnector clientConnector = new DTLSConnector(dtlsConfig);
		//coapTestEndpoint = new CoapTestEndpoint(clientConnector, config, new StrictDtlsEndpointContextMatcher());
		//myCoapEndpoint = new CoapEndpoint(clientConnector, config, new StrictDtlsEndpointContextMatcher());
		//this(clientConnector, false, config, new InMemoryObservationStore(config),new InMemoryMessageExchangeStore(config), new StrictDtlsEndpointContextMatcher());
		// myCoapEndpoint = (clientConnector, false, config, new RandomTokenGenerator(config), new InMemoryObservationStore(config),new InMemoryMessageExchangeStore(config), new StrictDtlsEndpointContextMatcher(), null, null, null, COAP_STACK_TEST_FACTORY, null);
		// ��coapStackFactory������ֵ COAP_STACK_TEST_FACTORY Ū��null, ���Լ���Ĭ�ϻ� ���� getDefaultCoapStackFactory()
		//myCoapEndpoint = new CoapEndpoint(clientConnector, false, config, new RandomTokenGenerator(config), new InMemoryObservationStore(config),new InMemoryMessageExchangeStore(config), new StrictDtlsEndpointContextMatcher(), null, null, null, null, null);
		
		CoapClient myclinet = new CoapClient(port1);
		myclinet.setDestinationContext(new DtlsEndpointContext(InetSocketAddress peerAddress, Principal peerIdentity, String sessionId, String epoch,
				String cipher, String timestamp));
		EndpointManager.getEndpointManager().setDefaultEndpoint(coapTestEndpoint);
	}
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		try (DatagramSocket datagramSocket = new DatagramSocket(0)) {
			int freePort = datagramSocket.getLocalPort();

			// Create an endpoint
			createTestEndpoint();

			// Send a request to an absent peer
			//CoapClient client = new CoapClient("coaps", TestTools.LOCALHOST_EPHEMERAL.getHostString(), freePort);
			//
			//coap��url��HTTP���к����Ƶĵط�����ͷ�ǡ�coap����Ӧ��http�����ߡ�coaps����Ӧ��https����
			//  CoAP with DTLS support is termed as secure CoAP (CoAPs)
			//  https://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.1060.8187&rep=rep1&type=pdf
			CoapClient client = new CoapClient("coaps", "localhost", 5656, "hello");
			//
			//
			//CountingCoapHandler handler = new CountingCoapHandler();
			CoapHandler handler = new CoapHandler() {

	            @Override
	            public void onLoad(CoapResponse response) {
	                //log.info("Command Response Ack: {}, {}", response.getCode(), response.getResponseText());
	            	System.out.println("on load: " + response.getResponseText());
	            	System.out.println("get code: " + response.getCode().name());
	            	
	            }

	            @Override
	            public void onError() {
	            }
	        };
			
			//��get����, ��ͣ�ػ����Ϣ
			client.get(handler);

			
			
			
			// Wait for error
			handler.waitOnErrorCalls(1, 5000, TimeUnit.MILLISECONDS);

			// We should get a handshake timeout error and so exchange store is
			// empty
			assertEquals("An error is expected", 1, handler.errorCalls.get());

			// Ensure there is no leak : all exchanges are completed
			assertAllExchangesAreCompleted(coapTestEndpoint, time);
			client.shutdown();
		}
		
		
	}
	*/
}
