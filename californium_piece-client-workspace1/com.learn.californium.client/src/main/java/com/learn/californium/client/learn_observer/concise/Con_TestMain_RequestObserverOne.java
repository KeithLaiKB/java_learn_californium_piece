package com.learn.californium.client.learn_observer.concise;


import java.util.Scanner;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;

/**
 * 
 * 
 * <p>
 * 							description:																			</br>	
 * &emsp;						client to observe																	</br>
 * 
 * 							ref:																					</br>	
 * &emsp;						californium/demo-apps/cf-plugtest-client/src/main/java/org/eclipse/californium/plugtests/PlugtestClient.java  	</br>	
 *  																												</br>
 *  
 *
 * @author laipl
 *
 */
public class Con_TestMain_RequestObserverOne {
    public static void main(String[] args) {

    	String myuri1 	     = "coap://localhost:5656/hello_observer";
    	//
    	CoapClient client = new CoapClient(myuri1);
        //
        CoapObserveRelation coapObRelation1;
		//
        try {
			//
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
	        // observe
	        coapObRelation1 = client.observe(myObserveHandler);
	        //
	        System.out.println("wow_hello");
	        //
	        if (coapObRelation1!=null) {
	        	System.out.println( "coapObRelation1:" + coapObRelation1.toString() );

	        } 
	        else {
	        	System.out.println("Request failed");
	        }	
	        //
	        //
	        //---------------------------------------------
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
            		// 我认为 delete 挺重要的 所以我这选择的是同步
            		client.delete();				// 用的是 同步, 对面没回应, 就不能继续往下走
            		//client.delete(myDeleteHandler); // 用的是 异步
            	}
            	else if(int_choice==2) {
            		coapObRelation1.reactiveCancel();				//取消观察状态
            		System.out.println("reactiveCancel");
            		
            	}
            	else if(int_choice==3) {
            		coapObRelation1.proactiveCancel();				//取消观察状态
            		System.out.println("proactiveCancel");
            	}
            	else if(int_choice==4) {
            		coapObRelation1 = client.observe(myObserveHandler); //取消观察状态后 还是可以继续observe的
            		System.out.println("observe again");
            	}
            }
	        //
            //---------------------------------------------
            in.close();
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
