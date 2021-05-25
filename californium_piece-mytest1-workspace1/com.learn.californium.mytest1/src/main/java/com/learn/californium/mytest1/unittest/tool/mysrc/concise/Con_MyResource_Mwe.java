package com.learn.californium.mytest1.unittest.tool.mysrc.concise;



import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;

import org.eclipse.californium.core.server.resources.CoapExchange;



/**
 * 
 * 
 * <p>
 * 							description:																</br>	
 * &emsp;						MWE means minimal working example										</br>
 * &emsp;						MWE 意思就是  简化的例子														</br>
 * 																										</br>
 *
 *
 * @author laipl
 *
 */
public class Con_MyResource_Mwe extends CoapResource {
	public Con_MyResource_Mwe(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public Con_MyResource_Mwe(String name, boolean visible) {
		super(name, visible);
	}
	
	
	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond("hello world"); // reply with 2.05 payload (text/plain)
		//exchange.respond(ResponseCode.CREATED);
	}
	
	
	/**
	 * ref: //https://github.com/eclipse/californium/blob/master/demo-apps/cf-plugtest-server/src/main/java/org/eclipse/californium/plugtests/resources/Observe.java
	 * 		
	 */
	@Override
	public void handlePUT(CoapExchange exchange) {
		//https://github.com/eclipse/californium/blob/master/demo-apps/cf-plugtest-server/src/main/java/org/eclipse/californium/plugtests/resources/Observe.java
		if (!exchange.getRequestOptions().hasContentFormat()) {
			exchange.respond(ResponseCode.BAD_REQUEST, "Content-Format not set");
			return;
		}
		//
		// store payload
		//storeData(exchange.getRequestPayload(), exchange.getRequestOptions().getContentFormat());
		//
		// complete the request
		exchange.respond(ResponseCode.CHANGED);		// reply with response code only (shortcut)
	}
	
	
	@Override
	public void handlePOST(CoapExchange exchange) {
		//
		//exchange.accept(); // make it a separate response
		//
		if (exchange.getRequestOptions() != null) {
			// do something specific to the request options
			System.out.println("handlePOST:"+ exchange.getRequestOptions().toString());
		}
		exchange.respond("hello world"); // reply with 2.05 payload (text/plain)
		//exchange.respond(ResponseCode.CREATED); // reply with response code only (shortcut)
	}
}
