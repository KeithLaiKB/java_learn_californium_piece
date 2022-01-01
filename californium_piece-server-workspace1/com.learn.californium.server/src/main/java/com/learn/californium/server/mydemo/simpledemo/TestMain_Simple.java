package com.learn.californium.server.mydemo.simpledemo;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.learn.californium.server.mydemo.myresc.MyResource;




/**
 * 
 * 
 * <p>
 * description																	</br>
 * &nbsp;&nbsp;&nbsp;&nbsp;		in this class,									</br>						
 * &nbsp;&nbsp;&nbsp;&nbsp;		it contains server and resource					</br>
 * </p>
 * 
 * 
 * @author laipl
 *
 */
public class TestMain_Simple extends CoapResource {
	public TestMain_Simple(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public TestMain_Simple(String name, boolean visible) {
		super(name, visible);
	}
	
	
	@Override
	public void handleGET(CoapExchange exchange) {
		//
		System.out.println("handleGET" +"//TOKEN:"+ exchange.advanced().getRequest().getTokenString());
		//
		//System.out.println(new String(exchange.getRequestPayload()));
		System.out.println("handleGET" +"//getSourceAddress:"+ exchange.getSourceAddress());
		System.out.println("handleGET" +"//getSourcePort:"+ exchange.getSourcePort());
		System.out.println("handleGET" +"//getSourceSocketAddress:"+ exchange.getSourceSocketAddress());
		System.out.println("handleGET" +"//getRequestText:"+ exchange.getRequestText());
		System.out.println("handleGET" +"//getRequestPayload:"+ exchange.getRequestPayload());
		System.out.println("handleGET" +"//getQueryParameter_myvar1:"+ exchange.getQueryParameter("my_var1"));
		System.out.println("handleGET" +"//getRequestOptions:"+ exchange.getRequestOptions());
		//
		System.out.println("handleGET" +"//getRequestOptions:"+ exchange.getQueryParameter("my_var1"));
		//
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
		exchange.respond(ResponseCode.CHANGED);
	}
	
	
	@Override
	public void handlePOST(CoapExchange exchange) {
		//
		// 原来
		// 53144	-> 	5656 	CON		POST ....
		// 5656		->	53144	ACK
		//
		// 如果多了 这句话 相比于 没有这句话 多出ACK
		// 53144	-> 	5656 	CON		POST ....
		// 5656		->	53144	ACK
		// 5656		->	53144	CON		hello_nihao
		// 53144	->	5656	ACK
		exchange.accept(); // make it a separate response
		//
		if (exchange.getRequestOptions() != null) {
			// do something specific to the request options
			System.out.println("handlePOST1");
		}
		System.out.println("handlePOST2");
		exchange.respond(ResponseCode.CREATED); // reply with response code only (shortcut)
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 如果不填参数，则默认端口是5683
		// 这里我尝试自己定义一个端口5656
		CoapServer server = new CoapServer(5656);
		// 注意 这里的 hello 大小写是敏感的
		// 因为 client那边 是根据 coap://localhost:5656/hello 来发送请求的
		server.add(new MyResource("hello"));
		//
		server.start(); // does all the magic

	}

}