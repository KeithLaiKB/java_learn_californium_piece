package com.learn.californium.mytest1.unittest.onecase;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
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
class UT_Asynchronous_Synchronous {
	
	String port1 = "coap://localhost:5656/hello";
	String port2 = "coap://160.32.219.56:5656/hello";		//有线连接树莓派, 路由给的地址是192.168.50.178
															// 我把它的192.168.50.178:5656 映射成160.32.219.56:5656
	String port3 = "coap://160.32.219.56:5657/hello";		//无线连接树莓派, 路由给的地址是192.168.50.179
															// 我把它的192.168.50.179:5656 映射成160.32.219.56:5657
	static CoapServer server1 = null;
	static CoapClient client1 = null;
	//static CoapHandler myclientHandler1 = null;
	Con_MyResource_Mwe myResc1 = null;
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
	UT_Asynchronous_Synchronous(){
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
		myResc1 = new Con_MyResource_Mwe("hello");	// name "hello" is letter sensitive
		server1.add(myResc1);	
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
	
	
	@AfterEach
	void aftersomething() {
		server1.destroy();
	}
	
	@Test
	void testAsynchronousRequest() {
		//
		// set handler
		CoapHandler myclientHandler1 = new CoapHandler() { // e.g., anonymous inner class

			@Override
			public void onLoad(CoapResponse response) { // also error resp.
				resultFromServer1 = response.isSuccess();
				System.out.println("result from server:" + response.isSuccess() );
			}

			@Override
			public void onError() { // I/O errors and timeouts
				System.err.println("Failed");
			}
		};
		//----------------------------------------
		System.out.println("+++++ sending request +++++");
		client1.post(myclientHandler1, str_post_content, MediaTypeRegistry.TEXT_PLAIN);
		System.out.println("++++++ sent request ++++++");
		//----------------------------------------
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(true,resultFromServer1,"if_success");
		//fail("Not yet implemented");
	}
	
	
	@Test
	void testSynchronousRequest() {
		CoapResponse resp = null;
		//----------------------------------------
		try {
			System.out.println("+++++ sending request +++++");
			resp = client1.post(dtoFruit1AsString, MediaTypeRegistry.TEXT_PLAIN);
			System.out.println("++++++ sent request ++++++");
		} catch (ConnectorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//----------------------------------------
		System.out.println("result from server:" + resp.isSuccess() );
		assertEquals(true,resp.isSuccess(),"if_success");
		//fail("Not yet implemented");
	}

}
