package com.learn.californium.mytest1.unittest.multicase;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Scanner;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.californium.mytest1.MyThreadSleep;
import com.learn.californium.server.minimalexample.datadto.DtoFruit;
import com.learn.californium.server.minimalexample.myresc.concise.Con_MyObserverResource_Con_Mwe;
import com.learn.californium.server.minimalexample.myresc.concise.Con_MyResource_Mwe;





/**
 * 
 * 
 * <p>
 * 							description:																</br>	
 * &emsp;						before using this junit, please maven install other projects, then update this project	</br>
 * &emsp;&emsp;						debug ->maven clean-> maven install	(other projects)				</br>
 * &emsp;&emsp;						maven ->update project	(this projects)								</br>
 * 																										</br>
 * 	
 * &emsp;						hello_observer	</br>
 * &emsp;&emsp;						hello_observer_child1												</br>
 * &emsp;&emsp;&emsp;					hello_observer_child2											</br>
 * &emsp;&emsp;&emsp;&emsp;					hello_observer_child3										</br>
 * 																										</br>
 *
 * @author laipl
 *
 */
class UT_Observer_toOperateDelete_cancelobserve {
	
	
	String port2 = "coap://160.32.219.56:5656/hello_observer";		//有线连接树莓派, 路由给的地址是192.168.50.178
																	// 我把它的192.168.50.178:5656 映射成160.32.219.56:5656
	String port3 = "coap://160.32.219.56:5657/hello_observer";		//无线连接树莓派, 路由给的地址是192.168.50.179
																	// 我把它的192.168.50.179:5656 映射成160.32.219.56:5657
	
	String myuri1 		 = "coap://localhost:5656/hello_observer";

	
	static CoapServer server1 = null;
	static CoapClient client1 = null;
	//static CoapHandler myclientHandler1 = null;
	//Con_MyObserverResource_Con_Mwe myobResc1=null;
	//
	//---------------- data field ----------------
	String str_post_content="hi_i_am_string";
	// set data vo to test
	DtoFruit dtoFruit1= null;
	static ObjectMapper objectMapper = null;
	static String dtoFruit1AsString =null;
	//--------------------------------------------
	static CoapResponse resultFromServer1 = null;
	
	CoapObserveRelation coapObRelation1 =null;
	//
	//----------------------------------------------------------
	//
	UT_Observer_toOperateDelete_cancelobserve(){
		System.out.println("constructor");
	}
	
	
	static void datapreparation() {
		// set data vo to test
		DtoFruit dtoFruit1 = new DtoFruit();
		dtoFruit1.setName("i am apple");
		dtoFruit1.setWeight(23.666);
		//
		//
		// transform the vo into json
		objectMapper = new ObjectMapper();
		dtoFruit1AsString = new String("");
		//
		try {
			dtoFruit1AsString = objectMapper.writeValueAsString(dtoFruit1);
			// resp = client1.post(dtoFruit1AsString, MediaTypeRegistry.APPLICATION_JSON);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//
		//
	}
	//
	//
	//----------------------------------------------------------
	//---------------------start test---------------------------
	

	@BeforeAll
	static void preparation() {
		datapreparation();
	}

	
	@BeforeEach
	void beforesomething() {
		System.out.println("---------------------------------------------------------");
		//
		resultFromServer1 = null;
		//
		// -----------configure server-----------------------
		// new server
		server1 = new CoapServer(5656);										// define port to be 5656 
		//
		// add resource
		Con_MyObserverResource_Con_Mwe myobResc1 	= new Con_MyObserverResource_Con_Mwe("hello_observer");	// name "hello" is letter sensitive		
		//
		//
		server1.add(myobResc1);	
		//
		// -----------start server-----------------------
		System.out.println("starting server");
		server1.start();
		System.out.println("started server");
		//
		//----------------------------------------------------
		//--------------------- client1 ----------------------
		//
		// new client
		client1 = new CoapClient(myuri1);
		//
		//
		// set observe handler
		CoapHandler myObserveHandler1 = new CoapHandler() { // e.g., anonymous inner class

			@Override
			public void onLoad(CoapResponse response) { // also error resp.
				System.out.println("-------- client side onload start --------------");
				resultFromServer1 = response;
				System.out.println("result from server:" + response.isSuccess() );
				System.out.println("on load: " + response.getResponseText());
				System.out.println("response code name: " + response.getCode().name());
				System.out.println("--------- client side onload end ---------------");
			}

			@Override
			public void onError() { // I/O errors and timeouts
				System.err.println("Failed");
			}
		};
		
		// ----------- client1 observe -----------
		System.out.println("+++++ sending request +++++");
		coapObRelation1 = client1.observe(myObserveHandler1);
		System.out.println("++++++ sent request ++++++");
		//----------------------------------------
	}
	
	
	@AfterEach
	void aftersomething() {
		// server side
		server1.destroy();						//destory 只是释放了端口
		// client side
		coapObRelation1.reactiveCancel();		//还需要手动关掉relation							
        //MyThreadSleep.sleep20s();
		System.out.println("###############################################server1.destroy");
	}
	

	@Test
	void test_reactivecancel() {
		System.out.println("--------------------- test_reactivecancel ----------------------------");
		//
		// sleep main function avoid ending the program 
		// to let the handler thread to get more notifications from server
		MyThreadSleep.sleep20s();				
		//
		//----------------- client1 deletes server resource ----------------------
		//
		coapObRelation1.reactiveCancel();
		//------------------------------------------------------------------------
		//
		// sleep main function for getting the last notification due to concurrency
		MyThreadSleep.sleep10s();
        //
		resultFromServer1 = null;
		//
		// if reactiveCancel is unsuccessful, resultFromServer1 is not null
		// because during the main function is sleeping, the observe handler can receive the payload
		MyThreadSleep.sleep20s();
		//
        assertEquals(null,resultFromServer1,"test_canceled_client1");
	}
	
	
	@Test
	void test_proactivecancel() {
		System.out.println("--------------------- test_proactivecancel ----------------------------");
		//
		// sleep main function avoid ending the program 
		// to let the handler thread to get more notifications from server
		MyThreadSleep.sleep20s();				
		//
		//----------------- client1 deletes server resource ----------------------
		//
		coapObRelation1.reactiveCancel();
		//------------------------------------------------------------------------
		//
		// sleep main function for getting the last notification due to concurrency
		MyThreadSleep.sleep10s();
        //
		resultFromServer1 = null;
		//
		// if reactiveCancel is unsuccessful, resultFromServer1 is not null
		// because during the main function is sleeping, the observe handler can receive the payload
		MyThreadSleep.sleep10s();
		//
        assertEquals(null,resultFromServer1,"test_canceled_client1");
	}
	
}
