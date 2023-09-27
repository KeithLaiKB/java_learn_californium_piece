package com.learn.californium.client.compare.scenario1.s1_uncancel;


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
 * &emsp;						in this demo you don't have too much selection operation such as delete				</br>
 * &emsp;						before shutting down, it does not use cancel subscription here						</br>
 * 
 * 							ref:																					</br>	
 * &emsp;						californium/demo-apps/cf-plugtest-client/src/main/java/org/eclipse/californium/plugtests/PlugtestClient.java  	</br>	
 *  																												</br>
 * 
 * </p>
 * @author laipl
 *
 */
public class TestMain_RequestObserverOne_Simp {
    public static void main(String[] args) {
    	//
    	String myuri1 	     					= "coap://localhost:5683/hello_observer2";
    	CoapObserveRelation coapObRelation1		= null;
    	CoapHandler myObserveHandler 			= null;
    	//
    	//
    	// new client
    	CoapClient client = new CoapClient(myuri1);
        // set handler
        try {
			//
        	// set handler for observer method, because observe method needs asynchronous operation
			myObserveHandler = new CoapHandler() {

	            @Override
	            public void onLoad(CoapResponse response) {
	            	System.out.println("on load: " + response.getResponseText());
	            	System.out.println("get code: " + response.getCode().name());
	            }

	            @Override
	            public void onError() {
	            }
	        };
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //
        // observe
        coapObRelation1 = client.observe(myObserveHandler);
        //
        //
        //---------------------------------------------
        // wait for the notifications
        long startObserveTime=System.nanoTime();   			//»ñÈ¡¿ªÊ¼Ê±¼ä  
		//
		//
		boolean judge_timeout = false;
		while (judge_timeout==false) {
			long nowTime_tmp=System.nanoTime();
			long timelimit_tmp=20*1000000000L;
			if(nowTime_tmp-startObserveTime>timelimit_tmp) {
				judge_timeout=true;
			}
		}
        //
		//------------------- close --------------------------
		// cancel subscription
		// coapObRelation1.proactiveCancel();
		// shutdown client
        client.shutdown();
        //
        System.exit(0);
		//		
    }
}