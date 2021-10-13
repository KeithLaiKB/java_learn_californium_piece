package com.learn.californium.server.compare;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.observe.ObserveRelationContainer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ResourceAttributes;
import org.eclipse.californium.core.server.resources.ResourceObserver;





/**
 * 
 * 
 * <p>
 * 							description:																			</br>	
 * &emsp;						MWE means minimal working example													</br>
 * &emsp;						MWE 意思就是  简化的例子																	</br>
 * &emsp;						for testing the observer															</br>
 * &emsp;						the "_Con_" in MyObserverResource_Con_Mwe means in this class						</br>
 * &emsp;&emsp;						it would use this.setObserveType(Type.CON)										</br>
 * 																													</br>
 * 
 * 							ref:																					</br>	
 * &emsp;						californium/api-demo/src/org/eclipse/californium/examples/CoAPObserveExample.java  	</br>	
 *
 *
 * @author laipl
 *
 */
public class Com_MyObserverResource_Con_Mwe  extends CoapResource {

		
		private int int_connect_get_num=0;
		private int int_mytask_used=0;
		//
		MyTimerTaskForUpdate myUpdateTask1 = null;
		Timer timer = null;
		
		
		
		//
		//
		public Com_MyObserverResource_Con_Mwe(String name) {
			super(name);
			//
			//----------------------------------------
			this.setObservable(true); 				// enable observing
			this.setObserveType(Type.CON); 			// configure the notification type to CONs, 如果不写这个默认的是 NON
			// 涉及到 https://tools.ietf.org/html/rfc6690#section-4 	(这讲了Linkformat 这么做的概念)
			// 和  https://tools.ietf.org/html/rfc6690#section-4.1
			// 其实就是设置好 application/link-format 
			this.getAttributes().setObservable(); // mark observable in the Link-Format (可以查 californium 的类LinkFormat)	
			//
			//----------------------------------------
			//
			// schedule a periodic update task, otherwise let events call changed()
			// Timer timer = new Timer();
			timer = new Timer();
			// 每5000ms 则去 执行一次 里面那个run 的 changed 从而通知所有的client, 通知的时候调用handleGet
			//timer.schedule(new MyUpdateTask(),0, 5000);
			myUpdateTask1 = new MyTimerTaskForUpdate();
			timer.schedule(myUpdateTask1,0, 5000);
			
		}
		


		/**
		 * 这里面 每一次changed 代表, 要去通知所有的client
		 * 则会调用handelGet
		 * 
		 * @author laipl
		 *
		 */
		private class MyTimerTaskForUpdate extends TimerTask {
			@Override
			public void run() {
				System.out.println("UpdateTask-------name:"+Com_MyObserverResource_Con_Mwe.this.getName());
				//
				int_mytask_used = int_mytask_used+1;
				// .. periodic update of the resource
				changed(); // notify all observers
			}
		}
		//
		//
		//
		//
		//--------------------------------------------------------------------------------
		//------------------------ handle get/ delete / put / post------------------------ 
		//
		//
		@Override
		public void handleGET(CoapExchange exchange) {
			System.out.println("handleGET: "+ super.getName());
			//
			int_connect_get_num = int_connect_get_num +1;
			//exchange.respond(ResponseCode.CONTENT, "task used num:"+int_mytask_used);
			exchange.respond("task used num:"+int_mytask_used);
		}
		
		@Override
		public void handleDELETE(CoapExchange exchange) {
			System.out.println("handleDELETE");
			//
			delete(); // will also call clearAndNotifyObserveRelations(ResponseCode.NOT_FOUND)
			//
			System.out.println("MY ATTENTION!!! this client is deleting this resource instead of records");
			//
			// 关闭计时器
			timer.cancel();
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
		//
		//
		//
		//--------------------------------------------------------------------------------
		//----------------------------------  my method ----------------------------------
		//把timer 停止了, 如果只是server.destory 是不会把这个 resource的 Timer结束的
		//所以我需要 自己设置一个方法来停止这个timer
		public int stopMyResource(){
			this.timer.cancel();
			return 1;
		}

	}