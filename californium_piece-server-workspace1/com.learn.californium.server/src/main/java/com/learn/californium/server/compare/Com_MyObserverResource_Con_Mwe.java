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
 * &emsp;						MWE ��˼����  �򻯵�����																	</br>
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
			this.setObserveType(Type.CON); 			// configure the notification type to CONs, �����д���Ĭ�ϵ��� NON
			// �漰�� https://tools.ietf.org/html/rfc6690#section-4 	(�⽲��Linkformat ��ô���ĸ���)
			// ��  https://tools.ietf.org/html/rfc6690#section-4.1
			// ��ʵ�������ú� application/link-format 
			this.getAttributes().setObservable(); // mark observable in the Link-Format (���Բ� californium ����LinkFormat)	
			//
			//----------------------------------------
			//
			// schedule a periodic update task, otherwise let events call changed()
			// Timer timer = new Timer();
			timer = new Timer();
			// ÿ5000ms ��ȥ ִ��һ�� �����Ǹ�run �� changed �Ӷ�֪ͨ���е�client, ֪ͨ��ʱ�����handleGet
			//timer.schedule(new MyUpdateTask(),0, 5000);
			myUpdateTask1 = new MyTimerTaskForUpdate();
			timer.schedule(myUpdateTask1,0, 5000);
			
		}
		


		/**
		 * ������ ÿһ��changed ����, Ҫȥ֪ͨ���е�client
		 * ������handelGet
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
			// �رռ�ʱ��
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
		//��timer ֹͣ��, ���ֻ��server.destory �ǲ������� resource�� Timer������
		//��������Ҫ �Լ�����һ��������ֹͣ���timer
		public int stopMyResource(){
			this.timer.cancel();
			return 1;
		}

	}