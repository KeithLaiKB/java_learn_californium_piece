package com.learn.californium.server_dtls;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.network.interceptors.MessageTracer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;

import com.learn.californium.server_dtls.CredentialsUtil.Mode;

public class TestMainDTLSCoapServer {

	// �������server ��  client ������
	public static final List<Mode> SUPPORTED_MODES = Arrays.asList(Mode.PSK, Mode.ECDHE_PSK, Mode.RPK, Mode.X509,
			Mode.WANT_AUTH, Mode.NO_AUTH);

	// allows configuration via Californium.properties
	//public static final int DTLS_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_SECURE_PORT);
	public static final int DTLS_PORT = 5656;
	
	public static void main(String[] args) {

		System.out.println("Usage: java -jar ... [PSK] [ECDHE_PSK] [RPK] [X509] [WANT_AUTH|NO_AUTH]");
		System.out.println("Default :            [PSK] [ECDHE_PSK] [RPK] [X509]");
		//
		//


		//
		//
		// 
		DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder();
		//
		//
		// �����Ƕ�  �����д��� Main�����Ĳ���	�� 	builder����	 ������������
		// ���û�д������, �������������ʲô����
		CredentialsUtil.setupCid(args, builder);
		//
		//
		builder.setAddress(new InetSocketAddress(DTLS_PORT));
		builder.setRecommendedCipherSuitesOnly(false);
		//
		//
		//
		//
		// �� main�����������mode���� 		��		server��mode ����		��		�����clientҪ����Ҫ��mode �Ĳ����������
		List<Mode> modes = CredentialsUtil.parse(args, CredentialsUtil.DEFAULT_SERVER_MODES, SUPPORTED_MODES);
		// Ȼ��ͳһ	�� builder ����setup
		CredentialsUtil.setupCredentials(builder, CredentialsUtil.SERVER_NAME, modes);
		//
		// ���� builder��һЩ������Ϊ���� ��  ����DTLSConnector
		DTLSConnector connector = new DTLSConnector(builder.build());
		CoapEndpoint.Builder coapBuilder = new CoapEndpoint.Builder();
		coapBuilder.setConnector(connector);
		// 
		//
		//
		// mode->builder
		// dtlsconnector
		// put the builder in the connector
		// endpoint set connector
		// endpoint
		//
		// ���� server
		CoapServer server = new CoapServer();
		// ���resource
		server.add(new CoapResource("secure") {

			@Override
			public void handleGET(CoapExchange exchange) {
				exchange.respond(ResponseCode.CONTENT, "hello security GET");
			}
			@Override
			public void handlePUT(CoapExchange exchange) {
				exchange.respond(ResponseCode.CONTENT, "hello I am server security PUT");
			}
		});
		//
		//
		//
		// ETSI Plugtest environment
		// server.addEndpoint(new CoAPEndpoint(new DTLSConnector(new InetSocketAddress("::1", DTLS_PORT)), NetworkConfig.getStandard()));
		// server.addEndpoint(new CoAPEndpoint(new DTLSConnector(new InetSocketAddress("127.0.0.1", DTLS_PORT)), NetworkConfig.getStandard()));
		// server.addEndpoint(new CoAPEndpoint(new DTLSConnector(new InetSocketAddress("2a01:c911:0:2010::10", DTLS_PORT)), NetworkConfig.getStandard()));
		// server.addEndpoint(new CoAPEndpoint(new DTLSConnector(new InetSocketAddress("10.200.1.2", DTLS_PORT)), NetworkConfig.getStandard()));
		//
		//
		//
		//
		// �� CoapServer ���� DTLSConnector
		server.addEndpoint(coapBuilder.build());
		server.start();

		// add special interceptor for message traces
		for (Endpoint ep : server.getEndpoints()) {
			ep.addInterceptor(new MessageTracer());
		}

		System.out.println("Secure CoAP server powered by Scandium (Sc) is listening on port " + DTLS_PORT);
	}

}
