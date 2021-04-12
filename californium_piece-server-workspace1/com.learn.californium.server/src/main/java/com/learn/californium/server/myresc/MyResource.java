package com.learn.californium.server.myresc;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.observe.ObserveNotificationOrderer;
import org.eclipse.californium.core.observe.ObserveRelationContainer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.core.server.resources.ResourceAttributes;
import org.eclipse.californium.core.server.resources.ResourceObserver;

public class MyResource extends CoapResource {
	public MyResource(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public MyResource(String name, boolean visible) {
		super(name, visible);
	}
	
	
	@Override
	public void handleGET(CoapExchange exchange) {
		
		
		
		System.out.println("handleGET" +"//TOKEN:"+ exchange.advanced().getRequest().getTokenString());
		
		//System.out.println(new String(exchange.getRequestPayload()));
		exchange.respond("hello world"); // reply with 2.05 payload (text/plain)
		//exchange.respond(ResponseCode.CREATED);
	}
	
	
	@Override
	public void handlePUT(CoapExchange exchange) {
		System.out.println("handlePUT"+"//TOKEN:"+ exchange.advanced().getRequest().getTokenString()+"//"+exchange.advanced().getRequest().getTokenBytes());
		//
		if (!exchange.getRequestOptions().hasContentFormat()) {
			exchange.respond(ResponseCode.BAD_REQUEST, "Content-Format not set");
			return;
		}
		
		// store payload
		//storeData(exchange.getRequestPayload(), exchange.getRequestOptions().getContentFormat());

		// complete the request
		exchange.respond(ResponseCode.CHANGED);
	}
	
	
	@Override
	public void handlePOST(CoapExchange exchange) {
		exchange.accept(); // make it a separate response
			
		if (exchange.getRequestOptions() != null) {
			// do something specific to the request options
			System.out.println("handlePOST1");
		}
		System.out.println("handlePOST2");
		exchange.respond(ResponseCode.CREATED); // reply with response code only (shortcut)
	}
}
