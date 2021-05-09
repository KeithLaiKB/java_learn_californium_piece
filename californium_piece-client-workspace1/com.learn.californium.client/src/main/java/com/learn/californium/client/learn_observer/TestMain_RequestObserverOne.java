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

public class TestMain_RequestObserverOne {
    public static void main(String[] args) {

        //CoapClient client = new CoapClient("coap://localhost:5683/hello_observer");
        CoapClient client = new CoapClient("coap://localhost:5656/hello_observer");
        //
        CoapObserveRelation response;
		//
        try {
			
			//response = client.get();
	        //Request rq1=new Request(Code.POST);
	        //rq1.setType(Type.CON);
	        //
	        
			CoapHandler myObserveHandler = new CoapHandler() {

	            @Override
	            public void onLoad(CoapResponse response) {
	                //log.info("Command Response Ack: {}, {}", response.getCode(), response.getResponseText());
	            	System.out.println("---------------------------------------");
	            	System.out.println("on load: " + response.getResponseText());
	            	System.out.println("get code: " + response.getCode().name());
	            	
	            }

	            @Override
	            public void onError() {
	            }
	        };

	        
			CoapHandler myDeleteHandler = new CoapHandler() {

	            @Override
	            public void onLoad(CoapResponse response) {
	                //log.info("Command Response Ack: {}, {}", response.getCode(), response.getResponseText());
	            	System.out.println("---------------------------------------");
	            	System.out.println("on load: " + response.getResponseText());
	            	System.out.println("get code: " + response.getCode().name());
	            	
	            }

	            @Override
	            public void onError() {
	            }
	        };
			//response = client.observe(rq1,handler);
	        //
	        //
	        //
			// ԭ��
			// 53144	-> 	5656 	CON		GET ....
			// 5656		->	53144	ACK		1st_num
	        // 5656		->	53144	NON		2nd_num
	        // 5656		->	53144	NON		3rd_num
	        // 5656		->	53144	NON		4th_num
	        response = client.observe(myObserveHandler);
			//response = client.observeAndWait(handler);
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
	        
			//String xml = client.get(MediaTypeRegistry.APPLICATION_XML).getResponseText();
			
	        
			//response.reactiveCancel();
			
	        if (response!=null) {
	        
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
	        
	        
	        
	        Scanner in =new Scanner(System.in) ;
            int int_choice = 0;
            while(int_choice!=-1) {
            	System.out.println("here is the choice:");
            	System.out.println("-1: to exit");
            	System.out.println("1: to delete");
            	System.out.println("2: nothing");
            	System.out.println("3: to reactiveCancel");
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
            		//sampleClient.reconnect();
            		//System.out.println("reconnect broker");
            	}
            	else if(int_choice==3) {
            		response.reactiveCancel();
            		System.out.println("reactiveCancel");
            	}
            	else if(int_choice==4) {
            		response = client.observe(myObserveHandler);
            		System.out.println("observe again");
            	}
            }
	        
	        
            in.close();
            
            
            /*
	        System.out.println("enter to exit!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try { br.readLine(); } catch (IOException e) { }
			System.out.println("CANCELLATIONING");
			response.proactiveCancel();
			System.out.println("CANCELLATION FINISHED");
			*/
		//		
		//	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
