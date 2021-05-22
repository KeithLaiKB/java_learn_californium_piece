package com.learn.californium.server.mydemo.observerdemo.myresc.useless;

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

import com.learn.californium.server.mydemo.IMyCoapServer;
import com.learn.californium.server.mydemo.myresc.MyCoapResource;

/**
 * 
 * @author laipl
 *
 * �ο��� 
 * californium/api-demo/src/org/eclipse/californium/examples/CoAPObserveExample.java 
 *
 * ���⻹
 * �̳� MyCoapResource, �����  ��΢��д�˵� CoapResource �е�һЩ�������� 
 *
 *
 * ��MyObserverResource_Modified ���Ƴ�����, ����һģһ����
 * ֻ�ǲ��ʱ���ж����Ǹ���Դ��������, �ò��������
 * ����ɾ����
 *
 */
public class MyObserverResource_Modified_Cp3  extends MyCoapResource {

		
		private int int_connect_get_num=0;
		private int int_mytask_used=0;
		
		private IMyCoapServer myCoapServer1=null;
	
		Timer timer = null;
		
		// ԭ�����õ�����  setObserveType(Type.CON)
		// ������� ��仰 ����� û����仰 ���ACK
		// 53144	-> 	5656 	CON		GET ....
		// 5656		->	53144	ACK		1st_num
        // 5656		->	53144	CON		2nd_num
		// 53144	-> 	5656 	ACK		
        // 5656		->	53144	CON		3rd_num
		// 53144	-> 	5656 	ACK		
        // 5656		->	53144	CON		4th_num
		// 53144	-> 	5656 	ACK		
		//
		// ���û����仰
		// ��Ĭ�� Type.NON 
		// 53144	-> 	5656 	CON		GET ....
		// 5656		->	53144	ACK		1st_num
        // 5656		->	53144	NON		2nd_num
        // 5656		->	53144	NON		3rd_num
        // 5656		->	53144	NON		4th_num
		public MyObserverResource_Modified_Cp3(String name) {
			super(name);
			setObservable(true); // enable observing
			// Exchange.class 
			// public void setCurrentResponse(Response newCurrentResponse)
			setObserveType(Type.CON); // configure the notification type to CONs
			getAttributes().setObservable(); // mark observable in the Link-Format
			
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
			//
			//
			//exchange.get
			//
			//
			if(this.getObserverCount()==0) {
				System.out.println("end points list is null");
				exchange.respond(ResponseCode.CREATED, "task used num:"+int_mytask_used);
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
				// ��ȡ ��ǰ����� ip �� �˿� �� ������Ϣ, ���� 127.0.0.1:49599#71D02AE4EEAECC79
				ObserveRelation ob_tmp = exchange.advanced().getRelation();
				System.out.println("rsc_endp: "+ob_tmp.getKey().toString());
				// ��ȡ ��ǰ����� ip �� �˿�  
				System.out.println(exchange.getSourceSocketAddress());
				// 
				//
				// 
				exchange.respond(ResponseCode.CREATED, "task used num:"+int_mytask_used+"//" +this.myCoapServer1.getMyEndPoints().size()+ "//"+ exchange.getSourceSocketAddress());
				//
				
			}
			
			
		}
		
		/**
		 * ע��, �������delete���� 
		 * ��Ϊ��  ɾ�������Դ�Ĳ���, ��Ϊ������ ��������delete() 
		 * ��˼��ɾ�������Դ, ��client�޷�����observe
		 * 
		 * ��Ȼ handelDelete ��������
         * 		1. �������� 	������ɾ�� �����Դ
         * 		2. Ҳ��������	������ɾ�� ĳ����¼(����server�Ǳ� ���˸����ݿ�)
		 * 
		 * ֻ�������� ѡ���� 1. �������� 	������ɾ�� �����Դ
		 * ��Ȼ����Ըĳ�2, 
		 * 		��ô����Ҫɾ��������� delete(), 
		 * 		Ȼ����� ɾ�����ݿ����ĳ����¼�Ĳ����� 
		 * 
		 */
		@Override
		public void handleDELETE(CoapExchange exchange) {
			System.out.println("handleDELETE:"+exchange.getRequestPayload());
			//
			//
			delete(); // will also call clearAndNotifyObserveRelations(ResponseCode.NOT_FOUND)
			exchange.respond(ResponseCode.DELETED);
			//
			System.out.println("MY ATTENTION!!! this client is deleting this resource instead of records");
			//
			// �رռ�ʱ��
			timer.cancel();
			// ��resource �����client ���Ƴ�
			this.myCoapServer1.remove(this);
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