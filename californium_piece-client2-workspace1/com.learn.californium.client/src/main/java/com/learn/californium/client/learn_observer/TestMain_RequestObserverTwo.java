package com.learn.californium.client.learn_observer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;

/**
 * 
 * 
 * <p>
 * 							description:																			</br>	
 * &emsp;						client to observe																	</br>
 * 																													</br>
 * 
 * 							ref:																					</br>	
 * &emsp;						 californium/demo-apps/cf-plugtest-client/src/main/java/org/eclipse/californium/plugtests/PlugtestClient.java  	</br>	
 * 																													</br>
 *
 * @author laipl
 *
 */
public class TestMain_RequestObserverTwo {
    public static void main(String[] args) {
    	/**
    	 * ���� ������������
    	 * ��Դ	hello_observer
    	 * ����	hello_observer 			������һ������Դ 	hello_observer_child1 
    	 * ����	hello_observer_child1 	������һ������Դ 	hello_observer_child2 
    	 * ����	hello_observer_child2 	������һ������Դ 	hello_observer_child3 
    	 * 
    	 */
    	String myuri1 	     = "coap://localhost:5656/hello_observer";
    	String myuri1_wrong1 = "coap://localhost:5656/hello_observer_child1";				//���� ����ļ���, �����Ƿ��ʲ��� hello_observer ������Դ		hello_observer_child1 
    	String myuri1_wrong2 = "coap://localhost:5656/hello_observer_child2";				//���� ����ļ���, �����Ƿ��ʲ��� hello_observer ������Դ		hello_observer_child1 	������Դ	 hello_observer_child2 
    	String myuri1_c1 	 = "coap://localhost:5656/hello_observer/hello_observer_child1";
    	String myuri1_c2 	 = "coap://localhost:5656/hello_observer/hello_observer_child1/hello_observer_child2";
    	String myuri1_c3	 = "coap://localhost:5656/hello_observer/hello_observer_child1/hello_observer_child2/hello_observer_child3";
    	
        //
    	//-----------------------------------------------------------------------
    	// �� uri �Ľ���
    	//
    	// ���һ:
    	// ������ localhost:5656/hello_observer/hello_observer_child1
    	// ʹ���� delete
    	// ֹͣ��
    	// ����     localhost:5656/hello_observer/hello_observer_child1/hello_observer_child2
    	// ��NOT_FOUND��
    	// +++++++++++++++++++++
    	//
    	// �����:
    	// ������ localhost:5656/hello_observer/hello_observer_child1
    	// ʹ���� delete
    	// ֹͣ��
    	// ����     localhost:5656/hello_observer/hello_observer_child1/hello_observer_child2/hello_observer_child3
    	// ��NOT_FOUND��
    	// +++++++++++++++++++++
    	//
    	// �����:
    	// ������ localhost:5656/hello_observer/hello_observer_child1
    	// ʹ���� delete
    	// ֹͣ��
    	// ����     localhost:5656/hello_observer/hello_observer_child1/hello_observer_child2
    	// �ǿ��Է��ʵĵ���
    	//
    	// Ҳ����˵ ���������ɾ��һ��resource, ������resourceҲ�ᱻɾ��
    	//-----------------------------------------------------------------------
    	//
    	//
    	//CoapClient client = new CoapClient("coap://localhost:5683/hello_observer");
    	CoapClient client = new CoapClient(myuri1);
        //
        CoapObserveRelation coapObRelation1;
		//
        try {
			
			//response = client.get();
	        //Request rq1=new Request(Code.POST);
	        //rq1.setType(Type.CON);
	        //
        	// set handler for observer method, because observe method needs asynchronous operation
			CoapHandler myObserveHandler = new CoapHandler() {

	            @Override
	            public void onLoad(CoapResponse response) {
	                //log.info("Command Response Ack: {}, {}", response.getCode(), response.getResponseText());
	            	System.out.println("---------------------------------------");
	            	System.out.println("-------- observe handler onload start --------------");
					System.out.println("result from server:" + response.isSuccess() );
	            	System.out.println("on load: " + response.getResponseText());
	            	System.out.println("get code: " + response.getCode().name());
	            	System.out.println("---------- observe handler onload end --------------");
	            	
	            }

	            @Override
	            public void onError() {
	            }
	        };

	        // set handler for delete method, you can choose use or not to use
	        // if you use this handler in delete(handler), it means you want asynchronous operation
			CoapHandler myDeleteHandler = new CoapHandler() {

	            @Override
	            public void onLoad(CoapResponse response) {
	                //log.info("Command Response Ack: {}, {}", response.getCode(), response.getResponseText());
	            	System.out.println("---------------------------------------");
	            	System.out.println("-------- delete handler onload start --------------");
	            	System.out.println("result from server:" + response.isSuccess() );
					//
	            	System.out.println("on load: " + response.getResponseText());
	            	System.out.println("get code: " + response.getCode().name());
	            	System.out.println("---------- delete handler onload end --------------");
	            	
	            }

	            @Override
	            public void onError() {
	            }
	        };
	        //
	        //
	        //
			// ����server�е�resource �趨��type��NON ��ô Ч��������������
			// 53144	-> 	5656 	CON		GET ....
			// 5656		->	53144	ACK		1st_num
	        // 5656		->	53144	NON		2nd_num
	        // 5656		->	53144	NON		3rd_num
	        // 5656		->	53144	NON		4th_num
	        coapObRelation1 = client.observe(myObserveHandler);
			//response = client.observeAndWait(handler);
	        //response = client.observe(rq1,handler);
	        //
	        // �����observe��ʱ��
	        // ��������,
	        // ����������client, û����server, 
	        // ��ô��ʱ��	��	��ʾwow_hello,
	        //
	        // �����observeAndWait��ʱ��
	        // ����������client, û����server, 
	        // ��ô��ʱ��	����	��ʾwow_hello,
	        //
	        // ���Ծ�����һ���߳������˵ĸо�
	        System.out.println("wow_hello");
	        //
	        if (coapObRelation1!=null) {
	        
	        	System.out.println( "response getCode:");
	        	System.out.println( "response getOptions:");
	        	//System.out.println( "response text:" + response.toString() );
	        	//System.out.println( "payload:" + response.getCurrent().getResponseText() );
	        	//response.getCurrentResponse();
	        	//System.out.println( "payload:" + new String(response.getCurrent().getPayload()) );
	        	
	        	//System.out.println(xml);
	        } else {
	        	
	        	System.out.println("Request failed");
	        	
	        }	
	        //
	        //
	        //
	        Scanner in =new Scanner(System.in) ;
            int int_choice = 0;
            while(int_choice!=-1) {
            	System.out.println("here is the choice:");
            	System.out.println("-1: to exit");
            	System.out.println("1: to delete");
            	System.out.println("2: to reactiveCancel");
            	System.out.println("3: to proactiveCancel");
            	System.out.println("4: to observe again");
            	System.out.println("enter the choice:");
            	// input
            	int_choice = in.nextInt();
            	if(int_choice==-1) {
            		//System.exit(0);
            		break;
            	}
            	else if(int_choice==1) {
            		//
            		System.out.println("deleteing record");
            		//System.out.println("deleting resources");
            		//
            		//
            		// ����Ϊ delete ͦ��Ҫ�� ��������ѡ�����ͬ��
            		//client.delete();				// �õ��� ͬ��, ����û��Ӧ, �Ͳ��ܼ���������
            		client.delete(myDeleteHandler); // �õ��� �첽
            		//
            		// ע�� ���delete 
            		// �������� 	������ɾ�� �����Դ
            		// Ҳ��������	������ɾ�� ĳ����¼(����server�Ǳ� ���˸����ݿ�)
            		// ��ȡ���� server �Ǳߵ� handleDelete ��Ĳ���
            		//
            		// ��� ���÷�����ɾ�� �����Դ
            		// �Ժ� client �������յ����resource������, ���� server���������� 
            		// ����server�Ǳ���Ҫ ��timer�ص�, ���⻹�п���Ҫ remove(Resource resource)
            		//
            		//
            		//System.out.println("deleted resources");
            		//System.out.println("deleted record");
            	}
            	else if(int_choice==2) {
            		
            		// ref https://datatracker.ietf.org/doc/html/rfc7641#section-3.6
            		// When the server then
            		//  sends the next notification, the client will not recognize the token
            		//  in the message and thus will return a Reset message
            		//
            		// in order words it means
            		// send a RST when next notification arrives
            		// 
            		// Ҳ���� ˵ �ȵ���һ�� server���� Notification������ʱ�� 
            		// ���subscriber �ŷ��� RST��server ��ȡ���۲������Ϣ
            		coapObRelation1.reactiveCancel();				//ȡ���۲�״̬
            		System.out.println("reactiveCancel");
            		
            	}
            	else if(int_choice==3) {

            		// ref https://datatracker.ietf.org/doc/html/rfc7641#section-3.6
            		// In some circumstances, it may be desirable to cancel an observation
            		//   and release the resources allocated by the server to it more eagerly.
            		//   In this case, a client MAY explicitly deregister by issuing a GET
            		//   request that has the Token field set to the token of the observation
            		//   to be cancelled and includes an Observe Option with the value set to
            		//   1 (deregister)
            		//
            		// in order words it means
            		// send another cancellation request, with an Observe Option set to 1 (deregister)
            		// 
            		// Ҳ���� ˵  	���� 		�ȵ���һ�� server���� Notification������ʱ��,  ���subscriber �ŷ��� RST��server ��ȡ���۲������Ϣ
            		// ����ֱ��	���� һ�� get����	��server ��ȡ���۲������Ϣ 
            		coapObRelation1.proactiveCancel();				//ȡ���۲�״̬
            		System.out.println("proactiveCancel");
            	}
            	else if(int_choice==4) {
            		coapObRelation1 = client.observe(myObserveHandler); //ȡ���۲�״̬�� ���ǿ��Լ���observe��
            		System.out.println("observe again");
            	}
            }
	        //
            //---------------------------------------------
            in.close();
            
			//String xml = client.get(MediaTypeRegistry.APPLICATION_XML).getResponseText();
			//response.reactiveCancel();
            /*
	        System.out.println("enter to exit!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try { br.readLine(); } catch (IOException e) { }
			System.out.println("CANCELLATIONING");
			response.proactiveCancel();
			System.out.println("CANCELLATION FINISHED");
			*/
            //
            client.shutdown();
            System.exit(0);
		//		
		//	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
