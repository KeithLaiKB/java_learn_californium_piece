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
class UT_Observer {
	
	String port1 = "coap://localhost:5656/hello_observer";
	String port2 = "coap://160.32.219.56:5656/hello_observer";		//有线连接树莓派, 路由给的地址是192.168.50.178
																	// 我把它的192.168.50.178:5656 映射成160.32.219.56:5656
	String port3 = "coap://160.32.219.56:5657/hello_observer";		//无线连接树莓派, 路由给的地址是192.168.50.179
																	// 我把它的192.168.50.179:5656 映射成160.32.219.56:5657
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
	static boolean resultFromServer1=false;
	
	
	
	//----------------------------------------------------------
	//
	UT_Observer(){
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
		server1 = new CoapServer(5656);				// define port to be 5656 
		//
		// add resource
		myobResc1 = new Con_MyObserverResource_Con_Mwe("hello_observer");	// name "hello" is letter sensitive
		server1.add(myobResc1);	
		//
		// -----------start server-----------------------
		System.out.println("starting server");
		server1.start();
		System.out.println("started server");
		// -----------prepare client-----------------------
		//
		// new client
		client1 = new CoapClient(port1);
		
		//

	}
	
	/*
	@AfterEach
	void aftersomething() {
		server1.destroy();
	}*/
	
	@Test
	void testObserve() {
		//
		// set handler
		CoapHandler myObserveHandler1 = new CoapHandler() { // e.g., anonymous inner class

			@Override
			public void onLoad(CoapResponse response) { // also error resp.
				System.out.println("-------- client side onload start --------------");
				resultFromServer1 = response.isSuccess();
				System.out.println("result from server:" + response.isSuccess() );
				System.out.println("on load: " + response.getResponseText());
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
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
        		// 我认为 delete 挺重要的 所以我这选择的是同步
        		//client.delete();				// 用的是 同步, 对面没回应, 就不能继续往下走
        		//client1.delete(myDeleteHandler); // 用的是 异步
        	}
        	else if(int_choice==2) {	
        		// ref https://datatracker.ietf.org/doc/html/rfc7641#section-3.6
        		coapObRelation1.reactiveCancel();				//取消观察状态
        		System.out.println("reactiveCancel");
        		
        	}
        	else if(int_choice==3) {

        		// ref https://datatracker.ietf.org/doc/html/rfc7641#section-3.6 
        		coapObRelation1.proactiveCancel();				//取消观察状态
        		System.out.println("proactiveCancel");
        	}
        	else if(int_choice==4) {
        		coapObRelation1 = client1.observe(myObserveHandler1); //取消观察状态后 还是可以继续observe的
        		System.out.println("observe again");
        	}
        }
        //assertEquals(false,resultFromServer1,"if_success");
		assertEquals(true,resultFromServer1,"if_success");
		//fail("Not yet implemented");
	}
	
	
}
