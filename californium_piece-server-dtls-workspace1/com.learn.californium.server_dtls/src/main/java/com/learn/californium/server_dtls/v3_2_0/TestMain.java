package com.learn.californium.server_dtls.v3_2_0;

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

public class TestMain {

	private static final Logger LOG = LoggerFactory
			.getLogger(TestMain.class.getName());

	public TestMain() {
		
	}



	public static void main(String[] args) {

		MyServer server = new MyServer();
		server.my_configureToPrepare();
		server.start();
		//
		//
		try {
			for (;;) {
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
		}
	}
	
	
	



}