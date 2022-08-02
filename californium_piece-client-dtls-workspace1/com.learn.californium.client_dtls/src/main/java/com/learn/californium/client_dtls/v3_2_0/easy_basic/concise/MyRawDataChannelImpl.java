package com.learn.californium.client_dtls.v3_2_0.easy_basic.concise;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.californium.elements.Connector;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.elements.RawDataChannel;
import org.eclipse.californium.scandium.DTLSConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyRawDataChannelImpl implements RawDataChannel {

	private Connector connector;
	private static final Logger LOG = LoggerFactory.getLogger(MyRawDataChannelImpl.class.getName());
	
	
	private static CountDownLatch messageCounter;
	private AtomicInteger clientMessageCounter;
	
	private static String payload = "HELLO WORLD";
	
	
	private MyClient myClient;
	
	public MyRawDataChannelImpl(MyClient myClient) {
		this.myClient = myClient;
	}
	
	
	

	/*
	@Override
	public void receiveData(final RawData raw) {
		if (LOG.isInfoEnabled()) {
			LOG.info("Received request: {}", new String(raw.getBytes()));
		}
		RawData response = RawData.outbound("ACK".getBytes(),
				raw.getEndpointContext(), null, false);
		connector.send(response);
	}
	*/
	
	/*
	public static void setMessageCounter(CountDownLatch messageCounter) {
		this.messageCounter = messageCounter;
	}
	
		public static CountDownLatch getMessageCounter() {
		return messageCounter;
	}
	
	*/
		/*
	
	public void setClientMessageCounter(AtomicInteger clientMessageCounter) {
		this.clientMessageCounter = clientMessageCounter;
	}




	public AtomicInteger getClientMessageCounter() {
		return clientMessageCounter;
	}
*/








	@Override
	public void receiveData(RawData raw) {
		/*
		if (((DTLSConnector)connector).isRunning()) {
			receive(raw);
		}
		*/
		if (this.myClient.getDtlsConnector().isRunning()) {
			this.myClient.receive(raw);
		}
	}
	
	
	
	

}