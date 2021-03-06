package com.learn.californium.client_dtls.v2_6_0;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.List;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.DtlsEndpointContext;
import org.eclipse.californium.elements.EndpointContext;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedSinglePskStore;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;

import com.learn.californium.client_dtls.v2_6_0.CredentialsUtil.Mode;
import com.learn.californium.client_dtls.v2_6_0.easy_basic_demo.MyRawDataChannelImpl;

public class MyDtlsCoapClient {
	public static final List<Mode> SUPPORTED_MODES = Arrays.asList(Mode.PSK, Mode.ECDHE_PSK, Mode.RPK, Mode.X509,
			Mode.RPK_TRUST, Mode.X509_TRUST);
	//private static final String SERVER_URI = "coaps://127.0.0.1:5684/secure";
	private static final String SERVER_URI = "coaps://127.0.0.1:5656/secure";

	private DTLSConnector dtlsConnector;
	
	public MyDtlsCoapClient() {
		
	}
	
	public void my_configureToPrepare(String[] args) {
		System.out.println("Usage: java -cp ... org.eclipse.californium.examples.SecureClient [PSK|ECDHE_PSK] [RPK|RPK_TRUST] [X509|X509_TRUST]");
		System.out.println("Default:            [PSK] [RPK] [X509]");
		
		
		// step1: setting up the builder
		DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder();
		CredentialsUtil.setupCid(args, builder);
		builder.setClientOnly();
		builder.setSniEnabled(false);
		builder.setRecommendedCipherSuitesOnly(false);
		//
		//
		List<Mode> modes = CredentialsUtil.parse(args, CredentialsUtil.DEFAULT_CLIENT_MODES, SUPPORTED_MODES);
		if (modes.contains(CredentialsUtil.Mode.PSK) || modes.contains(CredentialsUtil.Mode.ECDHE_PSK)) {
			builder.setAdvancedPskStore(new AdvancedSinglePskStore(CredentialsUtil.OPEN_PSK_IDENTITY, CredentialsUtil.OPEN_PSK_SECRET));
		} else {
			builder.setSupportedCipherSuites(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256);
		}
		CredentialsUtil.setupCredentials(builder, CredentialsUtil.CLIENT_NAME, modes);
		//
		//
		// step2: setting up the dtls connector
		dtlsConnector = new DTLSConnector(builder.build());
	}
	
	
	
	//
	//
	// put... in ....
	// uri->client
	// dtlsconnector->builder
	// builder->client
	// client.get()
	public void test() {
		CoapResponse response = null;
		try {
			URI uri = new URI(SERVER_URI);
			CoapClient client = new CoapClient(uri);
			CoapEndpoint.Builder builder = new CoapEndpoint.Builder();
			builder.setConnector(dtlsConnector);
			
			client.setEndpoint(builder.build());
			//
			//
			//
			//response = client.get();
			response = client.put("helloput", MediaTypeRegistry.TEXT_PLAIN);
			client.shutdown();
		} catch (URISyntaxException e) {
			System.err.println("Invalid URI: " + e.getMessage());
			System.exit(-1);
		} catch (ConnectorException | IOException e) {
			System.err.println("Error occurred while sending request: " + e);
			System.exit(-1);
		}

		if (response != null) {

			System.out.println(response.getCode() + " - " + response.getCode().name());
			System.out.println(response.getOptions());
			System.out.println(response.getResponseText());
			System.out.println();
			System.out.println("ADVANCED:");
			EndpointContext context = response.advanced().getSourceContext();
			Principal identity = context.getPeerIdentity();
			if (identity != null) { 
				System.out.println(context.getPeerIdentity());
			} else {
				System.out.println("anonymous");
			}
			System.out.println(context.get(DtlsEndpointContext.KEY_CIPHER));
			System.out.println(Utils.prettyPrint(response));
		} else {
			System.out.println("No response received.");
		}
	}
}