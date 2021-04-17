package com.learn.californium.server.minimalexample.mysrc;



import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;

import org.eclipse.californium.core.server.resources.CoapExchange;



/**
 * 
 * MWE means minimal working example
 * 也就是最简化 的例子
 * 
 * @author laipl
 *
 */
public class MyResource_Mwe extends CoapResource {
	public MyResource_Mwe(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public MyResource_Mwe(String name, boolean visible) {
		super(name, visible);
	}
	
	
	@Override
	public void handleGET(CoapExchange exchange) {
		//
		System.out.println("handleGET" +"//TOKEN:"+ exchange.advanced().getRequest().getTokenString());
		// https://datatracker.ietf.org/meeting/interim-2016-t2trg-03/materials/slides-interim-2016-t2trg-03-sessa-californium-coap-00
		exchange.respond("hello world"); // reply with 2.05 payload (text/plain)
		//exchange.respond(ResponseCode.CREATED);
	}
	
	
	@Override
	public void handlePUT(CoapExchange exchange) {
		//
		System.out.println("handlePUT"+"//TOKEN:"+ exchange.advanced().getRequest().getTokenString()+"//"+exchange.advanced().getRequest().getTokenBytes());
		//
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
		exchange.accept(); // make it a separate response
		//
		if (exchange.getRequestOptions() != null) {
			// do something specific to the request options
			System.out.println("handlePOST1");
		}
		System.out.println("handlePOST2");
		exchange.respond(ResponseCode.CREATED); // reply with response code only (shortcut)
	}
}
