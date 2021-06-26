package com.learn.californium.server.mydemo.observerdemo.myresc;

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

import com.learn.californium.server.mydemo.IMyCoapServer;


/**
 * 
 * 
 * <p>
 * description:																		</br>	
 * &emsp;					it contains server and resource							</br>
 * &emsp;					consist IMyCoapServer									</br>						
 * 
 * </p>
 *
 * param:																			</br>
 * &emsp;					myCoapServer1:											</br>
 * &emsp;&emsp;&emsp;			for further extension in a multiple requirements	</br>
 * 																					</br>
 * 
 * </p>
 *
 *
 * @author laipl
 *
 */
public class MyObserverResource  extends CoapResource {

		
		private int int_connect_get_num=0;
		private int int_mytask_used=0;
		
		private IMyCoapServer myCoapServer1=null;
	
		/* The list of observers (not CoAP observer). */
		//private List<ResourceObserver> observers;
		
		

		public MyObserverResource(String name) {
			super(name);
			this.setObservable(true); // enable observing
			this.setObserveType(Type.CON); // configure the notification type to CONs
			this.getAttributes().setObservable(); // mark observable in the Link-Format
			
			// schedule a periodic update task, otherwise let events call changed()
			Timer timer = new Timer();
			// 每10000ms 则去 执行一次 里面那个run 的 changed 从而通知所有的client, 通知的时候调用handleGet
			timer.schedule(new UpdateTask(),0, 10000);
		}
		


		/**
		 * 这里面 每一次changed 代表, 要去通知所有的client
		 * 则会调用handelGet
		 * 
		 * @author laipl
		 *
		 */
		private class UpdateTask extends TimerTask {
			@Override
			public void run() {
				System.out.println("UpdateTask-------name:"+MyObserverResource.this.getName());
				//
				int_mytask_used = int_mytask_used+1;
				// .. periodic update of the resource
				changed(); // notify all observers
			}
		}
		
		
		@Override
		public void handleGET(CoapExchange exchange) {
			System.out.println("handleGET: "+ super.getName());
			//
			int_connect_get_num = int_connect_get_num +1;
			System.out.println("connect num: "+int_connect_get_num);
			System.out.println("task used num: "+int_mytask_used);
			//
			//exchange.setMaxAge(1); // the Max-Age value should match the update interval
			//exchange.respond(ResponseCode.CREATED);
			if(this.getObserverCount()==0) {
				System.out.println("end points list is null");
				exchange.respond(ResponseCode.CREATED, "task used num:"+int_mytask_used);
			}
			else {
				Iterator it_tmp=this.getAttributes().getAttributeKeySet().iterator();
				System.out.println("rsc_attr: "+it_tmp.next().toString());
				ResourceAttributes rscAtr_tmp = this.getAttributes();
				System.out.println("rsc_attr: "+rscAtr_tmp);
				System.out.println("rsc_attr: "+this.getAttributes().getAttributeValues("obs"));
				ObserveRelation ob_tmp = exchange.advanced().getRelation();
				System.out.println("rsc_endp: "+ob_tmp.getKey().toString());
				System.out.println(exchange.getSourceSocketAddress());
				exchange.respond(ResponseCode.CREATED, "task used num:"+int_mytask_used);
			}
			
			
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
		
		//
		//
		//
		//
		public IMyCoapServer getMyCoapServer() {
			return myCoapServer1;
		}

		public void setMyCoapServer(IMyCoapServer myCoapServer1) {
			this.myCoapServer1 = myCoapServer1;
		}

		


	}