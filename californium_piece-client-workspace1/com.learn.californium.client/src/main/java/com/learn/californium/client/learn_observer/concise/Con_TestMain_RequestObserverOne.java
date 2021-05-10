package com.learn.californium.client.learn_observer.concise;

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
 * @author laipl
 *
 * �ο��� californium/demo-apps/cf-plugtest-client/src/main/java/org/eclipse/californium/plugtests/PlugtestClient.java
 */
public class Con_TestMain_RequestObserverOne {
    public static void main(String[] args) {

    	String myuri1 	     = "coap://localhost:5656/hello_observer";
    	//
    	//
    	CoapClient client = new CoapClient(myuri1);
        //
        CoapObserveRelation coapObRelation1;
		//
        try {
			
        	// set handler for observer method, because observe method needs asynchronous operation
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
	        //
	        //
	        coapObRelation1 = client.observe(myObserveHandler);
	        //
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
            		// ����Ϊ delete ͦ��Ҫ�� ��������ѡ�����ͬ��
            		client.delete();				// �õ��� ͬ��, ����û��Ӧ, �Ͳ��ܼ���������
            		//client.delete(myDeleteHandler); // �õ��� �첽
            	}
            	else if(int_choice==2) {
            		coapObRelation1.reactiveCancel();				//ȡ���۲�״̬
            		System.out.println("reactiveCancel");
            		
            	}
            	else if(int_choice==3) {
            		coapObRelation1.proactiveCancel();				//ȡ���۲�״̬
            		System.out.println("proactiveCancel");
            	}
            	else if(int_choice==4) {
            		coapObRelation1 = client.observe(myObserveHandler); //ȡ���۲�״̬�� ���ǿ��Լ���observe��
            		System.out.println("observe again");
            	}
            }
	        
	        
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
