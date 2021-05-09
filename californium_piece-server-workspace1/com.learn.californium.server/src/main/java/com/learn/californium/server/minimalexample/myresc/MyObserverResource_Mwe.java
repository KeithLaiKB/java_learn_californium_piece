package com.learn.californium.server.minimalexample.myresc;

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
 * 
 * @author laipl
 *
 * �ο��� 
 * californium/api-demo/src/org/eclipse/californium/examples/CoAPObserveExample.java 
 *
 *
 */
public class MyObserverResource_Mwe  extends CoapResource {

		
		private int int_connect_get_num=0;
		private int int_mytask_used=0;
		
		//private IMyCoapServer myCoapServer1=null;
	
		/* The list of observers (not CoAP observer). */
		//private List<ResourceObserver> observers;
		
		Timer timer = null;
		
		
		public MyObserverResource_Mwe(String name) {
			super(name);
			//
			//----------------------------------------
			this.setObservable(true); // enable observing
			this.setObserveType(Type.CON); // configure the notification type to CONs
			// ���� setObservable() ʹ�� mark observable in the Link-Format 
			// ���Բ� californium ����LinkFormat	
			// �漰�� https://tools.ietf.org/html/rfc6690#section-4 	(�⽲��Linkformat ��ô���ĸ���)
			// ��  https://tools.ietf.org/html/rfc6690#section-4.1
			// https://blog.csdn.net/xukai871105/article/details/45167069/
			// ��ʵ�������ú� application/link-format 
			this.getAttributes().setObservable(); // mark observable in the Link-Format (���Բ� californium ����LinkFormat)	
			//
			//----------------------------------------
			//
			// schedule a periodic update task, otherwise let events call changed()
			//Timer timer = new Timer();
			timer = new Timer();
			// ÿ10000ms ��ȥ ִ��һ�� �����Ǹ�run �� changed �Ӷ�֪ͨ���е�client, ֪ͨ��ʱ�����handleGet
			timer.schedule(new UpdateTask(),0, 10000);
		}
		


		/**
		 * ������ ÿһ��changed ����, Ҫȥ֪ͨ���е�client
		 * ������handelGet
		 * 
		 * @author laipl
		 *
		 */
		private class UpdateTask extends TimerTask {
			@Override
			public void run() {
				System.out.println("UpdateTask-------");
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
		//--------------------- handle get/ delete / put / post--------------------- 
		//
		//
		@Override
		public void handleGET(CoapExchange exchange) {
			System.out.println("handleGET");
			//
			int_connect_get_num = int_connect_get_num +1;
			System.out.println("connect num: "+int_connect_get_num);
			System.out.println("task used num: "+int_mytask_used);
			//
			//exchange.setMaxAge(1); // the Max-Age value should match the update interval
			//exchange.respond(ResponseCode.CREATED);
			// initial, the first time, the getObserverCount()==0
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
				//exchange.respond(ResponseCode.CREATED, "task used num:"+int_mytask_used+"//" +this.myCoapServer1.getMyEndPoints().size()+ "//"+ exchange.getSourceSocketAddress());
				exchange.respond(ResponseCode.CREATED, "task used num:"+int_mytask_used+ "//" + exchange.getSourceSocketAddress());
			}
			
			
		}
		
		@Override
		public void handleDELETE(CoapExchange exchange) {
			System.out.println("handleDELETE");
			//
			//
			/*
			delete(); // will also call clearAndNotifyObserveRelations(ResponseCode.NOT_FOUND)
			exchange.respond(ResponseCode.DELETED);
			//
			System.out.println("MY ATTENTION!!! this client is deleting this resource instead of records");
			//
			//
			timer.cancel();
			*/
			//
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
		//----------------------------------------------------------------- 
		//
		//
		//
		/*
		public IMyCoapServer getMyCoapServer() {
			return myCoapServer1;
		}

		public void setMyCoapServer(IMyCoapServer myCoapServer1) {
			this.myCoapServer1 = myCoapServer1;
		}
		*/

	}