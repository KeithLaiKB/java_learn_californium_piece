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
			// 原来
			// 53144	-> 	5656 	CON		GET ....
			// 5656		->	53144	ACK		1st_num
	        // 5656		->	53144	NON		2nd_num
	        // 5656		->	53144	NON		3rd_num
	        // 5656		->	53144	NON		4th_num
	        response = client.observe(myObserveHandler);
			//response = client.observeAndWait(handler);
	        //
	        // 如果用observe的时候
	        // 区别在于,
	        // 假设先启动client, 没启动server, 
	        // 那么此时他	会	显示wow_hello,
	        //
	        // 如果用observeAndWait的时候
	        // 假设先启动client, 没启动server, 
	        // 那么此时他	不会	显示wow_hello,
	        //
	        // 所以就像是一个线程阻塞了的感觉
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
            		// 我认为 delete 挺重要的 所以我这选择的是同步
            		//client.delete();				// 用的是 同步, 对面没回应, 就不能继续往下走
            		client.delete(myDeleteHandler); // 用的是 异步
            		//
            		// 注意 这个delete 
            		// 可以是让 	服务器删除 这个资源
            		// 也可以是让	服务器删除 某个记录(比如server那边 连了个数据库)
            		// 这取决于 server 那边的 handleDelete 里的操作
            		//
            		// 如果 是让服务器删除 这个资源
            		// 以后 client 不会再收到这个resource的内容, 但是 server还是在运行 
            		// 所以server那边需要 把timer关掉, 此外还有可能要 remove(Resource resource)
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
