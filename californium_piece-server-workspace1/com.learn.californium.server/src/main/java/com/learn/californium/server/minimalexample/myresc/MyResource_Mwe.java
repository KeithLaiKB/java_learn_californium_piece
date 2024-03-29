package com.learn.californium.server.minimalexample.myresc;



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
 * </p>
 *
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
		// 尝试exchange
		//System.out.println("handleGET" +"//TOKEN:"+ exchange.advanced().getRequest().getTokenString());
		//
		// https://datatracker.ietf.org/meeting/interim-2016-t2trg-03/materials/slides-interim-2016-t2trg-03-sessa-californium-coap-00
		exchange.respond("hello world"); // reply with 2.05 payload (text/plain)
		//System.out.println("hello world");
		//exchange.respond(""); // reply with 2.05 payload (text/plain)
		//exchange.respond(ResponseCode.CREATED);
	}
	
	
	/**
	 * ref: //https://github.com/eclipse/californium/blob/master/demo-apps/cf-plugtest-server/src/main/java/org/eclipse/californium/plugtests/resources/Observe.java
	 * 		
	 */
	@Override
	public void handlePUT(CoapExchange exchange) {
		//
		//System.out.println("handlePUT"+"//TOKEN:"+ exchange.advanced().getRequest().getTokenString()+"//"+exchange.advanced().getRequest().getTokenBytes());
		//
		//
		//https://github.com/eclipse/californium/blob/master/demo-apps/cf-plugtest-server/src/main/java/org/eclipse/californium/plugtests/resources/Observe.java
		if (!exchange.getRequestOptions().hasContentFormat()) {
			exchange.respond(ResponseCode.BAD_REQUEST, "Content-Format not set");
			return;
		}
		//System.out.println("handlePUT:"+ exchange.getRequestOptions().toString());
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
		/*
		// 如果 	不写		exchange.accept(),  
		// 那么 client  在 server  大约1分钟后       还没得到回答, 就会报错
		// 如果 	写了		exchange.accept(),  
		// 那么 client  在 server  大约5分钟后       还没得到回答, 就会报错, 
		//		但我觉得这个时间应该可以在 用accpet的基础上设置的 
		exchange.accept(); 	// make it a separate response
		try {
			
			Thread.sleep(60000*5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		//
		//
		if (exchange.getRequestOptions() != null) {
			// do something specific to the request options
			System.out.println("handlePOST:\n"+ "\t"+ exchange.getRequestOptions().toString()+"\n"+"\t" +exchange.getRequestText());
		}
		exchange.respond("hello world"); // reply with 2.05 payload (text/plain)
		//exchange.respond(ResponseCode.CREATED); // reply with response code only (shortcut)
	}
}