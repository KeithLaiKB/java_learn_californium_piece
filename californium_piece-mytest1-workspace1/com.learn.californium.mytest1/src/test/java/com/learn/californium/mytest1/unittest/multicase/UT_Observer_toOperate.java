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
 *
 *
 * @author laipl
 *
 */
class UT_Observer_toOperate {
	
	
	String port2 = "coap://160.32.219.56:5656/hello_observer";		//有线连接树莓派, 路由给的地址是192.168.50.178
																	// 我把它的192.168.50.178:5656 映射成160.32.219.56:5656
	String port3 = "coap://160.32.219.56:5657/hello_observer";		//无线连接树莓派, 路由给的地址是192.168.50.179
																	// 我把它的192.168.50.179:5656 映射成160.32.219.56:5657
	
	String port1 		 = "coap://localhost:5656/hello_observer";
	String myuri1_c1 	 = "coap://localhost:5656/hello_observer/hello_observer_child1";
	String myuri1_c2 	 = "coap://localhost:5656/hello_observer/hello_observer_child1/hello_observer_child2";
	String myuri1_c3	 = "coap://localhost:5656/hello_observer/hello_observer_child1/hello_observer_child2/hello_observer_child3";
	
	
	static CoapServer server1 = null;
	static CoapClient client1 = null;
	//static CoapHandler myclientHandler1 = null;
	Con_MyObserverResource_Con_Mwe myobResc1=null;
	//
	//---------------- data field ----------------
	String str_post_content="hi_i_am_string";
	// set data vo to test
	DtoFruit dtoFruit1= null;
	static ObjectMapper objectMapper = null;
	static String dtoFruit1AsString =null;
	//--------------------------------------------
	static CoapResponse resultFromServer1 = null;
	static CoapResponse resultFromServer2 = null;
	
	
	//----------------------------------------------------------
	//
	UT_Observer_toOperate(){
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
		// -----------configure server-----------------------
		// new server
		server1 = new CoapServer(5656);										// define port to be 5656 
		//
		// add resource
		myobResc1 = new Con_MyObserverResource_Con_Mwe("hello_observer");	// name "hello" is letter sensitive
		//
		Con_MyObserverResource_Con_Mwe myobResc1_c1 = new Con_MyObserverResource_Con_Mwe("hello_observer_child1");
		Con_MyObserverResource_Con_Mwe myobResc1_c2 = new Con_MyObserverResource_Con_Mwe("hello_observer_child2");
		Con_MyObserverResource_Con_Mwe myobResc1_c3 = new Con_MyObserverResource_Con_Mwe("hello_observer_child3");
		//
		myobResc1_c2.add(myobResc1_c3);
		myobResc1_c1.add(myobResc1_c2);
		myobResc1.add(myobResc1_c1);
		//
		server1.add(myobResc1);	
		//
		// -----------start server-----------------------
		System.out.println("starting server");
		server1.start();
		System.out.println("started server");


	}
	
	/*
	@AfterEach
	void aftersomething() {
		server1.destroy();
	}*/
	
	@Test
	void testDelete_syn() {
		//
		// -----------prepare client-----------------------
		//
		// new client
		client1 = new CoapClient(myuri1_c1);
		//
		//
		// set handler
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
		//----------------------------------------
		System.out.println("+++++ sending request +++++");
		CoapObserveRelation coapObRelation1 = client1.observe(myObserveHandler1);
		System.out.println("++++++ sent request ++++++");
		//----------------------------------------
		
		//
		MyThreadSleep.sleep30s();
		//----------------- delete resource -----------------------

		CoapResponse del_reponse = null;
		try {
			del_reponse = client1.delete();
			System.out.println(del_reponse.getCode().name());
		} catch (ConnectorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		MyThreadSleep.sleep20s();
        //
		//
        assertEquals(false,resultFromServer1.isSuccess(),"if_success");
        assertEquals("DELETED",del_reponse.getCode().name(),"test_deleted");
		//
		//
		//------------------------------------------------------------------------
		//------------------------ second client ---------------------------------
		// new client
		CoapClient client2 = null;
		client2 = new CoapClient(myuri1_c1);
		//
		//
		// set handler
		CoapHandler myObserveHandler2 = new CoapHandler() { // e.g., anonymous inner class

			@Override
			public void onLoad(CoapResponse response) { // also error resp.
				System.out.println("-------- client side onload start --------------");
				resultFromServer2 = response;
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
		CoapObserveRelation coapObRelation2 = client2.observe(myObserveHandler2);
		MyThreadSleep.sleep20s();
        assertEquals(false,resultFromServer2.isSuccess(),"if_success");
        assertEquals("NOT_FOUND",resultFromServer2.getCode().name(),"if_success");

	}
	
	
	
	
	
}
