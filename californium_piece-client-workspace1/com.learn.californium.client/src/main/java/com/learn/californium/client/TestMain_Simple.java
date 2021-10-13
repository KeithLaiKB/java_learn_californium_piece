package com.learn.californium.client;

import java.io.IOException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.elements.exception.ConnectorException;
/**
 * 
 * 
 * <p>
 * 							description:																			</br>	
 * &emsp;						simple demo mainly refer to the official website									</br>
 * 																													</br>
 * 				
 * 							ref:																					</br>	
 * &emsp;						californium/api-demo/src/org/eclipse/californium/examples/CoAPObserveExample.java  	</br>
 * &emsp;						californium/demo-apps/cf-plugtest-client/src/main/java/org/eclipse/californium/plugtests/PlugtestClient.java	
 *
 *
 * @author laipl
 *
 */
public class TestMain_Simple {
    public static void main(String[] args) {

        CoapClient client = new CoapClient("coap://localhost:5656/hello");
        //
        CoapResponse response;
		//
        try {
        	// 你仔细查底下 get()的实现
        	// 你发现它 自带了 Type.Con 这个设置
			response = client.get();
			//String xml = client.get(MediaTypeRegistry.APPLICATION_XML).getResponseText();
			//
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
		} catch (ConnectorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
