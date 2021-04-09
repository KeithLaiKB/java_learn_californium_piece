package com.learn.californium.client;

import java.io.IOException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.elements.exception.ConnectorException;

public class TestMain_Synchronous {
	public static void main(String[] args) {
		CoapClient client2 = new CoapClient("coap://localhost:5656/hello");
		//
		CoapResponse resp;
		//
		try {
			// http://www.iana.org/assignments/core-parameters/core-parameters.xhtml#content-formats
			//resp = client2.put("payload", MediaTypeRegistry.TEXT_PLAIN);
			resp = client2.put("payload", MediaTypeRegistry.TEXT_PLAIN);
			System.out.println( resp.isSuccess() );
			System.out.println( resp.getOptions() );

			client2.useNONs();  // use autocomplete to see more methods
			client2.delete();
			//client2.useCONs().useEarlyNegotiation(32).get(); // it is a fluent API
			
			
			
		} catch (ConnectorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // for response details

		
	}
}
