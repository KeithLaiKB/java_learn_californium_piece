package com.learn.californium.server.mydemo.observerdemo;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.server.resources.CoapExchange;


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
 * 拿来测试 如果不用timer 会怎么样, 结果发现 不用timer的话, observe的时候, 如果有两个resource, 第一个resource没运行完, 不会有第二条resource的开始
 * 所以发现这种方式是不好的, 我还是推荐用Timer
 */
public class TestNoTimerObserver  extends CoapResource {

		
		private int int_connect_get_num=0;
	
		public TestNoTimerObserver(String name) {
			super(name);
			setObservable(true); // enable observing
			setObserveType(Type.CON); // configure the notification type to CONs
			getAttributes().setObservable(); // mark observable in the Link-Format
			
			// schedule a periodic update task, otherwise let events call changed()
			//Timer timer = new Timer();
			//timer.schedule(new UpdateTask(),0, 200);
		}

		/*
		private class UpdateTask extends TimerTask {
			@Override
			public void run() {
				System.out.println("UpdateTask");
				//
				// .. periodic update of the resource

				changed(); // notify all observers
			}
		}*/
		
		
		
		public void runna() {
			for(int i=0;i<=20-1;i++) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				changed(); // notify all observers
			}
			System.out.println("finished");
		}
		
		@Override
		public void handleGET(CoapExchange exchange) {
			System.out.println("handleGET: "+ super.getName());
			//
			int_connect_get_num = int_connect_get_num +1;
			System.out.println("connect num: "+int_connect_get_num);
			//
			//exchange.setMaxAge(1); // the Max-Age value should match the update interval
			//exchange.respond(ResponseCode.CREATED);
			//
			//
			//
			//exchange.respond(ResponseCode.CREATED, ""+int_connect_get_num+"//"+this.getEndpoints().size());
			exchange.respond(ResponseCode.CREATED, ""+int_connect_get_num);
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
			CoapServer server = new CoapServer(5683);
			TestNoTimerObserver rsc1=new TestNoTimerObserver("hello_observer");
			TestNoTimerObserver rsc2=new TestNoTimerObserver("hello_observer2");
			server.add(rsc1);
			server.add(rsc2);
			
			server.start();
			rsc1.runna();
			rsc2.runna();

		}

	}