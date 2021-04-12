package com.learn.californium.server.observerdemo;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class TestObserverResource_Simple  extends CoapResource {

		
		private int int_connect_get_num=0;
	
		public TestObserverResource_Simple(String name) {
			super(name);
			setObservable(true); // enable observing
			setObserveType(Type.CON); // configure the notification type to CONs
			getAttributes().setObservable(); // mark observable in the Link-Format
			
			// schedule a periodic update task, otherwise let events call changed()
			Timer timer = new Timer();
			timer.schedule(new UpdateTask(),0, 10000);
		}
		
		private class UpdateTask extends TimerTask {
			@Override
			public void run() {
				System.out.println("UpdateTask");
				//
				// .. periodic update of the resource

				changed(); // notify all observers
			}
		}
		
		@Override
		public void handleGET(CoapExchange exchange) {
			System.out.println("handleGET");
			//
			int_connect_get_num = int_connect_get_num +1;
			System.out.println("connect num: "+int_connect_get_num);
			//
			//exchange.setMaxAge(1); // the Max-Age value should match the update interval
			//exchange.respond(ResponseCode.CREATED);
			exchange.respond(ResponseCode.CREATED, ""+int_connect_get_num+"//"+this.getEndpoints().size());
			
		}
		
		@Override
		public void handleDELETE(CoapExchange exchange) {
			System.out.println("handleDELETE");
			//
			//
			delete(); // will also call clearAndNotifyObserveRelations(ResponseCode.NOT_FOUND)
			exchange.respond(ResponseCode.DELETED);
		}
		
		@Override
		public void handlePUT(CoapExchange exchange) {
			System.out.println("handlePUT");
			//
			//
			// ...
			exchange.respond(ResponseCode.CHANGED);
			changed(); // notify all observers
		}
		
		public static void main(String[] args) {
			CoapServer server = new CoapServer(5656);
			server.add(new TestObserverResource_Simple("hello_observer"));
			server.start();
			
		}

	}