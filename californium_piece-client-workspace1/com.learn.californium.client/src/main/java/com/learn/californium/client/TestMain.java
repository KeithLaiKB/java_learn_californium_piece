package com.learn.californium.client;

import java.io.IOException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.elements.exception.ConnectorException;

public class TestMain {
    public static void main(String[] args) {

        CoapClient client = new CoapClient("coap://localhost:5656/hello");
        //
        CoapResponse response;
		//
        try {
			
			response = client.get();
			//String xml = client.get(MediaTypeRegistry.APPLICATION_XML).getResponseText();
			
	        
	        if (response!=null) {
	        
	        	System.out.println( response.getCode().name() );
	        	System.out.println( response.getOptions() );
	        	System.out.println( "response text:" + response.getResponseText() );
	        	System.out.println( "payload:" + new String(response.getPayload()) );
	        	
	        	//System.out.println(xml);
	        	
	        } else {
	        	
	        	System.out.println("Request failed");
	        	
	        }			
		//		
		//	
		} catch (ConnectorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
