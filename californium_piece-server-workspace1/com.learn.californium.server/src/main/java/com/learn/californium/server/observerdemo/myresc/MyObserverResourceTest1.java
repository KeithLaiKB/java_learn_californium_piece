package com.learn.californium.server.observerdemo.myresc;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.observe.ObserveRelationContainer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ResourceAttributes;
import org.eclipse.californium.core.server.resources.ResourceObserver;

import com.learn.californium.server.IMyCoapServer;
import com.learn.californium.server.myresc.MyCoapResource;

public class MyObserverResourceTest1  extends MyCoapResource {

		
		private int int_connect_get_num=0;
		private IMyCoapServer myCoapServer1=null;
	
		
		

		public MyObserverResourceTest1(String name) {
			super(name);
			setObservable(true); // enable observing
			// Exchange.class 
			// public void setCurrentResponse(Response newCurrentResponse)
			//setObserveType(Type.CON); // configure the notification type to CONs
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
			//
			//
			//exchange.get
			//
			//
			if(this.getObserverCount()==0) {
				System.out.println("end points list is null");
				exchange.respond(ResponseCode.CREATED, ""+int_connect_get_num);
			}
			else {
				Iterator it_tmp=this.getAttributes().getAttributeKeySet().iterator();
				System.out.println("rsc_attr_key_set: "+it_tmp.next().toString());
				ResourceAttributes rscAtr_tmp = this.getAttributes();
				System.out.println("rsc_attr: "+rscAtr_tmp);
				System.out.println("rsc_attr_valus_of_var_obs: "+this.getAttributes().getAttributeValues("obs"));
				
				//
				//
				//
				// 获取 当前请求的 ip 和 端口 和 具体信息, 例如 127.0.0.1:49599#71D02AE4EEAECC79
				ObserveRelation ob_tmp = exchange.advanced().getRelation();
				System.out.println("rsc_endp: "+ob_tmp.getKey().toString());
				// 获取 当前请求的 ip 和 端口  
				System.out.println(exchange.getSourceSocketAddress());
				// 
				//
				// 
				exchange.respond(ResponseCode.CREATED, ""+int_connect_get_num+"//" +this.myCoapServer1.getMyEndPoints().size()+ "//"+ exchange.getSourceSocketAddress());
				//
				
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